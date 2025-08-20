output "secure_s3_bucket_arn" {
  description = "The ARN of the secure S3 bucket."
  value       = aws_s3_bucket.secure_customer_bucket.arn
}

output "s3_user_name" {
  description = "The name of the IAM user created for S3 access."
  value       = aws_iam_user.s3_user.name
}

output "s3_least_privilege_policy_arn" {
  description = "The ARN of the S3 least privilege IAM policy."
  value       = aws_iam_policy.s3_least_privilege_policy.arn
}

output "ec2_management_role_arn" {
  description = "The ARN of the IAM role for EC2 management."
  value       = aws_iam_role.ec2_management_role.arn
}

output "ec2_management_policy_arn" {
  description = "The ARN of the IAM policy for EC2 management."
  value       = aws_iam_policy.ec2_management_policy.arn
}

output "web_security_group_id" {
  description = "The ID of the created web security group."
  value       = aws_security_group.web_sg.id
}