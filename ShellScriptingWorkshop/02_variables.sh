#!/bin/bash
#
# ShellScriptingWorkshop/02_variables.sh
# Demonstrates how to declare and use variables in Bash.

# --- String Variables ---
# Assign a string value to a variable. No spaces around '='.
MY_NAME="Alice"
GREETING="Hello"

# Access variable values using '$' prefix. Enclosing in double quotes is good practice
# to prevent word splitting and globbing, especially if values contain spaces.
echo "$GREETING, $MY_NAME!"

# Reassigning a variable
MY_NAME="Bob"
echo "Now, $GREETING, $MY_NAME!"

# --- Numeric Variables ---
# Bash treats variables as strings by default. For arithmetic operations,
# use '((...))' or 'expr' or 'bc'.
NUM1=10
NUM2=5

# Performing arithmetic operations using '((...))'
SUM=$((NUM1 + NUM2))
echo "Sum of $NUM1 and $NUM2 is: $SUM"

DIFFERENCE=$((NUM1 - NUM2))
echo "Difference is: $DIFFERENCE"

PRODUCT=$((NUM1 * NUM2))
echo "Product is: $PRODUCT"

DIVISION=$((NUM1 / NUM2))
echo "Division is: $DIVISION"

# --- Array Variables ---
# Declare an array using parentheses, separating elements with spaces.
FRUITS=("Apple" "Banana" "Cherry" "Date")

# Access all elements of an array: "${ARRAY_NAME[@]}" or "${ARRAY_NAME[*]}"
echo "All fruits: ${FRUITS[@]}"

# Access a specific element by its index (arrays are 0-indexed)
echo "My favorite fruit is: ${FRUITS[0]}" # First element (Apple)
echo "Another fruit: ${FRUITS[2]}"       # Third element (Cherry)

# Get the number of elements in an array
echo "Number of fruits: ${#FRUITS[@]}"

# Add an element to an array
FRUITS[4]="Elderberry"
echo "Updated fruits list: ${FRUITS[@]}"

# Get the length of a string variable
MESSAGE="Learning Bash"
echo "Length of message '$MESSAGE' is: ${#MESSAGE}"