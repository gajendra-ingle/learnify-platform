package com.learnify.auth.service;

import com.learnify.auth.dto.AuthDtos;
import com.learnify.auth.dto.request.LoginRequest;
import com.learnify.auth.dto.request.RegisterRequest;
import com.learnify.auth.dto.response.AuthResponse;
import com.learnify.auth.entity.Role;
import com.learnify.auth.entity.User;
import com.learnify.auth.exception.AuthException;
import com.learnify.auth.kafka.AuthEventPublisher;
import com.learnify.auth.repository.UserRepository;
import com.learnify.auth.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private JwtService jwtService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private RedisTemplate<String, String> redisTemplate;
    @Mock private ValueOperations<String, String> valueOperations;
    @Mock private AuthEventPublisher eventPublisher;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest validRegisterRequest;
    private LoginRequest validLoginRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        validRegisterRequest = RegisterRequest.builder()
              //  .username("testuser")
                .email("test@example.com")
                .password("Test@12345")
                .firstName("John")
                .lastName("Doe")
                .role(Role.STUDENT)
                .build();

        validLoginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("Test@12345")
                .build();

        testUser = User.builder()
                .id(UUID.randomUUID())
               // .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .firstName("John")
                .lastName("Doe")
                .role(Role.STUDENT)
               // .enabled(true)
                .build();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("register - should create user and return auth response")
    void register_Success() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
       // when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateAccessToken(any(), anyString(), anyString())).thenReturn("access_token");
        when(jwtService.generateRefreshToken(any())).thenReturn("refresh_token");
        when(jwtService.getAccessTokenExpiration()).thenReturn(86400000L);

        // When
        AuthResponse response = authService.register(validRegisterRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access_token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh_token");
        assertThat(response.getAccessToken()).isEqualTo("Bearer");
        assertThat(response.getEmail()).isEqualTo("test@example.com");

        verify(userRepository).save(any(User.class));
        verify(eventPublisher).publishUserRegisteredEvent(any(User.class));
    }

    @Test
    @DisplayName("register - should throw EmailAlreadyExistsException when email taken")
    void register_EmailAlreadyExists_ThrowsException() {
        // Given
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // When / Then
        assertThatThrownBy(() -> authService.register(validRegisterRequest))
                .isInstanceOf(AuthException.EmailAlreadyExistsException.class)
                .hasMessageContaining("test@example.com");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("register - should throw UsernameAlreadyExistsException when username taken")
    void register_UsernameAlreadyExists_ThrowsException() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // When / Then
        assertThatThrownBy(() -> authService.register(validRegisterRequest))
                .isInstanceOf(AuthException.UsernameAlreadyExistsException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("login - should return auth response for valid credentials")
    void login_Success() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(jwtService.generateAccessToken(any(), anyString(), anyString())).thenReturn("access_token");
        when(jwtService.generateRefreshToken(any())).thenReturn("refresh_token");
        when(jwtService.getAccessTokenExpiration()).thenReturn(86400000L);

        // When
        AuthResponse response = authService.login(validLoginRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access_token");
        assertThat(response.getEmail()).isEqualTo("test@example.com");

        verify(authenticationManager).authenticate(any());
        verify(userRepository).updateLastLoginAt(eq(testUser.getId()), any());
    }

    @Test
    @DisplayName("login - should throw InvalidCredentialsException for wrong password")
    void login_InvalidCredentials_ThrowsException() {
        // Given
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // When / Then
        assertThatThrownBy(() -> authService.login(validLoginRequest))
                .isInstanceOf(AuthException.InvalidCredentialsException.class);
    }

    @Test
    @DisplayName("validateToken - should return valid response for good token")
    void validateToken_ValidToken_ReturnsValid() {
        // Given
        String token = "valid_token";
        when(redisTemplate.hasKey(contains("blacklisted"))).thenReturn(false);
        when(jwtService.extractUsername(token)).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(jwtService.isTokenValid(eq(token), any())).thenReturn(true);

        // When
        AuthDtos.TokenValidationResponse response = authService.validateToken(token);

        // Then
        assertThat(response.isValid()).isTrue();
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getRole()).isEqualTo("STUDENT");
    }

    @Test
    @DisplayName("validateToken - should return invalid for blacklisted token")
    void validateToken_BlacklistedToken_ReturnsInvalid() {
        // Given
        String token = "blacklisted_token";
        when(redisTemplate.hasKey(contains("blacklisted"))).thenReturn(true);

        // When
        AuthDtos.TokenValidationResponse response = authService.validateToken(token);

        // Then
        assertThat(response.isValid()).isFalse();
        verify(jwtService, never()).extractUsername(any());
    }
}
