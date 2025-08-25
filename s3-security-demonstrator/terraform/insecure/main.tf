# This Terraform configuration demonstrates an intentionally INSECURE S3 bucket.
# DO NOT USE THIS IN PRODUCTION.

# Generate a unique bucket suffix to avoid naming conflicts
resource "random_id" "bucket_suffix" {
  byte_length = 8
}

# Define the AWS provider, using the region from variables.tf
provider "aws" {
  region = var.aws_region
}

# --- INSECURE S3 Bucket Definition ---
resource "aws_s3_bucket" "insecure_bucket" {
  # Naming convention for the insecure bucket
  bucket = "tf-insecure-demo-${random_id.bucket_suffix.hex}"

  # Most crucial insecure setting: NOT explicitly enabling S3 Block Public Access.
  # While newer AWS provider versions might default to blocking public access
  # at the bucket level, older versions or explicit overrides (like the bucket policy below)
  # can still lead to public exposure. Here, we rely on the bucket policy to make it public.
  # We are deliberately *not* creating an `aws_s3_bucket_public_access_block` resource,
  # allowing the bucket policy to take effect if account-level settings permit.

  tags = {
    Environment = "Demo"
    Security    = "Insecure"
    Purpose     = "DemonstrationOfBadPractices"
  }
}

# --- Intentionally permissive Bucket Policy ---
# This policy grants public read access to all objects in the bucket.
# This is a significant security vulnerability.
resource "aws_s3_bucket_policy" "insecure_policy" {
  bucket = aws_s3_bucket.insecure_bucket.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect    = "Allow"
        Principal = "*" # Allows ANYONE
        Action    = "s3:GetObject"
        Resource  = "${aws_s3_bucket.insecure_bucket.arn}/*" # On ALL objects in the bucket
      },
    ]
  })
}

# --- Lack of Server-Side Encryption ---
# No `aws_s3_bucket_server_side_encryption_configuration` resource.
# This means objects are not encrypted by default at rest in the bucket,
# relying on client-side encryption or no encryption at all.
# (Modern S3 often defaults to SSE-S3, but this configuration explicitly omits it).

# --- Lack of Versioning ---
# No `aws_s3_bucket_versioning` resource.
# This means accidental deletions or malicious overwrites cannot be easily recovered.

# --- Lack of Logging ---
# No `aws_s3_bucket_logging_configuration` resource.
# This means no audit trail of who accessed the bucket or its objects.

# Upload a test object to make it immediately verifiable as public
resource "local_file" "test_object" {
  content  = "This is a test object in an insecure S3 bucket."
  filename = "test-object.txt"
}

resource "aws_s3_object" "public_test_object" {
  bucket       = aws_s3_bucket.insecure_bucket.id
  key          = "test-object.txt"
  source       = local_file.test_object.filename
  acl          = "public-read" # Explicitly making the object public (deprecated but works with public bucket)
  content_type = "text/plain"
}

# Cleanup the local test file after apply
resource "null_resource" "cleanup_local_file" {
  depends_on = [aws_s3_object.public_test_object] # Ensure object is uploaded first
  provisioner "local-exec" {
    command = "rm -f ${local_file.test_object.filename}"
    when    = destroy # Only run on destroy to ensure file exists for upload during apply
  }
}