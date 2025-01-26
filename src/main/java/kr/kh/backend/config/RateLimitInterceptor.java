package kr.kh.backend.config;

import kr.kh.backend.service.RateLimitingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitingService rateLimitingService;

    @Autowired
    public RateLimitInterceptor(RateLimitingService rateLimitingService) {
        this.rateLimitingService = rateLimitingService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String email = request.getParameter("email");

        if (email == null || email.isBlank()) {
            response.setCharacterEncoding("UTF-8"); // 응답 인코딩 설정
            response.setContentType("text/plain; charset=UTF-8"); // 콘텐츠 타입 설정
            response.setStatus(400);
            response.getWriter().write("이메일 파라미터가 필요합니다.");
            return false;
        }

        if (rateLimitingService.isEmailRequestLimited(email)) {
            response.setCharacterEncoding("UTF-8"); // 응답 인코딩 설정
            response.setContentType("text/plain; charset=UTF-8"); // 콘텐츠 타입 설정
            response.setStatus(429);
            response.getWriter().write("3분 후에 다시 요청해주세요");
            return false;
        }

        return true;
    }

}
