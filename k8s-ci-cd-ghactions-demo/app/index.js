const express = require('express');
const app = express();
const port = 3000; // Define the port for the application

// Define a simple route for the root path
app.get('/', (req, res) => {
  const visitorIP = req.headers['x-forwarded-for'] || req.socket.remoteAddress; // Get client IP address
  console.log(`Request received from IP: ${visitorIP}`); // Log the incoming request
  res.send(`<h1>Hello from Kubernetes!</h1>
            <p>This is a simple Node.js app deployed via GitHub Actions CI/CD.</p>
            <p>Your IP: ${visitorIP}</p>
            <p>Container Hostname: ${process.env.HOSTNAME}</p>
            <p>Application Version: 1.0.0</p>
            <p>Current Time: ${new Date().toLocaleString()}</p>`); // Send a dynamic response
});

// Start the server and listen on the defined port
app.listen(port, () => {
  console.log(`App listening at http://localhost:${port}`); // Log server startup
});

// Graceful shutdown handling (optional, but good practice)
process.on('SIGTERM', () => {
  console.log('SIGTERM signal received: closing HTTP server');
  server.close(() => {
    console.log('HTTP server closed');
  });
});