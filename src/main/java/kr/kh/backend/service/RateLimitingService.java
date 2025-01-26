package kr.kh.backend.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitingService {

    private final Map<String, UserRequestInfo> requestCounts = new ConcurrentHashMap<>();

    private static final int LIMIT = 3;
    private static final long DURATION = 3 * 60 * 1000L;

    // clientIP 기반으로 하였으나 로드밸런서 문제로인해 요청제한을 이메일을 기반으로 변경
    public boolean isEmailRequestLimited(String email) {
        long currentTime = System.currentTimeMillis();

        requestCounts.compute(email, (key, info) -> {
            if (info == null || currentTime - info.timestamp > DURATION) {
                return new UserRequestInfo(1, currentTime);
            } else {
                info.requestCount++;
                return info;
            }
        });

        UserRequestInfo info = requestCounts.get(email);
        return info.requestCount > LIMIT;
    }

    private static class UserRequestInfo {
        private int requestCount;
        private long timestamp;

        public UserRequestInfo(int requestCount, long timestamp) {
            this.requestCount = requestCount;
            this.timestamp = timestamp;
        }
    }
}
