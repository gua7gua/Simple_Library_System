package edu.cupk.simple_library_system.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cupk.simple_library_system.common.ApiResponse;
import edu.cupk.simple_library_system.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    private final TokenService tokenService;
    private final ObjectMapper objectMapper;

    public AuthInterceptor(TokenService tokenService, ObjectMapper objectMapper) {
        this.tokenService = tokenService;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getParameter("token");
        if (token == null || token.isBlank()) {
            token = request.getHeader("token");
        }
        Integer userId = tokenService.verify(token);
        if (userId == null) {
            response.setStatus(200);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.fail("Token无效或已过期")));
            return false;
        }
        request.setAttribute("currentUserId", userId);
        return true;
    }
}
