# Define required Terraform and provider versions
terraform {
  required_version = ">= 1.0.0" # Specify the minimum Terraform version required

  required_providers {
    aws = {
      source  = "hashicorp/aws" # Use the official AWS provider from HashiCorp
      version = "~> 5.0"        # Specify a compatible version range for the AWS provider
    }
  }
}