# This Terraform configuration demonstrates a SECURE S3 bucket following best practices.
# This configuration is suitable for production use (adjust details as needed).

# Generate a unique bucket suffix to avoid naming conflicts
resource "random_id" "bucket_suffix" {
  byte_length = 8
}

# Define the AWS provider, using the region from variables.tf
provider "aws" {
  region = var.aws_region
}

# Data source to get current AWS account ID for log policy
data "aws_caller_identity" "current" {}


# --- S3 Bucket for Access Logs ---
# It's a best practice to store access logs in a separate, highly secured bucket.
resource "aws_s3_bucket" "log_bucket" {
  bucket = "tf-secure-log-${random_id.bucket_suffix.hex}"
  # Default ACL for a private bucket
  acl    = "private"

  tags = {
    Environment = "Demo"
    Security    = "Secure"
    Purpose     = "S3AccessLogs"
  }
}

# Explicitly apply Block Public Access settings to the log bucket
# This ensures the log bucket itself is private and cannot be made public.
resource "aws_s3_bucket_public_access_block" "log_bucket_public_access_block" {
  bucket                  = aws_s3_bucket.log_bucket.id
  block_public_acls       = true
  ignore_public_acls      = true
  block_public_policy     = true
  restrict_public_buckets = true
}

# Enforce default encryption for the log bucket
resource "aws_s3_bucket_server_side_encryption_configuration" "log_bucket_encryption" {
  bucket = aws_s3_bucket.log_bucket.id
  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256" # Use SSE-S3 for logs
    }
  }
}

# Add a bucket policy to the log bucket to allow S3 log delivery service to write logs.
# This is crucial for enabling logging for other buckets.
# The policy restricts write access to the S3 logging service for our specific account and source bucket.
resource "aws_s3_bucket_policy" "log_bucket_delivery_policy" {
  bucket = aws_s3_bucket.log_bucket.id

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect    = "Allow",
        Principal = { Service = "logging.s3.amazonaws.com" },
        Action    = "s3:PutObject",
        Resource  = "${aws_s3_bucket.log_bucket.arn}/*", # Allow PutObject on all objects
        Condition = {
          StringEquals = {
            "aws:SourceAccount" = data.aws_caller_identity.current.account_id
          },
          ArnLike = {
            "aws:SourceArn" = aws_s3_bucket.secure_bucket.arn # Only allow logs from our secure bucket
          }
        }
      },
      {
        Effect    = "Allow",
        Principal = { Service = "logging.s3.amazonaws.com" },
        Action    = "s3:GetBucketAcl",
        Resource  = aws_s3_bucket.log_bucket.arn # Required for S3 to verify ACLs
      }
    ]
  })
  # Explicitly depend on secure_bucket existing for arn in policy
  depends_on = [aws_s3_bucket.secure_bucket]
}


# --- SECURE S3 Bucket Definition ---
resource "aws_s3_bucket" "secure_bucket" {
  bucket = "tf-secure-data-${random_id.bucket_suffix.hex}"
  # Default ACL for a private bucket
  acl    = "private"

  tags = {
    Environment = "Demo"
    Security    = "Secure"
    Purpose     = "DemonstrationOfGoodPractices"
  }
}

# 1. Enable S3 Block Public Access
# This is the most critical security setting, preventing all forms of public access.
resource "aws_s3_bucket_public_access_block" "secure_bucket_public_access_block" {
  bucket                  = aws_s3_bucket.secure_bucket.id
  block_public_acls       = true
  ignore_public_acls      = true
  block_public_policy     = true
  restrict_public_buckets = true
}

# 2. Enforce Server-Side Encryption
# All objects uploaded to this bucket will be encrypted by default using AES256 (SSE-S3).
resource "aws_s3_bucket_server_side_encryption_configuration" "secure_bucket_encryption" {
  bucket = aws_s3_bucket.secure_bucket.id
  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256" # Use SSE-S3
    }
  }
}

# 3. Enable Versioning
# Protects against accidental overwrites and deletions, enabling recovery of previous object versions.
resource "aws_s3_bucket_versioning" "secure_bucket_versioning" {
  bucket = aws_s3_bucket.secure_bucket.id
  versioning_configuration {
    status = "Enabled"
  }
}

# 4. Configure Access Logging
# All requests to the secure bucket will be logged to the designated log bucket.
resource "aws_s3_bucket_logging" "secure_bucket_logging" {
  bucket        = aws_s3_bucket.secure_bucket.id
  target_bucket = aws_s3_bucket.log_bucket.id
  target_prefix = "secure_bucket_logs/"
  # Ensure the log bucket policy is set correctly for log delivery (handled by `log_bucket_delivery_policy` above)
  # Explicitly depend on the log bucket policy to be applied first.
  depends_on = [aws_s3_bucket_policy.log_bucket_delivery_policy]
}

# 5. Lifecycle Policy (Optional but recommended)
# Automatically transitions or expires objects, useful for cost management and data retention.
resource "aws_s3_bucket_lifecycle_configuration" "secure_bucket_lifecycle" {
  bucket = aws_s3_bucket.secure_bucket.id
  rule {
    id     = "expire_old_versions"
    status = "Enabled"
    noncurrent_version_expiration {
      days = 90 # Expire non-current versions after 90 days
    }
    # Example: Transition current versions to Glacier after 30 days
    transition {
      days          = 30
      storage_class = "GLACIER"
    }
    # Example: Expire objects after 365 days
    expiration {
      days = 365
    }
  }
}

# Upload a test object to demonstrate encryption and private access
resource "local_file" "secure_test_object" {
  content  = "This is a test object in a secure S3 bucket."
  filename = "secure-test-object.txt"
}

resource "aws_s3_object" "private_test_object" {
  bucket       = aws_s3_bucket.secure_bucket.id
  key          = "secure-test-object.txt"
  source       = local_file.secure_test_object.filename
  acl          = "private" # Explicitly private, enforced by Block Public Access
  content_type = "text/plain"
}

# Cleanup the local test file after apply
resource "null_resource" "cleanup_local_file" {
  depends_on = [aws_s3_object.private_test_object] # Ensure object is uploaded first
  provisioner "local-exec" {
    command = "rm -f ${local_file.secure_test_object.filename}"
    when    = destroy # Only run on destroy to ensure file exists for upload during apply
  }
}