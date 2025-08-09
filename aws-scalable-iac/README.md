Infrastructure as Code: Scalable AWS Architecture with Terraform

This project demonstrates how to provision a foundational and scalable AWS architecture using Terraform. It includes a Virtual Private Cloud (VPC), public and private subnets, an EC2 instance, and an RDS database, all configured with appropriate networking and security.

The goal is to provide a clear, working example of Infrastructure as Code (IaC) principles for deploying a common web application stack on AWS.

Project Components:

*   AWS VPC: A logically isolated section of the AWS Cloud.
*   Public Subnets: For resources that need direct internet access (e.g., EC2 web server).
*   Private Subnets: For resources that do not need direct internet access but may need outbound access (e.g., RDS database, application servers).
*   Internet Gateway: Enables communication between the VPC and the internet.
*   NAT Gateway: Allows instances in private subnets to connect to the internet while remaining private.
*   EC2 Instance: A virtual server deployed in a public subnet, configured to host a simple web server (Apache).
*   RDS Database: A managed relational database service deployed in private subnets, ensuring secure backend operations.
*   Security Groups: Fine-grained control over inbound and outbound traffic for EC2 and RDS.

Prerequisites:

Before you begin, ensure you have the following installed and configured:

1.  AWS CLI: Configured with credentials that have sufficient permissions to create AWS resources.
2.  Terraform: Version 1.0 or newer.

Deployment Steps:

To deploy this AWS architecture, follow these steps:

1.  Navigate to the project root directory in your terminal:
    cd aws-scalable-iac

2.  Initialize Terraform: This command downloads the necessary AWS provider plugin.
    terraform init

3.  Review the execution plan: This command shows you what Terraform will create, modify, or destroy.
    terraform plan

4.  Apply the configuration: This command provisions the resources in your AWS account.
    terraform apply --auto-approve

    Note: The --auto-approve flag is used for automation purposes (like in CI/CD). For manual deployment, you can omit it and type 'yes' when prompted.

Accessing Resources:

After successful deployment, Terraform will output key information:

*   ec2_public_ip: The public IP address of the EC2 instance. You can SSH into this instance using the specified key pair.
*   rds_endpoint: The endpoint for the RDS database. You can connect to this using the database credentials defined in variables.

To Destroy Resources:

To remove all provisioned resources from your AWS account:

terraform destroy --auto-approve

CI/CD Integration:

The 'ci-cd/deploy.sh' script provides an example of how these Terraform commands can be integrated into a Continuous Integration/Continuous Deployment (CI/CD) pipeline. In a real-world scenario, your CI/CD system would execute these commands (init, plan, apply) upon code commits, often after running tests. Secret management for database credentials and SSH keys would be handled via secure CI/CD variables or secrets managers.