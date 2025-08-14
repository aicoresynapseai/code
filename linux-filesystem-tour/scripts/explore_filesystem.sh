
#!/bin/bash
# This script guides the user through the Linux file system hierarchy.

# Set strict mode for robustness:
# -e: Exit immediately if a command exits with a non-zero status.
# -u: Treat unset variables as an error.
# -o pipefail: The return value of a pipeline is the status of the last command to exit with a non-zero status,
#              or zero if all commands in the pipeline exit successfully.
set -euo pipefail

# --- Utility Functions ---

# print_header: Displays a title for each section of the tour.
print_header() {
    echo ""
    echo "--- $1 ---"
    echo ""
}

# pause: Halts script execution until the user presses Enter.
# This allows the user to read the output before proceeding.
pause() {
    read -rp "Press Enter to continue..."
    echo ""
}

# check_dir: Verifies if a given directory exists.
# Prints a warning and returns 1 if it doesn't, otherwise returns 0.
check_dir() {
    if [[ -d "$1" ]]; then
        return 0 # Directory exists
    else
        echo "Warning: Directory '$1' does not exist on this system. Skipping detailed exploration."
        return 1 # Directory does not exist
    fi
}

# --- Directory Exploration Functions ---

# explore_root: Explains and lists the contents of the root directory.
explore_root() {
    print_header "Exploring / (Root Directory)"
    echo "The root directory '/' is the top-most directory in the Linux file system hierarchy."
    echo "All other directories and files are contained within it."
    echo ""
    echo "Your current directory (pwd):"
    pwd
    echo ""
    echo "Listing contents of / (ls -F /):"
    # ls -F lists directories with a trailing slash, executables with an asterisk, etc.
    # column -c 80 tries to format output into 80 columns for readability.
    # || true is used to prevent the script from exiting if 'column' command is not found or fails.
    ls -F / | column -c 80 || true
    echo ""
    echo "Notice common directories like 'home', 'etc', 'bin', 'var', 'usr', 'dev', 'proc', 'sys'."
    pause
}

# explore_home: Explains /home and attempts to show user's home directory.
explore_home() {
    print_header "Exploring /home"
    echo "/home contains the personal directories for each user on the system."
    echo "For example, if you are 'youruser', your personal files are in /home/youruser."
    echo ""
    if check_dir "/home"; then
        echo "Listing contents of /home (ls -l /home):"
        # ls -l provides long listing format, showing permissions, owner, group, size, date, name.
        ls -l /home || true # || true in case /home is empty or permissions restrict access
        echo ""
        echo "You should see directories named after the users on this system."
        if [[ -d "$HOME" ]]; then
            echo "Let's change to your home directory: cd ~"
            cd ~
            echo "Current directory (pwd):"
            pwd
            echo ""
            echo "Listing contents of your home directory (ls -F):"
            ls -F || true
            echo "This is where your documents, downloads, and personal configurations usually reside."
            cd / # Go back to root for consistent navigation
        else
            echo "Could not find your home directory ($HOME)."
        fi
    fi
    pause
}

