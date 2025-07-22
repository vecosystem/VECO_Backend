package com.example.Veco.domain.comment.service;

import com.example.Veco.domain.comment.converter.CommentConverter;
import com.example.Veco.domain.comment.converter.CommentRoomConverter;
import com.example.Veco.domain.comment.dto.request.CommentCreateDTO;
import com.example.Veco.domain.comment.entity.CommentRoom;
import com.example.Veco.domain.comment.repository.repository.CommentRepository;
import com.example.Veco.domain.external.repository.ExternalRepository;
import com.example.Veco.domain.goal.repository.GoalRepository;
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

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRoomRepository commentRoomRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;

    public Long addComment(CommentCreateDTO commentCreateDTO) {

        CommentRoom commentRoom = findOrCreateCommentRoom(commentCreateDTO.getTargetId(), commentCreateDTO.getCategory());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Member member = memberRepository.findBySocialUid(userDetails.getSocialUid())
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus._MEMBER_NOT_FOUND));

        return commentRepository.save(CommentConverter.toComment(commentCreateDTO, commentRoom, member)).getId();
    }



    private CommentRoom findOrCreateCommentRoom(Long resourceId, Category resourceType) {
        Optional<CommentRoom> existingRoom = switch (resourceType) {
            case ISSUE -> commentRoomRepository.findByIssueId(resourceId);
            case GOAL -> commentRoomRepository.findByGoalId(resourceId);
            case EXTERNAL -> commentRoomRepository.findByExternalId(resourceId);
        };

        return existingRoom.orElseGet(() -> createNewCommentRoom(resourceId, resourceType));
    }

    private CommentRoom createNewCommentRoom(Long resourceId, Category resourceType) {

        CommentRoom commentRoom = CommentRoomConverter.toCommentRoom(resourceId, resourceType);

        commentRoomRepository.save(commentRoom);

        return commentRoom;
    }


}
