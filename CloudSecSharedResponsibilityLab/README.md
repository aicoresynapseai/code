Welcome to the Cloud Security Shared Responsibility Lab!

This project aims to demystify the shared responsibility model in cloud security, specifically focusing on Amazon Web Services (AWS). Understanding this model is crucial for anyone operating in the cloud, as it clearly defines what the cloud provider secures versus what the customer is responsible for. Misunderstandings can lead to significant security vulnerabilities.

The Shared Responsibility Model in a Nutshell:
This model dictates that security is a shared endeavor between the cloud provider (like AWS, Azure, GCP) and the customer.

Cloud Provider (AWS) Responsibility - "Security OF the Cloud":
AWS is responsible for protecting the infrastructure that runs all of the services offered in the AWS Cloud. This infrastructure includes the hardware, software, networking, and facilities that run AWS Cloud services.
Examples of AWS's responsibilities:
* Physical security of data centers.
* Global infrastructure (Regions, Availability Zones, Edge Locations).
* Core network infrastructure.
* Hypervisor and underlying compute, storage, and database services.
* Managed services like RDS, S3 (maintaining the service itself, not your data in it).

Customer Responsibility - "Security IN the Cloud":
The customer's responsibility depends on the cloud services used. For IaaS (Infrastructure as a Service) like EC2, the customer has more responsibility. For PaaS (Platform as a Service) or SaaS (Software as a Service), the customer's responsibility is reduced. In general, customers are responsible for what they put IN the cloud.
Examples of Customer's responsibilities:
* Data: Your data, its encryption (at rest and in transit), and integrity.
* Identity and Access Management (IAM): Who can access your resources and what they can do (e.g., users, roles, permissions).
* Network and Firewall Configuration: Security groups, Network ACLs, VPNs, routing.
* Operating Systems: Patching, configuration, and security of operating systems on EC2 instances.
* Applications: Security of applications you deploy.
* Server-side encryption and client-side encryption.

Practical Examples in this Lab:
This lab provides concrete examples to illustrate these concepts:

1.  IAM Policies:
    *   iam_policies/customer_least_privilege_s3_access.json: This policy demonstrates a good practice, granting a specific user/role only the necessary permissions (read/write) to a particular S3 bucket. This falls squarely under the customer's "Security IN the Cloud" for Identity and Access Management.
    *   iam_policies/misconfigured_overly_permissive_s3_access.json: This policy shows a common misconfiguration: granting public read access to an S3 bucket. This is a severe customer security lapse, as sensitive data could be exposed. Again, it's a customer's responsibility for data and its access.
    *   iam_policies/customer_iam_ec2_management.json: This policy grants a user/role permissions to manage EC2 instances, including security groups. Managing security groups is a customer's responsibility for "Network and Firewall Configuration".

2.  Terraform Infrastructure as Code:
    *   terraform/main.tf: This Terraform configuration demonstrates how a customer might provision an S3 bucket, attach a least-privilege policy, and create an IAM role with EC2 management permissions. It highlights how customers implement their "Security IN the Cloud" responsibilities through infrastructure code.
    *   terraform/variables.tf: Defines input variables for the Terraform configuration.
    *   terraform/outputs.tf: Defines outputs after Terraform applies the configuration.

3.  Misconfiguration Risks:
    *   misconfiguration_risks.md: This document elaborates on common customer-side misconfiguration risks beyond just overly permissive S3 buckets, emphasizing the importance of diligence in "Security IN the Cloud".

4.  Python Audit Script:
    *   scripts/audit_s3_public_access.py: A Python script to demonstrate how customers can audit their S3 buckets for public access, which is a key part of their "Security IN the Cloud" responsibilities for data protection.

How to Use This Lab:
1.  Review the `README.md` for the conceptual understanding.
2.  Examine the IAM policy files in the `iam_policies/` directory to see practical examples of good and bad permissions.
3.  Explore the Terraform files in `terraform/` to understand how infrastructure and policies are provisioned. You could optionally deploy these (with an AWS account and Terraform installed) to see them in action.
    *   cd terraform
    *   terraform init
    *   terraform plan
    *   terraform apply
4.  Read `misconfiguration_risks.md` to deepen your understanding of common pitfalls.
5.  Inspect `scripts/audit_s3_public_access.py` to see how you can programmatically check for misconfigurations. You could run this script (after configuring AWS credentials) to audit your own S3 buckets.
    *   pip install boto3
    *   python scripts/audit_s3_public_access.py

By walking through these examples, you'll gain a clearer understanding of the shared responsibility model and how your actions as a customer directly impact your cloud security posture.