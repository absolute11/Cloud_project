package ru.cloudproject.cloud.cloudtest.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import ru.cloudproject.cloud.cloudtest.entities.User;
import ru.cloudproject.cloud.cloudtest.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)

public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;
    private User user;

    @Test
    public void testUserService_getUserByEmail(){
        user = new User();
        user.setEmail("testemail@gmail.com");
        user.setPassword("1122");
        Mockito.when(userRepository.findUserByEmail(user.getEmail())).thenReturn(user);
        User userTest = userService.getUserByEmail(user.getEmail());
        Assertions.assertNotNull(userTest);

    }

    @Test
    public void testUserService_existsByUserName(){
        user = new User();
        user.setEmail("testemail@gmail.com");
        user.setPassword("1122");
        Mockito.when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);
        boolean userTest = userService.existByUserName(user.getEmail());
        Assertions.assertTrue(userTest);
    }
}
