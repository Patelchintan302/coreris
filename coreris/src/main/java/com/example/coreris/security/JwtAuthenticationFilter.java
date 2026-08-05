package com.example.coreris.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.MDC;

import java.io.IOException;
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if(authHeader == null || !authHeader.startsWith("Bearer ")) { // sp note :-it is not attempting to log in it can be login request or public endpoint
            filterChain.doFilter(request,response);
            return;
        }

        final String jwt = authHeader.substring(7); // sp note :- removing "Bearer " prefix to get remaining jwt token
        final String username = jwtService.extractUsername(jwt);
        try {
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username); // sp note :- lode user details from the DB

                if (jwtService.isTokenValid(jwt, userDetails)) {//sp note :- check whether the token is expired and user matches the token in defined in JwtService
                    //sp note :- create auth token containing user, credentials and authorities or role
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    //sp note :- attach details of http request to authToken
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    //sp note :- Authenticate the user by placing them into the SecurityContextHolder
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    //Store the logged-in username in MDC context
                    MDC.put("username", username);
                }
            }
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
