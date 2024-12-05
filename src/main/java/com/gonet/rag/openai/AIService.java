package com.gonet.rag.openai;

import com.gonet.rag.vector.service.VectorService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.model.Media;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

@Service
public class AIService {

    private final ChatClient chatClient;
    private final VectorService vectorService;
    private final String PROMPT_TEMPLATE = """
            Answer the query strictly referring the provided context:
                  {context}
                  Query:
                  {query}
                  In case you don't have any answer from the context provided, just say:
                  죄송합니다. 찾으시는 정보가 없습니다.
            """;

    @Autowired
    public AIService(ChatClient chatClient, VectorService vectorService) {
        this.chatClient = chatClient;
        this.vectorService = vectorService;
    }


    public ChatResponse call(String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .chatResponse();
    }

    public String callByDocs() throws MalformedURLException {
        MimeType imagePng = MimeTypeUtils.IMAGE_PNG;
        UserMessage userMessage = new UserMessage("Explain what do you see on this picture?",
                new Media(imagePng, new ClassPathResource("/temp/multimodal.test.png")));

        Prompt prompt = new Prompt(userMessage);

        ChatResponse response = chatClient.prompt(prompt).call().chatResponse();
        assert response != null;
        return response.getResult().getOutput().getContent();
    }

    public String callByDocsJson() {
        String message = "how can I solve 8x + 7 = -23";
        String jsonSchema = """
                {
                    "type": "object",
                    "properties": {
                        "steps": {
                            "type": "array",
                            "items": {
                                "type": "object",
                                "properties": {
                                    "explanation": { "type": "string" },
                                    "output": { "type": "string" }
                                },
                                "required": ["explanation", "output"],
                                "additionalProperties": false
                            }
                        },
                        "final_answer": { "type": "string" }
                    },
                    "required": ["steps", "final_answer"],
                    "additionalProperties": false
                }
                """;

        Prompt prompt = new Prompt(message,
                OpenAiChatOptions.builder()
                        .withResponseFormat(new ResponseFormat(ResponseFormat.Type.JSON_SCHEMA, jsonSchema))
                        .withParallelToolCalls(false)
                        .build());
        ChatResponse response = chatClient.prompt(prompt).call().chatResponse();

        return response.getResult().getOutput().getContent();
    }


    public Map<String, String> generate(String message) {
        String content = this.chatClient.prompt().user(message).call().content();
        assert content != null;
        return Map.of("generation", content);
    }

    public Flux<ChatResponse> generateStream(String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return chatClient.prompt(prompt).stream().chatResponse();
    }

    public String callNo2(String message) {
        List<Document> documentList = vectorService.searchData(message);
        String user = createPrompt(message, documentList);
        ChatResponse response = chatClient.prompt(user).call().chatResponse();

        return response.getResult().getOutput().getContent();
    }

    public String createPrompt(String query, List<Document> context) {
        PromptTemplate template = new PromptTemplate(PROMPT_TEMPLATE);
        template.add("query", query);
        template.add("context", context);
        return template.render();
    }
}
