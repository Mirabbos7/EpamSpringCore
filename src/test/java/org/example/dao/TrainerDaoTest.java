package org.example.dao;

import org.example.entity.Trainer;
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
class TrainerDaoTest {

    @Mock
    private InMemoryStorage storage;

    @InjectMocks
    private TrainerDao trainerDao;

    private Map<Long, Trainer> trainerMap;
    private Trainer testTrainer;

    @BeforeEach
    void setUp() {
        trainerMap = new HashMap<>();

        testTrainer = new Trainer();
        testTrainer.setTrainerId(1L);
        testTrainer.setUserId(1L);
        testTrainer.setSpecialization("Fitness");

        when(storage.getTrainerStorage()).thenReturn(trainerMap);
    }

    @Test
    void create_ShouldAssignIdAndSaveTrainer() {
        when(storage.getNextTrainerId()).thenReturn(1L);

        Trainer result = trainerDao.create(testTrainer);

        assertEquals(1L, result.getTrainerId());
        assertTrue(trainerMap.containsKey(1L));
        verify(storage, times(1)).getNextTrainerId();
    }

    @Test
    void update_ShouldUpdateTrainerInStorage() {
        trainerMap.put(1L, testTrainer);
        testTrainer.setSpecialization("Yoga");

        Trainer result = trainerDao.update(testTrainer);

        assertEquals("Yoga", result.getSpecialization());
        assertEquals("Yoga", trainerMap.get(1L).getSpecialization());
    }

    @Test
    void delete_ShouldRemoveTrainerFromStorage() {
        trainerMap.put(1L, testTrainer);

        trainerDao.delete(1L);

        assertFalse(trainerMap.containsKey(1L));
    }

    @Test
    void findById_ShouldReturnTrainerWhenExists() {
        trainerMap.put(1L, testTrainer);

        Optional<Trainer> result = trainerDao.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(testTrainer, result.get());
    }

    @Test
    void findById_ShouldReturnEmptyWhenNotExists() {
        Optional<Trainer> result = trainerDao.findById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void findByUserId_ShouldReturnTrainerWhenExists() {
        trainerMap.put(1L, testTrainer);

        Optional<Trainer> result = trainerDao.findByUserId(1L);

        assertTrue(result.isPresent());
        assertEquals(testTrainer, result.get());
    }

    @Test
    void findByUserId_ShouldReturnEmptyWhenNotExists() {
        Optional<Trainer> result = trainerDao.findByUserId(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void findAll_ShouldReturnAllTrainers() {
        Trainer trainer2 = new Trainer();
        trainer2.setTrainerId(2L);
        trainerMap.put(1L, testTrainer);
        trainerMap.put(2L, trainer2);

        List<Trainer> result = trainerDao.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void findAll_ShouldReturnEmptyListWhenNoTrainers() {
        List<Trainer> result = trainerDao.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}