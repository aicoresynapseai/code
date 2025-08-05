// Function to append a message to the chat output area
function appendChatMessage(sender, message) {
    const chatOutput = document.getElementById('chat-output');
    const messageDiv = document.createElement('div');
    messageDiv.classList.add('chat-message', sender); // Add classes for styling (user or ai)
    messageDiv.innerHTML = `<strong>${sender.toUpperCase()}:</strong> ${message}`;
    chatOutput.appendChild(messageDiv);
    chatOutput.scrollTop = chatOutput.scrollHeight; // Scroll to the bottom
}

// Function to send a message to the chatbot API
async function sendMessage() {
    const chatInput = document.getElementById('chat-input');
    const query = chatInput.value.trim();

    if (!query) {
        alert('Please enter a query!');
        return;
    }

    appendChatMessage('user', query); // Display user's message
    chatInput.value = ''; // Clear input field

    try {
        const response = await fetch('/api/tutor/chat', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ query: query })
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.error || 'Failed to get AI response.');
        }

        const data = await response.json();
        appendChatMessage('ai', data.response); // Display AI's response
    } catch (error) {
        console.error('Error sending message:', error);
        appendChatMessage('ai', 'Oops! Something went wrong. Please try again.');
    }
}

// Function to send code for explanation to the API
async function explainCode() {
    const codeInput = document.getElementById('code-input');
    const code = codeInput.value.trim();
    const codeOutput = document.getElementById('code-output');

    if (!code) {
        alert('Please paste some code to explain!');
        return;
    }

    codeOutput.innerHTML = 'Thinking...'; // Show loading indicator

    try {
        const response = await fetch('/api/tutor/explainCode', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ code: code })
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.error || 'Failed to explain code.');
        }

        const data = await response.json();
        codeOutput.innerHTML = `<strong>Explanation:</strong><br>${data.explanation}`;
    } catch (error) {
        console.error('Error explaining code:', error);
        codeOutput.innerHTML = 'Error: Could not explain the code. Please try again.';
    }
}

// Function to generate an onboarding guide
async function generateOnboardingGuide() {
    const topicInput = document.getElementById('onboard-topic-input');
    const topic = topicInput.value.trim();
    const onboardOutput = document.getElementById('onboard-output');

    if (!topic) {
        alert('Please enter a topic for the onboarding guide!');
        return;
    }

    onboardOutput.innerHTML = 'Generating guide...'; // Show loading indicator

    try {
        const response = await fetch('/api/tutor/onboard', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ topic: topic })
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.error || 'Failed to generate guide.');
        }

        const data = await response.json();
        // Display guide, replace newlines with <br> for HTML display
        onboardOutput.innerHTML = `<strong>Onboarding Guide for "${topic}":</strong><br><br>${data.guide.replace(/\n/g, '<br>')}`;
    } catch (error) {
        console.error('Error generating onboarding guide:', error);
        onboardOutput.innerHTML = 'Error: Could not generate the onboarding guide. Please try again.';
    }
}

// Function to load and display all pre-defined FAQs
async function loadFaqs() {
    const faqListOutput = document.getElementById('faq-list-output');
    faqListOutput.innerHTML = 'Loading FAQs...';

    try {
        const response = await fetch('/api/tutor/faq');

        if (!response.ok) {
            throw new Error('Failed to load FAQs.');
        }

        const faqs = await response.json();
        if (faqs.length === 0) {
            faqListOutput.innerHTML = 'No FAQs found.';
            return;
        }

        let faqHtml = '<strong>Pre-defined FAQs:</strong><br><br>';
        faqs.forEach((faq, index) => {
            faqHtml += `<div class="faq-item"><strong>Q${index + 1}:</strong> ${faq.question}<br><strong>A:</strong> ${faq.answer}</div>`;
        });
        faqListOutput.innerHTML = faqHtml;
    } catch (error) {
        console.error('Error loading FAQs:', error);
        faqListOutput.innerHTML = 'Error: Could not load FAQs. Please try again.';
    }
}

// Function to ask a specific FAQ question and get an AI-enhanced answer
async function askFaqQuestion() {
    const faqAskInput = document.getElementById('faq-ask-input');
    const question = faqAskInput.value.trim();
    const faqAnswerOutput = document.getElementById('faq-answer-output');

    if (!question) {
        alert('Please enter a question!');
        return;
    }

    faqAnswerOutput.innerHTML = 'Searching for answer...';

    try {
        const response = await fetch('/api/tutor/faq/ask', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ question: question })
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.error || 'Failed to get FAQ answer.');
        }

        const data = await response.json();
        faqAnswerOutput.innerHTML = `<strong>Your Question:</strong> ${question}<br><strong>AI Answer:</strong> ${data.answer}`;
    } catch (error) {
        console.error('Error asking FAQ question:', error);
        faqAnswerOutput.innerHTML = 'Error: Could not retrieve answer. Please try again.';
    }
}

// Optional: Load FAQs automatically when the page loads
document.addEventListener('DOMContentLoaded', loadFaqs);