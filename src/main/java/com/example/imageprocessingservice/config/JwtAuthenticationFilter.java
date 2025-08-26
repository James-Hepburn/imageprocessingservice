package com.example.imageprocessingservice.config;

import com.example.imageprocessingservice.model.User;
import com.example.imageprocessingservice.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private UserRepository userRepository;

    private String SECRET = "K9f3nQ8pL2vW7tXzRm4uE6sH1bG0yPK9f3nQ8pL2vW7tXzRm4uE6sH1bG0yPK9f3nQ8pL2vW7tXzRm4uE6sH1bG0yP";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader ("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith ("Bearer ")) {
            token = authHeader.substring (7);

            try {
                Claims claims = Jwts.parser ().setSigningKey (this.SECRET.getBytes ()).parseClaimsJws (token).getBody ();
                username = claims.getSubject ();
            } catch (Exception e) {
                System.err.println ("Invalid token");
            }
        }

        if (username != null && SecurityContextHolder.getContext ().getAuthentication () == null) {
            Optional <User> userOpt = this.userRepository.findByUsername (username);

            if (userOpt.isPresent ()) {
                UserDetails userDetails = org.springframework.security.core.userdetails.User
                        .withUsername (userOpt.get ().getUsername ())
                        .password (userOpt.get ().getPassword ())
                        .authorities (Collections.emptyList ())
                        .build ();
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken (userDetails, null, userDetails.getAuthorities ());
                authToken.setDetails (new WebAuthenticationDetailsSource ().buildDetails (request));
                SecurityContextHolder.getContext ().setAuthentication (authToken);
            }
        }

        filterChain.doFilter (request, response);
    }
}