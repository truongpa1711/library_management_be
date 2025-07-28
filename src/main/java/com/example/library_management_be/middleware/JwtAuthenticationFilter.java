package com.example.library_management_be.middleware;

import com.example.library_management_be.repository.BlacklistTokenRepository;
import com.example.library_management_be.service.CustomUserDetailsService;
import com.example.library_management_be.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService customUserDetailsService;
    private final BlacklistTokenRepository blacklistTokenRepository;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, CustomUserDetailsService customUserDetailsService, BlacklistTokenRepository blacklistTokenRepository) {
        this.jwtUtils = jwtUtils;
        this.customUserDetailsService = customUserDetailsService;
        this.blacklistTokenRepository = blacklistTokenRepository;
    }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api/auth") || path.startsWith("/ws") || path.startsWith("/socket.io");
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                 @NonNull FilterChain filterChain) throws java.io.IOException, ServletException {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"status\":\"error\",\"message\":\"Missing or invalid Authorization header\"}");
                return;
            }

            String jwtToken = authHeader.substring(7); // Remove "Bearer " prefix
            // Kiểm tra xem token có nằm trong blacklist không
            if (blacklistTokenRepository.existsByToken(jwtToken)) {
                System.out.println("Token is blacklisted: " + jwtToken);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"status\":\"error\",\"message\":\"Token is blacklisted\"}");
                return;
            }
            if(jwtUtils.validateAccessToken(jwtToken)){
                String username = jwtUtils.getUsernameFromToken(jwtToken);
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // Gán thông tin về IP và session ID vào authentication token
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set the authentication in the context
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"status\":\"error\",\"message\":\"Invalid JWT token\"}");
                return;
            }


        }catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Unauthorized access\"}");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
