#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

# --- Configuration ---
PROJECT_NAME="aws-sec-misconfig-remediator"
LAMBDA_HANDLER="lambda_function.lambda_handler"
LAMBDA_RUNTIME="python3.9"
LAMBDA_TIMEOUT=60 # seconds
LAMBDA_MEMORY=128 # MB
AWS_REGION="us-east-1" # Customize your AWS region here
SCHEDULE_EXPRESSION="rate(1 hour)" # Customize the schedule for the Lambda function

LAMBDA_ZIP_FILE="${PROJECT_NAME}.zip"
IAM_ROLE_NAME="${PROJECT_NAME}-role"
IAM_POLICY_NAME="${PROJECT_NAME}-policy"
CLOUDWATCH_RULE_NAME="${PROJECT_NAME}-rule"

echo "--- Starting Deployment of ${PROJECT_NAME} ---"

# --- 1. Create IAM Role for Lambda ---
echo "1. Creating/Updating IAM Role: ${IAM_ROLE_NAME}"
# Check if role exists
ROLE_ARN=$(aws iam get-role --role-name ${IAM_ROLE_NAME} --query 'Role.Arn' --output text 2>/dev/null || echo "")

if [ -z "$ROLE_ARN" ]; then
    echo "  Role does not exist, creating..."
    aws iam create-role \
        --role-name ${IAM_ROLE_NAME} \
        --assume-role-policy-document file://assume-role-policy.json > /dev/null
    ROLE_ARN=$(aws iam get-role --role-name ${IAM_ROLE_NAME} --query 'Role.Arn' --output text)
    echo "  IAM Role created with ARN: ${ROLE_ARN}"
else
    echo "  IAM Role already exists: ${ROLE_ARN}"
fi

# Give AWS a moment to propagate the role
sleep 5

# --- 2. Create and Attach IAM Policy ---
echo "2. Creating/Updating IAM Policy: ${IAM_POLICY_NAME}"
# Check if policy exists
POLICY_ARN=$(aws iam get-policy --scope Local --policy-name ${IAM_POLICY_NAME} --query 'Policy.Arn' --output text 2>/dev/null || echo "")

if [ -z "$POLICY_ARN" ]; then
    echo "  Policy does not exist, creating..."
    POLICY_ARN=$(aws iam create-policy \
        --policy-name ${IAM_POLICY_NAME} \
        --policy-document file://iam_policy.json \
        --query 'Policy.Arn' --output text)
    echo "  IAM Policy created with ARN: ${POLICY_ARN}"
else
    echo "  IAM Policy already exists: ${POLICY_ARN}"
    # Update the policy if it exists (useful for changes to iam_policy.json)
    aws iam create-policy-version \
        --policy-arn "$POLICY_ARN" \
        --policy-document file://iam_policy.json \
        --set-as-default
    echo "  IAM Policy updated."
fi

# Attach policy to role
echo "  Attaching policy ${POLICY_ARN} to role ${IAM_ROLE_NAME}"
# Check if policy is already attached (avoiding error on subsequent runs)
ATTACHED_POLICIES=$(aws iam list-attached-role-policies --role-name ${IAM_ROLE_NAME} --query "AttachedPolicies[?PolicyName=='${IAM_POLICY_NAME}'] | length(@)")
if [ "$ATTACHED_POLICIES" -eq 0 ]; then
    aws iam attach-role-policy \
        --role-name ${IAM_ROLE_NAME} \
        --policy-arn ${POLICY_ARN}
    echo "  Policy attached."
else
    echo "  Policy already attached."
fi

# Give AWS a moment to propagate the policy attachment
sleep 5

# --- 3. Package Lambda Function Code ---
echo "3. Packaging Lambda function code..."
zip -r ${LAMBDA_ZIP_FILE} lambda_function.py > /dev/null
echo "  Lambda code packaged into ${LAMBDA_ZIP_FILE}"

# --- 4. Create/Update Lambda Function ---
echo "4. Creating/Updating Lambda Function: ${PROJECT_NAME}"

# Check if Lambda function exists
FUNCTION_ARN=$(aws lambda get-function --function-name ${PROJECT_NAME} --query 'Configuration.FunctionArn' --output text 2>/dev/null || echo "")

