package com.gonet.rag.web;

import com.gonet.rag.openai.AIService;
import com.gonet.rag.vector.service.VectorService;
import io.milvus.grpc.ListDatabasesResponse;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

@RestController
public class HomeController {

    private final VectorService vectorService;
    private final AIService aiService;

    public HomeController(VectorService vectorService, AIService aiService) {
        this.vectorService = vectorService;
        this.aiService = aiService;
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/milvus/add")
    public ResponseEntity<?> add() {
        vectorService.add();

        List<Document> result = vectorService.result();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/milvus/get")
    public ResponseEntity<?> get() {
        List<Document> result = vectorService.result();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/milvus/clientData")
    public ResponseEntity<?> clientData() {
        ListDatabasesResponse data = vectorService.clientData();

        return ResponseEntity.ok(data.toString());
    }

    @GetMapping("/milvus/addDoc")
    public ResponseEntity<?> add(
            @RequestParam(name = "context", defaultValue = "") String context
    ) {
        vectorService.add(context);

        List<Document> result = vectorService.result();

        return ResponseEntity.ok(result);
    }
}
