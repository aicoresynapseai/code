Serverless Java with Event-Driven AI

This project demonstrates a cutting-edge approach to Java development, showcasing how serverless architectures can be powerfully combined with event-driven AI. It provides a practical implementation of a modern paradigm for building highly scalable and responsive applications.

Overview
The core idea is to process incoming data (e.g., text) in an event-driven manner using serverless functions, and then leverage AI services to extract insights. This architecture is inherently scalable, cost-effective (pay-per-execution), and highly available, making it ideal for modern cloud-native applications.

Architecture
1.  **Input Queue (AWS SQS):** Acts as the ingestion point for raw events, such as text snippets for analysis. This decouples the producer from the consumer.
2.  **Serverless Java Function (AWS Lambda - SentimentAnalyzerLambda):** Triggered by new messages in the SQS queue.
    *   It retrieves the text content from the SQS message.
    *   It invokes an AI service (AWS Comprehend) to perform sentiment analysis on the text.
    *   It stores the analysis results (original text, sentiment, confidence) in a persistent data store (AWS DynamoDB).
3.  **AI Service (AWS Comprehend):** A fully managed natural language processing (NLP) service that uses machine learning to find insights and relationships in text.
4.  **Results Database (AWS DynamoDB):** A fast, flexible NoSQL database service used to store the sentiment analysis results.

This setup ensures that the AI processing is triggered only when new data arrives, minimizing idle resources and maximizing efficiency.

Key Advantages Demonstrated
*   **Scalability:** Automatically scales with the volume of incoming events.
*   **Cost-Effectiveness:** Pay only for the compute time consumed by Lambda functions and the usage of AI services.
*   **Responsiveness:** Events are processed almost in real-time as they arrive in the queue.
*   **Loose Coupling:** Components are decoupled via event queues, making the system more resilient and easier to maintain.
*   **Reduced Operational Overhead:** No servers to provision, patch, or manage.

Prerequisites
*   Java Development Kit (JDK) 17 or higher
*   Apache Maven
*   AWS CLI configured with appropriate credentials
*   AWS SAM CLI (Serverless Application Model CLI)

Setup and Deployment
1.  Clone this repository:
    git clone https://github.com/your-repo/EventDrivenAIServerless.git
    cd EventDrivenAIServerless

2.  Build the Java project:
    mvn clean install

3.  Deploy the serverless application using AWS SAM CLI. This command will package your code, create the necessary AWS resources (Lambda function, SQS queue, DynamoDB table, IAM roles), and deploy them to your AWS account.
    sam deploy --guided

    Follow the prompts:
    *   Stack Name: e.g., EventDrivenAIServerlessStack
    *   AWS Region: Choose your preferred AWS region (e.g., us-east-1)
    *   Confirm changesets: Yes
    *   Allow SAM CLI to create IAM roles: Yes

Testing the Application
1.  Once deployed, navigate to the AWS SQS console in your AWS account.
2.  Find the SQS queue created by SAM (its name will be an output of the sam deploy command, or look for something like EventDrivenAIServerlessStack-InputSQSQueue-<random_string>).
3.  Click on the queue and select "Send and receive messages".
4.  In the "Message body" text area, enter some text, for example:
    { "text": "This serverless AI solution is absolutely fantastic and works flawlessly!" }
    or
    { "text": "I am so disappointed with this product, it completely failed to meet expectations." }
5.  Click "Send message".

Verification
1.  Navigate to the AWS DynamoDB console.
2.  Find the DynamoDB table created by SAM (its name will be an output of the sam deploy command, or look for something like EventDrivenAIServerlessStack-OutputDynamoDBTable-<random_string>).
3.  Click on the table and go to the "Explore items" tab.
4.  You should see new items appear, each containing the original text, the detected sentiment (POSITIVE, NEGATIVE, NEUTRAL, MIXED), and sentiment scores from AWS Comprehend.

This demonstrates the end-to-end flow: an event enters the SQS queue, triggers the Lambda function, which then leverages AWS Comprehend for AI analysis, and finally stores the results in DynamoDB.