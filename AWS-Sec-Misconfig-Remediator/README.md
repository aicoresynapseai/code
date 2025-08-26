This project provides an automated solution for detecting and remediating common security misconfigurations in AWS, specifically focusing on overly permissive EC2 Security Group rules. The core of the solution is an AWS Lambda function that scans for Security Groups allowing public access (0.0.0.0/0) on sensitive ports and can automatically remove such rules.

Key Features:
- **Automated Detection:** A Python Lambda function regularly scans your AWS account for misconfigured Security Groups.
- **Configurable Remediation:** The Lambda function can be configured to either just report misconfigurations or automatically remediate them by revoking the insecure ingress rules.
- **Scheduled Scans:** Utilizes AWS CloudWatch Events (EventBridge) to trigger the Lambda function on a defined schedule (e.g., hourly).
- **Easy Deployment:** A simple shell script facilitates the deployment of the Lambda function, IAM role, and CloudWatch Event rule.

How It Works:
1.  **Deployment:** The deploy.sh script creates an IAM role with necessary permissions, deploys the Python Lambda function, and sets up a CloudWatch Event rule to invoke the Lambda on a schedule.
2.  **Scanning:** When triggered, the Lambda function uses the AWS SDK (boto3) to describe all EC2 Security Groups in the region.
3.  **Detection:** It iterates through each Security Group's ingress rules, checking for IpPermissions that allow inbound traffic from 0.0.0.0/0 on a predefined list of sensitive ports (e.g., SSH, RDP, HTTP, HTTPS).
4.  **Remediation (Optional):** If the 'REMEDIATE' environment variable is set to 'true' for the Lambda function, it will automatically revoke the identified insecure ingress rules. Otherwise, it will only log the findings.
5.  **Logging:** All detection and remediation actions are logged to AWS CloudWatch Logs.

Prerequisites:
- AWS Account with appropriate permissions to create IAM roles, Lambda functions, and CloudWatch Event rules.
- AWS CLI installed and configured with credentials.
- Zip utility (usually pre-installed on Linux/macOS).

Setup and Deployment:
1.  **Clone the Repository:**
    git clone <repository-url>
    cd AWS-Sec-Misconfig-Remediator

2.  **Review Configuration:**
    - Open lambda_function.py to review the list of DANGEROUS_PORTS.
    - Open deploy.sh to customize the AWS region, Lambda memory, timeout, or the schedule for the CloudWatch Event rule.

3.  **Deploy the Solution:**
    Execute the deployment script:
    bash deploy.sh

    The script will:
    - Create an IAM role for the Lambda function.
    - Attach a policy granting necessary permissions (EC2 describe/revoke, CloudWatch Logs).
    - Package the Lambda function code.
    - Create the Lambda function.
    - Create a CloudWatch Event rule to trigger the Lambda on an hourly schedule.
    - Grant the CloudWatch Event rule permission to invoke the Lambda.

4.  **Enable Remediation (Optional):**
    By default, the Lambda function is deployed in 'detection-only' mode (REMEDIATE=false). To enable automatic remediation, update the Lambda function's environment variable:
    aws lambda update-function-configuration --function-name aws-sec-misconfig-remediator --environment Variables="{REMEDIATE=true}"

    **Warning:** Enable remediation with caution. Ensure you fully understand the impact before automatically modifying Security Group rules in a production environment.

Monitoring:
- All Lambda invocations and findings will be logged in CloudWatch Logs under the log group: /aws/lambda/aws-sec-misconfig-remediator.
- You can monitor the Lambda function's metrics (invocations, errors) in the AWS Lambda console.

Cleanup:
To remove all resources created by this project, you can use the following AWS CLI commands:

# Remove CloudWatch Event Rule and Target
aws events remove-targets --rule aws-sec-misconfig-remediator-rule --ids 1
aws events delete-rule --name aws-sec-misconfig-remediator-rule
aws lambda remove-permission --function-name aws-sec-misconfig-remediator --statement-id allow_cloudwatch_to_invoke_lambda

# Delete Lambda Function
aws lambda delete-function --function-name aws-sec-misconfig-remediator

# Detach Policy and Delete IAM Role
aws iam detach-role-policy --role-name aws-sec-misconfig-remediator-role --policy-arn arn:aws:iam::<YOUR_ACCOUNT_ID>:policy/aws-sec-misconfig-remediator-policy
aws iam delete-policy --policy-arn arn:aws:iam::<YOUR_ACCOUNT_ID>:policy/aws-sec-misconfig-remediator-policy
aws iam delete-role --role-name aws-sec-misconfig-remediator-role

Replace <YOUR_ACCOUNT_ID> with your actual AWS account ID.

Future Enhancements:
- Support for other AWS services (S3 buckets, RDS instances).
- Integration with AWS Security Hub or GuardDuty.
- Parameterize dangerous ports and public CIDRs via Lambda environment variables or AWS Systems Manager Parameter Store.
- Use CloudFormation or AWS CDK for more robust infrastructure as code deployment.