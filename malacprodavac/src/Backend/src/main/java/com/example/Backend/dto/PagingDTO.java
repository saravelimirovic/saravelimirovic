package com.example.Backend.dto;

import lombok.Getter;

// uzimam
@Getter
public class PagingDTO {
    private String category;
    private Integer pageIndex;
    private Integer pageSize;
}
