package com.restauranthub.restaurant_user_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageMetadata {
    private int pageNumber;
    private int pageSize;
    private SortResponse sort;
}
