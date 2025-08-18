
# --- Introduction to Loops ---
print("--- Python Loop Basics ---")
print("This script demonstrates 'for' and 'while' loops in various scenarios.\n")

# --- 1. For Loops ---
print("--- 1. For Loops ---")

# 1.1 Iterating over a list
print("\n1.1 Iterating over a list:")
fruits = ["apple", "banana", "cherry"]
for fruit in fruits:
    print(f"  Current fruit: {fruit}")

# 1.2 Iterating over a range of numbers
print("\n1.2 Iterating over a range (0 to 4):")
# range(5) generates numbers from 0 up to (but not including) 5
for i in range(5):
    print(f"  Number: {i}")

print("\n1.3 Iterating over a range (2 to 7):")
# range(start, stop) generates numbers from start up to (but not including) stop
for j in range(2, 8):
    print(f"  Number (start, stop): {j}")

print("\n1.4 Iterating over a range with a step (0 to 10, step 2):")
# range(start, stop, step) generates numbers from start up to (not including) stop, with the given step
for k in range(0, 11, 2):
    print(f"  Even number: {k}")

# 1.5 Iterating over a string
print("\n1.5 Iterating over a string:")
word = "Python"
for char in word:
    print(f"  Character: {char}")

# 1.6 Iterating over a dictionary
print("\n1.6 Iterating over a dictionary:")
student = {"name": "Alice", "age": 25, "major": "CS"}

# Iterating over keys (default)
print("  Keys:")
for key in student: # or student.keys()
    print(f"    Key: {key}")

# Iterating over values
print("  Values:")
for value in student.values():
    print(f"    Value: {value}")

# Iterating over key-value pairs (items)
print("  Key-Value Pairs:")
for key, value in student.items():
    print(f"    {key}: {value}")

# 1.7 Using enumerate for index and value
print("\n1.7 Using enumerate (for index and value):")
colors = ["red", "green", "blue", "yellow"]
for index, color in enumerate(colors):
    print(f"  Index {index}: {color}")

# 1.8 Using zip for parallel iteration
print("\n1.8 Using zip (for parallel iteration):")
names = ["Alice", "Bob", "Charlie"]
ages = [30, 24, 35]
for name, age in zip(names, ages):
    print(f"  {name} is {age} years old.")

# 1.9 For loop with an 'else' block
# The 'else' block executes if the loop completes without encountering a 'break' statement.
print("\n1.9 For loop with 'else' block:")
for num in range(3):
    print(f"  Processing number {num}")
else:
    print("  For loop completed successfully (no 'break' encountered).")

print("\n1.9.1 For loop with 'else' and 'break':")
for num in range(5):
    if num == 3:
        print(f"  Breaking loop at {num}")
        break # This prevents the 'else' block from executing
    print(f"  Processing number {num}")
else:
    print("  This line will NOT be printed because of 'break'.")


# --- 2. While Loops ---
print("\n--- 2. While Loops ---")

# 2.1 Basic while loop
print("\n2.1 Basic while loop (countdown from 5):")
count = 5
while count > 0:
    print(f"  Countdown: {count}")
    count -= 1 # Decrement the counter
print("  Lift-off!")

# 2.2 Using 'break' to exit a while loop
print("\n2.2 Using 'break' in a while loop (guessing game):")
secret_number = 7
guess = 0
while True: # Infinite loop until 'break' is encountered
    try:
        guess = int(input("  Guess the secret number (between 1 and 10): "))
        if guess == secret_number:
            print("  Congratulations! You guessed it!")
            break # Exit the loop
        elif guess < secret_number:
            print("  Too low! Try again.")
        else:
            print("  Too high! Try again.")
    except ValueError:
        print("  Invalid input. Please enter a number.")

# 2.3 Using 'continue' to skip an iteration
print("\n2.3 Using 'continue' in a while loop (printing odd numbers):")
num_to_check = 0
while num_to_check < 10:
    num_to_check += 1
    if num_to_check % 2 == 0: # If number is even
        continue # Skip the rest of the current iteration and go to the next loop cycle
    print(f"  Odd number: {num_to_check}")

# 2.4 While loop with an 'else' block
# The 'else' block executes if the loop condition becomes false (without 'break').
print("\n2.4 While loop with 'else' block:")
x = 0
while x < 3:
    print(f"  Current value of x: {x}")
    x += 1
else:
    print("  While loop finished because condition became false (x is no longer < 3).")

print("\n2.4.1 While loop with 'else' and 'break':")
y = 0
while y < 5:
    if y == 2:
        print(f"  Breaking loop at y = {y}")
        break # This prevents the 'else' block from executing
    print(f"  Current value of y: {y}")
    y += 1
else:
    print("  This line will NOT be printed because of 'break'.")


# --- 3. File Iteration with For Loop ---
print("\n--- 3. File Iteration with For Loop ---")
print("\n3.1 Reading lines from 'data/numbers.txt':")
try:
    # 'with' statement ensures the file is properly closed after its block finishes
    with open("data/numbers.txt", "r") as file:
        for line_num, line in enumerate(file, 1): # Start enumeration from 1 for line numbers
            print(f"  Line {line_num}: {line.strip()}") # .strip() removes leading/trailing whitespace, including newline
except FileNotFoundError:
    print("  Error: 'data/numbers.txt' not found. Please ensure the file exists in the 'data' directory.")
except Exception as e:
    print(f"  An error occurred: {e}")

print("\n--- End of Demo ---")