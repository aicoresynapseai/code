
print("--- PyDataExplorer: Working with Lists and Dictionaries ---")
print("\n")

# --- Section 1: Working with Lists ---
print("--- Section 1: Lists ---")

# 1.1 Creating a List
# Lists are ordered collections of items. They can contain items of different data types.
my_fruits = ["apple", "banana", "cherry", "date", "elderberry"]
print(f"1.1 Initial List of Fruits: {my_fruits}")
print(f"   Number of fruits in the list: {len(my_fruits)}") # len() returns the number of items in a list.

# 1.2 Accessing Elements
# Elements in a list are accessed by their index, which starts from 0 for the first element.
print(f"1.2 Accessing elements:")
print(f"   First fruit (index 0): {my_fruits[0]}")
print(f"   Third fruit (index 2): {my_fruits[2]}")
# Negative indices access elements from the end of the list. -1 is the last item.
print(f"   Last fruit (index -1): {my_fruits[-1]}")
print(f"   Second to last fruit (index -2): {my_fruits[-2]}")

# 1.3 List Slicing
# Slicing allows you to get a sub-section of a list.
# Syntax: list[start:end] (end index is exclusive)
print(f"1.3 List Slicing:")
print(f"   Fruits from index 1 to 3 (exclusive): {my_fruits[1:4]}") # banana, cherry, date
print(f"   First three fruits: {my_fruits[:3]}") # apple, banana, cherry
print(f"   Fruits from index 2 to the end: {my_fruits[2:]}") # cherry, date, elderberry
print(f"   A copy of the entire list: {my_fruits[:]}")

# 1.4 Modifying Elements
# You can change an element by accessing its index and assigning a new value.
my_fruits[1] = "blueberry"
print(f"1.4 Modified second fruit to 'blueberry': {my_fruits}")

# 1.5 Adding Elements
# append(): Adds an item to the end of the list.
my_fruits.append("fig")
print(f"1.5 Added 'fig' using append(): {my_fruits}")
# insert(): Inserts an item at a specified index.
my_fruits.insert(1, "grape") # Inserts 'grape' at index 1, shifting others to the right.
print(f"   Inserted 'grape' at index 1 using insert(): {my_fruits}")

# 1.6 Removing Elements
# pop(): Removes and returns the item at a specified index (or the last item if no index is given).
removed_fruit = my_fruits.pop() # Removes 'fig'
print(f"1.6 Removed '{removed_fruit}' using pop() (no index): {my_fruits}")
removed_fruit = my_fruits.pop(0) # Removes 'apple' (which was at index 0 after 'grape' was inserted)
print(f"   Removed '{removed_fruit}' using pop(0): {my_fruits}")
# remove(): Removes the first occurrence of a specified value.
if "cherry" in my_fruits: # Good practice to check before removing to avoid ValueError
    my_fruits.remove("cherry")
print(f"   Removed 'cherry' using remove(): {my_fruits}")
# del statement: Can remove an item at a specific index or slice.
del my_fruits[1] # Deletes the element at index 1 (which is 'date' now)
print(f"   Removed item at index 1 using del: {my_fruits}")

# 1.7 Iterating Through a List
# The 'for' loop is commonly used to go through each item in a list.
print(f"1.7 Iterating through the current fruit list:")
for fruit in my_fruits:
    print(f"   - {fruit}")

print("\n")

# --- Section 2: Working with Dictionaries ---
print("--- Section 2: Dictionaries ---")

# 2.1 Creating a Dictionary
# Dictionaries are unordered collections of key-value pairs.
# Each key must be unique and immutable (e.g., strings, numbers, tuples).
# Values can be of any data type.
person_details = {
    "name": "Alice Smith",
    "age": 30,
    "city": "New York",
    "is_student": False,
    "hobbies": ["reading", "hiking", "coding"]
}
print(f"2.1 Initial Dictionary (Person Details): {person_details}")
print(f"   Number of key-value pairs: {len(person_details)}")

# 2.2 Accessing Values
# Values are accessed by their associated keys.
print(f"2.2 Accessing values:")
print(f"   Person's name: {person_details['name']}")
print(f"   Person's age: {person_details['age']}")
# Using .get() method is safer as it returns None or a specified default if the key doesn't exist.
print(f"   Person's city (using .get()): {person_details.get('city')}")
print(f"   Person's country (key not present, using .get() with default): {person_details.get('country', 'Unknown')}")

# 2.3 Adding and Modifying Key-Value Pairs
# To add a new pair or modify an existing one, simply assign a value to a key.
person_details["email"] = "alice.smith@example.com" # Adds a new key-value pair
print(f"2.3 Added 'email': {person_details}")
person_details["age"] = 31 # Modifies the value for the 'age' key
print(f"   Modified 'age': {person_details}")

# 2.4 Removing Key-Value Pairs
# pop(): Removes the item with the specified key and returns its value.
removed_email = person_details.pop("email")
print(f"2.4 Removed 'email' ('{removed_email}') using pop(): {person_details}")
# del statement: Removes the item with the specified key.
del person_details["is_student"]
print(f"   Removed 'is_student' using del: {person_details}")

# 2.5 Iterating Through a Dictionary
# You can iterate over keys, values, or key-value pairs (items).
print(f"2.5 Iterating through the dictionary:")
print("   - Iterating over keys (.keys()):")
for key in person_details.keys():
    print(f"     Key: {key}")

print("   - Iterating over values (.values()):")
for value in person_details.values():
    print(f"     Value: {value}")

print("   - Iterating over key-value pairs (.items()):")
for key, value in person_details.items():
    print(f"     {key}: {value}")

# Check for key existence using 'in' operator
print(f"   Is 'name' a key in the dictionary? {'name' in person_details}")
print(f"   Is 'phone' a key in the dictionary? {'phone' in person_details}")

print("\n")

# --- Section 3: Combining Lists and Dictionaries ---
print("--- Section 3: Combining Lists and Dictionaries ---")

# A common pattern is to have a list of dictionaries, where each dictionary represents a record.
students = [
    {"id": 101, "name": "Bob", "grade": "A"},
    {"id": 102, "name": "Charlie", "grade": "B"},
    {"id": 103, "name": "Diana", "grade": "A-"}
]
print(f"3.1 List of Dictionaries (Students): {students}")

# 3.2 Accessing data in a list of dictionaries
print(f"3.2 Accessing data:")
print(f"   Second student's name: {students[1]['name']}") # Access list item by index, then dictionary value by key

# 3.3 Iterating through a list of dictionaries
print(f"3.3 Iterating through students:")
for student in students:
    print(f"   ID: {student['id']}, Name: {student['name']}, Grade: {student['grade']}")

# 3.4 Adding a new student
new_student = {"id": 104, "name": "Eve", "grade": "C+"}
students.append(new_student)
print(f"3.4 Added new student: {students}")

# 3.5 Updating a student's grade
# Find the student and then update their dictionary entry.
for student in students:
    if student["name"] == "Bob":
        student["grade"] = "A+"
        break # Exit loop once updated
print(f"3.5 Updated Bob's grade: {students}")

print("\n--- End of PyDataExplorer Demo ---")