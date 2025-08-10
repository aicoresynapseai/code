package main

import (
	"log"
	"net/http"
	"strconv"
	"time"

	"github.com/prometheus/client_golang/prometheus"
	"github.com/prometheus/client_golang/prometheus/promauto" // Use promauto for convenience
	"github.com/prometheus/client_golang/prometheus/promhttp"
)

// Define Prometheus metrics. Using promauto for easier registration.
var (
	// httpRequestsTotal is a counter that records the total number of HTTP requests,
	// categorized by the request path.
	httpRequestsTotal = promauto.NewCounterVec(
		prometheus.CounterOpts{
			Name: "http_requests_total",
			Help: "Total number of HTTP requests.",
		},
		[]string{"path"},
	)

	// lastRequestTimestamp is a gauge that records the Unix timestamp of the last request.
	lastRequestTimestamp = promauto.NewGauge(
		prometheus.GaugeOpts{
			Name: "last_request_timestamp_seconds",
			Help: "Unix timestamp of the last HTTP request.",
		},
	)

	// currentGoroutines is a gauge that can be updated manually to show the number of goroutines.
	// This is just an example; usually, client libraries might provide this automatically (e.g., Go runtime metrics).
	currentGoroutines = promauto.NewGauge(
		prometheus.GaugeOpts{
			Name: "current_goroutines",
			Help: "Number of active goroutines.",
		},
	)
)

func main() {
	// Expose Prometheus metrics on the /metrics endpoint.
	http.Handle("/metrics", promhttp.Handler())

	// Define a handler for the root path.
	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		// Increment the counter for the requested path.
		httpRequestsTotal.WithLabelValues(r.URL.Path).Inc()
		// Set the timestamp of the last request.
		lastRequestTimestamp.Set(float64(time.Now().Unix()))

		// Simulate some work and update a custom gauge.
		time.Sleep(time.Duration(r.ContentLength % 100) * time.Millisecond) // Simulate variable work
		// For demonstration, let's just set a random number for goroutines
		// In a real app, you'd integrate with actual goroutine count or similar.
		currentGoroutines.Set(float64(len(r.URL.Path) + 10)) // Arbitrary value for demo

		w.WriteHeader(http.StatusOK)
		w.Write([]byte("Hello from Sample Microservice! Request Path: " + r.URL.Path))
	})

	// Define a health check endpoint.
	http.HandleFunc("/health", func(w http.ResponseWriter, r *http.Request) {
		w.WriteHeader(http.StatusOK)
		w.Write([]byte("OK"))
	})

	// Start the HTTP server on port 8080.
	port := 8080
	log.Printf("Starting server on :%d", port)
	log.Fatal(http.ListenAndServe(":"+strconv.Itoa(port), nil))
}