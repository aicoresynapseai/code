# Main Terraform configuration for provisioning AWS resources

# Configure the AWS provider
provider "aws" {
  region = var.aws_region # Use the AWS region defined in variables
}

# 1. Create a Virtual Private Cloud (VPC)
resource "aws_vpc" "main" {
  cidr_block           = var.vpc_cidr_block      # VPC CIDR block from variables
  enable_dns_hostnames = true                    # Enable DNS hostnames for instances
  enable_dns_support   = true                    # Enable DNS resolution for instances

  tags = {
    Name        = "${var.project_name}-vpc"
    Environment = "dev"
  }
}

# 2. Create Internet Gateway (for public subnet internet access)
resource "aws_internet_gateway" "main" {
  vpc_id = aws_vpc.main.id # Attach to the created VPC

  tags = {
    Name = "${var.project_name}-igw"
  }
}

# 3. Create Public Subnets
resource "aws_subnet" "public" {
  count             = length(var.public_subnet_cidrs) # Create multiple public subnets based on list
  vpc_id            = aws_vpc.main.id                 # Associate with the main VPC
  cidr_block        = var.public_subnet_cidrs[count.index] # Assign CIDR from list
  availability_zone = "${var.aws_region}${element(["a", "b", "c", "d"], count.index)}" # Distribute across AZs
  map_public_ip_on_launch = true # Automatically assign public IP to instances in these subnets

  tags = {
    Name = "${var.project_name}-public-subnet-${count.index + 1}"
  }
}

# 4. Create Private Subnets
resource "aws_subnet" "private" {
  count             = length(var.private_subnet_cidrs) # Create multiple private subnets
  vpc_id            = aws_vpc.main.id                   # Associate with the main VPC
  cidr_block        = var.private_subnet_cidrs[count.index] # Assign CIDR from list
  availability_zone = "${var.aws_region}${element(["a", "b", "c", "d"], count.index)}" # Distribute across AZs

  tags = {
    Name = "${var.project_name}-private-subnet-${count.index + 1}"
  }
}

# 5. Create Elastic IP for NAT Gateway (required for NAT Gateway)
resource "aws_eip" "nat" {
  vpc = true # Associate with a VPC
  tags = {
    Name = "${var.project_name}-nat-eip"
  }
}

# 6. Create NAT Gateway (for private subnet outbound internet access)
# Placed in a public subnet to allow internet connectivity for private instances
resource "aws_nat_gateway" "main" {
  allocation_id = aws_eip.nat.id             # Associate with the Elastic IP
  subnet_id     = aws_subnet.public[0].id    # Place in the first public subnet

  tags = {
    Name = "${var.project_name}-nat-gw"
  }
  # Ensures NAT Gateway is created after its EIP is associated
  depends_on = [aws_internet_gateway.main]
}

# 7. Create Route Tables for public subnets
resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id # Associate with the main VPC

  route {
    cidr_block = "0.0.0.0/0"                 # Default route to the internet
    gateway_id = aws_internet_gateway.main.id # Direct traffic to Internet Gateway
  }

  tags = {
    Name = "${var.project_name}-public-rt"
  }
}

# 8. Associate Public Route Table with Public Subnets
resource "aws_route_table_association" "public" {
  count          = length(aws_subnet.public) # Associate with all public subnets
  subnet_id      = aws_subnet.public[count.index].id
  route_table_id = aws_route_table.public.id
}

# 9. Create Route Table for private subnets
resource "aws_route_table" "private" {
  vpc_id = aws_vpc.main.id # Associate with the main VPC

  route {
    cidr_block = "0.0.0.0/0"            # Default route for private subnets
    nat_gateway_id = aws_nat_gateway.main.id # Direct traffic to NAT Gateway
  }

  tags = {
    Name = "${var.project_name}-private-rt"
  }
}

# 10. Associate Private Route Table with Private Subnets
resource "aws_route_table_association" "private" {
  count          = length(aws_subnet.private) # Associate with all private subnets
  subnet_id      = aws_subnet.private[count.index].id
  route_table_id = aws_route_table.private.id
}

