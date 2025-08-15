#!/bin/bash

# Define the data directory
DATA_DIR="../data"

echo "--- Changing File Ownership using chown ---"
echo "Note: 'chown' often requires 'sudo' privileges and assumes the target user/group exists on your system."
echo "If you don't have 'sudo' or specific users/groups, the commands below might fail."
echo "Please replace 'newuser' and 'newgroup' with actual existing user/group names on your system."
echo ""

# Display initial ownership for a file
echo "Initial ownership for $DATA_DIR/file1.txt:"
ls -l "$DATA_DIR/file1.txt"
echo ""

# Example 1: Change only the owner user
# IMPORTANT: Replace 'newuser' with an actual user on your system.
# You might need to create a test user first (e.g., 'sudo adduser testuser').
echo "1. Attempting to change owner of file1.txt to 'newuser' (requires sudo)"
echo "   sudo chown newuser $DATA_DIR/file1.txt"
# Uncomment the line below to execute if you have sudo and 'newuser' exists
# sudo chown newuser "$DATA_DIR/file1.txt"
ls -l "$DATA_DIR/file1.txt"
echo "If the owner didn't change, 'newuser' might not exist or sudo wasn't used/successful."
echo ""

# Example 2: Change only the owner group
# IMPORTANT: Replace 'newgroup' with an actual group on your system.
# You might need to create a test group first (e.g., 'sudo addgroup testgroup').
echo "2. Attempting to change group of file2.txt to 'newgroup' (requires sudo)"
echo "   sudo chown :newgroup $DATA_DIR/file2.txt"
# Uncomment the line below to execute if you have sudo and 'newgroup' exists
# sudo chown :newgroup "$DATA_DIR/file2.txt"
ls -l "$DATA_DIR/file2.txt"
echo "If the group didn't change, 'newgroup' might not exist or sudo wasn't used/successful."
echo ""

# Example 3: Change both owner user and owner group
echo "3. Attempting to change owner of my_script.sh to 'newuser' and group to 'newgroup' (requires sudo)"
echo "   sudo chown newuser:newgroup $DATA_DIR/my_script.sh"
# Uncomment the line below to execute if you have sudo and both exist
# sudo chown newuser:newgroup "$DATA_DIR/my_script.sh"
ls -l "$DATA_DIR/my_script.sh"
echo "If ownership didn't change, check user/group existence or sudo."
echo ""

# Example 4: Changing ownership of a directory and its contents recursively
echo "4. Attempting to change ownership of the data directory and its contents recursively (requires sudo)"
echo "   sudo chown -R newuser:newgroup $DATA_DIR"
# Uncomment the line below to execute with caution, as it affects all files created
# inside the data directory. Replace 'newuser' and 'newgroup' accordingly.
# sudo chown -R newuser:newgroup "$DATA_DIR"
ls -l "$DATA_DIR"
echo ""
echo "Note: The '-R' option is crucial for recursive ownership changes on directories."

echo "Chown demonstrations complete."
echo "Remember to revert ownership changes if you made them for sensitive files, or clean up."
echo "You can now run batch_permissions.sh to see how to apply permissions to multiple files."