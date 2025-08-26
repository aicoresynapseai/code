# This Terraform configuration sets up an AWS KMS Key and an S3 bucket
# with default encryption using the created KMS key.

# Configure the AWS provider
provider "aws" {
  region = "us-east-1" # You can change your preferred AWS region here
}

# 1. Create an AWS KMS Key for encryption at rest
resource "aws_kms_key" "my_encryption_key" {
  description             = "KMS key for CloudSecureEncrypt project"
  deletion_window_in_days = 7 # Minimum 7 days for key deletion
  enable_key_rotation     = true # Enable automatic key rotation for enhanced security

  tags = {
    Name = "CloudSecureEncrypt-KMS-Key"
  }
}

# 2. Create an S3 Bucket with Default Encryption using the KMS Key
resource "aws_s3_bucket" "encrypted_bucket" {
  bucket = "cloudsecureencrypt-data-at-rest-${random_id.bucket_suffix.hex}" # Unique bucket name

  tags = {
    Name        = "CloudSecureEncrypt-S3-Bucket"
    Environment = "Demo"
  }
}

# Add a random suffix to the bucket name to ensure global uniqueness
resource "random_id" "bucket_suffix" {
  byte_length = 8
}

# Configure default server-side encryption for the S3 bucket
# This ensures that all objects uploaded to this bucket are encrypted by default
# using the specified AWS KMS key.
resource "aws_s3_bucket_server_side_encryption_configuration" "bucket_encryption" {
  bucket = aws_s3_bucket.encrypted_bucket.id

  rule {
    apply_server_side_encryption_by_default {
      kms_master_key_id = aws_kms_key.my_encryption_key.arn # Use the ARN of our KMS key
      sse_algorithm     = "aws:kms"                        # Specify KMS as the encryption algorithm
    }
  }
}

# Output the ARN of the KMS key and the name of the S3 bucket
# These outputs can be used by other scripts or for reference.
output "kms_key_arn" {
  description = "The ARN of the AWS KMS key."
  value       = aws_kms_key.my_encryption_key.arn
}

output "s3_bucket_name" {
  description = "The name of the S3 bucket configured with KMS encryption."
  value       = aws_s3_bucket.encrypted_bucket.bucket
}