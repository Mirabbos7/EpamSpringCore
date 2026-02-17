package org.example.dao;

import org.example.entity.Training;
import org.example.storage.InMemoryStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingDaoTest {

    @Mock
    private InMemoryStorage storage;

    @InjectMocks
    private TrainingDao trainingDao;

    private Map<Long, Training> trainingMap;
    private Training testTraining;

    @BeforeEach
    void setUp() {
        trainingMap = new HashMap<>();

        testTraining = new Training();
        testTraining.setTrainingId(1L);
        testTraining.setTraineeId(1L);
        testTraining.setTrainerId(1L);
        testTraining.setTrainingName("Morning Workout");
        testTraining.setTrainingType("Fitness");
        testTraining.setTrainingDuration(60);
        testTraining.setTrainingDate(LocalDate.of(2024, 1, 15));

        when(storage.getTrainingStorage()).thenReturn(trainingMap);
    }

    @Test
    void create_ShouldAssignIdAndSaveTraining() {
        when(storage.getNextTrainingId()).thenReturn(1L);

        Training result = trainingDao.create(testTraining);

        assertEquals(1L, result.getTrainingId());
        assertTrue(trainingMap.containsKey(1L));
        verify(storage, times(1)).getNextTrainingId();
    }

    @Test
    void update_ShouldUpdateTrainingInStorage() {
        trainingMap.put(1L, testTraining);
        testTraining.setTrainingName("Evening Workout");

        Training result = trainingDao.update(testTraining);

        assertEquals("Evening Workout", result.getTrainingName());
        assertEquals("Evening Workout", trainingMap.get(1L).getTrainingName());
    }

    @Test
    void delete_ShouldRemoveTrainingFromStorage() {
        trainingMap.put(1L, testTraining);

        trainingDao.delete(1L);

        assertFalse(trainingMap.containsKey(1L));
    }

    @Test
    void findById_ShouldReturnTrainingWhenExists() {
        trainingMap.put(1L, testTraining);

        Optional<Training> result = trainingDao.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(testTraining, result.get());
    }

    @Test
    void findById_ShouldReturnEmptyWhenNotExists() {
        Optional<Training> result = trainingDao.findById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void findByTraineeId_ShouldReturnTrainingsForTrainee() {
        Training training2 = new Training();
        training2.setTrainingId(2L);
        training2.setTraineeId(1L);
        training2.setTrainerId(2L);
        trainingMap.put(1L, testTraining);
        trainingMap.put(2L, training2);

        List<Training> result = trainingDao.findByTraineeId(1L);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(t -> t.getTraineeId().equals(1L)));
    }

    @Test
    void findByTraineeId_ShouldReturnEmptyWhenNoMatch() {
        trainingMap.put(1L, testTraining);

        List<Training> result = trainingDao.findByTraineeId(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void findByTrainerId_ShouldReturnTrainingsForTrainer() {
        Training training2 = new Training();
        training2.setTrainingId(2L);
        training2.setTraineeId(2L);
        training2.setTrainerId(1L);
        trainingMap.put(1L, testTraining);
        trainingMap.put(2L, training2);

        List<Training> result = trainingDao.findByTrainerId(1L);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(t -> t.getTrainerId().equals(1L)));
    }

    @Test
    void findByTrainerId_ShouldReturnEmptyWhenNoMatch() {
        trainingMap.put(1L, testTraining);

        List<Training> result = trainingDao.findByTrainerId(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_ShouldReturnAllTrainings() {
        Training training2 = new Training();
        training2.setTrainingId(2L);
        trainingMap.put(1L, testTraining);
        trainingMap.put(2L, training2);

        List<Training> result = trainingDao.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void findAll_ShouldReturnEmptyListWhenNoTrainings() {
        List<Training> result = trainingDao.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}