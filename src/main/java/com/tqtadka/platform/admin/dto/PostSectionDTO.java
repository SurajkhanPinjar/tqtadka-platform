package com.tqtadka.platform.admin.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostSectionDTO {
    private String title;
    private String content;
    private String image;
}