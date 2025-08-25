# Outputs for the insecure S3 bucket demonstration.

output "insecure_bucket_name" {
  description = "The name of the intentionally insecure S3 bucket."
  value       = aws_s3_bucket.insecure_bucket.id
}

output "insecure_bucket_arn" {
  description = "The ARN of the intentionally insecure S3 bucket."
  value       = aws_s3_bucket.insecure_bucket.arn
}

output "public_object_url" {
  description = "Example URL for a public object (assuming one is uploaded)."
  value       = "https://${aws_s3_bucket.insecure_bucket.id}.s3.${var.aws_region}.amazonaws.com/test-object.txt"
}