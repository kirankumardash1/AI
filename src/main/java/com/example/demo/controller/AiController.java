package com.example.demo.controller;

import com.example.demo.service.AiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/ask")
    public String ask(@RequestParam String q) {
        return aiService.ask(q);
    }
    @GetMapping("/askWithContext")
    public String askWithContext(@RequestParam String q) {
        return aiService.askWithContext(q);
    }

    @GetMapping("/askRag")
    public String askRag(@RequestParam String q) {
        return aiService.askRag(q);
    }

}