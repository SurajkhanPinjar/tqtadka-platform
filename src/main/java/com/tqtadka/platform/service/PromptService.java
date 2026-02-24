package com.tqtadka.platform.service;


import com.tqtadka.platform.dto.PromptRequest;

public interface PromptService {
    String buildPrompt(PromptRequest request);
}