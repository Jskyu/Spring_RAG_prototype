package com.gonet.rag.web;

import com.gonet.rag.openai.AIService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.net.MalformedURLException;
import java.util.Map;

@RestController
@RequestMapping("/ai")
public class DefaultAIController {

    private final AIService aiService;
    private final ChatClient chatClient;

    @Autowired
    public DefaultAIController(AIService aiService, ChatClient chatClient) {
        this.aiService = aiService;
        this.chatClient = chatClient;
    }

    @GetMapping("")
    public Map<String, String> completion(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message
            ,@RequestParam(value = "voice", defaultValue = "man") String voice) {
        String voice1 = this.chatClient.prompt()
//                .system(sp -> sp.param("voice", voice))
                .user(message)
                .call()
                .content();
        return Map.of("completion", voice1);
    }

    @GetMapping("/default")
    public Map<String, String> efault(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return aiService.generate(message);
    }

    @GetMapping("/callByDocs")
    public String callByDocs(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) throws MalformedURLException {
        return aiService.callByDocs();
    }

    @GetMapping("/callByDocsJson")
    public String callByDocsJson(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return aiService.callByDocsJson();
    }

    @GetMapping("/generateStream")
    public Flux<ChatResponse> generateStream(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        Flux<ChatResponse> chatResponseFlux = aiService.generateStream(message);

        return chatResponseFlux;
    }

    @GetMapping("/callNo2")
    public ResponseEntity<String> callNo2(
            @RequestParam(value = "message", defaultValue = "이 회사 이름은 뭐야?") String message) {
        String answer = aiService.callNo2(message);

        return ResponseEntity.ok(answer);
    }


}
