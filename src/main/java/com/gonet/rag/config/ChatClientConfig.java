package com.gonet.rag.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    private final String model = OpenAiApi.ChatModel.GPT_4_O_MINI.getValue();

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
//                .defaultSystem("You are a friendly chat bot that answers question in the voice of a {voice}")
                .defaultOptions(OpenAiChatOptions.builder()
                        .withModel(model)
                        .withTemperature(0.8)
                        .build())
//                .defaultFunctions(/*NOT NULL*/)
//                .defaultAdvisors(/*NOT NULL*/)
//                .defaultToolContext(/*NOT NULL*/)
                // TODO
                .build();
    }
}
