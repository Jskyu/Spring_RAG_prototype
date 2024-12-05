package com.gonet.rag.vector.service;

import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.ListDatabasesResponse;
import io.milvus.param.R;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class VectorService {

    private final VectorStore vectorStore;
    private final MilvusServiceClient client;

    List<Document> documents = List.of(
            new Document("1. 회사명: 지오넷", Map.of("meta1", "회사")),
            new Document("2. 부서명: 플랫폼개발사업부"),
            new Document("3. 직원목록: ['정성규', '박이수']", Map.of("meta2", "직원")));

    @Autowired
    public VectorService(VectorStore vectorStore, MilvusServiceClient client) {
        this.vectorStore = vectorStore;
        this.client = client;
    }

    public void add() {
        this.add(this.documents);
    }

    public void add(String context) {
        Document document = new Document(context);
        this.add(List.of(document));
    }

    public void add(List<Document> documentList) {
        this.vectorStore.add(documentList);
    }

    public List<Document> result() {
        List<Document> resultList = this.vectorStore.similaritySearch(SearchRequest.query("Spring").withTopK(5));
        var jsonSchema = """
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

        return resultList;
    }

    public ListDatabasesResponse clientData() {
        R<ListDatabasesResponse> listDatabasesResponse = client.listDatabases();
        ListDatabasesResponse data = listDatabasesResponse.getData();

        return data;
    }

    public List<Document> searchData(String query) {
        return this.vectorStore.similaritySearch(SearchRequest.query(query).withTopK(5));
    }
}
