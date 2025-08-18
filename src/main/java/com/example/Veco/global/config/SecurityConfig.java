package com.example.Veco.global.config;

import com.example.Veco.global.auth.jwt.filter.JwtAuthFilter;
import com.example.Veco.global.auth.jwt.util.JwtUtil;
import com.example.Veco.global.auth.oauth2.handler.OAuth2SuccessHandler;
import com.example.Veco.global.auth.oauth2.resolver.CustomAuthorizationRequestResolver;
import com.example.Veco.global.auth.oauth2.service.OAuth2UserService;
import com.example.Veco.global.auth.user.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity // Spring Securiy 설정 활성화
@Slf4j
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final OAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final CustomAuthorizationRequestResolver customAuthorizationRequestResolver;
    private final JwtUtil jwtUtil;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/test/**", "/api/teams/*/externals/**", "/api/teams/*/externals", "/api/test/login", "/api/token/reissue", "/login-test.html", "/v3/api-docs/**", "/swagger-ui/**",
                                "/swagger-resources/**", "/css/**", "/images/**", "/healthcheck",
                                "/js/**", "/h2-console/**", "/profile","/workspace/create-url","/slack/callback", "/github/**","/api/github/**").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout.logoutSuccessUrl("https://web.vecoservice.shop/onboarding"))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(config -> config
                                .authorizationRequestRepository(new HttpSessionOAuth2AuthorizationRequestRepository())
                                .authorizationRequestResolver(customAuthorizationRequestResolver)
                        )
                        .loginPage("https://web.vecoservice.shop/onboarding")
                        .successHandler(oAuth2SuccessHandler)
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .failureHandler((request, response, exception) -> {
                            // 로그인 실패 시 처리 로직
                            log.error("OAuth2 로그인 실패: {}", exception.getMessage());
                            response.sendRedirect("https://web.vecoservice.shop/onboarding");
                        })
                );
        return http.build();
    }

    @Bean
    JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter(jwtUtil, customUserDetailsService);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
