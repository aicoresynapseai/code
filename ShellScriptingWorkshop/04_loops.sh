#!/bin/bash
#
# ShellScriptingWorkshop/04_loops.sh
# Demonstrates for and while loops in Bash.

# --- For Loop Example 1: Iterating over a range of numbers ---
echo "--- For Loop (Numeric Range) ---"
# Using sequence generation '{START..END}'
for i in {1..5}; do
  echo "Count: $i"
done

# Using 'seq' command (less modern but compatible with older Bash versions)
# for i in $(seq 1 5); do
#   echo "Count (seq): $i"
# done

# --- For Loop Example 2: Iterating over a list of items (strings) ---
echo -e "\n--- For Loop (List of Strings) ---"
FRUITS=("Apple" "Banana" "Cherry" "Date")

for fruit in "${FRUITS[@]}"; do
  echo "I like $fruit."
done

# --- For Loop Example 3: Iterating over command output ---
echo -e "\n--- For Loop (Command Output) ---"
echo "Listing some files/directories in current path:"
for item in *.sh config.conf; do # Iterates over files ending with .sh and config.conf
  if [[ -f "$item" ]]; then
    echo "Found script file: $item"
  elif [[ -d "$item" ]]; then
    echo "Found directory: $item"
  else
    echo "Could not find: $item"
  fi
done

# --- While Loop Example 1: Basic counter ---
echo -e "\n--- While Loop (Counter) ---"
COUNTER=1
while [[ $COUNTER -le 5 ]]; do # '-le' means 'less than or equal to'
  echo "Counter is: $COUNTER"
  COUNTER=$((COUNTER + 1)) # Increment the counter
done

# --- While Loop Example 2: Reading a file line by line ---
echo -e "\n--- While Loop (Reading File) ---"
echo "Line 1" > temp_file.txt
echo "Line 2" >> temp_file.txt
echo "Line 3" >> temp_file.txt

echo "Content of temp_file.txt:"
while IFS= read -r line; do # 'IFS=' prevents word splitting, '-r' prevents backslash interpretation
  echo "File Line: $line"
done < temp_file.txt

# Clean up temporary file
rm temp_file.txt

# --- While Loop Example 3: Loop with 'break' and 'continue' ---
echo -e "\n--- While Loop (Break and Continue) ---"
NUMBER=0
while true; do # Infinite loop
  NUMBER=$((NUMBER + 1))
  if [[ $NUMBER -eq 3 ]]; then
    echo "Skipping number 3 (continue)..."
    continue # Skip the rest of the current iteration and go to the next
  fi

  echo "Current number: $NUMBER"

  if [[ $NUMBER -eq 5 ]]; then
    echo "Breaking loop at number 5..."
    break # Exit the loop entirely
  fi
done