package ru.cloudproject.cloud.cloudtest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.cloudproject.cloud.cloudtest.dto.AuthResponseDTO;
import ru.cloudproject.cloud.cloudtest.repositories.UserRepository;
import ru.cloudproject.cloud.cloudtest.services.UserTokenService;
import ru.cloudproject.cloud.cloudtest.security.JwtUtil;
import ru.cloudproject.cloud.cloudtest.utils.LoginRequest;

import java.security.Principal;

@RestController
@RequestMapping("/cloud")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;
    private final UserTokenService userTokenService;

    @Autowired
    public AuthController(UserRepository userRepository, JwtUtil jwtUtil, AuthenticationManager authManager, UserTokenService userTokenService) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.authManager = authManager;
        this.userTokenService = userTokenService;
    }

    @PostMapping("/login")
    @Transactional
    public ResponseEntity<AuthResponseDTO> loginHandler(@RequestBody LoginRequest body) {
        // Создаем аутентификационный токен
        UsernamePasswordAuthenticationToken authInputToken =
                new UsernamePasswordAuthenticationToken(body.getEmail(), body.getPassword());

        // Аутентифицируем пользователя
        authManager.authenticate(authInputToken);

        // Генерируем JWT токен
        String token = jwtUtil.generateToken(body.getEmail());
        userTokenService.saveToken(body.getEmail(), token);

        // Возвращаем ответ с токеном
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(Principal principal) {
        // Удаляем токен из мапы при выходе пользователя
        userTokenService.deleteToken(principal.getName());

        // Возвращаем успешный ответ
        return ResponseEntity.ok().build();
    }


}