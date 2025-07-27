import argparse
import json
import yaml
import os

def generate_terraform(config: dict) -> str:
    """
    Generates a simplified Terraform configuration for an AWS Java microservice deployment.
    This function simulates the GenAI's capability to translate high-level intent
    into concrete IaC, including a Spring Boot app, PostgreSQL DB, ALB, and API Gateway.
    """
    app_name = config.get('application_name', 'java-microservice')
    environment = config.get('environment', 'dev')
    region = config.get('region', 'us-east-1')
    components = config.get('components', {})

    tf_code = f"""
provider "aws" {{
  region = "{region}"
}}

# Basic VPC for network isolation
resource "aws_vpc" "main" {{
  cidr_block = "10.0.0.0/16"
  enable_dns_hostnames = true
  tags = {{
    Name = "{app_name}-{environment}-vpc"
    Environment = "{environment}"
  }}
}}

# Public Subnets for Load Balancer and potentially EC2 instances (for simplicity)
resource "aws_subnet" "public_a" {{
  vpc_id     = aws_vpc.main.id
  cidr_block = "10.0.1.0/24"
  availability_zone = "{region}a"
  map_public_ip_on_launch = true
  tags = {{
    Name = "{app_name}-{environment}-public-subnet-a"
  }}
}}

resource "aws_subnet" "public_b" {{
  vpc_id     = aws_vpc.main.id
  cidr_block = "10.0.2.0/24"
  availability_zone = "{region}b"
  map_public_ip_on_launch = true
  tags = {{
    Name = "{app_name}-{environment}-public-subnet-b"
  }}
}}

# Internet Gateway to allow communication to/from the internet
resource "aws_internet_gateway" "main" {{
  vpc_id = aws_vpc.main.id
  tags = {{
    Name = "{app_name}-{environment}-igw"
  }}
}}

# Route Table for public subnets
resource "aws_route_table" "public" {{
  vpc_id = aws_vpc.main.id
  route {{
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.main.id
  }}
  tags = {{
    Name = "{app_name}-{environment}-public-rt"
  }}
}}

# Associate public subnets with route table
resource "aws_route_table_association" "public_a" {{
  subnet_id      = aws_subnet.public_a.id
  route_table_id = aws_route_table.public.id
}}

resource "aws_route_table_association" "public_b" {{
  subnet_id      = aws_subnet.public_b.id
  route_table_id = aws_route_table.public.id
}}

"""

    # --- Spring Boot Application (EC2 with Auto Scaling Group) ---
    if 'spring_boot_app' in components:
        app_config = components['spring_boot_app']
        instance_type = app_config.get('instance_type', 't3.micro')
        min_instances = app_config.get('min_instances', 1)
        max_instances = app_config.get('max_instances', 2)
        app_port = app_config.get('port', 8080)
        docker_image = app_config.get('docker_image', 'nginx') # Placeholder, in real-world this would be custom AMI or ECS/EKS
        
        tf_code += f"""
# Security Group for Spring Boot application (allow HTTP/S and app port)
resource "aws_security_group" "app_sg" {{
  name        = "{app_name}-{environment}-app-sg"
  description = "Allow HTTP/S and app port for Spring Boot"
  vpc_id      = aws_vpc.main.id

  ingress {{
    description = "Allow HTTP from anywhere"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }}

  ingress {{
    description = "Allow HTTPS from anywhere"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }}

  ingress {{
    description = "Allow Spring Boot app port from Load Balancer"
    from_port   = {app_port}
    to_port     = {app_port}
    protocol    = "tcp"
    security_groups = [aws_security_group.lb_sg.id] # Reference LB SG
  }}

  egress {{
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }}
  tags = {{
    Name = "{app_name}-{environment}-app-sg"
  }}
}}

# EC2 Launch Template for Spring Boot
resource "aws_launch_template" "app_lt" {{
  name_prefix   = "{app_name}-{environment}-app-lt"
  image_id      = "ami-053b0d53c27927f8a" # Example: Amazon Linux 2 AMI (HVM) - SSD Volume Type
  instance_type = "{instance_type}"
  key_name      = "your-key-pair" # IMPORTANT: Replace with your actual Key Pair Name
  vpc_security_group_ids = [aws_security_group.app_sg.id]

  user_data = base64encode(<<EOF
#!/bin/bash
echo "Installing Docker..."
sudo yum update -y
sudo yum install docker -y
sudo service docker start
sudo usermod -a -G docker ec2-user
echo "Pulling and running Spring Boot Docker image..."
sudo docker run -d -p {app_port}:{app_port} {docker_image} # Placeholder for actual Java app
EOF
  )
  tags = {{
    Name = "{app_name}-{environment}-app-launch-template"
  }}
}}

# Auto Scaling Group for Spring Boot
resource "aws_autoscaling_group" "app_asg" {{
  name                      = "{app_name}-{environment}-app-asg"
  vpc_zone_identifier       = [aws_subnet.public_a.id, aws_subnet.public_b.id]
  min_size                  = {min_instances}
  max_size                  = {max_instances}
  health_check_type         = "ELB"
  target_group_arns         = [aws_lb_target_group.app_tg.arn] # Reference Target Group

  launch_template {{
    id      = aws_launch_template.app_lt.id
    version = "$Latest"
  }}
  tags_as_resources {{
    propagate_at_launch = true
    tags = {{
      Name = "{app_name}-{environment}-app-instance"
      Environment = "{environment}"
      Service = "{app_name}"
    }}
  }}
}}
"""
    # --- Load Balancer ---
    if 'load_balancer' in components and components['load_balancer'].get('enabled', False):
        lb_type = components['load_balancer'].get('type', 'application')
        lb_port = components['load_balancer'].get('port', 80)
        
        tf_code += f"""
# Security Group for Application Load Balancer
resource "aws_security_group" "lb_sg" {{
  name        = "{app_name}-{environment}-lb-sg"
  description = "Allow HTTP/S traffic to Load Balancer"
  vpc_id      = aws_vpc.main.id

  ingress {{
    description = "Allow HTTP from anywhere"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }}

  ingress {{
    description = "Allow HTTPS from anywhere"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }}

  egress {{
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }}
  tags = {{
    Name = "{app_name}-{environment}-lb-sg"
  }}
}}

# Application Load Balancer
resource "aws_lb" "app_lb" {{
  name               = "{app_name}-{environment}-lb"
  internal           = false
  load_balancer_type = "{lb_type}"
  security_groups    = [aws_security_group.lb_sg.id]
  subnets            = [aws_subnet.public_a.id, aws_subnet.public_b.id]
  enable_deletion_protection = false # Set to true for production

  tags = {{
    Name = "{app_name}-{environment}-lb"
  }}
}}

# Target Group for Spring Boot application
resource "aws_lb_target_group" "app_tg" {{
  name     = "{app_name}-{environment}-tg"
  port     = {app_port} # Spring Boot application port
  protocol = "HTTP"
  vpc_id   = aws_vpc.main.id
  health_check {{
    path                = "/actuator/health" # Common Spring Boot health endpoint
    protocol            = "HTTP"
    port                = "{app_port}"
    healthy_threshold   = 2
    unhealthy_threshold = 2
    timeout             = 5
    interval            = 30
    matcher             = "200"
  }}
  tags = {{
    Name = "{app_name}-{environment}-tg"
  }}
}}

# Listener for the Load Balancer
resource "aws_lb_listener" "http_listener" {{
  load_balancer_arn = aws_lb.app_lb.arn
  port              = {lb_port}
  protocol          = "HTTP"

  default_action {{
    type             = "forward"
    target_group_arn = aws_lb_target_group.app_tg.arn
  }}
  tags = {{
    Name = "{app_name}-{environment}-http-listener"
  }}
}}
"""

    # --- PostgreSQL Database (RDS) ---
    if 'postgresql_database' in components:
        db_config = components['postgresql_database']
        db_name = db_config.get('db_name', 'mydb')
        db_user = db_config.get('username', 'admin')
        allocated_storage = db_config.get('allocated_storage_gb', 20)
        instance_class = db_config.get('instance_class', 'db.t3.micro')

        tf_code += f"""
# Security Group for RDS PostgreSQL (allow traffic from app instances)
resource "aws_security_group" "db_sg" {{
  name        = "{app_name}-{environment}-db-sg"
  description = "Allow access to RDS PostgreSQL from app security group"
  vpc_id      = aws_vpc.main.id

  ingress {{
    description     = "Allow PostgreSQL from application instances"
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [aws_security_group.app_sg.id]
  }}

  egress {{
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }}
  tags = {{
    Name = "{app_name}-{environment}-db-sg"
  }}
}}

# RDS PostgreSQL Instance
resource "aws_db_instance" "postgresql_db" {{
  allocated_storage    = {allocated_storage}
  engine               = "postgres"
  engine_version       = "14.5"
  instance_class       = "{instance_class}"
  name                 = "{db_name}"
  username             = "{db_user}"
  password             = "MySuperSecretPassword123" # IMPORTANT: Use AWS Secrets Manager in production!
  vpc_security_group_ids = [aws_security_group.db_sg.id]
  db_subnet_group_name = aws_db_subnet_group.default.name
  skip_final_snapshot  = true # Set to false for production
  publicly_accessible  = false # Best practice: keep DB private

  tags = {{
    Name = "{app_name}-{environment}-postgresql-db"
  }}
}}

# DB Subnet Group (using default VPC subnets for simplicity, create new ones for production)
resource "aws_db_subnet_group" "default" {{
  name       = "{app_name}-{environment}-db-subnet-group"
  subnet_ids = [aws_subnet.public_a.id, aws_subnet.public_b.id] # Use private subnets in production!
  tags = {{
    Name = "{app_name}-{environment}-db-subnet-group"
  }}
}}
"""

    # --- API Gateway ---
    if 'api_gateway' in components and components['api_gateway'].get('enabled', False):
        api_path = components['api_gateway'].get('path', '/api')
        # This is a very simplified API Gateway setup, merely proxying to the ALB
        tf_code += f"""
# API Gateway for exposing the microservice
resource "aws_api_gateway_rest_api" "main_api" {{
  name        = "{app_name}-{environment}-api"
  description = "API Gateway for {app_name} microservice"

  tags = {{
    Name = "{app_name}-{environment}-api"
  }}
}}

resource "aws_api_gateway_resource" "proxy_resource" {{
  rest_api_id = aws_api_gateway_rest_api.main_api.id
  parent_id   = aws_api_gateway_rest_api.main_api.root_resource_id
  path_part   = "{api_path.strip('/')}" # Remove leading/trailing slashes
}}

resource "aws_api_gateway_method" "proxy_method_any" {{
  rest_api_id   = aws_api_gateway_rest_api.main_api.id
  resource_id   = aws_api_gateway_resource.proxy_resource.id
  http_method   = "ANY"
  authorization = "NONE"
}}

resource "aws_api_gateway_integration" "proxy_integration_any" {{
  rest_api_id             = aws_api_gateway_rest_api.main_api.id
  resource_id             = aws_api_gateway_resource.proxy_resource.id
  http_method             = aws_api_gateway_method.proxy_method_any.http_method
  type                    = "HTTP_PROXY"
  integration_http_method = "ANY"
  uri                     = "http://${{aws_lb.app_lb.dns_name}}/{api_path.strip('/')}}" # Proxy to ALB
}}

resource "aws_api_gateway_deployment" "main_deployment" {{
  rest_api_id = aws_api_gateway_rest_api.main_api.id
  # Add a dummy_id to force redeploy on changes to methods/resources
  triggers = {{
    redeployment = sha1(jsonencode(aws_api_gateway_rest_api.main_api.body))
  }}
  lifecycle {{
    create_before_destroy = true
  }}
}}

resource "aws_api_gateway_stage" "main_stage" {{
  deployment_id = aws_api_gateway_deployment.main_deployment.id
  rest_api_id   = aws_api_gateway_rest_api.main_api.id
  stage_name    = "{environment}"
  tags = {{
    Name = "{app_name}-{environment}-api-stage"
  }}
}}

output "api_gateway_invoke_url" {{
  description = "The invoke URL of the API Gateway"
  value       = aws_api_gateway_stage.main_stage.invoke_url
}}

output "alb_dns_name" {{
  description = "The DNS name of the Application Load Balancer"
  value       = aws_lb.app_lb.dns_name
}}
"""
    return tf_code.strip()


