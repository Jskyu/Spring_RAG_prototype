package com.gonet.rag.config;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MilvusClientConfig {

    @Bean
    public MilvusServiceClient milvusServiceClient() {
        ConnectParam param = ConnectParam.newBuilder()
                .withHost("localhost")
                .withPort(19530)
                .withDatabaseName("rag")
                .withToken("root:Milvus")
                .withUri("http://localhost:19530")
                .build();

        return new MilvusServiceClient(param);
    }
}
