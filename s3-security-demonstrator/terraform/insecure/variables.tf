# Variables for the insecure S3 bucket demonstration.

variable "aws_region" {
  description = "The AWS region to deploy resources into."
  type        = string
  default     = "us-east-1"
}