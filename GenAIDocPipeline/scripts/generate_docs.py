import os
import openai # Using OpenAI as an example. For OCI AI, you'd use 'oci' SDK and specific service clients.
from dotenv import load_dotenv

# Load environment variables from .env file
load_dotenv()

# --- Configuration ---
# Set up OpenAI API key from environment variables
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
if not OPENAI_API_KEY:
    print("Warning: OPENAI_API_KEY not found. Documentation generation will be skipped or fail.")
    # In a real scenario, you might exit or raise an error.
    # For this demo, we'll proceed but the API call will fail if key is missing.

# Initialize OpenAI client if API key is available
client = openai.OpenAI(api_key=OPENAI_API_KEY) if OPENAI_API_KEY else None

# Define paths relative to the project root
PROJECT_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
JAVA_APP_PATH = os.path.join(PROJECT_ROOT, "src", "main", "java", "com", "genaidoc", "pipeline", "App.java")
JAVA_CONFIG_PATH = os.path.join(PROJECT_ROOT, "src", "main", "java", "com", "genaidoc", "pipeline", "config", "ApplicationConfig.java")
JENKINSFILE_PATH = os.path.join(PROJECT_ROOT, "Jenkinsfile")
OUTPUT_DOCS_DIR = os.path.join(PROJECT_ROOT, "docs")
PIPELINE_README_PATH = os.path.join(OUTPUT_DOCS_DIR, "PIPELINE_README.md")

# Ensure the output directory exists
os.makedirs(OUTPUT_DOCS_DIR, exist_ok=True)

# --- Helper Functions ---

def read_file_content(file_path):
    """Reads the content of a file."""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            return f.read()
    except FileNotFoundError:
        print(f"Error: File not found at {file_path}")
        return None
    except Exception as e:
        print(f"Error reading file {file_path}: {e}")
        return None

def call_genai_api(prompt, model_name="gpt-3.5-turbo", max_tokens=500):
    """
    Calls the GenAI API (e.g., OpenAI) with a given prompt.
    For OCI AI, you would replace this with calls to oci.ai_language.AIServiceLanguageClient
    or oci.generative_ai.GenerativeAiClient.
    """
    if not client:
        print("GenAI client not initialized. Skipping API call.")
        return "GENAI_CLIENT_NOT_CONFIGURED"

    try:
        print(f"Calling GenAI model '{model_name}' with prompt sample: {prompt[:100]}...")
        response = client.chat.completions.create(
            model=model_name,
            messages=[
                {"role": "system", "content": "You are a helpful assistant for software documentation."},
                {"role": "user", "content": prompt}
            ],
            max_tokens=max_tokens,
            temperature=0.7 # Controls randomness: lower for more focused, higher for more creative
        )
        return response.choices[0].message.content.strip()
    except openai.APIError as e:
        print(f"OpenAI API error: {e}")
        return f"ERROR_GENERATING_DOCS: {e}"
    except Exception as e:
        print(f"An unexpected error occurred during GenAI API call: {e}")
        return f"ERROR_GENERATING_DOCS: {e}"

def generate_code_documentation(file_path, doc_type="class"):
    """
    Generates documentation for a given Java code file using GenAI.
    'doc_type' can be 'class', 'configuration', etc., to tailor the prompt.
    """
    file_content = read_file_content(file_path)
    if not file_content:
        return f"<!-- Documentation for {os.path.basename(file_path)} could not be generated due to file read error. -->"

    prompt = f"""
    Analyze the following Java {doc_type} file and generate a concise technical summary.
    Focus on its purpose, key methods/properties, and how it contributes to the application.
    Format the output as a Markdown section suitable for a README, including a suitable heading.

    File: {os.path.basename(file_path)}
    Content: