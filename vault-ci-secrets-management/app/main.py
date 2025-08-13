import os

def run_application():
    """
    A placeholder application function that demonstrates
    reading sensitive information from environment variables.
    In a real application, these would be used to connect to a database,
    API, or other service.
    """
    print("--- Running Application ---")

    db_username = os.getenv("DB_USERNAME")
    db_password = os.getenv("DB_PASSWORD")

    if db_username and db_password:
        print(f"Successfully retrieved DB_USERNAME: {db_username}")
        # In a real app, you'd use this to establish a connection
        print(f"Successfully retrieved DB_PASSWORD: {'*' * len(db_password)}") # Mask sensitive output
        print("Application is ready to connect to the database securely.")
    else:
        print("Error: Database credentials not found in environment variables.")
        print("This indicates a failure in secret retrieval or injection.")

    print("--- Application Finished ---")

if __name__ == "__main__":
    run_application()