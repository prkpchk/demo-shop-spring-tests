package com.demoshop.qa.data.dto.api.common;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        int number,
        int size,
        long totalElements,
        int totalPages,
        boolean last,
        boolean first,
        boolean empty
) {
}
