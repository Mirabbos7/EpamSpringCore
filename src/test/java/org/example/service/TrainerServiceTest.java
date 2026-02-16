package org.example.service;

import org.example.dao.TrainerDao;
import org.example.dao.UserDao;
import org.example.entity.Trainer;
import org.example.entity.User;
import org.example.utils.PasswordGenerator;
import org.example.utils.UsernameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private UserDao userDao;

    @Mock
    private UsernameGenerator usernameGenerator;

    @Mock
    private PasswordGenerator passwordGenerator;

    @InjectMocks
    private TrainerService trainerService;

    private User testUser;
    private Trainer testTrainer;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setFirstName("Sarah");
        testUser.setLastName("Williams");
        testUser.setUsername("Sarah.Williams");
        testUser.setPassword("Pass123456");
        testUser.setActive(true);

        testTrainer = new Trainer();
        testTrainer.setTrainerId(1L);
        testTrainer.setUserId(1L);
        testTrainer.setSpecialization("Fitness");
    }

    @Test
    void create_ShouldCreateTrainerSuccessfully() {
        // Arrange
        String firstName = "Sarah";
        String lastName = "Williams";
        String specialization = "Fitness";

        when(usernameGenerator.generateUsername(eq(firstName), eq(lastName), any()))
                .thenReturn("Sarah.Williams");
        when(passwordGenerator.generatePassword()).thenReturn("Pass123456");
        when(userDao.create(any(User.class))).thenReturn(testUser);
        when(trainerDao.create(any(Trainer.class))).thenReturn(testTrainer);

        // Act
        Trainer result = trainerService.create(firstName, lastName, specialization);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getTrainerId());
        assertEquals(1L, result.getUserId());
        assertEquals(specialization, result.getSpecialization());

        verify(usernameGenerator, times(1)).generateUsername(eq(firstName), eq(lastName), any());
        verify(passwordGenerator, times(1)).generatePassword();
        verify(userDao, times(1)).create(any(User.class));
        verify(trainerDao, times(1)).create(any(Trainer.class));
    }

    @Test
    void create_ShouldSetUserFieldsCorrectly() {
        // Arrange
        String firstName = "Mike";
        String lastName = "Johnson";
        String specialization = "Yoga";

        when(usernameGenerator.generateUsername(anyString(), anyString(), any()))
                .thenReturn("Mike.Johnson");
        when(passwordGenerator.generatePassword()).thenReturn("TestPass99");
        when(userDao.create(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            assertEquals("Mike", user.getFirstName());
            assertEquals("Johnson", user.getLastName());
            assertEquals("Mike.Johnson", user.getUsername());
            assertEquals("TestPass99", user.getPassword());
            assertTrue(user.isActive());
            user.setUserId(2L);
            return user;
        });
        when(trainerDao.create(any(Trainer.class))).thenReturn(testTrainer);

        // Act
        trainerService.create(firstName, lastName, specialization);

        // Assert
        verify(userDao, times(1)).create(argThat(user ->
                user.getFirstName().equals("Mike") &&
                        user.getLastName().equals("Johnson") &&
                        user.getUsername().equals("Mike.Johnson") &&
                        user.getPassword().equals("TestPass99") &&
                        user.isActive()
        ));
    }

    @Test
    void create_ShouldSetTrainerFieldsCorrectly() {
        // Arrange
        String firstName = "Anna";
        String lastName = "Smith";
        String specialization = "CrossFit";

        when(usernameGenerator.generateUsername(anyString(), anyString(), any()))
                .thenReturn("Anna.Smith");
        when(passwordGenerator.generatePassword()).thenReturn("Pass999");

        User createdUser = new User();
        createdUser.setUserId(5L);
        when(userDao.create(any(User.class))).thenReturn(createdUser);

        when(trainerDao.create(any(Trainer.class))).thenAnswer(invocation -> {
            Trainer trainer = invocation.getArgument(0);
            assertEquals(5L, trainer.getUserId());
            assertEquals("CrossFit", trainer.getSpecialization());
            return trainer;
        });

        // Act
        trainerService.create(firstName, lastName, specialization);

        // Assert
        verify(trainerDao, times(1)).create(argThat(trainer ->
                trainer.getUserId().equals(5L) &&
                        trainer.getSpecialization().equals("CrossFit")
        ));
    }

    @Test
    void update_ShouldUpdateTrainerSuccessfully() {
        // Arrange
        Long trainerId = 1L;
        String newSpecialization = "Pilates";

        when(trainerDao.findById(trainerId)).thenReturn(Optional.of(testTrainer));
        when(trainerDao.update(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Trainer result = trainerService.update(trainerId, newSpecialization);

        // Assert
        assertNotNull(result);
        assertEquals(newSpecialization, result.getSpecialization());

        verify(trainerDao, times(1)).findById(trainerId);
        verify(trainerDao, times(1)).update(any(Trainer.class));
    }

    @Test
    void update_ShouldThrowExceptionWhenTrainerNotFound() {
        // Arrange
        Long trainerId = 999L;
        String specialization = "Fitness";

        when(trainerDao.findById(trainerId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> trainerService.update(trainerId, specialization)
        );

        assertEquals("Trainer not found", exception.getMessage());
        verify(trainerDao, times(1)).findById(trainerId);
        verify(trainerDao, never()).update(any(Trainer.class));
    }

    @Test
    void select_ShouldReturnTrainerWhenExists() {
        // Arrange
        Long trainerId = 1L;

        when(trainerDao.findById(trainerId)).thenReturn(Optional.of(testTrainer));

        // Act
        Optional<Trainer> result = trainerService.select(trainerId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testTrainer, result.get());
        verify(trainerDao, times(1)).findById(trainerId);
    }

    @Test
    void select_ShouldReturnEmptyWhenTrainerNotExists() {
        // Arrange
        Long trainerId = 999L;

        when(trainerDao.findById(trainerId)).thenReturn(Optional.empty());

        // Act
        Optional<Trainer> result = trainerService.select(trainerId);

        // Assert
        assertFalse(result.isPresent());
        verify(trainerDao, times(1)).findById(trainerId);
    }

    @Test
    void selectAll_ShouldReturnAllTrainers() {
        // Arrange
        Trainer trainer2 = new Trainer();
        trainer2.setTrainerId(2L);
        trainer2.setUserId(2L);
        trainer2.setSpecialization("Yoga");

        List<Trainer> trainers = Arrays.asList(testTrainer, trainer2);

        when(trainerDao.findAll()).thenReturn(trainers);

        // Act
        List<Trainer> result = trainerService.selectAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testTrainer, result.get(0));
        assertEquals(trainer2, result.get(1));
        verify(trainerDao, times(1)).findAll();
    }

    @Test
    void selectAll_ShouldReturnEmptyListWhenNoTrainers() {
        // Arrange
        when(trainerDao.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Trainer> result = trainerService.selectAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(trainerDao, times(1)).findAll();
    }
}