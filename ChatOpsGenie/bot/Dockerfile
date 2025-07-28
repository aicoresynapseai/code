# Use a lightweight Python base image
FROM python:3.9-slim-buster

# Set the working directory inside the container
WORKDIR /app

# Copy the requirements file into the container
COPY requirements.txt .

# Install Python dependencies from requirements.txt
# --no-cache-dir: Don't store downloaded packages in the cache, reducing image size
# -U: Upgrade all specified packages to the newest available version
RUN pip install --no-cache-dir -U -r requirements.txt

# Copy the bot application files into the container
COPY . .

# Expose the port for the Slack Bolt app (if using HTTP mode, for Socket Mode this is not strictly needed)
EXPOSE 3000

# Define the command to run the application
# Use 'python -u' for unbuffered output, which is good for logging in containers.
CMD ["python", "-u", "app.py"]