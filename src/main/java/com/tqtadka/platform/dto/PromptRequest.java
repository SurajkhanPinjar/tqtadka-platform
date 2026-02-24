package com.tqtadka.platform.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Data
@Setter
@Getter
@ToString
public class PromptRequest {

    private String topic;
    private String category;
    private String primaryKeyword;
    private String structure;
    private String hookType;
    private String publishingDay;
    private List<String> targetRoles;
    private String specialAngle;
    private int emotionalLevel;
    private String length;

}