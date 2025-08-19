
# Import all functions from the calculator module
from calculator import add, subtract, multiply, greet, calculate_average, display_info, is_even

def run_demonstrations():
    """
    This function orchestrates the demonstration of various functions
    defined in the calculator.py module.
    """

    print("--- Function Demonstrations ---")

    # 1. Demonstrating `add` function: basic parameters and return
    result_add = add(10, 5)
    print(f"\n1. add(10, 5) = {result_add}")

    # 2. Demonstrating `subtract` function: another basic example
    result_subtract = subtract(100, 45)
    print(f"2. subtract(100, 45) = {result_subtract}")

    # 3. Demonstrating `multiply` function: with and without default parameter
    result_multiply_default = multiply(7) # Uses default y=2
    print(f"\n3. multiply(7) (using default y=2) = {result_multiply_default}")
    result_multiply_custom = multiply(7, 3) # Overrides default y
    print(f"   multiply(7, 3) (custom y=3) = {result_multiply_custom}")

    # 4. Demonstrating `greet` function: with and without default parameters
    greeting_default = greet() # Uses default name="Guest", message="Hello"
    print(f"\n4. greet() (default) = '{greeting_default}'")
    greeting_name = greet("Alice") # Uses default message="Hello"
    print(f"   greet('Alice') (default message) = '{greeting_name}'")
    greeting_full = greet("Bob", "Hi there") # Custom name and message
    print(f"   greet('Bob', 'Hi there') (custom) = '{greeting_full}'")

    # 5. Demonstrating `calculate_average` function: arbitrary arguments (*args)
    avg_two = calculate_average(10, 20)
    print(f"\n5. calculate_average(10, 20) = {avg_two}")
    avg_five = calculate_average(1, 2, 3, 4, 5)
    print(f"   calculate_average(1, 2, 3, 4, 5) = {avg_five}")
    avg_none = calculate_average()
    print(f"   calculate_average() (no numbers) = {avg_none}")

    # 6. Demonstrating `display_info` function: arbitrary keyword arguments (**kwargs)
    print("\n6. display_info(name='John Doe', age=30, city='New York')")
    display_info(name='John Doe', age=30, city='New York')

    print("\n   display_info(product='Laptop', price=1200.50, brand='TechCo', in_stock=True)")
    display_info(product='Laptop', price=1200.50, brand='TechCo', in_stock=True)

    # 7. Demonstrating `is_even` function: boolean return value
    check_even_1 = is_even(4)
    print(f"\n7. is_even(4) = {check_even_1}")
    check_even_2 = is_even(7)
    print(f"   is_even(7) = {check_even_2}")

    print("\n--- End of Demonstrations ---")

# This ensures that run_demonstrations() is called only when the script is executed directly,
# not when it's imported as a module into another script.
if __name__ == "__main__":
    run_demonstrations()