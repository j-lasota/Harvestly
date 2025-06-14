package com.backend.security;

import com.backend.repository.OpinionRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("opinionSecurity")
public class OpinionSecurity {
    private final OpinionRepository opinions;

    public OpinionSecurity(OpinionRepository opinions) {
        this.opinions = opinions;
    }

    public boolean isAuthor(Authentication authentication, Long opinionId) {
        return opinions.findById(opinionId)
                .map(o -> o.getUser().getId().equals(authentication.getName()))
                .orElse(false);
    }
}
