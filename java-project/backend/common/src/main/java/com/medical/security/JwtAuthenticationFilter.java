package com.medical.security;

import com.medical.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);
            String path = request.getRequestURI();
            System.out.println("[JwtAuthenticationFilter] 请求路径: " + path);

            if (StringUtils.hasText(jwt)) {
                if (jwtUtils.validateToken(jwt)) {
                    Long userId = jwtUtils.getUserIdFromToken(jwt);
                    String role = jwtUtils.getRoleFromToken(jwt);

                    UserDetails userDetails = userDetailsService.loadUserById(userId);

                    List<GrantedAuthority> authorities = Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + role)
                    );

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, authorities
                    );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    // Token无效，清除上下文
                    logger.warn("无效的JWT令牌");
                    SecurityContextHolder.clearContext();
                }
            } else if (!shouldNotFilter(request) && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 对于需要认证的路径，如果没有JWT且SecurityContext中没有认证信息，记录日志
                logger.warn("请求需要认证但未提供有效令牌: " + path);
            }
        } catch (Exception e) {
            logger.error("无法设置用户认证: " + e.getMessage(), e);
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // 允许OPTIONS请求通过
        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }
        
        return path.equals("/api/users/login")
                || path.equals("/api/users/register")
                || path.equals("/api/appointments/today/count")
                || path.startsWith("/api/appointments/doctor/")
                || path.equals("/api/doctors/active/count")
                || path.equals("/api/appointments/waiting/count")
                // 移除对medicine-inventory的过滤，确保它需要认证
                // || path.equals("/api/medicine-inventory")
                || path.equals("/api/patient/list")
                || path.startsWith("/api/patient/user/")
                || path.equals("/api/medicine-stock-records")
                || path.matches("/api/appointments/user/\\d+/page")
                || path.startsWith("/api/medicine/stock/")
                || path.equals("/api/departments")
                || path.startsWith("/api/departments/")
                || path.equals("/api/doctor-schedules")
                || path.startsWith("/api/doctor-schedules/")
                || path.equals("/api/appointments")
                || path.equals("/api/medical-records/take-medicine")
                || path.equals("/api/medical-records")
                || path.startsWith("/api/medical-records/patient/")
                || path.startsWith("/api/medical-records/doctor/")
                || path.startsWith("/api/medical-records/detail/")
                || path.startsWith("/api/swagger")
                || path.startsWith("/api/v2/api-docs")
                || path.startsWith("/api/doc.html")
                || path.startsWith("/api/webjars/")
                || path.startsWith("/api/favicon.ico")
                || path.equals("/api/error");
    }
}