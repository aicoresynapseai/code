#!/bin/bash

# This script demonstrates how Terraform commands can be used in a CI/CD pipeline.
# In a real CI/CD system, environment variables would manage credentials and
# sensitive values (like DB passwords), and robust error handling would be crucial.

# Exit immediately if a command exits with a non-zero status.
set -e

echo "Starting Terraform deployment for AWS scalable architecture..."

# --- Step 1: Initialize Terraform ---
# This command downloads the necessary AWS provider plugin and initializes
# the backend (e.g., S3 for state management, though not configured in this sample).
echo "Running 'terraform init'..."
terraform init -backend-config="bucket=${PROJECT_NAME}-tfstate" -backend-config="key=terraform.tfstate" -backend-config="region=${AWS_REGION}"
# Note: For production, you'd configure a robust backend (e.g., S3 + DynamoDB)
# using `backend "s3" {}` block in `main.tf` or `versions.tf`.
# The `backend-config` flags are illustrative if not defined in code.

# --- Step 2: Validate Terraform Configuration ---
# Ensures the configuration files are syntactically valid and internally consistent.
echo "Running 'terraform validate'..."
terraform validate

# --- Step 3: Generate and Review Terraform Plan ---
# This step creates an execution plan, showing exactly what Terraform will do.
# In CI/CD, this plan can be saved to a file and reviewed manually or
# automatically before the apply stage.
echo "Running 'terraform plan'..."
terraform plan -out=tfplan # Save the plan to a file for later application

# For CI/CD, you might have a manual approval step here based on the plan output.

# --- Step 4: Apply Terraform Configuration ---
# This command applies the changes required to reach the desired state of the configuration.
# The `-auto-approve` flag bypasses interactive approval, which is standard for CI/CD.
echo "Applying Terraform changes from 'tfplan'..."
terraform apply "tfplan"

echo "Terraform deployment completed successfully!"

# --- Optional: Output important information ---
# In a CI/CD pipeline, you might capture these outputs and pass them
# to subsequent stages (e.g., for application deployment, monitoring setup).
echo "Terraform Outputs:"
terraform output

# --- Error Handling Example (Conceptual) ---
# In a real pipeline, you'd integrate proper error reporting.
# For example, using `trap` for cleanup or notification on failure.
# trap 'echo "Terraform deployment failed! Review logs." && exit 1' ERR