# explore_etc: Explains /etc and lists some common configuration files.
explore_etc() {
    print_header "Exploring /etc"
    echo "/etc (Etcetera) holds system-wide configuration files."
    echo "These files control how your system behaves, including network settings, user accounts, and service configurations."
    echo ""
    if check_dir "/etc"; then
        echo "Listing some common configuration files in /etc (ls -l /etc/hostname /etc/resolv.conf etc.):"
        # ls -l for long listing, using specific common config files and wildcards for others.
        # 2>/dev/null suppresses errors for files that might not exist.
        ls -l /etc/hostname /etc/resolv.conf /etc/passwd /etc/group /etc/*release* /etc/*conf 2>/dev/null || true
        echo ""
        echo "Examples: 'passwd' (user accounts), 'group' (user groups), 'resolv.conf' (DNS settings), 'hostname' (system name)."
    fi
    pause
}

# explore_var: Explains /var and lists contents, including recent log files.
explore_var() {
    print_header "Exploring /var"
    echo "/var (Variable) stores variable data files. These are files whose content is expected to change frequently during system operation."
    echo "Common subdirectories include /var/log (system logs), /var/mail (email spool), /var/spool (printer/cron spools)."
    echo ""
    if check_dir "/var"; then
        echo "Listing contents of /var (ls -F /var):"
        ls -F /var || true
        echo ""
        if check_dir "/var/log"; then
            echo "Listing some recent log files in /var/log (ls -lt /var/log | head -n 6):"
            # ls -lt sorts by modification time (newest first), head -n 6 shows the top 5 entries + header.
            ls -lt /var/log | head -n 6 || true
            echo "Log files are crucial for troubleshooting system issues."
        fi
    fi
    pause
}

# explore_bin_sbin: Explains /bin and /sbin and lists essential commands.
explore_bin_sbin() {
    print_header "Exploring /bin and /sbin"
    echo "/bin (Binaries) contains essential user command binaries available to all users."
    echo "/sbin (System Binaries) contains essential system administration binaries, typically for root."
    echo ""
    if check_dir "/bin"; then
        echo "Listing some essential user commands in /bin (ls -F /bin | head -n 10):"
        # ls -F lists types, head shows first 10 for brevity.
        ls -F /bin | head -n 10 || true
        echo "Examples: 'ls', 'cp', 'mv', 'cat', 'echo'."
        echo ""
    fi
    if check_dir "/sbin"; then
        echo "Listing some essential system commands in /sbin (ls -F /sbin | head -n 10):"
        ls -F /sbin | head -n 10 || true
        echo "Examples: 'fdisk', 'ifconfig' (on older systems), 'reboot', 'mount'."
    fi
    pause
}

# explore_usr: Explains /usr and its important subdirectories.
explore_usr() {
    print_header "Exploring /usr"
    echo "/usr (Unix System Resources) is one of the largest directories and contains most user-installed applications and utilities, and their associated data."
    echo "It's further subdivided into /usr/bin, /usr/sbin, /usr/local, /usr/share, /usr/lib."
    echo ""
    if check_dir "/usr"; then
        echo "Listing main subdirectories of /usr (ls -F /usr):"
        ls -F /usr || true
        echo ""
        if check_dir "/usr/bin"; then
            echo "Listing some non-essential user commands in /usr/bin (ls -F /usr/bin | head -n 10):"
            ls -F /usr/bin | head -n 10 || true
            echo "Most desktop applications and common tools are found here (e.g., 'firefox', 'git')."
            echo ""
        fi
        if check_dir "/usr/local"; then
            echo "Listing contents of /usr/local (ls -F /usr/local):"
            ls -F /usr/local || true
            echo "This is where locally compiled or third-party software often gets installed."
        fi
    fi
    pause
}

# explore_lib: Explains /lib and /lib64 and lists common library files.
explore_lib() {
    print_header "Exploring /lib and /lib64"
    echo "/lib (Libraries) contains essential shared libraries and kernel modules required by the binaries in /bin and /sbin."
    echo "/lib64 is for 64-bit architecture specific libraries."
    echo ""
    if check_dir "/lib"; then
        echo "Listing some common library files in /lib (ls -F /lib | grep -E '\.so$' | head -n 5):"
        # grep for .so files (shared objects/libraries) which are common shared libraries.
        ls -F /lib 2>/dev/null | grep -E '\.so$' | head -n 5 || true
        echo "These files contain code that many programs share."
        echo ""
    fi
    if check_dir "/lib64"; then
        echo "Listing some common library files in /lib64 (ls -F /lib64 | grep -E '\.so$' | head -n 5):"
        ls -F /lib64 2>/dev/null | grep -E '\.so$' | head -n 5 || true
    fi
    pause
}

# explore_opt: Explains /opt and lists its contents.
explore_opt() {
    print_header "Exploring /opt"
    echo "/opt (Optional) is used for installing optional, third-party software that is not part of the standard distribution."
    echo "Often, large software packages install themselves entirely within a subdirectory under /opt (e.g., /opt/google/chrome)."
    echo ""
    if check_dir "/opt"; then
        echo "Listing contents of /opt (ls -F /opt):"
        ls -F /opt || true
        echo "If you have proprietary software like Google Chrome or Slack installed from their installers, they might reside here."
    fi
    pause
}

# explore_tmp: Explains /tmp and lists some recent temporary files.
explore_tmp() {
    print_header "Exploring /tmp"
    echo "/tmp (Temporary) stores temporary files created by the system and users."
    echo "Contents of this directory are often deleted upon reboot or after a certain period by a cleanup service."
    echo "It's generally writable by all users."
    echo ""
    if check_dir "/tmp"; then
        echo "Listing some recent files in /tmp (ls -lt /tmp | head -n 6):"
        ls -lt /tmp | head -n 6 || true
        echo "Don't store important files here, as they might be removed!"
    fi
    pause
}

# explore_dev: Explains /dev and lists some device files.
explore_dev() {
    print_header "Exploring /dev"
    echo "/dev (Devices) contains special files that represent hardware devices."
    echo "These files are not actual files on disk but are interfaces to kernel-managed devices."
    echo "Examples: /dev/sda (first hard drive), /dev/null (null device), /dev/zero (zero device), /dev/tty (terminal)."
    echo ""
    if check_dir "/dev"; then
        echo "Listing some device files in /dev (ls -l /dev | head -n 10):"
        ls -l /dev | head -n 10 || true
        echo "Notice the 'c' (character device) and 'b' (block device) in the first column for device files."
    fi
    pause
}

# explore_proc: Explains /proc and lists process information and system files.
explore_proc() {
    print_header "Exploring /proc"
    echo "/proc (Processes) is a virtual file system (pseudo-filesystem) that provides an interface to kernel data structures."
    echo "It contains information about running processes (numbered directories) and system resources in real-time."
    echo "Files here are generated by the kernel on the fly; they don't consume disk space."
    echo ""
    if check_dir "/proc"; then
        echo "Listing some contents of /proc (ls -F /proc | head -n 10):"
        ls -F /proc | head -n 10 || true
        echo "You'll see numbered directories corresponding to process IDs (PIDs) and files like 'meminfo', 'cpuinfo'."
        echo ""
        if [[ -f "/proc/cpuinfo" ]]; then
            echo "Contents of /proc/cpuinfo (head -n 5 /proc/cpuinfo):"
            head -n 5 /proc/cpuinfo || true
            echo "This file provides detailed CPU information."
        fi
    fi
    pause
}

# explore_sys: Explains /sys and lists its contents.
explore_sys() {
    print_header "Exploring /sys"
    echo "/sys (System) is another virtual file system, similar to /proc."
    echo "It provides a structured view of the hardware devices connected to the system and their configuration parameters, exposed by the kernel's 'sysfs' filesystem."
    echo ""
    if check_dir "/sys"; then
        echo "Listing some contents of /sys (ls -F /sys | head -n 10):"
        ls -F /sys | head -n 10 || true
        echo "You'll find directories like 'block' (for block devices), 'class', 'bus', 'devices', 'firmware', 'kernel'."
    fi
    pause
}

# explore_srv: Explains /srv and lists its contents.
explore_srv() {
    print_header "Exploring /srv"
    echo "/srv (Services) contains site-specific data served by the system."
    echo "For example, if you run a web server, its website data might be found in /srv/www."
    echo "It's often empty on desktop systems unless specific services are configured to use it."
    echo ""
    if check_dir "/srv"; then
        echo "Listing contents of /srv (ls -F /srv):"
        ls -F /srv || true
    fi
    pause
}

# explore_mnt_media: Explains /mnt and /media and lists their contents.
explore_mnt_media() {
    print_header "Exploring /mnt and /media"
    echo "/mnt (Mount) is a traditionally empty directory used as a temporary mount point for mounting file systems, such as network shares or external drives."
    echo "/media is used as a mount point for removable media like USB drives, CDs, and DVDs, typically managed by desktop environments."
    echo ""
    if check_dir "/mnt"; then
        echo "Listing contents of /mnt (ls -F /mnt):"
        ls -F /mnt || true
        echo ""
    fi
    if check_dir "/media"; then
        echo "Listing contents of /media (ls -F /media):"
        ls -F /media || true
    fi
    pause
}

# --- Main Script Execution ---

# Clear the terminal screen before starting the tour.
clear
echo "=========================================="
echo "   Welcome to the Linux File System Tour!"
echo "=========================================="
echo ""
echo "This script will guide you through the key directories"
echo "of a Linux file system, explaining their purpose and"
echo "showing example contents using 'ls', 'pwd', and 'cd'."
echo ""
pause

# Call each exploration function in logical order.
explore_root
explore_home
explore_etc
explore_var
explore_bin_sbin
explore_usr
explore_lib
explore_opt
explore_tmp
explore_dev
explore_proc
explore_sys
explore_srv
explore_mnt_media

# Final message to the user.
print_header "Tour Complete!"
echo "You've now had a guided tour of the fundamental Linux file system hierarchy."
echo "Understanding these directories is key to effective Linux system administration and usage."
echo "Feel free to explore further on your own using 'cd', 'ls', and 'man <command>'!"
echo ""