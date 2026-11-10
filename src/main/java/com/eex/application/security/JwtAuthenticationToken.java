// Before: Missing JwtAuthenticationToken class
// No code here

// After: Create JwtAuthenticationToken class
package com.eex.application.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private final Integer userId;
    private final Integer clientId;

    public JwtAuthenticationToken(Integer userId, Integer clientId, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.userId = userId;
        this.clientId = clientId;
        setAuthenticated(true); // Mark as authenticated
    }

    @Override
    public Object getCredentials() {
        return null; // No credentials needed for JWT
    }

    @Override
    public Object getPrincipal() {
        return userId; // Return userId as the principal
    }

    public Integer getClientId() {
        return clientId;
    }
}