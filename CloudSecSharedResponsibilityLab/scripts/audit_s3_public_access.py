import boto3
import json

def audit_s3_public_access():
    """
    Audits S3 buckets in the AWS account for public access configurations.
    Identifies buckets that are publicly accessible via ACLs or bucket policies,
    or have Block Public Access settings disabled.
    """
    s3 = boto3.client('s3')
    
    print("Starting S3 Public Access Audit...\n")

    try:
        response = s3.list_buckets()
        buckets = response['Buckets']

        if not buckets:
            print("No S3 buckets found in this account.")
            return

        for bucket in buckets:
            bucket_name = bucket['Name']
            print(f"Auditing bucket: {bucket_name}")

            is_public_acl = False
            is_public_policy = False
            block_public_access_enabled = True

            # 1. Check Bucket ACL for Public Access
            try:
                acl_response = s3.get_bucket_acl(Bucket=bucket_name)
                for grant in acl_response['Grants']:
                    # Check for 'AllUsers' (public) or 'AuthenticatedUsers' (public for authenticated AWS users)
                    if 'URI' in grant['Grantee'] and \
                       ('http://acs.amazonaws.com/groups/global/AllUsers' == grant['Grantee']['URI'] or \
                        'http://acs.amazonaws.com/groups/global/AuthenticatedUsers' == grant['Grantee']['URI']):
                        is_public_acl = True
                        print(f"  WARNING: Public ACL found for bucket '{bucket_name}'. Grantee URI: {grant['Grantee']['URI']}, Permission: {grant['Permission']}")
                        break
            except s3.exceptions.ClientError as e:
                if e.response['Error']['Code'] == 'NoSuchBucket':
                    print(f"  Error: Bucket '{bucket_name}' not found. Skipping ACL check.")
                elif e.response['Error']['Code'] == 'AccessDenied':
                    print(f"  Access Denied: Could not check ACL for bucket '{bucket_name}'. Ensure IAM permissions.")
                else:
                    print(f"  Error checking ACL for '{bucket_name}': {e}")

            # 2. Check Bucket Policy for Public Access
            try:
                policy_response = s3.get_bucket_policy(Bucket=bucket_name)
                policy_doc = json.loads(policy_response['Policy'])
                
                for statement in policy_doc.get('Statement', []):
                    if statement.get('Effect') == 'Allow':
                        # Check if principal is wildcard '*' which signifies public access
                        principal = statement.get('Principal')
                        if principal == '*' or (isinstance(principal, dict) and 'AWS' in principal and principal['AWS'] == '*'):
                            # Further check if action grants read access
                            actions = statement.get('Action', [])
                            if isinstance(actions, str):
                                actions = [actions]
                            
                            # Common public read actions
                            public_read_actions = ['s3:GetObject', 's3:GetObjectVersion', 's3:ListBucket']
                            if any(action in public_read_actions or action == 's3:*' for action in actions):
                                resource = statement.get('Resource')
                                print(f"  WARNING: Public Bucket Policy found for '{bucket_name}'. Principal: {principal}, Action: {actions}, Resource: {resource}")
                                is_public_policy = True
                                break # Found public policy, no need to check other statements
            except s3.exceptions.ClientError as e:
                if e.response['Error']['Code'] == 'NoSuchBucketPolicy':
                    # No bucket policy exists, which is not an error but means no public policy via this method
                    pass
                elif e.response['Error']['Code'] == 'AccessDenied':
                    print(f"  Access Denied: Could not check Bucket Policy for bucket '{bucket_name}'. Ensure IAM permissions.")
                else:
                    print(f"  Error checking Bucket Policy for '{bucket_name}': {e}")
            
            # 3. Check Block Public Access settings
            try:
                bpa_response = s3.get_public_access_block(Bucket=bucket_name)
                config = bpa_response['PublicAccessBlockConfiguration']
                # If any of these are False, public access is NOT blocked
                if not (config.get('BlockPublicAcls') and 
                        config.get('IgnorePublicAcls') and 
                        config.get('BlockPublicPolicy') and 
                        config.get('RestrictPublicBuckets')):
                    block_public_access_enabled = False
                    print(f"  WARNING: Block Public Access settings are NOT fully enabled for '{bucket_name}'. Configuration: {config}")
            except s3.exceptions.ClientError as e:
                if e.response['Error']['Code'] == 'NoSuchPublicAccessBlockConfiguration':
                    block_public_access_enabled = False # Default is no configuration means no blocking
                    print(f"  WARNING: No Block Public Access configuration found for '{bucket_name}'. Public access is not explicitly blocked.")
                elif e.response['Error']['Code'] == 'AccessDenied':
                    print(f"  Access Denied: Could not check Public Access Block for bucket '{bucket_name}'. Ensure IAM permissions.")
                else:
                    print(f"  Error checking Public Access Block for '{bucket_name}': {e}")

            if not is_public_acl and not is_public_policy and block_public_access_enabled:
                print(f"  Bucket '{bucket_name}' appears to be secure (no public ACL/policy, BPA fully enabled).")
            print("-" * 50)

    except s3.exceptions.ClientError as e:
        if e.response['Error']['Code'] == 'AccessDenied':
            print("ERROR: Access Denied. Please ensure your AWS credentials have 's3:ListAllMyBuckets', 's3:GetBucketAcl', 's3:GetBucketPolicy', and 's3:GetPublicAccessBlock' permissions.")
        else:
            print(f"An unexpected error occurred: {e}")
    
    print("\nS3 Public Access Audit Complete.")

if __name__ == "__main__":
    audit_s3_public_access()