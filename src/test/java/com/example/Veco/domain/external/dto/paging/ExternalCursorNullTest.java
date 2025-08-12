package com.example.Veco.domain.external.dto.paging;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class ExternalCursorNullTest {

    @Test
    @DisplayName("createdAt이 null일 때도 정상 작동 테스트")  
    void testCreatedAtNullHandling() {
        // Given
        ExternalCursor cursor = new ExternalCursor();
        cursor.setId(123L);
        cursor.setGroupValue("TEST");
        // ID 기반 커서에서는 createdAt이 필요 없음

        // When & Then - 예외가 발생하지 않아야 함
        assertDoesNotThrow(() -> {
            String encoded = cursor.encode();
            ExternalCursor decoded = ExternalCursor.decode(encoded);
            assertEquals(cursor.getId(), decoded.getId());
            assertEquals(cursor.getGroupValue(), decoded.getGroupValue());
        });
    }

    @Test
    @DisplayName("id가 null일 때 예외 발생 테스트")
    void testIdNullThrowsException() {
        // Given
        ExternalCursor cursor = new ExternalCursor();
        cursor.setGroupValue("TEST");
        // id는 null로 두기

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cursor.encode();
        });
        
        assertTrue(exception.getMessage().contains("id는 null일 수 없습니다"));
    }
}