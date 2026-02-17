package org.example.dao;

import org.example.entity.User;
import org.example.storage.InMemoryStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDaoTest {

    @Mock
    private InMemoryStorage storage;

    @InjectMocks
    private UserDao userDao;

    private Map<Long, User> userMap;
    private User testUser;

    @BeforeEach
    void setUp() {
        userMap = new HashMap<>();

        testUser = new User();
        testUser.setUserId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setUsername("John.Doe");
        testUser.setPassword("Pass123456");
        testUser.setActive(true);

        when(storage.getUserStorage()).thenReturn(userMap);
    }

    @Test
    void create_ShouldAssignIdAndSaveUser() {
        when(storage.getNextUserId()).thenReturn(1L);

        User result = userDao.create(testUser);

        assertEquals(1L, result.getUserId());
        assertTrue(userMap.containsKey(1L));
        verify(storage, times(1)).getNextUserId();
    }

    @Test
    void update_ShouldUpdateUserInStorage() {
        userMap.put(1L, testUser);
        testUser.setUsername("John.Doe2");

        User result = userDao.update(testUser);

        assertEquals("John.Doe2", result.getUsername());
        assertEquals("John.Doe2", userMap.get(1L).getUsername());
    }

    @Test
    void delete_ShouldRemoveUserFromStorage() {
        userMap.put(1L, testUser);

        userDao.delete(1L);

        assertFalse(userMap.containsKey(1L));
    }

    @Test
    void findById_ShouldReturnUserWhenExists() {
        userMap.put(1L, testUser);

        Optional<User> result = userDao.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
    }

    @Test
    void findById_ShouldReturnEmptyWhenNotExists() {
        Optional<User> result = userDao.findById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void findByUsername_ShouldReturnUserWhenExists() {
        userMap.put(1L, testUser);

        Optional<User> result = userDao.findByUsername("John.Doe");

        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
    }

    @Test
    void findByUsername_ShouldReturnEmptyWhenNotExists() {
        Optional<User> result = userDao.findByUsername("Unknown");

        assertFalse(result.isPresent());
    }

    @Test
    void findAll_ShouldReturnAllUsers() {
        User user2 = new User();
        user2.setUserId(2L);
        user2.setUsername("Jane.Doe");
        userMap.put(1L, testUser);
        userMap.put(2L, user2);

        List<User> result = userDao.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void findAll_ShouldReturnEmptyListWhenNoUsers() {
        List<User> result = userDao.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void existsByUsername_ShouldReturnTrueWhenExists() {
        userMap.put(1L, testUser);

        boolean result = userDao.existsByUsername("John.Doe");

        assertTrue(result);
    }

    @Test
    void existsByUsername_ShouldReturnFalseWhenNotExists() {
        boolean result = userDao.existsByUsername("Unknown");

        assertFalse(result);
    }
}