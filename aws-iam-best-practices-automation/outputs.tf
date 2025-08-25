
# Output the ARN of the created IAM Role
# This is useful for referencing the role in other services or for auditing.
output "iam_role_arn" {
  description = "The ARN of the IAM Role created for the application service."
  value       = aws_iam_role.application_role.arn
}

# Output the name of the created IAM Role
output "iam_role_name_output" {
  description = "The name of the IAM Role created."
  value       = aws_iam_role.application_role.name
}

# Output the ARN of the custom S3 read-only policy
output "s3_read_only_policy_arn" {
  description = "The ARN of the custom S3 read-only IAM policy."
  value       = aws_iam_policy.s3_read_only_custom_policy.arn
}