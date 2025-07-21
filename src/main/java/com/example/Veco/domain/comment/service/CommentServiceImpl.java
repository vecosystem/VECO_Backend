package com.example.Veco.domain.comment.service;

import com.example.Veco.domain.comment.repository.repository.CommentRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl {

    private final CommentRoomRepository commentRoomRepository;

}
