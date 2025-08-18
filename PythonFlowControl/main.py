
# A simple script to demonstrate Python's if, elif, and else statements,
# along with various comparison and logical operators.

print("--- Python Flow Control: Decision Making Demo ---")
print("This script will guide you through examples of conditional logic.")

# --- Example 1: Basic 'if' statement ---
# The 'if' statement executes a block of code only if its condition is True.
print("\n--- Example 1: Basic 'if' statement ---")
temperature = 28 # An example variable
if temperature > 25: # Condition: Is temperature greater than 25?
    print(f"It's {temperature}°C. It's a hot day!") # This line executes if the condition is True
# This line always executes, regardless of the 'if' condition, because it's outside the 'if' block.
print("The weather observation for this example is complete.")


# --- Example 2: 'if-else' statement ---
# The 'if-else' statement provides two execution paths:
# one if the condition is True, and another if it's False.
print("\n--- Example 2: 'if-else' statement ---")
user_age_str = input("Enter your age: ") # Get user input for age
try:
    user_age = int(user_age_str) # Convert input to an integer
    if user_age >= 18: # Condition: Is age 18 or greater?
        print("You are an adult.") # Executed if True
    else: # If the 'if' condition is False, the 'else' block is executed.
        print("You are a minor.") # Executed if False
except ValueError: # Handle cases where input is not a valid number
    print("Invalid age entered. Please enter a number.")


# --- Example 3: 'if-elif-else' statement ---
# The 'if-elif-else' statement allows checking multiple conditions sequentially.
# Only the block of the first 'True' condition is executed. If none are True,
# the 'else' block (if present) is executed.
print("\n--- Example 3: 'if-elif-else' (Grading System) ---")
score_str = input("Enter your test score (0-100): ") # Get user input for score
try:
    score = int(score_str) # Convert input to integer
    if score >= 90: # First condition: Is score 90 or higher?
        print("Grade: A (Excellent work!)")
    elif score >= 80: # Second condition: Is score 80 or higher? (Only checked if first is False)
        print("Grade: B (Very good!)")
    elif score >= 70: # Third condition: Is score 70 or higher? (Only checked if previous are False)
        print("Grade: C (Good effort.)")
    elif score >= 60: # Fourth condition: Is score 60 or higher?
        print("Grade: D (Pass.)")
    else: # If none of the above 'if' or 'elif' conditions are True
        print("Grade: F (Needs improvement.)")
except ValueError:
    print("Invalid score entered. Please enter a number.")


# --- Example 4: Using various Comparison Operators ---
# Comparison operators evaluate to a Boolean value (True or False).
# == (equal to)
# != (not equal to)
# < (less than)
# > (greater than)
# <= (less than or equal to)
# >= (greater than or equal to)
print("\n--- Example 4: Comparison Operators ---")
num1_str = input("Enter the first number: ")
num2_str = input("Enter the second number: ")

try:
    num1 = float(num1_str) # Convert input to a float for numerical comparison
    num2 = float(num2_str)

    print(f"Comparing {num1} and {num2}:")
    if num1 == num2: # Checks if num1 is exactly equal to num2
        print(f"  {num1} == {num2} : {num1 == num2}")
    if num1 != num2: # Checks if num1 is not equal to num2
        print(f"  {num1} != {num2} : {num1 != num2}")
    if num1 < num2: # Checks if num1 is strictly less than num2
        print(f"  {num1} < {num2} : {num1 < num2}")
    if num1 > num2: # Checks if num1 is strictly greater than num2
        print(f"  {num1} > {num2} : {num1 > num2}")
    if num1 <= num2: # Checks if num1 is less than or equal to num2
        print(f"  {num1} <= {num2} : {num1 <= num2}")
    if num1 >= num2: # Checks if num1 is greater than or equal to num2
        print(f"  {num1} >= {num2} : {num1 >= num2}")

except ValueError:
    print("Invalid numbers entered. Please enter numeric values.")


# --- Example 5: Combining conditions with Logical Operators ---
# Logical operators ('and', 'or', 'not') allow building more complex conditions
# by combining simpler Boolean expressions.
# 'and': True if BOTH conditions are True.
# 'or': True if AT LEAST ONE condition is True.
# 'not': Inverts the truth value of a condition (True becomes False, False becomes True).
print("\n--- Example 5: Logical Operators ('and', 'or', 'not') ---")
is_sunny = True
is_warm = False
has_hat = True
money_in_wallet = 35

print(f"Current conditions: Sunny={is_sunny}, Warm={is_warm}, Has Hat={has_hat}, Money={money_in_wallet}")

# Using 'and'
if is_sunny and is_warm: # This condition (True and False) evaluates to False
    print("It's sunny AND warm today. Perfect beach weather!")
else:
    print("It's not both sunny AND warm.")

# Using 'or'
if is_sunny or is_warm: # This condition (True or False) evaluates to True
    print("It's either sunny OR warm (or both). Good for outdoor activities!")
else:
    print("It's neither sunny nor warm.")

# Using 'not'
if not has_hat: # This condition (not True) evaluates to False
    print("You might need a hat if it's sunny!")
else:
    print("You have a hat, good for sun protection!")

# More complex condition combining multiple operators
if (is_sunny and money_in_wallet >= 30) or (is_warm and not has_hat):
    print("Consider a nice outdoor treat or a new hat!")
else:
    print("Staying indoors seems fine for now.")


print("\n--- End of Demo ---")