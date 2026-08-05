package com.example.coreris.security;

import com.example.coreris.dto.AuthResponseDto;
import com.example.coreris.dto.LoginRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {

        log.debug("Authentication attempt initiated for username: '{}'", loginRequestDto.getUsername());

        //sp note :- authenticate the user using userDetailsService and check if user present in recode or DB and either give authentication object or throw exception
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken( // sp note :- creating the token and sending it to authentication manager
                            loginRequestDto.getUsername(),
                            loginRequestDto.getPassword()
                    )
            );
        } catch(AuthenticationException ex) {
            log.warn("Authentication failed for username: '{}' - Invalid credentials", loginRequestDto.getUsername());
            throw ex;
        }
        //sp note :- gating UserDetails object from authentication
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String token = jwtService.generateToken(userDetails);

        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        log.info("User '{}' successfully authenticated with role: {}", loginRequestDto.getUsername(), role);

        return ResponseEntity.ok(
                AuthResponseDto.builder()
                        .token(token)
                        .username(userDetails.getUsername())
                        .role(role)
                        .build()
        );
    }
}

