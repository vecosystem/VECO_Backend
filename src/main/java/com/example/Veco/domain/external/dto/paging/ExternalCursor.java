package com.example.Veco.domain.external.dto.paging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class ExternalCursor {
    private Integer statusPriority;
    private LocalDateTime createdAt;
    private Long id;
    private Boolean isStatusFiltered;
    private String groupValue;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public String encode(){
        try{
            // 간단한 구분자 방식 사용 - timestamp를 long으로 변환
            long timestamp = createdAt.toEpochSecond(ZoneOffset.UTC);
            String simpleData = String.format("%d_%d_%s", 
                timestamp, 
                id,
                groupValue != null ? groupValue : "NULL"
            );
            return Base64.getUrlEncoder().withoutPadding().encodeToString(simpleData.getBytes(StandardCharsets.UTF_8));
        }catch (Exception e){
            throw new IllegalArgumentException("커서 인코딩 실패: " + e.getMessage());
        }
    }

    public static ExternalCursor decode(String encodedCursor){
        try {
            String decoded = new String(Base64.getUrlDecoder().decode(encodedCursor), StandardCharsets.UTF_8);
            String[] parts = decoded.split("_", 3);
            
            if (parts.length < 3) {
                throw new IllegalArgumentException("커서 형식이 올바르지 않습니다");
            }

            ExternalCursor cursor = new ExternalCursor();
            // timestamp를 LocalDateTime으로 변환
            long timestamp = Long.parseLong(parts[0]);
            cursor.setCreatedAt(LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.UTC));
            cursor.setId(Long.parseLong(parts[1]));
            cursor.setGroupValue("NULL".equals(parts[2]) ? null : parts[2]);

            return cursor;
        } catch (Exception e) {
            throw new IllegalArgumentException("잘못된 커서 형식: " + e.getMessage(), e);
        }
    }
}
