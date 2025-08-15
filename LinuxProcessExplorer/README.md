This project, "Linux Process Explorer", provides a hands-on introduction to basic process management in Linux. It covers essential commands like `ps`, `top`, `htop`, and `kill` which are crucial for monitoring and managing running processes. Additionally, it includes a custom shell script to demonstrate continuous CPU usage monitoring for a specific process.

Key Concepts and Commands Explained:

1.  ps (Process Status)
    The `ps` command displays information about currently running processes. It's a snapshot of the processes.
    *   `ps aux`: Shows all processes (owned by any user) running on the system, including those not attached to a terminal.
        *   `a`: show processes for all users.
        *   `u`: display user/owner and other detailed information.
        *   `x`: show processes not attached to a terminal.
    *   `ps -ef`: Another common way to display all processes in a full format.
        *   `e`: selects all processes.
        *   `f`: does a full-format listing.
    *   `ps aux | grep [process_name]`: Useful for finding a specific process by its name (or part of its name). The `grep` command filters the output of `ps`.

2.  top (Table of Processes)
    The `top` command provides a real-time, dynamic view of a running system. It shows a summary of system and process information, including CPU usage, memory usage, and a list of the most CPU-intensive tasks.
    *   Interactive usage:
        *   Press `k`: To kill a process (it will prompt for PID and signal).
        *   Press `r`: To renice a process (change its priority).
        *   Press `q`: To quit `top`.

3.  htop (Enhanced top)
    `htop` is an interactive process viewer that is an enhanced alternative to `top`. It offers a more user-friendly interface with features like vertical and horizontal scrolling, mouse support, and clearer visual representation.
    *   Installation: `htop` is often not installed by default. You can install it on Debian/Ubuntu with `sudo apt install htop` or on Fedora/RHEL with `sudo dnf install htop`.
    *   Features: Easy process killing by selecting and pressing F9, tree view, filtering, and more.

4.  kill (Send Signal to Processes)
    The `kill` command is used to send signals to processes. The most common use is to terminate processes.
    *   `kill [PID]`: Sends the SIGTERM (terminate) signal (signal 15). This is a polite request for the process to shut down, allowing it to clean up before exiting.
    *   `kill -9 [PID]`: Sends the SIGKILL (kill) signal (signal 9). This is a forceful termination that cannot be ignored by the process. Use with caution as it doesn't allow the process to perform cleanup.
    *   `killall [process_name]`: Kills all processes with a given name. This is useful when you want to terminate multiple instances of an application.

Project Structure:

*   `scripts/monitor_cpu_usage.sh`: A shell script designed to continuously monitor the CPU usage of a specific process by its PID or name.
*   `scripts/dummy_process.sh`: A simple shell script that runs indefinitely, consuming some CPU, to serve as a target for monitoring and termination exercises.

How to Use This Project:

1.  Navigate to the `scripts` directory:
    cd LinuxProcessExplorer/scripts

2.  Make the scripts executable:
    chmod +x monitor_cpu_usage.sh dummy_process.sh

3.  Start the dummy process in the background:
    ./dummy_process.sh &
    This will start the `dummy_process.sh` and put it into the background, allowing you to continue using your terminal. Note the PID that is printed (e.g., "Dummy process started. PID: 12345").

4.  Find the PID of the dummy process (if you missed it):
    ps aux | grep dummy_process.sh
    Look for a line similar to `/bin/bash ./dummy_process.sh` and identify its PID (the second column).

5.  Monitor the CPU usage of the dummy process:
    You can use its PID:
    ./monitor_cpu_usage.sh [PID_of_dummy_process]
    (Replace `[PID_of_dummy_process]` with the actual PID you found, e.g., `./monitor_cpu_usage.sh 12345`)

    Or, you can use its name:
    ./monitor_cpu_usage.sh dummy_process.sh

    The script will start printing the CPU usage of the dummy process every few seconds. Press `Ctrl+C` to stop the monitoring script.

6.  Experiment with `top` and `htop`:
    Open a new terminal and run:
    top
    Observe the `dummy_process.sh` in the list, its CPU usage, and PID.
    (Press `q` to quit `top`).

    If you have `htop` installed, try:
    htop
    Enjoy the more interactive interface. You can search for `dummy_process.sh` (F3) or kill it directly (F9).
    (Press `F10` or `q` to quit `htop`).

7.  Terminate the dummy process:
    Once you're done monitoring, you can kill the dummy process.
    Using its PID:
    kill [PID_of_dummy_process]
    (e.g., `kill 12345`)

    Verify it's gone:
    ps aux | grep dummy_process.sh
    You should no longer see the dummy process running. The `monitor_cpu_usage.sh` script (if still running) will also report that the process no longer exists.

This project provides a practical foundation for understanding and interacting with processes in a Linux environment.