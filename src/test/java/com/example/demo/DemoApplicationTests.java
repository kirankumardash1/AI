package com.example.demo;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {

	@Test
	void contextLoads() {
	}
	@Test
	void test() {
		ChatLanguageModel model = OllamaChatModel.builder()
				.baseUrl("http://localhost:11434")
				.modelName("phi")
				.build();

		System.out.println(model.generate("Hello"));
	}



}
