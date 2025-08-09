# Define output values that are useful after deployment

output "vpc_id" {
  description = "The ID of the created VPC."
  value       = aws_vpc.main.id
}

output "public_subnet_ids" {
  description = "IDs of the public subnets."
  value       = [for s in aws_subnet.public : s.id]
}

output "private_subnet_ids" {
  description = "IDs of the private subnets."
  value       = [for s in aws_subnet.private : s.id]
}

output "ec2_public_ip" {
  description = "The public IP address of the EC2 web server."
  value       = aws_instance.web_server.public_ip
}

output "ec2_public_dns" {
  description = "The public DNS name of the EC2 web server."
  value       = aws_instance.web_server.public_dns
}

output "rds_endpoint" {
  description = "The endpoint address of the RDS database."
  value       = aws_db_instance.main.address
}

output "rds_port" {
  description = "The port of the RDS database."
  value       = aws_db_instance.main.port
}

output "rds_username" {
  description = "The master username for the RDS database."
  value       = aws_db_instance.main.username
}

# IMPORTANT: Do not output sensitive data like passwords in real production environments.
# This is for demonstration purposes only.
output "rds_password_DO_NOT_USE_IN_PROD" {
  description = "The master password for the RDS database (for demo ONLY, avoid in prod)."
  value       = aws_db_instance.main.password
  sensitive   = true
}