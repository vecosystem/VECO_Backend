package com.example.Veco.domain.external.dto.paging;

import lombok.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@ToString
public class ExternalCursor {
    private Long id;
    private String groupValue;

    public String encode(){
        try{
            // null 안전장치 추가
            if (id == null) {
                throw new IllegalArgumentException("id는 null일 수 없습니다");
            }
            
            // ID와 groupValue만 사용하는 간단한 형태
            String simpleData = String.format("%d_%s", 
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
            String[] parts = decoded.split("_", 2);
            
            if (parts.length < 2) {
                throw new IllegalArgumentException("커서 형식이 올바르지 않습니다");
            }

            ExternalCursor cursor = new ExternalCursor();
            cursor.setId(Long.parseLong(parts[0]));
            cursor.setGroupValue("NULL".equals(parts[1]) ? null : parts[1]);

            return cursor;
        } catch (Exception e) {
            throw new IllegalArgumentException("잘못된 커서 형식: " + e.getMessage(), e);
        }
    }
}
