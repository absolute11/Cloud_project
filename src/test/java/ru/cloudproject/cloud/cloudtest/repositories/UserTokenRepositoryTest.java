package ru.cloudproject.cloud.cloudtest.repositories;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.cloudproject.cloud.cloudtest.entities.User;
import ru.cloudproject.cloud.cloudtest.entities.UserToken;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserTokenRepositoryTest {
    @Autowired
    private UserTokenRepository userTokenRepository;
    private UserToken userToken;

    @BeforeEach
    public void init() {
        userToken = new UserToken();
        userToken.setEmail("testm@mail.ru");
        userToken.setToken("token");
    }

    @Test
    public void UserTokenRepositoryTest_findByEmail(){
        userTokenRepository.save(userToken);
        Assertions.assertNotNull(userTokenRepository.findByEmail(userToken.getEmail()));
    }

    @Test
    public void UserTokenTest_deleteTokenByEmail() {
        userTokenRepository.save(userToken);
        userTokenRepository.deleteTokenByEmail(userToken.getEmail());
        Assertions.assertNull(userTokenRepository.findByEmail(userToken.getEmail()));
    }
}
