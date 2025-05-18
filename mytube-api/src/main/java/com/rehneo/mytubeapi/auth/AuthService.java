package com.rehneo.mytubeapi.auth;

import com.rehneo.mytubeapi.security.JwtService;
import com.rehneo.mytubeapi.user.*;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final UserService userService;

    @Transactional
    public AuthResponse signUp(AuthRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException(
                    "User with username " + request.getUsername() + " already exists"
            );
        }
        userRepository.save(user);
        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return new AuthResponse(
                token,
                refreshToken,
                userMapper.map(user)
        );
    }

    @Transactional
    public AuthResponse signIn(AuthRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        return new AuthResponse(
                token,
                refreshToken,
                userMapper.map(userService.getByUsername(request.getUsername()))
        );
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String username;
        try {
            username = jwtService.extractUsername(request.getRefreshToken());
        }catch (JwtException ex){
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new InvalidRefreshTokenException("Invalid refresh token")
        );
        if(jwtService.isTokenValid(request.getRefreshToken(), user)) {
            String token = jwtService.generateToken(user);
            return new AuthResponse(
                    token,
                    request.getRefreshToken(),
                    userMapper.map(user)
            );
        }
        throw new InvalidRefreshTokenException("Invalid refresh token");
    }
}