package com.example.Veco.domain.external.dto.paging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
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

    public String encode(){
        try{
            String data = String.format("%s|%d|%s|%d",
                    Boolean.TRUE.equals(isStatusFiltered) ? "SIMPLE" : "COMPLEX",
                    statusPriority != null ? statusPriority : 0,
                    createdAt.toString(),
                    id);
            return Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
        }catch (Exception e){
            throw new IllegalArgumentException("커서 인코딩 실패");
        }
    }

    public static ExternalCursor decode(String encodedCursor){
        try {
            String decoded = new String(Base64.getDecoder().decode(encodedCursor), StandardCharsets.UTF_8);
            String[] parts = decoded.split("\\|");

            ExternalCursor cursor = new ExternalCursor();
            cursor.setIsStatusFiltered("SIMPLE".equals(parts[0]));

            int statusPriorityValue = Integer.parseInt(parts[1]);
            cursor.setStatusPriority(statusPriorityValue > 0 ? statusPriorityValue : null);
            cursor.setCreatedAt(LocalDateTime.parse(parts[2]));
            cursor.setId(Long.parseLong(parts[3]));

            return cursor;
        } catch (Exception e) {
            throw new IllegalArgumentException("잘못된 커서 형식", e);
        }
    }
}
