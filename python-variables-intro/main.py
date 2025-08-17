
# --- Part 1: Introduction to Python Syntax and Your First Program ---

# The 'print()' function is used to display output on the console.
# Strings (text) are enclosed in single or double quotes.
print("Hello, Python learners!")
print("Welcome to your first Python program!")
print("Let's explore variables!")

# --- Part 2: Working with Variables of Different Data Types ---

# Variables are containers for storing data values.
# Python is dynamically typed, meaning you don't declare the variable's type explicitly.
# The type is inferred when you assign a value.

# 1. String Data Type (str): Used for sequences of characters (text).
# Strings can be enclosed in single ('...') or double ("...") quotes.
print("\n--- Demonstrating String Variables ---")
student_name = "Alice Smith" # Assigning a string value to 'student_name'
course_name = 'Introduction to Programming' # Another way to define a string

print(f"Student Name: {student_name}") # Using an f-string for easy formatting
print(f"Enrolled Course: {course_name}")

# You can concatenate (join) strings using the '+' operator.
full_greeting = "Hello, " + student_name + "! Welcome to " + course_name + "."
print(full_greeting)


# 2. Integer Data Type (int): Used for whole numbers (positive, negative, or zero).
print("\n--- Demonstrating Integer Variables ---")
student_age = 20
number_of_assignments = 5
total_score = 450

print(f"Age of student: {student_age} years")
print(f"Number of assignments: {number_of_assignments}")
print(f"Total score achieved: {total_score}")

# Basic arithmetic operations can be performed on integers.
assignments_remaining = number_of_assignments - 2 # Subtracting 2 from number_of_assignments
print(f"Assignments remaining: {assignments_remaining}")


# 3. Float Data Type (float): Used for numbers with a decimal point.
print("\n--- Demonstrating Float Variables ---")
average_grade = 88.75
gpa = 3.8
pi_value = 3.14159

print(f"Average grade: {average_grade}")
print(f"GPA: {gpa}")
print(f"Value of Pi: {pi_value}")

# Floats can also be used in arithmetic operations.
half_gpa = gpa / 2 # Division often results in a float
print(f"Half of GPA: {half_gpa}")


# 4. Boolean Data Type (bool): Used for True or False values.
# Booleans are often used in conditional logic.
print("\n--- Demonstrating Boolean Variables ---")
is_enrolled = True # Represents a true condition
has_completed_course = False # Represents a false condition

print(f"Is {student_name} currently enrolled? {is_enrolled}")
print(f"Has {student_name} completed the course? {has_completed_course}")

# Booleans are fundamental for decision-making (e.g., if statements).
if is_enrolled: # This block executes because is_enrolled is True
    print(f"{student_name} is an active student.")
else:
    print(f"{student_name} is not currently active.")


# --- Part 3: Storing User Input and Displaying It Back ---

print("\n--- Interactive Section: Getting User Input ---")

# The 'input()' function pauses the program and waits for the user to type something
# and press Enter. Whatever the user types is returned as a string.

# Store user's name in a variable
user_name = input("Please enter your name: ")

# Store user's favorite hobby in another variable
favorite_hobby = input("What is your favorite hobby? ")

# Display the stored user input back to the user
print(f"\nHello, {user_name}! It's nice to meet you.")
print(f"Your favorite hobby is {favorite_hobby}. That sounds like fun!")

# Even if the user enters numbers, input() returns a string.
# To use it as a number, you would need to convert it (e.g., using int() or float()).
# This is a common next step in learning Python!
# For example:
# age_str = input("How old are you? ")
# user_age = int(age_str) # Converts the string 'age_str' to an integer 'user_age'
# print(f"You will be {user_age + 1} next year!")

print("\n--- Program End ---")
print("You've successfully run your first Python program and used variables!")
print("Experiment by changing values or adding new print statements!")