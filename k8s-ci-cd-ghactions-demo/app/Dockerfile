# Use an official Node.js runtime as the base image
FROM node:18-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy package.json and package-lock.json (if present) to the working directory
# This allows caching dependencies separately from application code
COPY package*.json ./

# Install application dependencies
# The --production flag ensures only production dependencies are installed
RUN npm install --production

# Copy the rest of the application code to the working directory
COPY . .

# Expose the port on which the Node.js application will run
EXPOSE 3000

# Define the command to run the application when the container starts
CMD [ "npm", "start" ]