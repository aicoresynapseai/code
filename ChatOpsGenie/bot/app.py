import os
import subprocess
import json
import logging
from slack_bolt import App
from slack_bolt.adapter.socket_mode import SocketModeHandler
from openai import OpenAI

# Import configurations from config.py
import config

# Set up logging for better visibility of bot's operations
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Initialize the Slack App with your bot token and app token (for Socket Mode)
app = App(token=config.SLACK_BOT_TOKEN)

# Initialize the OpenAI client with your API key
openai_client = OpenAI(api_key=config.OPENAI_API_KEY)

# --- Helper Functions ---

def run_llm_command_parser(user_input):
    """
    Uses OpenAI's LLM to parse natural language commands into a structured JSON.
    """
    try:
        response = openai_client.chat.completions.create(
            model=config.OPENAI_MODEL,
            messages=[
                {"role": "system", "content": config.LLM_PROMPT_TEMPLATE.format(user_input=user_input)},
                {"role": "user", "content": user_input}
            ],
            response_format={"type": "json_object"}, # Request JSON response
            temperature=0.0 # Keep it deterministic for command parsing
        )
        # Parse the JSON string from the LLM's response
        llm_output_json = json.loads(response.choices[0].message.content)
        return llm_output_json
    except Exception as e:
        logger.error(f"Error parsing command with LLM: {e}")
        return {"action": "unrecognized_command", "error": str(e)}

def trigger_deployment_script(app_name, version, environment, user_id, channel_id):
    """
    Simulates triggering a deployment script. In a real scenario, this would
    call a CI/CD pipeline (e.g., Jenkins job, GitHub Actions workflow).
    """
    try:
        # Construct the command to execute the shell script
        # Pass deployment parameters as environment variables or arguments to the script
        command = [
            "/bin/bash", config.DEPLOY_SCRIPT_PATH,
            app_name, version, environment, user_id, channel_id
        ]
        logger.info(f"Executing deployment script: {' '.join(command)}")

        # Start the subprocess in a non-blocking way to avoid freezing the bot
        # Redirect stdout and stderr to allow for real-time output processing if needed
        process = subprocess.Popen(
            command,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True, # Decode stdout/stderr as text
            cwd=os.path.dirname(config.DEPLOY_SCRIPT_PATH) # Set working directory for the script
        )

        # Store process details for status checking
        config.last_deployment_info = {
            "app_name": app_name,
            "version": version,
            "environment": environment,
            "initiated_by": user_id,
            "process_id": process.pid,
            "status": "In Progress",
            "start_time": process.poll() # None if still running
        }
        config.last_deployment_status = f"Deployment of {app_name} v{version} to {environment} initiated by <@{user_id}>. Status: In Progress."

        # Asynchronously read output to prevent deadlock if buffer fills (simplified here)
        stdout, stderr = process.communicate() # This will block until process finishes for simplicity
        
        if process.returncode == 0:
            config.last_deployment_status = f"Deployment of {app_name} v{version} to {environment} by <@{user_id}> SUCCESSFUL! 🎉\nOutput:\n