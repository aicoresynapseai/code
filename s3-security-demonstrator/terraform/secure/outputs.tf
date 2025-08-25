# Outputs for the secure S3 bucket demonstration.

output "secure_bucket_name" {
  description = "The name of the secured S3 bucket."
  value       = aws_s3_bucket.secure_bucket.id
}

output "secure_bucket_arn" {
  description = "The ARN of the secured S3 bucket."
  value       = aws_s3_bucket.secure_bucket.arn
}

output "log_bucket_name" {
  description = "The name of the S3 bucket used for access logs."
  value       = aws_s3_bucket.log_bucket.id
}

output "private_object_url_example" {
  description = "Example URL for a private object (requires authentication to access)."
  value       = "https://${aws_s3_bucket.secure_bucket.id}.s3.${var.aws_region}.amazonaws.com/secure-test-object.txt"
}