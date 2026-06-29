package com.minimarket.security.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SecurityAuditLogger {
    
    private static final Logger log = LoggerFactory.getLogger("SECURITY_AUDIT");

    public void loginSuccess(String username, String ip){
        log.info("LOGIN:SUCCESS user='{}'", username, ip);
    }

    public void loginFailure(String username, String ip, String reason){
        log.warn("LOGIN_FAILURE user='{}' ip='{}' reason='{}'", username, ip, reason);
    }

    public void accessDenied(String username, String path, String ip){
        log.warn("ACCESS_DENIED user='{}' ip'{}'", username, path, ip);
    }

    public void authenticationFailed(String path, String ip, String reason) {
        log.warn("AUTH_FAILED path='{}' ip='{}' reason'{}'", path, ip, reason);
    }

    public void invalidToken(String ip, String reason){
        log.warn("INVALID_TOKEN ip='{}? reason='{}'", ip, reason);
    }

    public void tokenRefreshed(String ip, String reason){
        log.info("INVALID_TOKEN ip='{}' reason='{}'", ip, reason);
    }

    public void tokenRevoked(String username, String ip){
        log.info("TOKEN_REVOKED user='{}' ip='{}'", username, ip);
    }
}
