package com.ashutosh.WorkSphere.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {

            String username = jwtService.extractUsername(token);

            boolean passwordChangeToken = jwtService.isPasswordChangeToken(token);

            // This will restrict new account to perform task rather than password change first.
            if (passwordChangeToken &&
                    (!"POST".equalsIgnoreCase(request.getMethod()) || !request.getServletPath().endsWith("/auth/change-password"))) {

                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Password change required before accessing other endpoints");
                return;
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                boolean valid = jwtService.isTokenValid(token, userDetails);

                if (valid) {
                    CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;

                    boolean passwordChangeRequired = customUserDetails.getUser().isPasswordChangeRequired();
                    boolean passwordChangeToken1 = jwtService.isPasswordChangeToken(token);
                    boolean isPasswordChangeEndpoint = "POST".equalsIgnoreCase(request.getMethod()) && request.getServletPath().endsWith("/auth/change-password");

                    if (passwordChangeRequired) {
                        if (!passwordChangeToken1 || !isPasswordChangeEndpoint) {
                            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Password change required before accessing other endpoints");
                            return;
                        }
                    } else if (passwordChangeToken1) {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Password-change token is no longer valid");
                        return;
                    }

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }

        } catch (Exception exception) {
            System.out.println(
                    "JWT ERROR: " + exception.getMessage()
            );
        }
        filterChain.doFilter(request, response);
    }
}