Linux-File-Permissions-Demystified

This project aims to provide a clear understanding of file permissions in Linux systems. It covers the fundamental concepts of rwx (read, write, execute) permissions, file ownership, and groups, along with practical examples using common commands like 'ls -l', 'chmod', and 'chown'.

Core Concepts Explained:

1.  rwx (Read, Write, Execute) Permissions:
    *   Read (r): Allows viewing the contents of a file or listing the contents of a directory.
    *   Write (w): Allows modifying the contents of a file or creating/deleting files within a directory.
    *   Execute (x): Allows running an executable file or entering a directory.

2.  Ownership:
    *   Each file and directory in Linux has an owner user and an owner group.
    *   Permissions are defined separately for:
        *   User (u): The owner of the file.
        *   Group (g): Members of the owner group.
        *   Others (o): Everyone else on the system.
        *   All (a): A shorthand for user, group, and others.

3.  ls -l (List Long Format):
    *   This command is used to view detailed information about files and directories, including their permissions.
    *   The first character indicates the file type (e.g., '-' for a regular file, 'd' for a directory).
    *   The next nine characters represent the rwx permissions for user, group, and others, respectively (e.g., 'rwxr-xr--').

4.  chmod (Change Mode):
    *   Used to modify file permissions.
    *   Symbolic Mode: Uses 'u', 'g', 'o', 'a' with '+', '-', '=' operators (e.g., 'chmod u+x myscript.sh').
    *   Octal (Numeric) Mode: Represents permissions using numbers (read=4, write=2, execute=1). Summing these gives the permission for each triplet (e.g., 'chmod 755 myscript.sh' for rwxr-xr-x).

5.  chown (Change Owner):
    *   Used to change the owner user and/or owner group of a file or directory.
    *   Often requires superuser privileges (sudo).
    *   Examples: 'chown newuser myfile.txt', 'chown :newgroup myfile.txt', 'chown newuser:newgroup myfile.txt'.

Project Structure:

The project consists of a 'scripts' directory containing several shell scripts to demonstrate these concepts interactively. A 'data' directory will be created by the setup script to hold example files.

How to Run the Examples:

1.  Navigate to the project root directory in your terminal.
2.  Run the setup script to create dummy files:
    bash scripts/setup.sh
3.  View initial permissions:
    bash scripts/view_permissions.sh
4.  Experiment with changing permissions using chmod:
    bash scripts/change_permissions_chmod.sh
5.  (Optional) Experiment with changing ownership using chown (may require sudo and existing users/groups):
    bash scripts/change_ownership_chown.sh
6.  See how to apply permissions in batches:
    bash scripts/batch_permissions.sh
7.  Clean up the created files and directories:
    bash scripts/cleanup.sh

Each script includes comments to explain the commands being executed.
Feel free to modify the scripts and experiment with different permission settings to deepen your understanding.