# 11. Create Security Group for EC2 instances (allowing SSH and HTTP)
resource "aws_security_group" "ec2_sg" {
  name        = "${var.project_name}-ec2-sg"
  description = "Allow SSH and HTTP inbound traffic"
  vpc_id      = aws_vpc.main.id

  ingress {
    description = "SSH from anywhere"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # WARNING: For production, restrict to known IPs
  }

  ingress {
    description = "HTTP from anywhere"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # WARNING: For production, restrict to known IPs
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1" # All protocols
    cidr_blocks = ["0.0.0.0/0"] # Allow all outbound traffic
  }

  tags = {
    Name = "${var.project_name}-ec2-sg"
  }
}

# 12. Create EC2 Instance (Web Server)
resource "aws_instance" "web_server" {
  ami           = var.ami_id                 # AMI ID from variables
  instance_type = var.ec2_instance_type      # Instance type from variables
  subnet_id     = aws_subnet.public[0].id    # Deploy in the first public subnet
  security_groups = [aws_security_group.ec2_sg.id] # Attach the EC2 Security Group
  key_name      = var.key_pair_name          # SSH key pair for access

  # User data to install Apache web server and create a simple HTML page
  user_data = <<-EOF
              #!/bin/bash
              sudo yum update -y
              sudo yum install -y httpd
              sudo systemctl start httpd
              sudo systemctl enable httpd
              echo "<h1>Hello from AWS EC2 by Terraform!</h1>" | sudo tee /var/www/html/index.html
              EOF

  tags = {
    Name = "${var.project_name}-web-server"
  }
}

# 13. Create Security Group for RDS (allowing traffic from EC2 SG)
resource "aws_security_group" "rds_sg" {
  name        = "${var.project_name}-rds-sg"
  description = "Allow inbound traffic from EC2 instances"
  vpc_id      = aws_vpc.main.id

  # Ingress rule to allow traffic from the EC2 Security Group
  ingress {
    description     = "Allow database traffic from EC2 instances"
    from_port       = (var.db_engine == "postgres" ? 5432 : (var.db_engine == "mysql" ? 3306 : 0)) # PostgreSQL default port 5432, MySQL 3306
    to_port         = (var.db_engine == "postgres" ? 5432 : (var.db_engine == "mysql" ? 3306 : 0))
    protocol        = "tcp"
    security_groups = [aws_security_group.ec2_sg.id] # Allow from EC2 security group
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${var.project_name}-rds-sg"
  }
}

# 14. Create RDS DB Subnet Group (required for RDS in a VPC)
resource "aws_db_subnet_group" "main" {
  name       = "${var.project_name}-db-subnet-group"
  subnet_ids = [for s in aws_subnet.private : s.id] # Use all private subnets

  tags = {
    Name = "${var.project_name}-db-subnet-group"
  }
}

# 15. Create RDS Database Instance
resource "aws_db_instance" "main" {
  allocated_storage    = 20                          # Minimum storage in GB
  engine               = var.db_engine               # Database engine from variables
  engine_version       = var.db_engine_version       # Engine version from variables
  instance_class       = var.db_instance_type        # Instance type from variables
  name                 = var.db_name                 # Database name from variables
  username             = var.db_username             # Master username from variables
  password             = var.db_password             # Master password from variables
  vpc_security_group_ids = [aws_security_group.rds_sg.id] # Attach RDS Security Group
  db_subnet_group_name = aws_db_subnet_group.main.name # Associate with DB Subnet Group
  skip_final_snapshot  = true                        # Skip snapshot on deletion (for demo)
  publicly_accessible  = false                       # Database should not be publicly accessible
  multi_az             = true                        # Deploy in multiple AZs for high availability
  storage_type         = "gp2"                       # General Purpose SSD storage
  identifier           = "${var.project_name}-db"    # Unique identifier for the DB instance

  tags = {
    Name = "${var.project_name}-rds-db"
  }
}