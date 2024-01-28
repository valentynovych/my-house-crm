package com.example.myhouse24admin.securityFilter;

import com.example.myhouse24admin.entity.Permission;
import com.example.myhouse24admin.repository.PermissionRepo;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Supplier;
@Component
public class RoleBasedVoter implements AuthorizationManager<RequestAuthorizationContext> {
    private final PermissionRepo permissionRepo;

    public RoleBasedVoter(PermissionRepo permissionRepo) {
        this.permissionRepo = permissionRepo;
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
        if(authentication.get().getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.get().getPrincipal();
            String requestURI = object.getRequest().getRequestURI();
            String[] uris = requestURI.split("/");
            if (uris[2].equals("system-settings")) {
                requestURI = "/" + uris[1] + "/" + uris[2] + "/" + uris[3];
            } else {
                requestURI = "/" + uris[1] + "/" + uris[2];
            }
            Optional<Permission> permission = permissionRepo.findByStaffEmailAndEndpoint(userDetails.getUsername(), requestURI);
            return permission.map(value -> new AuthorizationDecision(value.isAllowed()))
                    .orElseGet(() -> new AuthorizationDecision(true));
        } else {
            return new AuthorizationDecision(false);
        }
    }
}
