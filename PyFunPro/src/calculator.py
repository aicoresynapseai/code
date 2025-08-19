
def add(a, b):
    """
    Adds two numbers and returns their sum.
    This function demonstrates basic parameters and a return value.
    """
    return a + b

def subtract(num1, num2):
    """
    Subtracts the second number from the first and returns the result.
    Another example of basic arithmetic functions.
    """
    return num1 - num2

def multiply(x, y=2):
    """
    Multiplies two numbers. 'y' has a default parameter, meaning if it's not
    provided during the call, it defaults to 2.
    """
    return x * y

def greet(name="Guest", message="Hello"):
    """
    Greets a person with a customizable message.
    Demonstrates multiple default parameters.
    """
    return f"{message}, {name}!"

def calculate_average(*numbers):
    """
    Calculates the average of an arbitrary number of inputs.
    The '*numbers' syntax allows the function to accept any number of positional arguments,
    which are then collected into a tuple.
    """
    if not numbers:  # Check if the tuple is empty to avoid division by zero
        return 0
    return sum(numbers) / len(numbers)

def display_info(**details):
    """
    Displays information using arbitrary keyword arguments.
    The '**details' syntax allows the function to accept any number of keyword arguments,
    which are then collected into a dictionary.
    """
    print("--- User Info ---")
    for key, value in details.items():
        print(f"{key.replace('_', ' ').title()}: {value}")
    print("-----------------")

def is_even(number):
    """
    Checks if a given number is even.
    Returns a boolean value (True/False).
    """
    return number % 2 == 0