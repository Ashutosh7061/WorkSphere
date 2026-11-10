package com.ashutosh.WorkSphere.entity.audit;

import com.ashutosh.WorkSphere.security.CustomUserDetails;
import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuditRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {

            AuditRevisionEntity revision = (AuditRevisionEntity) revisionEntity;

            revision.setUserId(userDetails.getUser().getId());
        }
    }
}