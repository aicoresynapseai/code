#!/bin/bash

# Define the data directory
DATA_DIR="../data"

echo "--- Viewing File and Directory Permissions ---"
echo "Using 'ls -l' to inspect permissions."

# Navigate into the data directory for easier demonstration
# This is a subshell to avoid changing the current directory of the parent script
(
    cd "$DATA_DIR" || { echo "Error: Could not enter $DATA_DIR. Make sure setup.sh was run."; exit 1; }
    echo ""
    echo "Current directory: $(pwd)"
    echo "Viewing contents of '$DATA_DIR':"
    echo "--------------------------------------------------------------------------"
    # List all files and directories with long format
    # The output shows:
    # 1. File type and permissions (e.g., -rw-r--r-- for a file, drwxr-xr-x for a directory)
    # 2. Number of hard links
    # 3. Owner user
    # 4. Owner group
    # 5. File size in bytes
    # 6. Last modification date and time
    # 7. File or directory name
    ls -l

    echo "--------------------------------------------------------------------------"
    echo ""
    echo "Understanding the permission string (e.g., -rwxr-xr--):"
    echo "The first character indicates the file type:"
    echo "  '-' for a regular file"
    echo "  'd' for a directory"
    echo "  'l' for a symbolic link"
    echo "The next 9 characters are the permissions in three sets of 'rwx':"
    echo "  1st set (3 chars): Permissions for the 'User' (owner)"
    echo "  2nd set (3 chars): Permissions for the 'Group' (owner group)"
    echo "  3rd set (3 chars): Permissions for 'Others' (everyone else)"
    echo ""
    echo "Example Breakdown: '-rw-r--r--'"
    echo "  '-' : Regular file"
    echo "  'rw-' : Owner has Read and Write permissions"
    echo "  'r--' : Group has Read permission only"
    echo "  'r--' : Others have Read permission only"
    echo ""
    echo "Example Breakdown: 'drwxr-xr-x'"
    echo "  'd' : Directory"
    echo "  'rwx' : Owner has Read, Write, and Execute permissions (can list, create, delete, enter)"
    echo "  'r-x' : Group has Read and Execute permissions (can list, enter, but not modify contents)"
    echo "  'r-x' : Others have Read and Execute permissions (can list, enter, but not modify contents)"
    echo ""
)

echo "Permission viewing complete."
echo "You can now run change_permissions_chmod.sh to modify these permissions."