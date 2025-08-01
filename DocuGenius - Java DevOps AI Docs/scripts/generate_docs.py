import os
import openai
from dotenv import load_dotenv

# Load environment variables from .env file
load_dotenv()

# --- Configuration ---
# Set your OpenAI API key from environment variables
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
if not OPENAI_API_KEY:
    raise ValueError("OPENAI_API_KEY environment variable not set. "
                     "Please create a .env file with your key or set it directly.")

# Initialize OpenAI client
# For older versions of the openai library, you might use:
# openai.api_key = OPENAI_API_KEY
# For newer versions (1.x.x and above):
client = openai.OpenAI(api_key=OPENAI_API_KEY)

# Define paths to source files and output directory
PROJECT_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
JAVA_APP_PATH = os.path.join(PROJECT_ROOT, "src", "main", "java", "com", "docugenius", "demo")
PIPELINE_SCRIPT_PATH = os.path.join(PROJECT_ROOT, "scripts", "run_pipeline.sh")
POM_XML_PATH = os.path.join(PROJECT_ROOT, "pom.xml")
PRODUCT_SERVICE_JAVA = os.path.join(JAVA_APP_PATH, "service", "ProductService.java")
APPLICATION_PROPERTIES_PATH = os.path.join(PROJECT_ROOT, "src", "main", "resources", "application.properties")

OUTPUT_DIR = os.path.join(PROJECT_ROOT, "docs")
PIPELINE_DOCS_OUTPUT_PATH = os.path.join(OUTPUT_DIR, "pipeline_docs.md")

# Ensure the output directory exists
os.makedirs(OUTPUT_DIR, exist_ok=True)

def read_file_content(filepath):
    """Reads the content of a file and returns it as a string."""
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            return f.read()
    except FileNotFoundError:
        print(f"Error: File not found at {filepath}")
        return None
    except Exception as e:
        print(f"Error reading {filepath}: {e}")
        return None

def generate_documentation(prompt, model="gpt-4", max_tokens=1000, temperature=0.7):
    """
    Sends a prompt to the OpenAI API and returns the generated text.
    Uses the chat completion endpoint for more conversational and powerful models.
    """
    print(f"Sending prompt to OpenAI model '{model}'...")
    try:
        response = client.chat.completions.create(
            model=model,
            messages=[
                {"role": "system", "content": "You are a highly experienced technical writer and software architect. "
                                              "Your task is to generate clear, concise, and accurate documentation "
                                              "from provided code and configuration snippets."},
                {"role": "user", "content": prompt}
            ],
            max_tokens=max_tokens,
            temperature=temperature,
        )
        # Extract the content from the response
        return response.choices[0].message.content.strip()
    except openai.APIError as e:
        print(f"OpenAI API Error: {e}")
        print(f"Status Code: {e.status_code}")
        print(f"Response: {e.response}")
        return f"ERROR: Failed to generate documentation from OpenAI API. Details: {e}"
    except Exception as e:
        print(f"An unexpected error occurred: {e}")
        return f"ERROR: An unexpected error occurred during API call. Details: {e}"

def generate_pipeline_documentation():
    """
    Generates documentation for the CI/CD pipeline and its interaction with the Java app.
    """
    print("\n--- Generating Pipeline Documentation ---")

    # Read relevant file contents
    pipeline_script_content = read_file_content(PIPELINE_SCRIPT_PATH)
    pom_xml_content = read_file_content(POM_XML_PATH)
    product_service_content = read_file_content(PRODUCT_SERVICE_JAVA)
    app_properties_content = read_file_content(APPLICATION_PROPERTIES_PATH)

    if not all([pipeline_script_content, pom_xml_content, product_service_content, app_properties_content]):
        print("Could not read all necessary files for pipeline documentation. Aborting.")
        return

    # Construct the prompt for pipeline documentation
    prompt = f"""
    Analyze the following CI/CD pipeline script, Maven configuration, Java service code, and application properties.
    Generate a comprehensive Markdown document that explains the purpose, stages, and functionalities of this DevOps pipeline,
    highlighting how automated documentation generation is integrated.

    --- CI/CD Pipeline Script (scripts/run_pipeline.sh) ---
    {pipeline_script_content}

    --- Maven Configuration (pom.xml) ---
    {pom_xml_content}

    --- Java Product Service Code (src/main/java/com/docugenius/demo/service/ProductService.java) ---
    {product_service_content}

    --- Application Properties (src/main/resources/application.properties) ---
    {app_properties_content}

    Focus on these aspects in the generated documentation:
    1.  **Pipeline Overview:** What is the overall goal of this pipeline?
    2.  **Stages:** Describe each logical stage (e.g., Build, Test, Documentation Generation, Deployment - even if simplified).
    3.  **Key Tools/Technologies:** Mention Maven, Spring Boot, Python, GenAI.
    4.  **Automated Documentation Step:** Explain in detail how the `generate_docs.py` script works, what it analyzes, and what it produces.
    5.  **Benefits:** Briefly discuss the advantages of this automated approach.
    6.  **Usage/Execution:** How is this pipeline typically run?
    """

    generated_text = generate_documentation(prompt, model="gpt-4-turbo") # Or gpt-3.5-turbo if you prefer

    if generated_text:
        with open(PIPELINE_DOCS_OUTPUT_PATH, 'w', encoding='utf-8') as f:
            f.write(generated_text)
        print(f"Pipeline documentation generated and saved to: {PIPELINE_DOCS_OUTPUT_PATH}")
    else:
        print("Failed to generate pipeline documentation.")

if __name__ == "__main__":
    print("--- Starting Documentation Generation Process ---")
    generate_pipeline_documentation()
    print("--- Documentation Generation Process Finished ---")