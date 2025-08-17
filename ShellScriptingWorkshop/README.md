# Introduction to Shell Scripting Workshop

Welcome to the Shell Scripting Workshop! This repository provides a hands-on introduction to writing and executing Bash shell scripts. It covers fundamental concepts such as variables, loops, and conditional statements, culminating in a practical beginner project.

## What You'll Learn

*   How to create and run basic Bash scripts.
*   Using variables to store and manipulate data.
*   Controlling script flow with if/else statements.
*   Automating repetitive tasks with for and while loops.
*   Organizing code with functions (optional but included).

## Project: Disk Space Monitor

A simple script that checks the disk usage of a specified partition and sends a warning if it exceeds a predefined threshold. This project demonstrates how to combine the learned concepts into a useful automation tool.

## Repository Structure

*   README.md: This introduction.
*   ShellScriptingWorkshop/01_hello_world.sh: Your very first script.
*   ShellScriptingWorkshop/02_variables.sh: Demonstrates variable usage.
*   ShellScriptingWorkshop/03_conditionals.sh: Examples of if/else/elif.
*   ShellScriptingWorkshop/04_loops.sh: For and while loop examples.
*   ShellScriptingWorkshop/05_functions.sh: Basic function definition and calls.
*   ShellScriptingWorkshop/disk_monitor.sh: The disk space checking script.
*   ShellScriptingWorkshop/config.conf: Configuration file for disk_monitor.sh.

## How to Get Started

1.  **Clone the Repository:**
    You would typically clone a git repository to your local machine.

2.  **Navigate to the Project Directory:**
    cd ShellScriptingWorkshop

3.  **Make Scripts Executable:**
    Before running any script, you need to give it execute permissions. For example:
    chmod +x 01_hello_world.sh
    chmod +x disk_monitor.sh
    (Repeat for other .sh files as needed)

4.  **Run the Scripts:**
    Execute a script using:
    ./01_hello_world.sh

    For the disk monitor, you might first want to edit config.conf with your email address:
    nano config.conf
    Then run:
    ./disk_monitor.sh

    Remember: For email warnings, you might need to install a mail client like 'mailx' or 'postfix' on your system, depending on your Linux distribution. For example, on Debian/Ubuntu: sudo apt-get install mailutils. On CentOS/RHEL: sudo yum install mailx.

Enjoy your journey into Shell Scripting!