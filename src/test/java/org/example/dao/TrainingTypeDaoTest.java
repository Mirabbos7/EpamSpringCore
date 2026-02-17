package org.example.dao;

import org.example.entity.TrainingType;
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
class TrainingTypeDaoTest {

    @Mock
    private InMemoryStorage storage;

    @InjectMocks
    private TrainingTypeDao trainingTypeDao;

    private Map<Long, TrainingType> trainingTypeMap;
    private TrainingType testTrainingType;

    @BeforeEach
    void setUp() {
        trainingTypeMap = new HashMap<>();

        testTrainingType = new TrainingType();
        testTrainingType.setTrainingTypeId(1L);
        testTrainingType.setTrainingTypeName("Cardio");

        when(storage.getTrainingTypeStorage()).thenReturn(trainingTypeMap);
    }

    @Test
    void create_ShouldAssignIdAndSaveTrainingType() {
        when(storage.getNextTrainingTypeId()).thenReturn(1L);

        TrainingType result = trainingTypeDao.create(testTrainingType);

        assertEquals(1L, result.getTrainingTypeId());
        assertTrue(trainingTypeMap.containsKey(1L));
        verify(storage, times(1)).getNextTrainingTypeId();
    }

    @Test
    void update_ShouldUpdateTrainingTypeInStorage() {
        trainingTypeMap.put(1L, testTrainingType);
        testTrainingType.setTrainingTypeName("Yoga");

        TrainingType result = trainingTypeDao.update(testTrainingType);

        assertEquals("Yoga", result.getTrainingTypeName());
        assertEquals("Yoga", trainingTypeMap.get(1L).getTrainingTypeName());
    }

    @Test
    void delete_ShouldRemoveTrainingTypeFromStorage() {
        trainingTypeMap.put(1L, testTrainingType);

        trainingTypeDao.delete(1L);

        assertFalse(trainingTypeMap.containsKey(1L));
    }

    @Test
    void findById_ShouldReturnTrainingTypeWhenExists() {
        trainingTypeMap.put(1L, testTrainingType);

        Optional<TrainingType> result = trainingTypeDao.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(testTrainingType, result.get());
    }

    @Test
    void findById_ShouldReturnEmptyWhenNotExists() {
        Optional<TrainingType> result = trainingTypeDao.findById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void findByName_ShouldReturnTrainingTypeWhenExists() {
        trainingTypeMap.put(1L, testTrainingType);

        Optional<TrainingType> result = trainingTypeDao.findByName("Cardio");

        assertTrue(result.isPresent());
        assertEquals(testTrainingType, result.get());
    }

    @Test
    void findByName_ShouldReturnEmptyWhenNotExists() {
        Optional<TrainingType> result = trainingTypeDao.findByName("Unknown");

        assertFalse(result.isPresent());
    }

    @Test
    void findAll_ShouldReturnAllTrainingTypes() {
        TrainingType type2 = new TrainingType();
        type2.setTrainingTypeId(2L);
        type2.setTrainingTypeName("Strength");
        trainingTypeMap.put(1L, testTrainingType);
        trainingTypeMap.put(2L, type2);

        List<TrainingType> result = trainingTypeDao.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void findAll_ShouldReturnEmptyListWhenNoTrainingTypes() {
        List<TrainingType> result = trainingTypeDao.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}