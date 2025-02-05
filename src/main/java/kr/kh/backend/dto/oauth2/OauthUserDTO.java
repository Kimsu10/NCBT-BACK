package kr.kh.backend.dto.oauth2;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OauthUserDTO {

    private String username;
    private String email;
    private String role;
    private String platform;

}
