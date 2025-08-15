package com.example.Veco.domain.comment.service;

import com.example.Veco.domain.comment.converter.CommentConverter;
import com.example.Veco.domain.comment.converter.CommentRoomConverter;
import com.example.Veco.domain.comment.dto.request.CommentCreateDTO;
import com.example.Veco.domain.comment.dto.response.CommentListResponseDTO;
import com.example.Veco.domain.comment.dto.response.CommentResponseDTO;
import com.example.Veco.domain.comment.entity.Comment;
import com.example.Veco.domain.comment.entity.CommentRoom;
import com.example.Veco.domain.comment.repository.CommentRepository;
import com.example.Veco.domain.external.exception.ExternalException;
import com.example.Veco.domain.external.exception.code.ExternalErrorCode;
import com.example.Veco.domain.external.repository.ExternalRepository;
import com.example.Veco.domain.goal.exception.GoalException;
import com.example.Veco.domain.goal.exception.code.GoalErrorCode;
import com.example.Veco.domain.goal.repository.GoalRepository;
import com.example.Veco.domain.issue.exception.IssueException;
import com.example.Veco.domain.issue.exception.code.IssueErrorCode;
import com.example.Veco.domain.issue.repository.IssueRepository;
import com.example.Veco.domain.mapping.repository.CommentRoomRepository;
import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.member.error.MemberErrorStatus;
import com.example.Veco.domain.member.error.MemberHandler;
import com.example.Veco.domain.member.repository.MemberRepository;
import com.example.Veco.global.auth.user.userdetails.CustomUserDetails;
import com.example.Veco.global.enums.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRoomRepository commentRoomRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final IssueRepository issueRepository;
    private final GoalRepository goalRepository;
    private final ExternalRepository externalRepository;

    public Long addComment(CommentCreateDTO commentCreateDTO) {

        CommentRoom commentRoom = findOrCreateCommentRoom(commentCreateDTO.getTargetId(), commentCreateDTO.getCategory());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Member member = memberRepository.findBySocialUid(userDetails.getSocialUid())
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus._MEMBER_NOT_FOUND));

        return commentRepository.save(CommentConverter.toComment(commentCreateDTO, commentRoom, member)).getId();
    }

    @Transactional(readOnly = true)
    public CommentResponseDTO.CommentListDTO getComments(Long targetId, Category category) {
        // 리소스 존재 여부 검증
        validateResourceExists(targetId, category);
        
        CommentRoom commentRoom = commentRoomRepository.findByRoomTypeAndTargetId(category, targetId);
        
        if (commentRoom == null) {
            return CommentConverter.toCommentListDTO(new ArrayList<>()); // 댓글방이 없으면 빈 리스트 반환
        }

        List<Comment> comments = commentRepository.findAllByCommentRoomOrderByIdAsc(commentRoom);

        return CommentConverter.toCommentListDTO(comments);
    }

    private CommentRoom findOrCreateCommentRoom(Long resourceId, Category resourceType) {
        CommentRoom existingRoom = commentRoomRepository.findByRoomTypeAndTargetId(resourceType, resourceId);
        
        if (existingRoom != null) {
            return existingRoom;
        }
        
        return createNewCommentRoom(resourceId, resourceType);
    }

    private CommentRoom createNewCommentRoom(Long resourceId, Category resourceType) {
        // 리소스 존재 여부 검증
        validateResourceExists(resourceId, resourceType);
        
        CommentRoom commentRoom = CommentRoomConverter.toCommentRoom(resourceId, resourceType);
        return commentRoomRepository.save(commentRoom);
    }
    
    private void validateResourceExists(Long resourceId, Category category) {
        switch (category) {
            case ISSUE -> {
                if (!issueRepository.existsById(resourceId)) {
                    throw new IssueException(IssueErrorCode.NOT_FOUND);
                }
            }
            case GOAL -> {
                if (!goalRepository.existsById(resourceId)) {
                    throw new GoalException(GoalErrorCode.NOT_FOUND);
                }
            }
            case EXTERNAL -> {
                if (!externalRepository.existsById(resourceId)) {
                    throw new ExternalException(ExternalErrorCode.NOT_FOUND);
                }
            }
        }
    }


}
