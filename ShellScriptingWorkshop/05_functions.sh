#!/bin/bash
#
# ShellScriptingWorkshop/05_functions.sh
# Demonstrates how to define and use functions in Bash.

# --- Function Example 1: Simple function without arguments ---
# Define a function named 'greet'.
# The 'function' keyword is optional but can improve readability.
function greet() {
  echo "Hello from the greet function!"
}

# Call the function.
echo "Calling 'greet' function:"
greet
echo ""

# --- Function Example 2: Function with arguments ---
# Arguments are accessed inside the function using $1, $2, etc.
# '$@' or '$*' refers to all arguments. '$#' refers to the number of arguments.
print_arguments() {
  echo "Function 'print_arguments' received $# arguments."
  echo "First argument: $1"
  echo "Second argument: $2"
  echo "All arguments: $@" # Best practice to use "$@" in double quotes
}

echo "Calling 'print_arguments' function:"
print_arguments "Apple" "Banana" "Cherry"
echo ""

# --- Function Example 3: Function with local variables ---
# Use 'local' keyword to declare variables within a function's scope.
# Without 'local', variables are global.
calculate_sum() {
  local num1=$1 # Declare num1 as a local variable
  local num2=$2 # Declare num2 as a local variable
  local sum=$((num1 + num2)) # Calculate sum locally
  echo "The sum is: $sum"
}

# Global variable example
GLOBAL_VAR="I'm global outside function"
display_vars() {
  local LOCAL_VAR="I'm local inside function"
  echo "Inside function: GLOBAL_VAR = $GLOBAL_VAR"
  echo "Inside function: LOCAL_VAR = $LOCAL_VAR"
  GLOBAL_VAR="I'm modified globally by function" # Modifying global variable
}

echo "Calling 'calculate_sum' function:"
calculate_sum 20 30
echo ""

echo "Before calling 'display_vars': GLOBAL_VAR = $GLOBAL_VAR"
display_vars
echo "After calling 'display_vars': GLOBAL_VAR = $GLOBAL_VAR"
# echo "After calling 'display_vars': LOCAL_VAR = $LOCAL_VAR" # This would be empty/error

# --- Function Example 4: Function returning a value (using 'echo' and command substitution) ---
# Functions can't directly 'return' values like in some other languages.
# They typically output values to stdout, which can be captured using command substitution.
get_square() {
  local number=$1
  echo $((number * number))
}

echo "Calling 'get_square' function:"
RESULT=$(get_square 7) # Capture the output of the function
echo "The square of 7 is: $RESULT"
echo ""

# --- Function Example 5: Function returning success/failure status (using exit codes) ---
# Functions return an exit status (0 for success, non-zero for failure).
# This is accessed via '$?'.
check_file_exists() {
  local file_path=$1
  if [[ -f "$file_path" ]]; then
    echo "$file_path exists."
    return 0 # Success
  else
    echo "$file_path does not exist."
    return 1 # Failure
  fi
}

echo "Calling 'check_file_exists' function:"
check_file_exists "./01_hello_world.sh"
if [[ $? -eq 0 ]]; then
  echo "Function returned success."
else
  echo "Function returned failure."
fi

check_file_exists "./non_existent_file.txt"
if [[ $? -eq 0 ]]; then
  echo "Function returned success."
else
  echo "Function returned failure."
fi