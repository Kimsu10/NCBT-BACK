package kr.kh.backend.controller;

import kr.kh.backend.domain.User;
import kr.kh.backend.dto.security.LoginDTO;
import kr.kh.backend.mapper.UserMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

@SpringBootTest
public class MyBatisTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserMapper userMapper;

    @Test
    @DisplayName("AIT_02_HikariCP 구축 테스트")
    public void ConnectionPoolTest() {
        Assertions.assertNotNull(dataSource);
        try {
            Connection connection = dataSource.getConnection();
            Assertions.assertEquals(false, connection.isClosed());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @Transactional
    @DisplayName("AIT_03_MyBatis 구축 테스트")
    public void MyBatisTest() {
        // given
        LoginDTO testUser = new LoginDTO();
        testUser.setUsername("tester");
        testUser.setPassword("password");
        testUser.setEmail("tester@tester.com");
        testUser.setRoles("USER");

        // when
        userMapper.insertUser(testUser);

        // then
        List<User> allUsers = userMapper.findAll();
        User user = allUsers.get(allUsers.size() - 1);
        Assertions.assertEquals("tester", user.getUsername());
    }

}
