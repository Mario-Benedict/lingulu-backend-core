package com.lingulu.service;

import com.lingulu.dto.ApiResponse;
import com.lingulu.dto.LoginRequest;
import com.lingulu.dto.RegisterRequest;
import com.lingulu.dto.UserResponse;
import com.lingulu.entity.OAuthAccount;
import com.lingulu.entity.User;
import com.lingulu.entity.UserProfile;
import com.lingulu.exception.RegisterException;
import com.lingulu.repository.UserRepository;
import com.lingulu.security.JwtUtil;
import com.lingulu.repository.OAuthAccountRepository;
import com.lingulu.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;

import java.util.*;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final OAuthAccountRepository oAuthAccountRepository;
    private final JwtUtil jwtUtil;
    private final LeaderboardService leaderboardService;
    private final LearningService learningService;

    private final PasswordEncoder passwordEncoder;

    public User register(RegisterRequest request) {
        Map<String, List<String>> errors = new HashMap<>();

        if (userRepository.existsByEmail(request.getEmail())) {
            errors.computeIfAbsent("email", k -> new ArrayList<>()).add("Email already in use");
        }

        if (userProfileRepository.existsByUsername(request.getUsername())) {
            errors.computeIfAbsent("username", k -> new ArrayList<>()).add("Username already in use");
        }

        if (!errors.isEmpty()) {
            throw new RegisterException("Failed to register", errors);
        }

        User user = User.builder()
                    .email(request.getEmail())
                    .passwordHash(passwordEncoder.encode(request.getPassword()))
                    .build();

        userRepository.save(user);

        UserProfile userProfile = UserProfile.builder()
                                .username(request.getUsername())
                                .user(user)
                                .build();

        userProfileRepository.save(userProfile);

        OAuthAccount oAuthAccount = OAuthAccount.builder()
                                    .user(user)
                                    .accessToken(jwtUtil.generateAccessToken(user.getUserId()))
                                    .provider("Local")
                                    .build();

        oAuthAccountRepository.save(oAuthAccount);
        leaderboardService.addLeaderBoard(user);

        learningService.addUserLessons(user.getUserId());

        return user;
    }

    public User login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if(user.getOauthAccounts().getProvider().equals("Google")){
            throw new RuntimeException("Please login with Google OAuth");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())){
            throw new RuntimeException("Invalid Password");
        }

        updateAccessToken(jwtUtil.generateAccessToken(user.getUserId()), user.getUserId());

        return user;
    }

    private User registerViaGoogle(OAuth2User oAuth2User){

        Random random = new Random();
        int randomNumber = random.nextInt(1000000); // 0-99999
        String fiveDigit = String.format("%06d", randomNumber); 

        String userEmail = oAuth2User.getAttribute("email");

        User user = User.builder()
                    .email(userEmail)
                    .isEmailVerified(true)
                    .build();        

        UserProfile userProfile = UserProfile.builder()
                                .username(oAuth2User.getAttribute("given_name") + fiveDigit)
                                .user(user)
                                .build();

        OAuthAccount oAuthAccount = OAuthAccount.builder()
                                    .user(user)
                                    .provider("Google")
                                    .providerUserId(oAuth2User.getName())
                                    .build();
        
        userRepository.save(user);
        userProfileRepository.save(userProfile);
        oAuthAccountRepository.save(oAuthAccount);

        user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        leaderboardService.addLeaderBoard(user);
        learningService.addUserLessons(user.getUserId());

        return user;
    }

    public User googleOAuth(OAuth2User oAuth2User){

        User user = userRepository.findByEmail(oAuth2User.getAttribute("email"))
                .orElseGet(() -> registerViaGoogle(oAuth2User));
        
        if(oAuthAccountRepository.findByUser_UserId(user.getUserId()).getProvider().equals("Local")){
            throw new RuntimeException("You already registered. Please login with your email and password");
        }

        return user;
    }

    public String updateAccessToken(String accessToken, UUID userID){

        OAuthAccount oAuthAccount = oAuthAccountRepository.findByUser_UserId(userID);

        oAuthAccount.setAccessToken(accessToken);

        oAuthAccountRepository.save(oAuthAccount);

        return accessToken;
    }

    public ResponseEntity<ApiResponse<UserResponse>> response(User user){
         UserResponse userResponse = UserResponse.builder()
                            .accessToken(updateAccessToken(jwtUtil.generateAccessToken(user.getUserId()), user.getUserId()))
                            .build();

        return ResponseEntity.ok(new ApiResponse<UserResponse>(true, "Login berhasil", userResponse));
    }

    public void setEmailVerified(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEmailVerified(true);
        userRepository.save(user);
    }
}
