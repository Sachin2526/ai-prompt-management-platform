package com.pmp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AiService {

    private final ChatClient chatClient;

    public AiService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String runPrompt(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException("Prompt cannot be null or empty");
        }

        try {
            log.info("[AI] Received prompt: {}", prompt);

            OpenAiChatOptions options = OpenAiChatOptions.builder()
                    .model("llama-3.3-70b-versatile")
                    .temperature(0.7)
                    .maxTokens(1024)
                    .streamUsage(false)
                    .build();

            String response = chatClient.prompt()
                    .system("""
                            You are an execution engine for a Prompt Management Platform.
                            The user message is a stored prompt that must be executed, completed, or fulfilled exactly as intended.
                            Never respond as if you are an AI assistant having a conversation.
                            Never add greetings, disclaimers, or meta-commentary.
                            Execute the prompt directly and return only the result.
                            """)
                    .user(prompt)
                    .options(options)
                    .call()
                    .content();

            log.info("Groq response received successfully");
            return response;

        } catch (Exception e) {
            Throwable root = e;
            while (root.getCause() != null) {
                root = root.getCause();
            }

            log.error("Groq API ERROR: {}", e.getMessage(), e);
            log.error("Root cause: {}", root.toString());

            throw new RuntimeException("Groq API failed: " + root.getMessage(), e);
        }
    }
}