package org.example.service;

import org.example.dao.TrainerDao;
import org.example.entity.Trainer;
import org.example.entity.User;
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
    private UserService userService;

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
        when(userService.createUser("Sarah", "Williams")).thenReturn(testUser);
        when(trainerDao.create(any(Trainer.class))).thenReturn(testTrainer);

        Trainer result = trainerService.create("Sarah", "Williams", "Fitness");

        assertNotNull(result);
        assertEquals(1L, result.getTrainerId());
        assertEquals(1L, result.getUserId());
        assertEquals("Fitness", result.getSpecialization());

        verify(userService, times(1)).createUser("Sarah", "Williams");
        verify(trainerDao, times(1)).create(any(Trainer.class));
    }

    @Test
    void update_ShouldUpdateTrainerSuccessfully() {
        when(trainerDao.findById(1L)).thenReturn(Optional.of(testTrainer));
        when(trainerDao.update(any(Trainer.class))).thenAnswer(i -> i.getArgument(0));

        Trainer result = trainerService.update(1L, "Pilates");

        assertNotNull(result);
        assertEquals("Pilates", result.getSpecialization());

        verify(trainerDao, times(1)).findById(1L);
        verify(trainerDao, times(1)).update(any(Trainer.class));
    }

    @Test
    void update_ShouldThrowExceptionWhenTrainerNotFound() {
        when(trainerDao.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> trainerService.update(999L, "Fitness"));

        verify(trainerDao, never()).update(any(Trainer.class));
    }

    @Test
    void select_ShouldReturnTrainerWhenExists() {
        when(trainerDao.findById(1L)).thenReturn(Optional.of(testTrainer));

        Optional<Trainer> result = trainerService.select(1L);

        assertTrue(result.isPresent());
        assertEquals(testTrainer, result.get());
    }

    @Test
    void select_ShouldReturnEmptyWhenNotExists() {
        when(trainerDao.findById(999L)).thenReturn(Optional.empty());

        Optional<Trainer> result = trainerService.select(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void selectAll_ShouldReturnAllTrainers() {
        List<Trainer> trainers = Arrays.asList(testTrainer, new Trainer());
        when(trainerDao.findAll()).thenReturn(trainers);

        List<Trainer> result = trainerService.selectAll();

        assertEquals(2, result.size());
        verify(trainerDao, times(1)).findAll();
    }

    @Test
    void selectAll_ShouldReturnEmptyList() {
        when(trainerDao.findAll()).thenReturn(Arrays.asList());

        List<Trainer> result = trainerService.selectAll();

        assertTrue(result.isEmpty());
    }
}