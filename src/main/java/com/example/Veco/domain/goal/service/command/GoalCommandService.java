package com.example.Veco.domain.goal.service.command;

import com.example.Veco.domain.common.entity.Image;
import com.example.Veco.domain.common.repository.ImageRepository;
import com.example.Veco.domain.goal.repository.GoalRepository;
import com.example.Veco.global.aws.util.S3Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoalCommandService {

    private final S3Util s3Util;
    private final GoalRepository goalRepository;
    private final ImageRepository imageRepository;

    // 목표 작성

    // 목표 사진 첨부
    public String uploadFile(
            MultipartFile file
    ){
        // 사진 업로드
        String url = s3Util.getImageUrl(s3Util.uploadFile(file, null));

        // 링크 저장
        Image image = Image.builder().url(url).build();
        imageRepository.save(image);

        return url;
    }
    // 목표 수정

    // 목표 삭제
}
