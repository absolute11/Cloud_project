package ru.cloudproject.cloud.cloudtest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.cloudproject.cloud.cloudtest.repositories.UserRepository;
import ru.cloudproject.cloud.cloudtest.utils.JwtUtil;
import ru.cloudproject.cloud.cloudtest.utils.LoginRequest;

import javax.naming.AuthenticationException;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/cloud")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private AuthenticationManager authManager;
    private PasswordEncoder passwordEncoder;

@Autowired
    public AuthController(UserRepository userRepository, JwtUtil jwtUtil, AuthenticationManager authManager, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.authManager = authManager;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public Map<String, Object> loginHandler(@RequestBody LoginRequest body){
        UsernamePasswordAuthenticationToken authInputToken =
                new UsernamePasswordAuthenticationToken(body.getEmail(), body.getPassword());

        authManager.authenticate(authInputToken);

        String token = jwtUtil.generateToken(body.getEmail());

        return Collections.singletonMap("auth-token", token);
    }
}
