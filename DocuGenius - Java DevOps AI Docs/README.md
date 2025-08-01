DocuGenius - Java DevOps AI Docs

This project demonstrates how to integrate Generative AI (GenAI), specifically leveraging models like OpenAI's GPT, into a Java DevOps pipeline to automate the generation and updating of project documentation. This includes core project README files and specific pipeline documentation.

The core idea is to parse source code, configuration files, and pipeline scripts, then send relevant sections to a GenAI model to produce human-readable documentation. This reduces manual effort and ensures documentation stays up-to-date with code changes.

Project Structure:

*   src/main/java: Contains a simple Java Spring Boot application.
*   src/main/resources: Application configuration files.
*   pom.xml: Maven build configuration for the Java application.
*   scripts/: Contains Python scripts for GenAI interaction and a shell script simulating a CI/CD pipeline.
*   docs/: Directory for generated documentation.
*   .env.example: Example environment variable file for API keys.

Prerequisites:

1.  Java Development Kit (JDK) 11 or higher.
2.  Apache Maven.
3.  Python 3.8 or higher.
4.  pip (Python package installer).
5.  An API key for a Generative AI service (e.g., OpenAI API Key).

Setup Instructions:

1.  Clone this repository to your local machine.
2.  Navigate into the project root directory.
3.  Install Python dependencies:
    *   Open your terminal or command prompt.
    *   Ensure you have pip installed.
    *   Run the command: pip install openai python-dotenv
4.  Configure your GenAI API Key:
    *   Create a file named '.env' in the project root directory.
    *   Copy the content from '.env.example' into your new '.env' file.
    *   Replace 'YOUR_OPENAI_API_KEY' with your actual OpenAI API key.

Building and Running the Application:

1.  Build the Java application using Maven:
    *   Open your terminal in the project root.
    *   Run the command: mvn clean install
    *   This will compile the Java code and package it.

2.  Simulate the DevOps Pipeline:
    *   The 'scripts/run_pipeline.sh' script simulates a simplified CI/CD pipeline.
    *   It performs actions like building the Java application, running tests (placeholder), and then triggering the documentation generation step.
    *   To execute the simulated pipeline:
        *   Navigate to the project root in your terminal.
        *   Run the command: sh scripts/run_pipeline.sh

Documentation Generation:

The 'scripts/generate_docs.py' Python script is responsible for interacting with the GenAI model.
It reads:
*   Java source files (e.g., 'src/main/java/com/docugenius/demo/service/ProductService.java')
*   Maven 'pom.xml'
*   The pipeline script ('scripts/run_pipeline.sh')

It then constructs prompts and sends them to the GenAI API. The generated documentation (e.g., 'docs/pipeline_docs.md') is saved into the 'docs/' directory.

After running the pipeline, check the 'docs/' directory for the generated 'pipeline_docs.md' file. This file will contain documentation explaining the CI/CD pipeline and its components, generated dynamically by the AI.

Extending Functionality:

*   **More Document Types:** Extend 'generate_docs.py' to generate user manuals, API documentation from Javadoc, or release notes.
*   **Prompt Engineering:** Experiment with different prompts to get more precise and higher-quality documentation.
*   **Pre-commit Hooks:** Integrate documentation generation into pre-commit hooks to ensure documentation is updated before every commit.
*   **Different AI Models:** Adapt 'generate_docs.py' to use other GenAI providers like OCI AI, Google Gemini, or AWS Bedrock.
*   **Version Control:** Ensure generated documentation is committed to version control to track changes and provide historical context.