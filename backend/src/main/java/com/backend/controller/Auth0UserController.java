package com.backend.controller;

import com.backend.model.Auth0UserDto;
import com.backend.service.Auth0UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auth0")
@RequiredArgsConstructor
public class Auth0UserController {
    private final Auth0UserService auth0UserService;

    // 1) Batch sync
    @PostMapping("/sync-users")
    public ResponseEntity<Void> syncUsers() {
        auth0UserService.synchronizeUsers();
        return ResponseEntity.ok().build();
    }

    // 2) Webhook – przyjmuje pojedynczego
    @PostMapping("/auth0-user")
    public ResponseEntity<Void> syncSingleUser(@RequestBody Auth0UserDto user) {
        auth0UserService.processIncomingUsers(List.of(user));
        return ResponseEntity.ok().build();
    }
}