# Define input variables for customizability and reusability

variable "aws_region" {
  description = "The AWS region to deploy resources into."
  type        = string
  default     = "us-east-1" # Default to N. Virginia if not specified
}

variable "project_name" {
  description = "A name prefix for all resources to help with identification."
  type        = string
  default     = "scalable-iac"
}

variable "vpc_cidr_block" {
  description = "The CIDR block for the VPC."
  type        = string
  default     = "10.0.0.0/16" # A common private CIDR range
}

variable "public_subnet_cidrs" {
  description = "A list of CIDR blocks for public subnets (e.g., for EC2 instances accessible from the internet)."
  type        = list(string)
  default     = ["10.0.1.0/24", "10.0.2.0/24"] # Two public subnets for high availability
}

variable "private_subnet_cidrs" {
  description = "A list of CIDR blocks for private subnets (e.g., for RDS databases or internal application servers)."
  type        = list(string)
  default     = ["10.0.101.0/24", "10.0.102.0/24"] # Two private subnets for high availability
}

variable "ec2_instance_type" {
  description = "The instance type for the EC2 web server."
  type        = string
  default     = "t2.micro" # A cost-effective instance type for testing
}

variable "ami_id" {
  description = "The AMI ID for the EC2 instance. (e.g., Amazon Linux 2 AMI HVM, SSD Volume Type)"
  type        = string
  default     = "ami-053b0d53c279acc90" # Example: Amazon Linux 2 in us-east-1. Update for your region/preference.
}

variable "key_pair_name" {
  description = "The name of an existing EC2 Key Pair to allow SSH access to the EC2 instance."
  type        = string
  # IMPORTANT: Replace with your actual key pair name. You must create this manually in AWS beforehand.
  default = "my-ec2-keypair" 
}

variable "db_instance_type" {
  description = "The instance type for the RDS database."
  type        = string
  default     = "db.t3.micro" # A cost-effective instance type for testing RDS
}

variable "db_engine" {
  description = "The database engine to use (e.g., postgres, mysql)."
  type        = string
  default     = "postgres"
}

variable "db_engine_version" {
  description = "The version of the database engine."
  type        = string
  default     = "14.6" # Example PostgreSQL version
}

variable "db_username" {
  description = "Master username for the RDS database."
  type        = string
  default     = "admin" # DO NOT use default in production, use secrets management
}

variable "db_password" {
  description = "Master password for the RDS database."
  type        = string
  default     = "MySecurePassword123" # DO NOT use default in production, use secrets management
  sensitive   = true # Mark as sensitive to prevent showing in logs/outputs
}

variable "db_name" {
  description = "The name of the database to create."
  type        = string
  default     = "webappdb"
}