def generate_cloudformation(config: dict) -> str:
    """
    Generates a simplified AWS CloudFormation template for a Java microservice.
    This demonstrates generating another IaC format from the same high-level input.
    """
    app_name = config.get('application_name', 'java-microservice')
    environment = config.get('environment', 'dev')
    
    cf_template = {
        "AWSTemplateFormatVersion": "2010-09-09",
        "Description": f"CloudFormation template for {app_name} Java Microservice ({environment}) - Generated by GenAI",
        "Resources": {
            "VPC": {
                "Type": "AWS::EC2::VPC",
                "Properties": {
                    "CidrBlock": "10.0.0.0/16",
                    "EnableDnsHostnames": True,
                    "Tags": [
                        {"Key": "Name", "Value": f"{app_name}-{environment}-VPC"},
                        {"Key": "Environment", "Value": environment}
                    ]
                }
            },
            "PublicSubnetA": {
                "Type": "AWS::EC2::Subnet",
                "Properties": {
                    "VpcId": {"Ref": "VPC"},
                    "CidrBlock": "10.0.1.0/24",
                    "AvailabilityZone": {"Fn::Join": ["", [{"Ref": "AWS::Region"}, "a"]]},
                    "MapPublicIpOnLaunch": True,
                    "Tags": [{"Key": "Name", "Value": f"{app_name}-{environment}-PublicSubnetA"}]
                }
            },
            "PublicSubnetB": {
                "Type": "AWS::EC2::Subnet",
                "Properties": {
                    "VpcId": {"Ref": "VPC"},
                    "CidrBlock": "10.0.2.0/24",
                    "AvailabilityZone": {"Fn::Join": ["", [{"Ref": "AWS::Region"}, "b"]]},
                    "MapPublicIpOnLaunch": True,
                    "Tags": [{"Key": "Name", "Value": f"{app_name}-{environment}-PublicSubnetB"}]
                }
            },
            "InternetGateway": {
                "Type": "AWS::EC2::InternetGateway",
                "Properties": {
                    "Tags": [{"Key": "Name", "Value": f"{app_name}-{environment}-IGW"}]
                }
            },
            "VPCGatewayAttachment": {
                "Type": "AWS::EC2::VPCGatewayAttachment",
                "Properties": {
                    "VpcId": {"Ref": "VPC"},
                    "InternetGatewayId": {"Ref": "InternetGateway"}
                }
            },
            "PublicRouteTable": {
                "Type": "AWS::EC2::RouteTable",
                "Properties": {
                    "VpcId": {"Ref": "VPC"},
                    "Tags": [{"Key": "Name", "Value": f"{app_name}-{environment}-PublicRouteTable"}]
                }
            },
            "DefaultPublicRoute": {
                "Type": "AWS::EC2::Route",
                "DependsOn": "InternetGateway", # Ensure IGW is attached before creating route
                "Properties": {
                    "RouteTableId": {"Ref": "PublicRouteTable"},
                    "DestinationCidrBlock": "0.0.0.0/0",
                    "GatewayId": {"Ref": "InternetGateway"}
                }
            },
            "SubnetARouteTableAssociation": {
                "Type": "AWS::EC2::SubnetRouteTableAssociation",
                "Properties": {
                    "SubnetId": {"Ref": "PublicSubnetA"},
                    "RouteTableId": {"Ref": "PublicRouteTable"}
                }
            },
            "SubnetBRouteTableAssociation": {
                "Type": "AWS::EC2::SubnetRouteTableAssociation",
                "Properties": {
                    "SubnetId": {"Ref": "PublicSubnetB"},
                    "RouteTableId": {"Ref": "PublicRouteTable"}
                }
            }
        },
        "Outputs": {
            "VPCId": {
                "Description": "The ID of the newly created VPC",
                "Value": {"Ref": "VPC"}
            }
        }
    }

    components_cfg = config.get('components', {})

    # --- PostgreSQL Database (RDS) ---
    if 'postgresql_database' in components_cfg:
        db_config = components_cfg['postgresql_database']
        db_name = db_config.get('db_name', 'javadb')
        db_user = db_config.get('username', 'dbuser')
        allocated_storage = db_config.get('allocated_storage_gb', 20)
        instance_class = db_config.get('instance_class', 'db.t3.micro')

        cf_template["Resources"].update({
            "DBSecurityGroup": {
                "Type": "AWS::EC2::SecurityGroup",
                "Properties": {
                    "GroupDescription": "Enable PostgreSQL access from application",
                    "VpcId": {"Ref": "VPC"},
                    "SecurityGroupIngress": [
                        {
                            "IpProtocol": "tcp",
                            "FromPort": 5432,
                            "ToPort": 5432,
                            "SourceSecurityGroupId": {"Fn::GetAtt": ["AppSecurityGroup", "GroupId"]} # Will be added if app is enabled
                        }
                    ],
                    "Tags": [{"Key": "Name", "Value": f"{app_name}-{environment}-DBSG"}]
                }
            },
            "DBSubnetGroup": {
                "Type": "AWS::RDS::DBSubnetGroup",
                "Properties": {
                    "DBSubnetGroupDescription": f"Subnets for {app_name} RDS DB",
                    "SubnetIds": [
                        {"Ref": "PublicSubnetA"}, # Use private subnets in production
                        {"Ref": "PublicSubnetB"}  # Use private subnets in production
                    ],
                    "Tags": [{"Key": "Name", "Value": f"{app_name}-{environment}-DBSubnetGroup"}]
                }
            },
            "PostgreSQLDatabase": {
                "Type": "AWS::RDS::DBInstance",
                "Properties": {
                    "DBName": db_name,
                    "AllocatedStorage": str(allocated_storage),
                    "DBInstanceClass": instance_class,
                    "Engine": "postgres",
                    "EngineVersion": "14.5",
                    "MasterUsername": db_user,
                    "MasterUserPassword": "MySuperSecretPassword123CF", # IMPORTANT: Use Secrets Manager!
                    "VPCSecurityGroups": [{"Fn::GetAtt": ["DBSecurityGroup", "GroupId"]}],
                    "DBSubnetGroupName": {"Ref": "DBSubnetGroup"},
                    "PubliclyAccessible": False,
                    "MultiAZ": False, # Set to True for production
                    "DeletionProtection": False, # Set to True for production
                    "Tags": [{"Key": "Name", "Value": f"{app_name}-{environment}-PostgreSQLDB"}]
                }
            }
        })
        cf_template["Outputs"].update({
            "DatabaseEndpoint": {
                "Description": "Endpoint for the PostgreSQL Database",
                "Value": {"Fn::GetAtt": ["PostgreSQLDatabase", "Endpoint.Address"]}
            }
        })

    # --- Spring Boot Application (EC2 with Auto Scaling Group) ---
    if 'spring_boot_app' in components_cfg:
        app_config = components_cfg['spring_boot_app']
        instance_type = app_config.get('instance_type', 't3.micro')
        min_instances = app_config.get('min_instances', 1)
        max_instances = app_config.get('max_instances', 2)
        app_port = app_config.get('port', 8080)
        docker_image = app_config.get('docker_image', 'nginx') # Placeholder

        cf_template["Resources"].update({
            "AppSecurityGroup": {
                "Type": "AWS::EC2::SecurityGroup",
                "Properties": {
                    "GroupDescription": "Allow HTTP/S and app port to Spring Boot instances",
                    "VpcId": {"Ref": "VPC"},
                    "SecurityGroupIngress": [
                        {
                            "IpProtocol": "tcp", "FromPort": 80, "ToPort": 80, "CidrIp": "0.0.0.0/0"
                        },
                        {
                            "IpProtocol": "tcp", "FromPort": 443, "ToPort": 443, "CidrIp": "0.0.0.0/0"
                        },
                        {
                            "IpProtocol": "tcp", "FromPort": app_port, "ToPort": app_port,
                            "SourceSecurityGroupId": {"Fn::GetAtt": ["LoadBalancerSecurityGroup", "GroupId"]} # Assumes LB
                        }
                    ],
                    "SecurityGroupEgress": [
                        {"IpProtocol": "-1", "FromPort": -1, "ToPort": -1, "CidrIp": "0.0.0.0/0"}
                    ],
                    "Tags": [{"Key": "Name", "Value": f"{app_name}-{environment}-AppSG"}]
                }
            },
            "AppLaunchTemplate": {
                "Type": "AWS::EC2::LaunchTemplate",
                "Properties": {
                    "LaunchTemplateName": f"{app_name}-{environment}-AppLT",
                    "LaunchTemplateData": {
                        "ImageId": "ami-053b0d53c27927f8a", # Example Amazon Linux 2 AMI
                        "InstanceType": instance_type,
                        "KeyName": "your-key-pair", # IMPORTANT: Replace with actual Key Pair Name
                        "SecurityGroupIds": [{"Fn::GetAtt": ["AppSecurityGroup", "GroupId"]}],
                        "UserData": {"Fn::Base64": {"Fn::Join": ["", [
                            "#!/bin/bash\n",
                            "echo \"Installing Docker...\"\n",
                            "sudo yum update -y\n",
                            "sudo yum install docker -y\n",
                            "sudo service docker start\n",
                            "sudo usermod -a -G docker ec2-user\n",
                            "echo \"Pulling and running Spring Boot Docker image...\"\n",
                            f"sudo docker run -d -p {app_port}:{app_port} {docker_image}\n"
                        ]]}}
                    },
                    "Tags": [{"Key": "Name", "Value": f"{app_name}-{environment}-AppLaunchTemplate"}]
                }
            },
            "AppAutoScalingGroup": {
                "Type": "AWS::AutoScaling::AutoScalingGroup",
                "Properties": {
                    "AutoScalingGroupName": f"{app_name}-{environment}-ASG",
                    "VPCZoneIdentifier": [{"Ref": "PublicSubnetA"}, {"Ref": "PublicSubnetB"}],
                    "MinSize": str(min_instances),
                    "MaxSize": str(max_instances),
                    "LaunchTemplate": {
                        "LaunchTemplateId": {"Ref": "AppLaunchTemplate"},
                        "Version": "$Latest"
                    },
                    "TargetGroupARNs": [{"Ref": "AppTargetGroup"}], # Assumes LB
                    "HealthCheckType": "ELB",
                    "Tags": [
                        {"Key": "Name", "Value": f"{app_name}-{environment}-Instance", "PropagateAtLaunch": True},
                        {"Key": "Environment", "Value": environment, "PropagateAtLaunch": True},
                        {"Key": "Service", "Value": app_name, "PropagateAtLaunch": True}
                    ]
                }
            }
        })

    # --- Load Balancer ---
    if 'load_balancer' in components_cfg and components_cfg['load_balancer'].get('enabled', False):
        lb_port = components_cfg['load_balancer'].get('port', 80)
        
        cf_template["Resources"].update({
            "LoadBalancerSecurityGroup": {
                "Type": "AWS::EC2::SecurityGroup",
                "Properties": {
                    "GroupDescription": "Allow HTTP/S to the Load Balancer",
                    "VpcId": {"Ref": "VPC"},
                    "SecurityGroupIngress": [
                        {"IpProtocol": "tcp", "FromPort": 80, "ToPort": 80, "CidrIp": "0.0.0.0/0"},
                        {"IpProtocol": "tcp", "FromPort": 443, "ToPort": 443, "CidrIp": "0.0.0.0/0"}
                    ],
                    "SecurityGroupEgress": [
                        {"IpProtocol": "-1", "FromPort": -1, "ToPort": -1, "CidrIp": "0.0.0.0/0"}
                    ],
                    "Tags": [{"Key": "Name", "Value": f"{app_name}-{environment}-LBSG"}]
                }
            },
            "ApplicationLoadBalancer": {
                "Type": "AWS::ElasticLoadBalancingV2::LoadBalancer",
                "Properties": {
                    "Name": f"{app_name}-{environment}-ALB",
                    "Scheme": "internet-facing",
                    "SecurityGroups": [{"Fn::GetAtt": ["LoadBalancerSecurityGroup", "GroupId"]}],
                    "Subnets": [{"Ref": "PublicSubnetA"}, {"Ref": "PublicSubnetB"}],
                    "Tags": [{"Key": "Name", "Value": f"{app_name}-{environment}-ALB"}]
                }
            },
            "AppTargetGroup": {
                "Type": "AWS::ElasticLoadBalancingV2::TargetGroup",
                "Properties": {
                    "Name": f"{app_name}-{environment}-TG",
                    "Port": app_port,
                    "Protocol": "HTTP",
                    "VpcId": {"Ref": "VPC"},
                    "HealthCheckPath": "/actuator/health", # Common Spring Boot health endpoint
                    "HealthCheckProtocol": "HTTP",
                    "HealthCheckPort": str(app_port),
                    "HealthCheckIntervalSeconds": 30,
                    "HealthCheckTimeoutSeconds": 5,
                    "HealthyThresholdCount": 2,
                    "UnhealthyThresholdCount": 2,
                    "TargetType": "instance",
                    "Tags": [{"Key": "Name", "Value": f"{app_name}-{environment}-TG"}]
                }
            },
            "ALBListener": {
                "Type": "AWS::ElasticLoadBalancingV2::Listener",
                "Properties": {
                    "LoadBalancerArn": {"Ref": "ApplicationLoadBalancer"},
                    "Port": lb_port,
                    "Protocol": "HTTP",
                    "DefaultActions": [{
                        "Type": "forward",
                        "TargetGroupArn": {"Ref": "AppTargetGroup"}
                    }]
                }
            }
        })
        cf_template["Outputs"].update({
            "ALBDnsName": {
                "Description": "The DNS name of the Application Load Balancer",
                "Value": {"Fn::GetAtt": ["ApplicationLoadBalancer", "DNSName"]}
            }
        })

    # --- API Gateway ---
    if 'api_gateway' in components_cfg and components_cfg['api_gateway'].get('enabled', False):
        api_path = components_cfg['api_gateway'].get('path', '/api')
        # Simplified API Gateway pointing to the ALB (via a dummy endpoint or direct ALB DNS)
        cf_template["Resources"].update({
            "ApiGateway": {
                "Type": "AWS::ApiGateway::RestApi",
                "Properties": {
                    "Name": f"{app_name}-{environment}-API",
                    "Description": f"API Gateway for {app_name} microservice"
                }
            },
            "ApiGatewayResource": {
                "Type": "AWS::ApiGateway::Resource",
                "Properties": {
                    "ParentId": {"Fn::GetAtt": ["ApiGateway", "RootResourceId"]},
                    "PathPart": api_path.strip('/'),
                    "RestApiId": {"Ref": "ApiGateway"}
                }
            },
            "ApiGatewayMethod": {
                "Type": "AWS::ApiGateway::Method",
                "Properties": {
                    "HttpMethod": "ANY",
                    "ResourceId": {"Ref": "ApiGatewayResource"},
                    "RestApiId": {"Ref": "ApiGateway"},
                    "AuthorizationType": "NONE",
                    "Integration": {
                        "IntegrationHttpMethod": "ANY",
                        "Type": "HTTP_PROXY",
                        "Uri": {"Fn::Join": ["", [
                            "http://",
                            {"Fn::GetAtt": ["ApplicationLoadBalancer", "DNSName"]},
                            api_path # Path to ALB, will need to be configured on target
                        ]]}
                    }
                }
            },
            "ApiGatewayDeployment": {
                "Type": "AWS::ApiGateway::Deployment",
                "Properties": {
                    "RestApiId": {"Ref": "ApiGateway"},
                    "Description": "Initial deployment"
                }
            },
            "ApiGatewayStage": {
                "Type": "AWS::ApiGateway::Stage",
                "Properties": {
                    "DeploymentId": {"Ref": "ApiGatewayDeployment"},
                    "RestApiId": {"Ref": "ApiGateway"},
                    "StageName": environment
                }
            }
        })
        cf_template["Outputs"].update({
            "APIGatewayInvokeURL": {
                "Description": "The invoke URL for the API Gateway",
                "Value": {"Fn::Join": ["", [
                    "https://",
                    {"Ref": "ApiGateway"},
                    ".execute-api.",
                    {"Ref": "AWS::Region"},
                    ".amazonaws.com/",
                    {"Ref": "ApiGatewayStage"},
                    api_path
                ]]}
            }
        })

    return yaml.dump(cf_template, sort_keys=False, indent=2)


