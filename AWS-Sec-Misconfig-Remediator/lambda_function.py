import boto3
import os
import json
import logging

# Configure logging
logger = logging.getLogger()
logger.setLevel(os.environ.get('LOG_LEVEL', 'INFO').upper())

# Initialize boto3 EC2 client
ec2 = boto3.client('ec2')

# Define potentially dangerous ports and the public CIDR block
DANGEROUS_PORTS = [
    22,    # SSH
    3389,  # RDP
    80,    # HTTP
    443,   # HTTPS
    8080,  # Common web server port
    27017, # MongoDB (common misconfig)
    5432,  # PostgreSQL
    3306,  # MySQL
    1521   # Oracle SQL
]
PUBLIC_CIDR = '0.0.0.0/0'

def lambda_handler(event, context):
    """
    Lambda handler function to detect and remediate insecure EC2 Security Group rules.
    """
    logger.info(f"Received event: {json.dumps(event)}")

    # Check if remediation is enabled via environment variable
    remediate_enabled = os.environ.get('REMEDIATE', 'false').lower() == 'true'
    logger.info(f"Remediation enabled: {remediate_enabled}")

    misconfigured_rules = []

    try:
        # Describe all security groups
        response = ec2.describe_security_groups()
        security_groups = response['SecurityGroups']

        for sg in security_groups:
            sg_id = sg['GroupId']
            sg_name = sg.get('GroupName', 'N/A')
            sg_description = sg.get('Description', 'N/A')

            logger.debug(f"Checking Security Group: {sg_name} ({sg_id})")

            # Iterate through ingress permissions
            for ip_permission in sg.get('IpPermissions', []):
                # Check for public CIDR in IpRanges
                for ip_range in ip_permission.get('IpRanges', []):
                    if ip_range['CidrIp'] == PUBLIC_CIDR:
                        # Check if the port falls within the dangerous range or is a specific dangerous port
                        from_port = ip_permission.get('FromPort')
                        to_port = ip_permission.get('ToPort')

                        # Handle cases where FromPort/ToPort might be missing (e.g., all ICMP traffic)
                        if from_port is None and to_port is None:
                            # This means all protocols are allowed publicly, which is highly insecure
                            logger.warning(
                                f"Misconfiguration found in SG '{sg_name}' ({sg_id}): "
                                f"All protocols publicly accessible via {PUBLIC_CIDR}. "
                                f"Protocol: {ip_permission.get('IpProtocol', 'All')}"
                            )
                            misconfigured_rules.append({
                                'GroupId': sg_id,
                                'IpPermissions': [ip_permission] # Add the entire permission to revoke
                            })
                            continue # Move to the next IpRange for this permission

                        # Check for specific dangerous ports
                        for dangerous_port in DANGEROUS_PORTS:
                            if from_port <= dangerous_port <= to_port:
                                logger.warning(
                                    f"Misconfiguration found in SG '{sg_name}' ({sg_id}): "
                                    f"Public access ({PUBLIC_CIDR}) allowed on port {dangerous_port}. "
                                    f"Protocol: {ip_permission.get('IpProtocol', 'N/A')}"
                                )
                                # Store the specific rule details for potential remediation
                                misconfigured_rules.append({
                                    'GroupId': sg_id,
                                    'IpPermissions': [{
                                        'IpProtocol': ip_permission.get('IpProtocol'),
                                        'FromPort': from_port,
                                        'ToPort': to_port,
                                        'IpRanges': [{'CidrIp': PUBLIC_CIDR}]
                                    }]
                                })
                                # Once a dangerous port is found in this permission, no need to check others
                                break
    except Exception as e:
        logger.error(f"Error during security group scan: {e}", exc_info=True)
        return {
            'statusCode': 500,
            'body': json.dumps(f"Error during scan: {e}")
        }

    if not misconfigured_rules:
        logger.info("No security misconfigurations found.")
        return {
            'statusCode': 200,
            'body': json.dumps("No security misconfigurations found.")
        }

    logger.warning(f"Found {len(misconfigured_rules)} potential security misconfigurations.")

    # Remediate if enabled
    if remediate_enabled:
        remediated_count = 0
        for rule in misconfigured_rules:
            sg_id = rule['GroupId']
            ip_permissions_to_revoke = rule['IpPermissions']

            try:
                # Revoke the security group ingress rule
                # The revoke_security_group_ingress API expects a list of IpPermissions,
                # so we can pass the extracted problematic rules directly.
                ec2.revoke_security_group_ingress(
                    GroupId=sg_id,
                    IpPermissions=ip_permissions_to_revoke
                )
                logger.info(
                    f"Successfully remediated SG '{sg_id}' by revoking public access for "
                    f"rules: {json.dumps(ip_permissions_to_revoke)}"
                )
                remediated_count += 1
            except Exception as e:
                logger.error(
                    f"Failed to remediate SG '{sg_id}' rule {json.dumps(ip_permissions_to_revoke)}: {e}",
                    exc_info=True
                )
        return {
            'statusCode': 200,
            'body': json.dumps(f"Detected {len(misconfigured_rules)} misconfigurations. "
                               f"Remediated {remediated_count} rules.")
        }
    else:
        logger.info("Remediation is disabled. Logging detected misconfigurations without making changes.")
        return {
            'statusCode': 200,
            'body': json.dumps(f"Detected {len(misconfigured_rules)} misconfigurations. "
                               "Remediation is disabled.")
        }