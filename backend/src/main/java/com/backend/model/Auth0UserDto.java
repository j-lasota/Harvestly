package com.backend.model;

import lombok.Data;

@Data
public class Auth0UserDto {
    private String userId;      // event.user.user_id
    private String email;       // event.user.email
    private String name;        // event.user.name
    private String createdAt;   // event.user.created_at
}
