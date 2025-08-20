variable "aws_region" {
  description = "The AWS region to deploy resources into."
  type        = string
  default     = "us-east-1"
}

variable "secure_s3_bucket_name" {
  description = "Name for the secure S3 bucket."
  type        = string
  default     = "my-secure-customer-bucket-12345-lab" # Ensure this is globally unique
}

variable "misconfigured_s3_bucket_name" {
  description = "Name for the intentionally misconfigured S3 bucket (for demonstration of risk)."
  type        = string
  default     = "my-misconfigured-public-bucket-lab-67890" # Ensure this is globally unique
}

variable "default_vpc_id" {
  description = "ID of the default VPC to create security groups in."
  type        = string
  default     = "vpc-xxxxxxxxxxxxxxxxx" # Replace with your default VPC ID or fetch dynamically
}