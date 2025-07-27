package com.example.Veco.domain.external.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExternalCursorTest {

    @DisplayName("커서 인코딩/디코딩 테스트: 상태 필터링 있음")
    @Test
    void testEncodeDecodeWithStateFilter() {
        // given
        ExternalCursor originalCursor = new ExternalCursor();
        originalCursor.setId(123L);
        originalCursor.setCreatedAt(LocalDateTime.of(2024, 1, 1, 12, 0));
        originalCursor.setIsStatusFiltered(true);
        originalCursor.setStatusPriority(null); // 상태 필터링 시 null

        // when
        String encoded = originalCursor.encode();
        ExternalCursor decoded = ExternalCursor.decode(encoded);

        // then
        assertThat(decoded.getId()).isEqualTo(originalCursor.getId());
        assertThat(decoded.getCreatedAt()).isEqualTo(originalCursor.getCreatedAt());
        assertThat(decoded.getIsStatusFiltered()).isEqualTo(originalCursor.getIsStatusFiltered());
        assertThat(decoded.getStatusPriority()).isNull();
    }

    @DisplayName("커서 인코딩/디코딩 테스트: 상태 필터링 없음")
    @Test
    void testEncodeDecodeWithoutStateFilter() {
        // given
        ExternalCursor originalCursor = new ExternalCursor();
        originalCursor.setId(456L);
        originalCursor.setCreatedAt(LocalDateTime.of(2024, 2, 1, 15, 30));
        originalCursor.setIsStatusFiltered(false);
        originalCursor.setStatusPriority(3); // DONE 상태의 우선순위

        // when
        String encoded = originalCursor.encode();
        ExternalCursor decoded = ExternalCursor.decode(encoded);

        // then
        assertThat(decoded.getId()).isEqualTo(originalCursor.getId());
        assertThat(decoded.getCreatedAt()).isEqualTo(originalCursor.getCreatedAt());
        assertThat(decoded.getIsStatusFiltered()).isEqualTo(originalCursor.getIsStatusFiltered());
        assertThat(decoded.getStatusPriority()).isEqualTo(originalCursor.getStatusPriority());
    }

    @DisplayName("커서 인코딩/디코딩 테스트: 상태 우선순위 0")
    @Test
    void testEncodeDecodeWithZeroStatusPriority() {
        // given
        ExternalCursor originalCursor = new ExternalCursor();
        originalCursor.setId(789L);
        originalCursor.setCreatedAt(LocalDateTime.of(2024, 3, 1, 9, 15));
        originalCursor.setIsStatusFiltered(false);
        originalCursor.setStatusPriority(0); // 0 값

        // when
        String encoded = originalCursor.encode();
        ExternalCursor decoded = ExternalCursor.decode(encoded);

        // then
        assertThat(decoded.getId()).isEqualTo(originalCursor.getId());
        assertThat(decoded.getCreatedAt()).isEqualTo(originalCursor.getCreatedAt());
        assertThat(decoded.getIsStatusFiltered()).isEqualTo(originalCursor.getIsStatusFiltered());
        assertThat(decoded.getStatusPriority()).isNull(); // 0은 null로 변환됨
    }

    @DisplayName("잘못된 커서 디코딩 테스트")
    @Test
    void testDecodeInvalidCursor() {
        // given
        String invalidCursor = "invalid_cursor_string";

        // when & then
        assertThatThrownBy(() -> ExternalCursor.decode(invalidCursor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잘못된 커서 형식");
    }

    @DisplayName("null 커서 디코딩 테스트")
    @Test
    void testDecodeNullCursor() {
        // when & then
        assertThatThrownBy(() -> ExternalCursor.decode(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잘못된 커서 형식");
    }

    @DisplayName("빈 커서 디코딩 테스트")
    @Test
    void testDecodeEmptyCursor() {
        // when & then
        assertThatThrownBy(() -> ExternalCursor.decode(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잘못된 커서 형식");
    }

    @DisplayName("커서 인코딩 실패 테스트")
    @Test
    void testEncodeWithNullValues() {
        // given
        ExternalCursor cursor = new ExternalCursor();
        cursor.setId(null);
        cursor.setCreatedAt(null);
        cursor.setIsStatusFiltered(null);
        cursor.setStatusPriority(null);

        // when & then
        assertThatThrownBy(() -> cursor.encode())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("커서 인코딩 실패");
    }

    @DisplayName("커서 인코딩 결과가 Base64인지 확인")
    @Test
    void testEncodeResultIsBase64() {
        // given
        ExternalCursor cursor = new ExternalCursor();
        cursor.setId(123L);
        cursor.setCreatedAt(LocalDateTime.of(2024, 1, 1, 12, 0));
        cursor.setIsStatusFiltered(true);
        cursor.setStatusPriority(null);

        // when
        String encoded = cursor.encode();

        // then
        assertThat(encoded).isNotEmpty();
        assertThat(encoded).matches("^[A-Za-z0-9+/]*={0,2}$"); // Base64 패턴
    }
}