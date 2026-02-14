package com.lingulu.service;

import com.lingulu.dto.request.account.LoginRequest;
import com.lingulu.dto.request.account.RegisterRequest;
import com.lingulu.entity.account.OAuthAccount;
import com.lingulu.entity.account.User;
import com.lingulu.entity.account.UserProfile;
import com.lingulu.exception.OAuthOnlyLoginException;
import com.lingulu.exception.RegisterException;
import com.lingulu.exception.UserNotFoundException;
import com.lingulu.repository.UserRepository;
import com.lingulu.security.JwtUtil;
import com.lingulu.repository.OAuthAccountRepository;
import com.lingulu.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;

import java.util.*;

import org.springframework.http.HttpStatus;
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
    private final EnrollmentService enrollmentService;
    private final UserLearningStatsService userLearningStatsService;
    private final UserProfileService userProfileService;

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


        UserProfile userProfile = UserProfile.builder()
                                .username(request.getUsername())
                                .user(user)
                                .avatarUrl("users/" + userProfileService.pickAvatarByUserId())
                                .bio("Interested in learning English")
                                .build();

        user.setUserProfile(userProfile);

        userRepository.save(user);
        userProfileRepository.save(userProfile);

        OAuthAccount oAuthAccount = OAuthAccount.builder()
                                    .user(user)
                                    .accessToken(jwtUtil.generateAccessToken(user))
                                    .provider("Local")
                                    .build();

        System.out.println(oAuthAccount.getAccessToken());

        oAuthAccountRepository.save(oAuthAccount);
        leaderboardService.addLeaderBoard(user);
        enrollmentService.enrollUserToAllLessons(user.getUserId());
        userLearningStatsService.addUserLearningStats(user);
        
        return user;
    }

    public String login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Invalid email or password", HttpStatus.UNAUTHORIZED));

        if(user.getOauthAccounts().getProvider().equals("Google")){
            throw new OAuthOnlyLoginException("Please login using Google OAuth", HttpStatus.UNAUTHORIZED);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())){
            throw new UserNotFoundException("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }

        return updateAccessToken(jwtUtil.generateAccessToken(user), user.getUserId());
    }

    private User registerViaGoogle(OAuth2User oAuth2User){
        String email = oAuth2User.getAttribute("email");
        String givenName = oAuth2User.getAttribute("given_name");

        User user = User.builder()
                    .email(email)
                    .isEmailVerified(true)
                    .build();        

        UserProfile userProfile = UserProfile.builder()
                                .username(givenName)
                                .user(user)
                                .avatarUrl("users/" + userProfileService.pickAvatarByUserId())
                                .bio("Interested in learning English")
                                .build();

        OAuthAccount oAuthAccount = OAuthAccount.builder()
                                    .user(user)
                                    .provider("Google")
                                    .providerUserId(oAuth2User.getName())
                                    .build();
        
        userRepository.save(user);
        userProfileRepository.save(userProfile);
        oAuthAccountRepository.save(oAuthAccount);

        user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found", HttpStatus.UNAUTHORIZED));

        leaderboardService.addLeaderBoard(user);
        enrollmentService.enrollUserToAllLessons(user.getUserId());

        userLearningStatsService.addUserLearningStats(user);
        return user;
    }

    public User googleOAuth(OAuth2User oAuth2User){

        User user = userRepository.findByEmail(oAuth2User.getAttribute("email"))
                .orElseGet(() -> registerViaGoogle(oAuth2User));
        
        if(oAuthAccountRepository.findByUser_UserId(user.getUserId()).getProvider().equals("Local")){
            throw new RegisterException("You already registered. Please login with your email and password", HttpStatus.BAD_REQUEST);
        }

        return user;
    }

    public String updateAccessToken(String accessToken, UUID userID){

        OAuthAccount oAuthAccount = oAuthAccountRepository.findByUser_UserId(userID);

        oAuthAccount.setAccessToken(accessToken);

        oAuthAccountRepository.save(oAuthAccount);

        return accessToken;
    }

    public void setEmailVerified(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found", HttpStatus.NOT_FOUND));

        user.setEmailVerified(true);
        userRepository.save(user);
    }
}
