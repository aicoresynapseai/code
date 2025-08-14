Linux File and Directory Management Basics

This project serves as a hands-on guide to fundamental file and directory management commands in Linux. It's designed for anyone looking to understand how to manipulate files and folders using the command line.

Key Concepts Covered:
- Creating files with touch
- Creating directories with mkdir
- Copying files and directories with cp
- Moving and renaming files/directories with mv
- Deleting files and directories with rm

How to Use This Project:

1.  Navigate to the project directory:
    Change your current directory to 'linux-file-management-basics' where these files are located.

2.  Make the scripts executable:
    Before running, ensure the shell scripts are executable.
    You can do this by running the following commands in your terminal:
    chmod +x demo.sh
    chmod +x scripts/create_complex_structure.sh
    chmod +x scripts/cleanup.sh

3.  Run the main demonstration script:
    Execute the 'demo.sh' script to see all operations in action. It will walk you through creating, copying, moving, renaming, and deleting files and directories step-by-step.
    ./demo.sh

4.  Observe the output:
    The 'demo.sh' script will print explanations and command outputs directly to your terminal. It creates a 'demo_area' directory to perform all operations, keeping your main project directory clean.

5.  Inspect the created structure:
    After the 'demo.sh' script finishes, you can explore the 'demo_area' directory to see the results of the commands.
    You might find the 'tree' command useful if installed on your system, otherwise, 'ls -R demo_area' will list contents recursively.

6.  Clean up the environment:
    To remove all files and directories created by the demonstration, run the 'cleanup.sh' script.
    ./scripts/cleanup.sh

Project Structure:
- README.md: This file, providing an overview and instructions.
- demo.sh: The main script that demonstrates all file and directory operations interactively.
- scripts/create_complex_structure.sh: A helper script used by 'demo.sh' to automate the creation of a more complex directory and file structure.
- scripts/cleanup.sh: A utility script to remove all files and directories created by the demo, restoring the initial state.