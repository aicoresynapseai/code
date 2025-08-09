import pytest
from app.main import app as flask_app # Import the Flask app instance

@pytest.fixture
def client():
    """
    Provides a test client for the Flask application.
    This client allows simulating requests to the application.
    """
    with flask_app.test_client() as client:
        # Clear the data_store before each test to ensure isolation
        flask_app.data_store['items'] = []
        yield client

def test_home_page(client):
    """
    Test the root endpoint of the application.
    Verifies that it returns the expected greeting.
    """
    response = client.get('/')
    assert response.status_code == 200
    assert b"Welcome to DevOpsCloudApp!" in response.data

def test_add_item_success(client):
    """
    Test adding an item successfully via POST request.
    Verifies the status code and the response message.
    """
    test_item = {"item": "Test Item 1"}
    response = client.post('/api/v1/items', json=test_item)
    assert response.status_code == 201
    assert b"Item added successfully" in response.data
    assert flask_app.data_store['items'] == ["Test Item 1"]

def test_add_item_invalid_input(client):
    """
    Test adding an item with invalid input (missing 'item' key).
    Verifies that the application returns a 400 error.
    """
    invalid_item = {"name": "Test Item 2"} # Missing 'item' key
    response = client.post('/api/v1/items', json=invalid_item)
    assert response.status_code == 400
    assert b"Invalid input, 'item' key required" in response.data
    assert not flask_app.data_store['items'] # Ensure no item was added

def test_get_items_empty(client):
    """
    Test retrieving items when the data store is empty.
    Verifies that an empty list is returned.
    """
    response = client.get('/api/v1/items')
    assert response.status_code == 200
    assert response.json == []

def test_get_items_after_adding(client):
    """
    Test retrieving items after one or more items have been added.
    Verifies that the correct items are returned.
    """
    client.post('/api/v1/items', json={"item": "First Item"})
    client.post('/api/v1/items', json={"item": "Second Item"})
    response = client.get('/api/v1/items')
    assert response.status_code == 200
    assert response.json == ["First Item", "Second Item"]

def test_health_check(client):
    """
    Test the health check endpoint.
    Verifies that it returns a healthy status.
    """
    response = client.get('/api/v1/health')
    assert response.status_code == 200
    assert response.json == {"status": "healthy", "service": "DevOpsCloudApp"}