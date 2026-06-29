package com.minimarket.controller;

import com.minimarket.dto.LoginRequestDTO;
import com.minimarket.security.audit.SecurityAuditLogger;
import com.minimarket.security.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final SecurityAuditLogger auditLogger;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, SecurityAuditLogger auditLogger) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.auditLogger = auditLogger;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest, HttpServletRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            String username = authentication.getName();
            String token = jwtUtil.generateToken(username);

            auditLogger.loginSuccess(username,request.getRemoteAddr());
            return ResponseEntity.ok(Map.of("token", token));
        } catch (AuthenticationException e) {
            auditLogger.loginFailure(loginRequest.getUsername(),
                    request.getRemoteAddr(), "credenciales invalidas");
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
        }
    }
}
