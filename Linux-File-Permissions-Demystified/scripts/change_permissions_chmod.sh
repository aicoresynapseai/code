#!/bin/bash

# Define the data directory
DATA_DIR="../data"

echo "--- Changing File Permissions using chmod ---"

echo ""
echo "Initial permissions for relevant files:"
ls -l "$DATA_DIR/file1.txt" "$DATA_DIR/my_script.sh" "$DATA_DIR/private_data.conf"

echo ""
echo "--- Demonstrating Symbolic Mode (u,g,o,a with +, -, =) ---"

# Example 1: Add execute permission for the owner to file1.txt
echo "1. Adding execute permission for owner to file1.txt (u+x)"
chmod u+x "$DATA_DIR/file1.txt"
ls -l "$DATA_DIR/file1.txt"
echo ""

# Example 2: Remove write permission for others from my_script.sh
echo "2. Removing write permission for others from my_script.sh (o-w)"
chmod o-w "$DATA_DIR/my_script.sh"
ls -l "$DATA_DIR/my_script.sh"
echo ""

# Example 3: Set read/write for group and no permissions for others on private_data.conf
echo "3. Setting read/write for group and no permissions for others on private_data.conf (g=rw,o=)"
chmod g=rw,o= "$DATA_DIR/private_data.conf"
ls -l "$DATA_DIR/private_data.conf"
echo ""

# Example 4: Add read permission for everyone to file2.txt (a+r)
echo "4. Adding read permission for everyone to file2.txt (a+r)"
chmod a+r "$DATA_DIR/file2.txt"
ls -l "$DATA_DIR/file2.txt"
echo ""

# Reset permissions for numeric mode demonstration
echo "--- Resetting some permissions for Numeric (Octal) Mode demonstration ---"
chmod 644 "$DATA_DIR/file1.txt"     # -rw-r--r--
chmod 755 "$DATA_DIR/my_script.sh"  # -rwxr-xr-x
chmod 600 "$DATA_DIR/private_data.conf" # -rw-------
chmod 644 "$DATA_DIR/file2.txt"     # -rw-r--r--
ls -l "$DATA_DIR/file1.txt" "$DATA_DIR/my_script.sh" "$DATA_DIR/private_data.conf" "$DATA_DIR/file2.txt"
echo ""

echo "--- Demonstrating Numeric (Octal) Mode ---"
echo "Numbers for permissions: r=4, w=2, x=1. Sum them for each set (user, group, others)."
echo "Example: 755 = (4+2+1) (4+0+1) (4+0+1) = rwx r-x r-x"
echo "Example: 640 = (4+2+0) (4+0+0) (0+0+0) = rw- r-- ---"

# Example 5: Set my_script.sh to 700 (owner rwx, no access for group/others)
echo "5. Setting my_script.sh to 700 (owner rwx, no access for group/others)"
chmod 700 "$DATA_DIR/my_script.sh"
ls -l "$DATA_DIR/my_script.sh"
echo ""

# Example 6: Set file1.txt to 664 (owner rw, group rw, others r)
echo "6. Setting file1.txt to 664 (owner rw, group rw, others r)"
chmod 664 "$DATA_DIR/file1.txt"
ls -l "$DATA_DIR/file1.txt"
echo ""

# Example 7: Set private_data.conf to 444 (read-only for all)
echo "7. Setting private_data.conf to 444 (read-only for all)"
chmod 444 "$DATA_DIR/private_data.conf"
ls -l "$DATA_DIR/private_data.conf"
echo ""

echo "Chmod demonstrations complete."
echo "You can now run change_ownership_chown.sh (if you have sudo privileges and other users/groups)."