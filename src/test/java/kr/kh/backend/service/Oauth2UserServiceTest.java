package kr.kh.backend.service;

import kr.kh.backend.domain.User;
import kr.kh.backend.dto.oauth2.OauthUserDTO;
import kr.kh.backend.exception.CustomException;
import kr.kh.backend.mapper.UserMapper;
import kr.kh.backend.service.security.Oauth2UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class Oauth2UserServiceTest {

    @Mock
    private UserMapper userMapperMock;

    @InjectMocks
    private Oauth2UserService userServiceMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Autowired
    private Oauth2UserService oauth2UserService;

    @Test
    @DisplayName("AIT_04_소셜로그인/회원가입 테스트_인가 코드 에러로 실패")
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
                    HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> {
                        oauth2UserService.getGithubUser(accessCode);
                    });
                    Assertions.assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
                }
        );
    }

    @Test
    @DisplayName("UT_01_소셜로그인/회원가입_기등록 이메일로 실패")
    public void existEmailFailureTest() {
        // given
        OauthUserDTO oauthUserDTO = new OauthUserDTO();
        oauthUserDTO.setUsername("test-username");
        oauthUserDTO.setEmail("test-email");
        oauthUserDTO.setRole("USER");

        // mock
        when(userMapperMock.findByEmail(oauthUserDTO.getEmail()))
                .thenReturn(User.builder()
                        .nickname(oauthUserDTO.getUsername())
                        .email(oauthUserDTO.getEmail())
                        .roles(oauthUserDTO.getRole())
                        .platform(oauthUserDTO.getPlatform())
                        .build());

        // when-then
        Throwable exception = Assertions.assertThrows(CustomException.class, () -> {
            userServiceMock.loadOauthUser(oauthUserDTO);
        });
        Assertions.assertEquals("Email already exists", exception.getMessage());
    }

    @Test
    @DisplayName("UT_01_소셜로그인/회원가입_회원가입 성공")
    public void oauthUserLoginSuccessTest() {
        // given
        OauthUserDTO oauthUserDTO = new OauthUserDTO();
        oauthUserDTO.setUsername("test-username");
        oauthUserDTO.setEmail("test-email");
        oauthUserDTO.setRole("USER");
        oauthUserDTO.setPlatform("naver");

        // mock
        when(userMapperMock.findByEmail(oauthUserDTO.getEmail())).thenReturn(null);

        // when
        Authentication authentication = userServiceMock.loadOauthUser(oauthUserDTO);

        // then
        Assertions.assertAll(
                () -> {
                    Assertions.assertNotNull(authentication);
                },
                () -> {
                    User userInfo = (User) authentication.getPrincipal();
                    Assertions.assertEquals("test-username", userInfo.getUsername());
                }
        );
    }

}
