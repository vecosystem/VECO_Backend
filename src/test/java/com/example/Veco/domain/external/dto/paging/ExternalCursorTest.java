package com.example.Veco.domain.external.dto.paging;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;


import static org.junit.jupiter.api.Assertions.*;

class ExternalCursorTest {

    @Test
    @DisplayName("커서 인코딩 및 디코딩 테스트")
    void testCursorEncodeAndDecode() {
        // Given
        Long testId = 123L;
        String testGroupValue = "TEST_GROUP";
        
        ExternalCursor originalCursor = new ExternalCursor();
        originalCursor.setId(testId);
        originalCursor.setGroupValue(testGroupValue);

        // When
        String encoded = originalCursor.encode();
        ExternalCursor decodedCursor = ExternalCursor.decode(encoded);

        // Then
        assertEquals(originalCursor.getId(), decodedCursor.getId());
        assertEquals(originalCursor.getGroupValue(), decodedCursor.getGroupValue());
    }

    @Test
    @DisplayName("null groupValue 처리 테스트")
    void testNullGroupValueHandling() {
        // Given
        Long testId = 123L;
        
        ExternalCursor originalCursor = new ExternalCursor();
        originalCursor.setId(testId);
        originalCursor.setGroupValue(null);

        // When
        String encoded = originalCursor.encode();
        ExternalCursor decodedCursor = ExternalCursor.decode(encoded);

        // Then
        assertEquals(originalCursor.getId(), decodedCursor.getId());
        assertNull(decodedCursor.getGroupValue());
    }

    @Test
    @DisplayName("잘못된 커서 형식 예외 처리 테스트")
    void testInvalidCursorFormat() {
        // Given
        String invalidCursor = "invalid_cursor_format";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            ExternalCursor.decode(invalidCursor);
        });
    }

    @Test
    @DisplayName("커서 인코딩 결과가 URL Safe Base64인지 확인")
    void testEncodedCursorIsUrlSafe() {
        // Given
        Long testId = 123L;
        String testGroupValue = "TEST_GROUP+/=";
        
        ExternalCursor cursor = new ExternalCursor();
        cursor.setId(testId);
        cursor.setGroupValue(testGroupValue);

        // When
        String encoded = cursor.encode();

        // Then
        // URL Safe Base64는 +, /, = 문자를 사용하지 않음
        assertFalse(encoded.contains("+"));
        assertFalse(encoded.contains("/"));
        assertFalse(encoded.contains("="));
    }
}