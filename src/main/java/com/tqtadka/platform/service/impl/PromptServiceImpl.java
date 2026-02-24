package com.tqtadka.platform.service.impl;

import com.tqtadka.platform.dto.PromptRequest;
import com.tqtadka.platform.service.PromptService;
import org.springframework.stereotype.Service;

@Service
public class PromptServiceImpl implements PromptService {

    @Override
    public String buildPrompt(PromptRequest req) {

        String toneInstruction = resolveDayTone(req.getPublishingDay());
        String emotionalInstruction = resolveEmotion(req.getEmotionalLevel());
        String roles = req.getTargetRoles() != null
                ? String.join(", ", req.getTargetRoles())
                : "General audience";

        return """
    🏛 TQTADKA TECH CLUSTER AUTHORITY POST MASTER PROMPT

    You are an AI & Technology Strategist, systems thinker,
    digital workflow builder, and IT professional who builds SaaS tools daily.

    OBJECTIVE:
    Generate a %s word high-authority tech blog post.
    Return final polished blog only.

    INPUT:
    TOPIC: %s
    CATEGORY: %s
    PRIMARY KEYWORD: %s
    STRUCTURE CHOICE: %s
    HOOK TYPE: %s
    TARGET ROLES: %s
    SPECIAL ANGLE: %s
    PUBLISHING DAY: %s

    TONE ROTATION:
    %s

    EMOTIONAL INTENSITY:
    %s

    ROLE ADAPTATION:
    Adapt depth and examples based on selected roles.
    Managers → ROI & decisions.
    Engineers → Implementation & architecture.
    Students → Mental models.
    Creators → Leverage & workflow.
    SaaS Builders → Execution & systems.

    STRUCTURE RULE:
    Follow the selected structure strictly.
    Do not mix structural models.

    HOOK RULE:
    Use selected hook.
    Primary keyword must appear within first 120 words.
    Do not start with generic industry phrases.

    SEO RULES:
    - Use primary keyword 3–5 times naturally.
    - 3–5 H2 sections.
    - Informational intent.
    - No keyword stuffing.

    WRITING PRINCIPLES:
    - Clear.
    - Practical.
    - Slightly punchy.
    - No hype.
    - No buzzword stacking.
    - No robotic rhythm.
    - Grade 7–9 readability.
    
    MUST AVOID:
    - Heavy jargon
    - Buzzword overload
    - Repetitive transitions
    - Over-explaining basics
    
    Human rhythm > smooth AI rhythm.

    RHYTHM CONTROL:
    - Mix short emphasis lines with deeper explanation.
    - Avoid symmetrical paragraph sizes.
    - Avoid mechanical summarization.

    ENDING RULE:
    Close with practical shift, reframing, or insight.
    Never use “In conclusion.”

    CONTEXT AWARENESS:
    Assume this belongs to a larger authority cluster.
    Avoid repeating beginner explanations unless required.
    
    Do not repeat same phrasing every post.
                
    🛡 ANTI-ROBOT SAFEGUARDS
    Avoid:
    - Identical intro pattern
    - Identical conclusion pattern
    - Symmetrical paragraph sizing
    - “In conclusion” endings
    - Future hype claims
    - Robotic transitions

    OUTPUT:
    Return final polished blog only.
    """.formatted(
                req.getLength(),
                req.getTopic(),
                req.getCategory(),
                req.getPrimaryKeyword(),
                req.getStructure(),
                req.getHookType(),
                roles,
                req.getSpecialAngle(),
                req.getPublishingDay(),
                toneInstruction,
                emotionalInstruction
        );
    }

    // 🔵 Tone Resolver
    private String resolveDayTone(String day) {
        return switch (day) {
            case "Monday" -> "Reflective lesson tone";
            case "Tuesday" -> "Friendly explainer";
            case "Wednesday" -> "Direct reality check";
            case "Thursday" -> "Story-driven use case";
            case "Friday" -> "Slightly contrarian";
            case "Saturday" -> "Tactical execution";
            case "Sunday" -> "Personal advisory";
            default -> "Professional structured tone";
        };
    }

    // 🔴 Emotion Resolver
    private String resolveEmotion(int level) {
        return switch (level) {
            case 1 -> "Calm, analytical, emotionally neutral";
            case 2 -> "Light engagement";
            case 3 -> "Balanced authority";
            case 4 -> "Strong engagement";
            case 5 -> "High persuasive intensity";
            default -> "Balanced tone";
        };
    }
}