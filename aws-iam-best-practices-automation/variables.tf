
# Define the AWS region where resources will be created
variable "aws_region" {
  description = "The AWS region to deploy resources."
  type        = string
  default     = "us-east-1" # Default region, can be overridden
}

# Define the name for the IAM Role
variable "iam_role_name" {
  description = "Name for the IAM role used by applications/services."
  type        = string
  default     = "ApplicationServiceRole" # Default role name
}