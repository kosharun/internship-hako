package com.bitconex.order_management.controller;

import com.bitconex.order_management.dto.AuthRequest;
import com.bitconex.order_management.dto.AuthResponse;
import com.bitconex.order_management.dto.UserDTO;
import com.bitconex.order_management.entity.User;
import com.bitconex.order_management.security.JwtTokenProvider;
import com.bitconex.order_management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            // Pokušaj autentifikacije
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            // Ako uspješno, dohvati korisnika i generiši token
            UserDTO user = userService.getUser(request.getUsername());
            String token = jwtTokenProvider.generateToken(user);

            return ResponseEntity.ok(new AuthResponse(token));

        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }
}
