#!/bin/bash

# This script simulates a CI/CD pipeline step where GenAI generates IaC.
# It requires Python 3 and optionally Terraform and AWS CLI installed.

echo "--- CI/CD Pipeline: Initiating IaC Generation ---"

# Step 1: Trigger the GenAI service to generate IaC templates
# In a real pipeline, this might be a call to an internal GenAI API service.
# Here, we run our local Python script.
echo "1. Triggering GenAI to generate IaC from high-level spec..."
python gen-ai-iac-generator.py \
  --input-file input_config_java_microservice.json \
  --output-terraform generated_main.tf \
  --output-cloudformation generated_cloudformation.yaml

GEN_AI_EXIT_CODE=$?
if [ $GEN_AI_EXIT_CODE -ne 0 ]; then
  echo "ERROR: GenAI IaC generation failed. Exiting pipeline."
  exit $GEN_AI_EXIT_CODE
fi

echo "   IaC templates successfully generated: generated_main.tf and generated_cloudformation.yaml"

echo "--- IaC Validation and Plan (Simulated) ---"

# Step 2: Commit generated IaC back to version control (conceptual)
# In a real scenario, this would involve 'git add', 'git commit', 'git push'
# or sending the generated files to a designated IaC repository.
echo "2. Generated IaC committed to version control (conceptual step)."

# Step 3: Validate generated Terraform using 'terraform init' and 'terraform plan'
# This ensures syntax correctness and shows what changes will be applied.
if command -v terraform &> /dev/null; then
  echo "3. Initializing Terraform..."
  terraform -chdir="$(dirname "$0")" init -backend=false # -backend=false for local validation without state
  
  echo "   Running Terraform plan (dry run)..."
  terraform -chdir="$(dirname "$0")" plan -out=tfplan.out -var "key_name=your-key-pair" # IMPORTANT: Replace with a valid key pair name for actual deployment
  TERRAFORM_PLAN_EXIT_CODE=$?
  if [ $TERRAFORM_PLAN_EXIT_CODE -eq 0 ]; then
    echo "   Terraform plan successful. Infrastructure changes previewed."
  else
    echo "ERROR: Terraform plan failed. Review generated_main.tf. Exiting pipeline."
    exit $TERRAFORM_PLAN_EXIT_CODE
  fi
else
  echo "3. Terraform CLI not found. Skipping 'terraform init' and 'terraform plan' simulation."
  echo "   (Manual review of generated_main.tf is recommended)"
fi

# Step 4: Validate generated CloudFormation using AWS CLI
# This checks for syntax errors and valid resource declarations.
if command -v aws &> /dev/null; then
  echo "4. Validating CloudFormation template..."
  aws cloudformation validate-template --template-body file://generated_cloudformation.yaml
  CF_VALIDATE_EXIT_CODE=$?
  if [ $CF_VALIDATE_EXIT_CODE -eq 0 ]; then
    echo "   CloudFormation template validation successful."
  else
    echo "ERROR: CloudFormation template validation failed. Review generated_cloudformation.yaml. Exiting pipeline."
    exit $CF_VALIDATE_EXIT_CODE
  fi
else
  echo "4. AWS CLI not found. Skipping CloudFormation validation simulation."
  echo "   (Manual review of generated_cloudformation.yaml is recommended)"
fi

echo "--- IaC Deployment (Simulated) ---"

# Step 5: Apply IaC (conceptual)
# In a real pipeline, this would be `terraform apply` or `aws cloudformation deploy`.
# This step is typically manual or requires a strict approval process for production.
echo "5. IaC deployment step (conceptual). For production, this usually requires human approval."
echo "   `terraform apply tfplan.out` (for Terraform) or"
echo "   `aws cloudformation deploy --template-file generated_cloudformation.yaml --stack-name MyJavaServiceStack` (for CloudFormation)"
echo "   would be executed here."

echo "--- CI/CD Pipeline: Completed Successfully (Simulation) ---"

echo "Congratulations! Your Java microservice infrastructure is ready (or almost ready) for deployment."
echo "Check the 'generated_main.tf' and 'generated_cloudformation.yaml' files for the generated IaC."