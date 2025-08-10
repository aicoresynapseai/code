# Use a minimal Go base image
FROM golang:1.22-alpine AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy go.mod and go.sum and download dependencies
COPY go.mod go.sum ./
RUN go mod download

# Copy the source code
COPY main.go .

# Build the application
# CGO_ENABLED=0 is important for creating statically linked binaries
# This ensures the binary does not depend on system libraries that might not be present in the final scratch image.
RUN CGO_ENABLED=0 go build -o /sample-microservice

# Use a scratch image for the final, minimal image
FROM scratch

# Copy the ca-certificates for HTTPS communication (e.g., for pulling images)
# and also for general DNS resolution if needed in the Go application (not strictly necessary for this example,
# but good practice for production images).
COPY --from=builder /etc/ssl/certs/ca-certificates.crt /etc/ssl/certs/

# Copy the built binary from the builder stage
COPY --from=builder /sample-microservice /sample-microservice

# Expose the port the application listens on
EXPOSE 8080

# Define the command to run the application
ENTRYPOINT ["/sample-microservice"]