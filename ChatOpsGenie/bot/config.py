import os
from dotenv import load_dotenv

# Load environment variables from a .env file if it exists
load_dotenv()

# --- Slack Configuration ---
# Your Slack Bot Token (starts with 'xoxb-'). Get this from your Slack App's 'OAuth & Permissions' page.
SLACK_BOT_TOKEN = os.getenv("SLACK_BOT_TOKEN")

# Your Slack App Token (starts with 'xapp-'). Only needed if using Socket Mode.
# Get this from your Slack App's 'Basic Information' -> 'App-Level Tokens'.
SLACK_APP_TOKEN = os.getenv("SLACK_APP_TOKEN")

# The Slack channel ID where the bot will post deployment notifications.
# You can get this by right-clicking on a channel in Slack and selecting 'Copy link'.
# The ID is the part after the last '/' in the URL (e.g., C0123ABCD).
SLACK_NOTIFICATIONS_CHANNEL_ID = os.getenv("SLACK_NOTIFICATIONS_CHANNEL_ID", "YOUR_DEFAULT_CHANNEL_ID_HERE") 
# IMPORTANT: Replace YOUR_DEFAULT_CHANNEL_ID_HERE with a real channel ID or set the env var.

# --- OpenAI Configuration ---
# Your OpenAI API Key. Get this from the OpenAI platform website.
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")

# The OpenAI model to use for generating responses or interpreting commands.
# 'gpt-4o', 'gpt-4', 'gpt-3.5-turbo' are common choices.
OPENAI_MODEL = os.getenv("OPENAI_MODEL", "gpt-3.5-turbo")

# --- Deployment Configuration ---
# Path to the deployment script relative to the bot's working directory.
DEPLOY_SCRIPT_PATH = "../scripts/deploy.sh"

# Last deployment status (for "status" command simulation)
last_deployment_status = "No deployment initiated yet."
last_deployment_info = {}

# Prompt for the LLM to interpret user commands
LLM_PROMPT_TEMPLATE = """
You are ChatOpsGenie, a helpful DevOps bot. Your task is to interpret user commands
related to Java application deployments and generate a structured JSON output.
The available actions are:

1.  "deploy": To deploy the Java application. Requires 'version' (e.g., "1.0.0") and 'environment' (e.g., "production", "staging", "development").
2.  "status": To check the status of the last deployment.
3.  "rollback": To trigger a rollback.
4.  "help": To list available commands.

If the user asks for a 'deploy' action but omits version or environment, ask for clarification.
If the command is not recognized, respond with "unrecognized_command".

Examples:
User: deploy java app to production version 1.0.0
JSON: {{"action": "deploy", "app": "java-app", "version": "1.0.0", "environment": "production"}}

User: deploy my app v2.0
JSON: {{"action": "deploy", "app": "java-app", "version": "2.0", "environment": "unknown_ask_clarification"}}

User: what is the status of my latest deployment?
JSON: {{"action": "status"}}

User: rollback the last deployment
JSON: {{"action": "rollback"}}

User: show me available commands
JSON: {{"action": "help"}}

User: tell me a joke
JSON: {{"action": "unrecognized_command"}}

User: {user_input}
JSON:
"""