package com.tqtadka.platform.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageSectionDto {

    private int order;          // displayOrder
    private String imageUrl;
    private String heading;
    private String description;
}