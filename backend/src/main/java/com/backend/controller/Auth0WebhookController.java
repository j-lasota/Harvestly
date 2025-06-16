package com.backend.controller;

import com.backend.model.Auth0UserDto;
import com.backend.service.Auth0UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth0")
@RequiredArgsConstructor
@Slf4j
public class Auth0WebhookController {

    private final Auth0UserService auth0UserService;

    @Value("${auth0.webhook.secret}")
    private String expectedSecret;

    @PostMapping("/user-webhook")
    public ResponseEntity<Void> handleUserWebhook(
            @RequestHeader("X-Auth0-Webhook-Secret") String receivedSecret,
            @RequestBody Auth0UserDto userDto) {

        if (receivedSecret == null || !receivedSecret.equals(expectedSecret)) {
            log.warn("Unauthorized webhook access attempt. Invalid secret.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (userDto == null || userDto.getUserId() == null || userDto.getEmail() == null) {
            log.error("Invalid user DTO received from webhook: {}", userDto);
            return ResponseEntity.badRequest().build();
        }

        try {
            log.info("Processing webhook for user: {}", userDto.getUserId());
            auth0UserService.processIncomingUser(userDto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error processing user webhook for user: " + userDto.getUserId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}