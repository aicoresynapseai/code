from flask import Flask, request, jsonify

# Initialize the Flask application
app = Flask(__name__)

# Simple in-memory data store for demonstration purposes
data_store = {
    "items": []
}

@app.route('/')
def home():
    """
    Root endpoint of the application.
    Returns a simple greeting message.
    """
    return "<h1>Welcome to DevOpsCloudApp!</h1><p>Explore our API at /api/v1/items</p>"

@app.route('/api/v1/items', methods=['GET'])
def get_items():
    """
    Endpoint to retrieve all items from the data store.
    """
    app.logger.info("GET /api/v1/items request received.")
    return jsonify(data_store['items'])

@app.route('/api/v1/items', methods=['POST'])
def add_item():
    """
    Endpoint to add a new item to the data store.
    Expects a JSON payload with an 'item' key.
    """
    new_item = request.json
    if not new_item or 'item' not in new_item:
        app.logger.warning("POST /api/v1/items received invalid payload: %s", new_item)
        return jsonify({"error": "Invalid input, 'item' key required"}), 400

    data_store['items'].append(new_item['item'])
    app.logger.info("Item added: %s", new_item['item'])
    return jsonify({"message": "Item added successfully", "item": new_item['item']}), 201

@app.route('/api/v1/health', methods=['GET'])
def health_check():
    """
    Health check endpoint for monitoring purposes.
    Returns a simple status message.
    """
    return jsonify({"status": "healthy", "service": "DevOpsCloudApp"}), 200

if __name__ == '__main__':
    # Run the Flask app
    # In a production environment, a WSGI server like Gunicorn would be used.
    app.run(debug=True, host='0.0.0.0', port=5000)