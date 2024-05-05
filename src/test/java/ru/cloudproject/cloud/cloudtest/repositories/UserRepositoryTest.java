package ru.cloudproject.cloud.cloudtest.repositories;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.cloudproject.cloud.cloudtest.entities.User;

import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void init() {
        user = new User();
        user.setEmail("testm@mail.ru");
        user.setPassword("1234");
    }

    @Test
    public void UserRepositoryTest_existsByEmail() {
        userRepository.save(user);

        Assertions.assertTrue(userRepository.existsByEmail(user.getEmail()));
    }

    @Test
    public void UserRepositoryTest_deleteUserByEmail(){
        userRepository.deleteUserByEmail(user.getEmail());
        Assertions.assertFalse(userRepository.existsByEmail(user.getEmail()));
    }

    @Test
    public void UserRepositoryTest_findUserByEmail(){
        User user1 = userRepository.findUserByEmail(user.getEmail());
        Assertions.assertNull(user1);
    }
}
