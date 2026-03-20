package com.example.demo.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties(AiProperties.class)
public class AiConfig {

    @Bean
    ChatLanguageModel chatLanguageModel(AiProperties properties) {
        return OllamaChatModel.builder()
                .baseUrl(properties.getOllama().getBaseUrl())
                .modelName(properties.getOllama().getChatModel())
                .timeout(Duration.ofSeconds(properties.getOllama().getTimeoutSeconds()))
                .build();
    }

    @Bean
    EmbeddingModel embeddingModel(AiProperties properties) {
        return OllamaEmbeddingModel.builder()
                .baseUrl(properties.getOllama().getBaseUrl())
                .modelName(properties.getOllama().getEmbeddingModel())
                .timeout(Duration.ofSeconds(properties.getOllama().getTimeoutSeconds()))
                .build();
    }

    @Bean
    EmbeddingStore<TextSegment> embeddingStore(AiProperties properties) {
        return QdrantEmbeddingStore.builder()
                .host(properties.getQdrant().getHost())
                .port(properties.getQdrant().getGrpcPort())
                .collectionName(properties.getQdrant().getCollectionName())
                .build();
    }
}
