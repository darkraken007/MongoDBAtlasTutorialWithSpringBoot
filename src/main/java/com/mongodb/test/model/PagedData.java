package com.mongodb.test.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PagedData {
    private List<Metadata> metadata;
    private List<ListingsAndReviews> data;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    static class Metadata {
        private int pageSize;
        private int pageNumber;
        private int total;
    }
}
