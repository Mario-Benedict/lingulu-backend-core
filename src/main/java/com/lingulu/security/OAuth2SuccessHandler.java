package com.lingulu.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.lingulu.entity.User;
import com.lingulu.service.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final AuthService authService;

    @Value("${spring.application.dev}")
    private Boolean isDev;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                      HttpServletResponse response,
                                      Authentication authentication) throws IOException {
        
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        User user = authService.googleOAuth(oAuth2User);
        String accessToken = jwtUtil.generateAccessToken(user);
        authService.updateAccessToken(accessToken, user.getUserId());

        Cookie tokenCookie = new Cookie("token", accessToken);
        tokenCookie.setHttpOnly(true);
        tokenCookie.setSecure(!isDev);
        tokenCookie.setPath("/");
        tokenCookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(tokenCookie);

        response.sendRedirect("http://localhost:5173/oauth2/success");
    }
}