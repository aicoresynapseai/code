# Use an official Python runtime as a parent image.
# This ensures consistency and provides a reliable base for our application.
FROM python:3.9-slim-buster

# Set the working directory in the container.
# All subsequent commands will be executed relative to this directory.
WORKDIR /app

# Copy the requirements file into the container at /app.
# This step is done early to leverage Docker's build cache.
# If requirements.txt doesn't change, this layer won't be rebuilt.
COPY requirements.txt .

# Install any needed packages specified in requirements.txt.
# This command runs `pip install` to get all Python dependencies.
RUN pip install --no-cache-dir -r requirements.txt

# Copy the entire application code into the container at /app.
# This copies the Flask application, tests, etc.
COPY app/ .

# Expose port 5000 so that the container can receive traffic on this port.
# This is the default port our Flask application runs on.
EXPOSE 5000

# Define the command to run the application when the container starts.
# `python main.py` starts the Flask development server.
# In a production environment, you'd typically use a WSGI server like Gunicorn:
# CMD ["gunicorn", "-w", "4", "-b", "0.0.0.0:5000", "main:app"]
CMD ["python", "main.py"]

# Add metadata about the image (optional but good practice)
LABEL author="Your Name"
LABEL description="DevOpsCloudApp Flask application"
LABEL version="1.0.0"