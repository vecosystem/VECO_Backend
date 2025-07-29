package com.example.Veco.domain.workspace.controller;

import com.example.Veco.domain.member.converter.MemberConverter;
import com.example.Veco.domain.member.dto.MemberResponseDTO;
import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.member.repository.MemberRepository;
import com.example.Veco.domain.member.service.MemberCommandService;
import com.example.Veco.domain.member.service.MemberQueryService;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.domain.team.service.TeamQueryService;
import com.example.Veco.domain.workspace.converter.WorkspaceConverter;
import com.example.Veco.domain.workspace.dto.WorkspaceRequestDTO;
import com.example.Veco.domain.workspace.dto.WorkspaceResponseDTO;
import com.example.Veco.domain.workspace.entity.WorkSpace;
import com.example.Veco.domain.workspace.service.WorkspaceCommandService;
import com.example.Veco.domain.workspace.service.WorkspaceQueryService;
import com.example.Veco.global.apiPayload.ApiResponse;
import com.example.Veco.global.auth.oauth2.CustomOAuth2User;
import com.example.Veco.global.auth.user.userdetails.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/workspace")
@Tag(name = "워크스페이스 API")
public class WorkspaceRestController {

}
