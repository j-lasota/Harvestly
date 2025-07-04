package com.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Auth0UserDto {

    @NotBlank
    @JsonProperty("user_id")
    private String userId;

    private String email;

    @JsonProperty("given_name")
    private String givenName;

    @JsonProperty("family_name")
    private String familyName;

    @JsonProperty("nickname")
    private String name;

    @JsonProperty("img")
    private String img;

    @JsonProperty("created_at")
    private String createdAt;
}