if [ -z "$FUNCTION_ARN" ]; then
    echo "  Function does not exist, creating..."
    aws lambda create-function \
        --function-name ${PROJECT_NAME} \
        --runtime ${LAMBDA_RUNTIME} \
        --handler ${LAMBDA_HANDLER} \
        --timeout ${LAMBDA_TIMEOUT} \
        --memory-size ${LAMBDA_MEMORY} \
        --role ${ROLE_ARN} \
        --zip-file fileb://${LAMBDA_ZIP_FILE} \
        --environment Variables="{REMEDIATE=false}" \
        --region ${AWS_REGION} > /dev/null
    FUNCTION_ARN=$(aws lambda get-function --function-name ${PROJECT_NAME} --query 'Configuration.FunctionArn' --output text)
    echo "  Lambda Function created with ARN: ${FUNCTION_ARN}"
else
    echo "  Lambda Function already exists, updating code and configuration..."
    aws lambda update-function-code \
        --function-name ${PROJECT_NAME} \
        --zip-file fileb://${LAMBDA_ZIP_FILE} \
        --region ${AWS_REGION} > /dev/null
    aws lambda update-function-configuration \
        --function-name ${PROJECT_NAME} \
        --runtime ${LAMBDA_RUNTIME} \
        --handler ${LAMBDA_HANDLER} \
        --timeout ${LAMBDA_TIMEOUT} \
        --memory-size ${LAMBDA_MEMORY} \
        --role ${ROLE_ARN} \
        --environment Variables="{REMEDIATE=false}" \
        --region ${AWS_REGION} > /dev/null
    echo "  Lambda Function updated."
fi

# --- 5. Configure CloudWatch Event Rule to trigger Lambda ---
echo "5. Configuring CloudWatch Event Rule: ${CLOUDWATCH_RULE_NAME}"

# Put the CloudWatch Event Rule
aws events put-rule \
    --name ${CLOUDWATCH_RULE_NAME} \
    --schedule-expression "${SCHEDULE_EXPRESSION}" \
    --description "Triggers ${PROJECT_NAME} Lambda to check for EC2 misconfigurations" \
    --region ${AWS_REGION} > /dev/null
echo "  CloudWatch Event Rule '${CLOUDWATCH_RULE_NAME}' created/updated."

# Get the ARN of the Lambda function for permissions
LAMBDA_ARN=$(aws lambda get-function --function-name ${PROJECT_NAME} --query 'Configuration.FunctionArn' --output text)

# Add permission for CloudWatch Events to invoke the Lambda
# Check if permission already exists to avoid errors on re-runs
PERMISSION_STATEMENT_ID="allow_cloudwatch_to_invoke_lambda"
if ! aws lambda get-policy --function-name ${PROJECT_NAME} --query 'Policy' --output text 2>/dev/null | grep -q "${PERMISSION_STATEMENT_ID}"; then
    aws lambda add-permission \
        --function-name ${PROJECT_NAME} \
        --statement-id ${PERMISSION_STATEMENT_ID} \
        --action 'lambda:InvokeFunction' \
        --principal 'events.amazonaws.com' \
        --source-arn "arn:aws:events:${AWS_REGION}:$(aws sts get-caller-identity --query 'Account' --output text):rule/${CLOUDWATCH_RULE_NAME}" \
        --region ${AWS_REGION} > /dev/null
    echo "  Permission added for CloudWatch Events to invoke Lambda."
else
    echo "  Permission for CloudWatch Events to invoke Lambda already exists."
fi


# Add the Lambda function as a target for the CloudWatch Event Rule
# Using 'put-targets' is idempotent, so it can be re-run safely.
aws events put-targets \
    --rule ${CLOUDWATCH_RULE_NAME} \
    --targets "Id"="1","Arn"="${LAMBDA_ARN}" \
    --region ${AWS_REGION} > /dev/null
echo "  Lambda Function added as target for CloudWatch Event Rule."

# --- Cleanup temporary files ---
echo "6. Cleaning up temporary files..."
rm -f ${LAMBDA_ZIP_FILE}
echo "  Removed ${LAMBDA_ZIP_FILE}"

echo "--- Deployment of ${PROJECT_NAME} Complete! ---"
echo "The Lambda function will now run on the schedule: ${SCHEDULE_EXPRESSION}"
echo "You can view logs in CloudWatch Logs under /aws/lambda/${PROJECT_NAME}"
echo "To enable automatic remediation, run: "
echo "aws lambda update-function-configuration --function-name ${PROJECT_NAME} --environment Variables=\"{REMEDIATE=true}\" --region ${AWS_REGION}"