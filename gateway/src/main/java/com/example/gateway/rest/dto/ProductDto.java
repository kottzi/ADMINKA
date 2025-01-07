package com.example.gateway.rest.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @NoArgsConstructor
@Accessors(chain = true)
public class ProductDto {

    private Long id;
    private String title;
    private Double price;
    private String description;
}
