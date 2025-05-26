package com.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Auth0UserDto {
    @NotBlank
    @JsonProperty("user_id")
    private String userId;

    @NotBlank
    private String email;

    @NotBlank
    private String name;    // event.user.name

    private String createdAt;   // event.user.created_at
}
