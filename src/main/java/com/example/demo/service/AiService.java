package com.example.demo.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
public class AiService {

    private final ChatLanguageModel model;
    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> store;
    private final AtomicBoolean documentsIngested = new AtomicBoolean(false);

    public AiService() {
        this.model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("phi")
                .timeout(Duration.ofSeconds(120))
                .build();

        this.embeddingModel = OllamaEmbeddingModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("nomic-embed-text")
                .timeout(Duration.ofSeconds(120))
                .build();

        this.store = QdrantEmbeddingStore.builder()
                .host("localhost")
                .port(6334)
                .collectionName("docs")
                .build();
    }

    public String ask(String question) {
        return model.generate(question);
    }

    public String askWithContext(String question) {

        String context = """
                Java is a programming language.
                Spring Boot is used to build backend APIs.
                RAG stands for Retrieval Augmented Generation.
                """;

        String prompt = """
                Answer ONLY using the context below.

                Context:
                %s

                Question:
                %s
                """.formatted(context, question);

        return model.generate(prompt);
    }

    public String askRag(String question) {
        ensureDocumentsIngested();

        List<TextSegment> results = retrieveRelevantDocs(question);

        String context = results.stream()
                .map(TextSegment::text)
                .collect(Collectors.joining("\n"));

        String prompt = """
                You MUST answer ONLY from the context.
                If not found, say "I don't know".

                Context:
                %s

                Question:
                %s
                """.formatted(context, question);

        return model.generate(prompt);
    }

    private void ensureDocumentsIngested() {
        if (documentsIngested.compareAndSet(false, true)) {
            ingestDocuments();
        }
    }

    private void ingestDocuments() {
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(store)
                .build();

        Document document = Document.from("""
                Java is a programming language.
                Spring Boot builds backend APIs.
                RAG combines retrieval with LLM.
                """);

        ingestor.ingest(document);
    }

    private List<TextSegment> retrieveRelevantDocs(String question) {
        Embedding questionEmbedding = embeddingModel.embed(question).content();

        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                .queryEmbedding(questionEmbedding)
                .maxResults(3)
                .build();

        EmbeddingSearchResult<TextSegment> result = store.search(request);

        return result.matches().stream()
                .map(EmbeddingMatch::embedded)
                .toList();
    }
}
