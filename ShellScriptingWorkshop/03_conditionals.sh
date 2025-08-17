#!/bin/bash
#
# ShellScriptingWorkshop/03_conditionals.sh
# Demonstrates if/else/elif conditional statements.

# --- Example 1: Basic if statement ---
# Check if a number is greater than 10.
NUM=15

# Use '[[ ... ]]' for conditional expressions.
# '-gt' means 'greater than'.
if [[ $NUM -gt 10 ]]; then
  echo "$NUM is greater than 10."
fi

# --- Example 2: if-else statement ---
# Check if a number is even or odd.
NUM=7

# '% 2' calculates the remainder when divided by 2.
# '-eq' means 'equal to'.
if [[ $((NUM % 2)) -eq 0 ]]; then
  echo "$NUM is an even number."
else
  echo "$NUM is an odd number."
fi

# --- Example 3: if-elif-else statement ---
# Check if a number is positive, negative, or zero.
NUM=-5

if [[ $NUM -gt 0 ]]; then
  echo "$NUM is a positive number."
elif [[ $NUM -lt 0 ]]; then # '-lt' means 'less than'.
  echo "$NUM is a negative number."
else
  echo "$NUM is zero."
fi

# --- Example 4: String comparison ---
# Compare two strings.
NAME="John"
if [[ "$NAME" == "John" ]]; then # '==' for string equality
  echo "Hello, John!"
elif [[ "$NAME" != "Jane" ]]; then # '!=' for string inequality
  echo "You are not Jane."
fi

# Check if a string is empty or not
EMPTY_STRING=""
if [[ -z "$EMPTY_STRING" ]]; then # '-z' checks if string is empty (zero length)
  echo "The string is empty."
fi

NON_EMPTY_STRING="Some text"
if [[ -n "$NON_EMPTY_STRING" ]]; then # '-n' checks if string is not empty
  echo "The string is not empty."
fi

# --- Example 5: File existence checks ---
FILE_PATH="./01_hello_world.sh"
NON_EXISTENT_FILE="./non_existent_file.txt"

if [[ -f "$FILE_PATH" ]]; then # '-f' checks if it's a regular file
  echo "$FILE_PATH is a regular file."
fi

if [[ -d "./" ]]; then # '-d' checks if it's a directory
  echo "./ is a directory."
fi

if [[ ! -e "$NON_EXISTENT_FILE" ]]; then # '-e' checks if file/directory exists
  echo "$NON_EXISTENT_FILE does not exist."
fi