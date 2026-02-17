package org.example.service;

import org.example.dao.UserDao;
import org.example.entity.User;
import org.example.utils.PasswordGenerator;
import org.example.utils.UsernameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private UsernameGenerator usernameGenerator;

    @Mock
    private PasswordGenerator passwordGenerator;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setUsername("John.Doe");
        testUser.setPassword("Pass123456");
        testUser.setActive(true);
    }

    @Test
    void createUser_ShouldCreateUserSuccessfully() {
        when(usernameGenerator.generateUsername(eq("John"), eq("Doe"), any()))
                .thenReturn("John.Doe");
        when(passwordGenerator.generatePassword()).thenReturn("Pass123456");
        when(userDao.create(any(User.class))).thenReturn(testUser);

        User result = userService.createUser("John", "Doe");

        assertNotNull(result);
        assertEquals("John.Doe", result.getUsername());
        assertEquals("Pass123456", result.getPassword());
        assertTrue(result.isActive());

        verify(usernameGenerator, times(1)).generateUsername(eq("John"), eq("Doe"), any());
        verify(passwordGenerator, times(1)).generatePassword();
        verify(userDao, times(1)).create(any(User.class));
    }

    @Test
    void createUser_ShouldSetAllFieldsCorrectly() {
        when(usernameGenerator.generateUsername(anyString(), anyString(), any()))
                .thenReturn("John.Doe");
        when(passwordGenerator.generatePassword()).thenReturn("Pass123456");
        when(userDao.create(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.createUser("John", "Doe");

        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("John.Doe", result.getUsername());
        assertEquals("Pass123456", result.getPassword());
        assertTrue(result.isActive());
    }

    @Test
    void createUser_ShouldPassExistsByUsernameToGenerator() {
        when(usernameGenerator.generateUsername(eq("John"), eq("Doe"), any()))
                .thenReturn("John.Doe1");
        when(passwordGenerator.generatePassword()).thenReturn("Pass123456");
        when(userDao.create(any(User.class))).thenReturn(testUser);

        userService.createUser("John", "Doe");

        verify(usernameGenerator, times(1))
                .generateUsername(eq("John"), eq("Doe"), any());
    }
}