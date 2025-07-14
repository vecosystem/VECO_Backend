package com.example.Veco.global.apiPayload.page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Builder
public class CursorPage<T> {
    private List<T> data;
    private String nextCursor;
    private boolean hasNext;
    private int size;

    public static <T> CursorPage<T> of(List<T> data, String nextCursor, boolean hasNext) {
        return CursorPage.<T>builder()
                .data(data)
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .size(data.size())
                .build();
    }
}
