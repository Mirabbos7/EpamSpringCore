package org.example.service;

import org.example.dao.TraineeDao;
import org.example.dao.UserDao;
import org.example.entity.Trainee;
import org.example.entity.User;
import org.example.utils.PasswordGenerator;
import org.example.utils.UsernameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private UserDao userDao;

    @Mock
    private UsernameGenerator usernameGenerator;

    @Mock
    private PasswordGenerator passwordGenerator;

    @InjectMocks
    private TraineeService traineeService;

    private User testUser;
    private Trainee testTrainee;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setUsername("John.Doe");
        testUser.setPassword("Pass123456");
        testUser.setActive(true);

        testTrainee = new Trainee();
        testTrainee.setTraineeId(1L);
        testTrainee.setUserId(1L);
        testTrainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        testTrainee.setAddress("123 Test St");
    }

    @Test
    void create_ShouldCreateTraineeSuccessfully() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        LocalDate dateOfBirth = LocalDate.of(1990, 1, 1);
        String address = "123 Test St";

        when(usernameGenerator.generateUsername(eq(firstName), eq(lastName), any()))
                .thenReturn("John.Doe");
        when(passwordGenerator.generatePassword()).thenReturn("Pass123456");
        when(userDao.create(any(User.class))).thenReturn(testUser);
        when(traineeDao.create(any(Trainee.class))).thenReturn(testTrainee);

        // Act
        Trainee result = traineeService.create(firstName, lastName, dateOfBirth, address);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getTraineeId());
        assertEquals(1L, result.getUserId());
        assertEquals(dateOfBirth, result.getDateOfBirth());
        assertEquals(address, result.getAddress());

        verify(usernameGenerator, times(1)).generateUsername(eq(firstName), eq(lastName), any());
        verify(passwordGenerator, times(1)).generatePassword();
        verify(userDao, times(1)).create(any(User.class));
        verify(traineeDao, times(1)).create(any(Trainee.class));
    }

    @Test
    void create_ShouldSetUserFieldsCorrectly() {
        // Arrange
        String firstName = "Jane";
        String lastName = "Smith";
        LocalDate dateOfBirth = LocalDate.of(1995, 5, 15);
        String address = "456 Main Ave";

        when(usernameGenerator.generateUsername(anyString(), anyString(), any()))
                .thenReturn("Jane.Smith");
        when(passwordGenerator.generatePassword()).thenReturn("TestPass99");
        when(userDao.create(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            assertEquals("Jane", user.getFirstName());
            assertEquals("Smith", user.getLastName());
            assertEquals("Jane.Smith", user.getUsername());
            assertEquals("TestPass99", user.getPassword());
            assertTrue(user.isActive());
            user.setUserId(2L);
            return user;
        });
        when(traineeDao.create(any(Trainee.class))).thenReturn(testTrainee);

        // Act
        traineeService.create(firstName, lastName, dateOfBirth, address);

        // Assert
        verify(userDao, times(1)).create(argThat(user ->
                user.getFirstName().equals("Jane") &&
                        user.getLastName().equals("Smith") &&
                        user.getUsername().equals("Jane.Smith") &&
                        user.getPassword().equals("TestPass99") &&
                        user.isActive()
        ));
    }

    @Test
    void update_ShouldUpdateTraineeSuccessfully() {
        // Arrange
        Long traineeId = 1L;
        LocalDate newDateOfBirth = LocalDate.of(1991, 2, 2);
        String newAddress = "456 New St";

        when(traineeDao.findById(traineeId)).thenReturn(Optional.of(testTrainee));
        when(traineeDao.update(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Trainee result = traineeService.update(traineeId, newDateOfBirth, newAddress);

        // Assert
        assertNotNull(result);
        assertEquals(newDateOfBirth, result.getDateOfBirth());
        assertEquals(newAddress, result.getAddress());

        verify(traineeDao, times(1)).findById(traineeId);
        verify(traineeDao, times(1)).update(any(Trainee.class));
    }

    @Test
    void update_ShouldThrowExceptionWhenTraineeNotFound() {
        // Arrange
        Long traineeId = 999L;
        LocalDate dateOfBirth = LocalDate.of(1990, 1, 1);
        String address = "Test Address";

        when(traineeDao.findById(traineeId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> traineeService.update(traineeId, dateOfBirth, address)
        );

        assertEquals("Trainee not found", exception.getMessage());
        verify(traineeDao, times(1)).findById(traineeId);
        verify(traineeDao, never()).update(any(Trainee.class));
    }

    @Test
    void delete_ShouldDeleteTraineeSuccessfully() {
        // Arrange
        Long traineeId = 1L;

        doNothing().when(traineeDao).delete(traineeId);

        // Act
        traineeService.delete(traineeId);

        // Assert
        verify(traineeDao, times(1)).delete(traineeId);
    }

    @Test
    void select_ShouldReturnTraineeWhenExists() {
        // Arrange
        Long traineeId = 1L;

        when(traineeDao.findById(traineeId)).thenReturn(Optional.of(testTrainee));

        // Act
        Optional<Trainee> result = traineeService.select(traineeId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testTrainee, result.get());
        verify(traineeDao, times(1)).findById(traineeId);
    }

    @Test
    void select_ShouldReturnEmptyWhenTraineeNotExists() {
        // Arrange
        Long traineeId = 999L;

        when(traineeDao.findById(traineeId)).thenReturn(Optional.empty());

        // Act
        Optional<Trainee> result = traineeService.select(traineeId);

        // Assert
        assertFalse(result.isPresent());
        verify(traineeDao, times(1)).findById(traineeId);
    }

    @Test
    void selectAll_ShouldReturnAllTrainees() {
        // Arrange
        Trainee trainee2 = new Trainee();
        trainee2.setTraineeId(2L);
        trainee2.setUserId(2L);
        trainee2.setDateOfBirth(LocalDate.of(1992, 3, 3));
        trainee2.setAddress("789 Another St");

        List<Trainee> trainees = Arrays.asList(testTrainee, trainee2);

        when(traineeDao.findAll()).thenReturn(trainees);

        // Act
        List<Trainee> result = traineeService.selectAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testTrainee, result.get(0));
        assertEquals(trainee2, result.get(1));
        verify(traineeDao, times(1)).findAll();
    }

    @Test
    void selectAll_ShouldReturnEmptyListWhenNoTrainees() {
        // Arrange
        when(traineeDao.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Trainee> result = traineeService.selectAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(traineeDao, times(1)).findAll();
    }
}