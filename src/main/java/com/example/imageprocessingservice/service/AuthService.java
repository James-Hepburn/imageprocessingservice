package com.example.imageprocessingservice.service;

import com.example.imageprocessingservice.model.User;
import com.example.imageprocessingservice.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String SECRET = "K9f3nQ8pL2vW7tXzRm4uE6sH1bG0yPK9f3nQ8pL2vW7tXzRm4uE6sH1bG0yPK9f3nQ8pL2vW7tXzRm4uE6sH1bG0yP";
    private long EXPIRATION = 3600000;

    public User register (String username, String password) {
        if (this.userRepository.existsByUsername (username)) {
            throw new RuntimeException ("Username already taken");
        }

        User user = new User (username, this.passwordEncoder.encode (password));
        return this.userRepository.save (user);
    }

    public String login (String username, String password) {
        Optional <User> userOptional = this.userRepository.findByUsername (username);

        if (userOptional.isEmpty ()) {
            throw new RuntimeException ("Invalid credentials");
        }

        User user = userOptional.get ();

        if (!this.passwordEncoder.matches (password, user.getPassword ())) {
            throw new RuntimeException ("Invalid credentials");
        }

        return generateToken (user);
    }

    private String generateToken (User user) {
        return Jwts.builder ()
                .setSubject (user.getUsername ())
                .setIssuedAt (new Date ())
                .setExpiration (new Date (System.currentTimeMillis () + EXPIRATION))
                .signWith (SignatureAlgorithm.HS256, SECRET.getBytes ())
                .compact ();
    }
}