# AWS Provider Configuration
provider "aws" {
  region = var.aws_region
}

# --- Shared Responsibility Model: Customer's "Security IN the Cloud" Examples ---

# 1. S3 Bucket with Least Privilege Policy (Data Security - Customer Responsibility)
# This demonstrates securing customer data and access to it.
resource "aws_s3_bucket" "secure_customer_bucket" {
  bucket = var.secure_s3_bucket_name
  acl    = "private" # Ensure the bucket is not publicly accessible by default

  # Enforce encryption for all objects
  server_side_encryption_configuration {
    rule {
      apply_server_side_encryption_by_default {
        sse_algorithm = "AES256" # Customer's responsibility to encrypt data at rest
      }
    }
  }

  # Block public access at the bucket level (strong customer control)
  # This helps prevent misconfigurations like the public S3 policy example.
  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true

  tags = {
    Environment = "Dev"
    Project     = "SharedResponsibilityLab"
    Security    = "LeastPrivilege"
  }
}

# Attach an S3 bucket policy based on the customer_least_privilege_s3_access.json
# Note: This policy needs to be attached to a specific user/role, or be a bucket policy.
# For demonstration, we'll create a user and attach the policy.
resource "aws_iam_user" "s3_user" {
  name = "s3-least-privilege-user"
  path = "/system/"
}

# Load the policy content from the file
data "aws_iam_policy_document" "s3_access_policy_doc" {
  source_json = file("${path.module}/../iam_policies/customer_least_privilege_s3_access.json")

  # Replace the placeholder bucket name with the actual bucket name
  # This demonstrates how customers parameterize their configurations.
  override {
    statement {
      sid = "AllowReadWriteToSpecificBucket"
      resources = [
        aws_s3_bucket.secure_customer_bucket.arn,
        "${aws_s3_bucket.secure_customer_bucket.arn}/*"
      ]
    }
  }
}

resource "aws_iam_policy" "s3_least_privilege_policy" {
  name        = "S3LeastPrivilegeAccessPolicy"
  description = "Grants least privilege access to a specific S3 bucket."
  policy      = data.aws_iam_policy_document.s3_access_policy_doc.json
}

resource "aws_iam_user_policy_attachment" "s3_user_policy_attachment" {
  user       = aws_iam_user.s3_user.name
  policy_arn = aws_iam_policy.s3_least_privilege_policy.arn
}


# 2. IAM Role for EC2 Management (Identity & Access Management, Network Config - Customer Responsibility)
# This shows how customers define who can manage EC2 instances and their associated security groups.
resource "aws_iam_role" "ec2_management_role" {
  name = "ec2-instance-management-role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ec2.amazonaws.com" # Example: allowing an EC2 instance to assume this role
        }
      },
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          AWS = "arn:aws:iam::${data.aws_caller_identity.current.account_id}:root" # Or specific user/role ARN for human access
        }
      }
    ]
  })

  tags = {
    Environment = "Dev"
    Project     = "SharedResponsibilityLab"
    Security    = "IAMRole"
  }
}

# Load the EC2 management policy content
data "aws_iam_policy_document" "ec2_management_policy_doc" {
  source_json = file("${path.module}/../iam_policies/customer_iam_ec2_management.json")
}

resource "aws_iam_policy" "ec2_management_policy" {
  name        = "EC2ManagementPolicy"
  description = "Grants permissions to manage EC2 instances and security groups."
  policy      = data.aws_iam_policy_document.ec2_management_policy_doc.json
}

resource "aws_iam_role_policy_attachment" "ec2_role_policy_attachment" {
  role       = aws_iam_role.ec2_management_role.name
  policy_arn = aws_iam_policy.ec2_management_policy.arn
}

# Data source for current AWS account ID
data "aws_caller_identity" "current" {}

# Example of a Security Group (Network & Firewall Configuration - Customer Responsibility)
resource "aws_security_group" "web_sg" {
  name        = "web-traffic-sg"
  description = "Allow inbound HTTP and HTTPS traffic"
  vpc_id      = var.default_vpc_id # Assuming you have a default VPC or specify one

  ingress {
    description = "Allow HTTP from anywhere"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # Customer chooses to open to the world or restrict
  }

  ingress {
    description = "Allow HTTPS from anywhere"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "WebSecurityGroup"
  }
}

# Placeholder for Misconfigured S3 Bucket (demonstrates potential risk if applied)
# We will not actually create this with a public policy directly in Terraform as a "good" example,
# but rather illustrate the policy itself in `misconfigured_overly_permissive_s3_access.json`.
# If you wanted to *demonstrate* the risk, you could enable public access here.
/*
resource "aws_s3_bucket" "misconfigured_public_bucket" {
  bucket = var.misconfigured_s3_bucket_name
  acl    = "public-read" # DANGEROUS: Makes bucket objects publicly readable

  tags = {
    Environment = "Test"
    Project     = "SharedResponsibilityLab"
    Security    = "Misconfigured"
  }
}

resource "aws_s3_bucket_policy" "misconfigured_public_policy" {
  bucket = aws_s3_bucket.misconfigured_public_bucket.id
  policy = file("${path.module}/../iam_policies/misconfigured_overly_permissive_s3_access.json")
  # WARNING: Applying this policy will make the bucket objects publicly readable.
}
*/