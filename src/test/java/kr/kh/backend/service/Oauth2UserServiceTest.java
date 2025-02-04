package kr.kh.backend.service;

import kr.kh.backend.exception.CustomException;
import kr.kh.backend.service.security.Oauth2UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

@SpringBootTest
public class Oauth2UserServiceTest {

    @Autowired
    private Oauth2UserService oauth2UserService;

    @Test
    @DisplayName("UT_01_소셜로그인/회원가입 테스트_인가 코드 에러로 실패")
    public void accessCodeConfirmTest() {
        // given
        String accessCode = "test-code";
        String state = "test-state";

        // when-then
        Assertions.assertAll(
                () -> {
                    Throwable exception = Assertions.assertThrows(CustomException.class, () -> {
                        oauth2UserService.getNaverUser(accessCode, state);
                    });
                    Assertions.assertEquals("SUPPLIER CODE ERROR", exception.getMessage());
                },
                () -> {
                    Throwable exception = Assertions.assertThrows(CustomException.class, () -> {
                        oauth2UserService.getGithubUser(accessCode);
                    });
                    Assertions.assertEquals("SUPPLIER CODE ERROR", exception.getMessage());
                }
        );
    }

}