def main():
    parser = argparse.ArgumentParser(
        description="Generate Infrastructure as Code (Terraform/CloudFormation) "
                    "from a high-level Java microservice configuration using simulated GenAI."
    )
    parser.add_argument(
        '--input-file',
        type=str,
        required=True,
        help='Path to the input JSON or YAML configuration file.'
    )
    parser.add_argument(
        '--output-terraform',
        type=str,
        default='generated_main.tf',
        help='Path to the output Terraform (.tf) file.'
    )
    parser.add_argument(
        '--output-cloudformation',
        type=str,
        default='generated_cloudformation.yaml',
        help='Path to the output CloudFormation (.yaml) file.'
    )

    args = parser.parse_args()

    # Load input configuration
    try:
        with open(args.input_file, 'r') as f:
            if args.input_file.endswith('.json'):
                config = json.load(f)
            elif args.input_file.endswith('.yaml') or args.input_file.endswith('.yml'):
                config = yaml.safe_load(f)
            else:
                raise ValueError("Unsupported input file format. Must be .json or .yaml/.yml")
    except FileNotFoundError:
        print(f"Error: Input file not found at {args.input_file}")
        exit(1)
    except (json.JSONDecodeError, yaml.YAMLError) as e:
        print(f"Error parsing input file {args.input_file}: {e}")
        exit(1)

    print(f"Generating IaC for application: {config.get('application_name', 'Unnamed')}...")

    # Generate Terraform
    terraform_code = generate_terraform(config)
    with open(args.output_terraform, 'w') as f:
        f.write(terraform_code)
    print(f"Generated Terraform file: {args.output_terraform}")

    # Generate CloudFormation
    cloudformation_code = generate_cloudformation(config)
    with open(args.output_cloudformation, 'w') as f:
        f.write(cloudformation_code)
    print(f"Generated CloudFormation file: {args.output_cloudformation}")

    print("IaC generation complete.")


if __name__ == "__main__":
    main()