
# Configure the AWS provider
provider "aws" {
  region = var.aws_region # Use the AWS region specified in variables.tf
}

# 1. IAM Roles: Define an IAM Role for a service (e.g., an EC2 instance)
# This role will allow an EC2 instance to assume this role, granting it permissions
# without needing to store AWS credentials directly on the instance.
resource "aws_iam_role" "application_role" {
  name = var.iam_role_name # Use the role name from variables.tf

  # Define the trust policy that allows an EC2 instance to assume this role.
  # This is the "who" part of the access control for the role itself.
  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Action = "sts:AssumeRole",
        Effect = "Allow",
        Principal = {
          Service = "ec2.amazonaws.com" # Allows EC2 service to assume this role
        }
      }
    ]
  })

  # Tags are good practice for organization and cost allocation
  tags = {
    Environment = "Dev"
    Project     = "IAM-Best-Practices"
  }
}

# 2. Least Privilege Access: Define a custom IAM Policy
# This policy grants read-only access to S3 buckets, demonstrating least privilege.
# It's defined as a separate resource for reusability.
resource "aws_iam_policy" "s3_read_only_custom_policy" {
  name        = "${var.iam_role_name}-s3-read-only" # Policy name based on role
  description = "Provides read-only access to S3 for the application role."

  # Policy document sourced from a local JSON file, promoting modularity.
  # This makes the policy definition clean and easy to manage.
  policy = file("${path.module}/policies/s3-read-only-policy.json")
}

# Attach the custom S3 read-only policy to the application role
# This explicitly links the defined permissions to the role.
resource "aws_iam_role_policy_attachment" "s3_read_only_attachment" {
  role       = aws_iam_role.application_role.name      # Reference the role by its name
  policy_arn = aws_iam_policy.s3_read_only_custom_policy.arn # Reference the policy by its ARN
}

# Optional: Attach a managed policy (e.g., for CloudWatch Logs)
# This shows how to combine custom policies with AWS managed policies.
resource "aws_iam_role_policy_attachment" "cloudwatch_logs_attachment" {
  role       = aws_iam_role.application_role.name
  policy_arn = "arn:${data.aws_partition.current.partition}:iam::aws:policy/CloudWatchLogsFullAccess"
}

# Data source to get current AWS partition, useful for ARN construction.
data "aws_partition" "current" {}