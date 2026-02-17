package org.example.dao;

import org.example.entity.Trainee;
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
class TraineeDaoTest {

    @Mock
    private InMemoryStorage storage;

    @InjectMocks
    private TraineeDao traineeDao;

    private Map<Long, Trainee> traineeMap;
    private Trainee testTrainee;

    @BeforeEach
    void setUp() {
        traineeMap = new HashMap<>();

        testTrainee = new Trainee();
        testTrainee.setTraineeId(1L);
        testTrainee.setUserId(1L);
        testTrainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        testTrainee.setAddress("123 Test St");

        when(storage.getTraineeStorage()).thenReturn(traineeMap);
    }

    @Test
    void create_ShouldAssignIdAndSaveTrainee() {
        when(storage.getNextTraineeId()).thenReturn(1L);

        Trainee result = traineeDao.create(testTrainee);

        assertEquals(1L, result.getTraineeId());
        assertTrue(traineeMap.containsKey(1L));
        verify(storage, times(1)).getNextTraineeId();
    }

    @Test
    void update_ShouldUpdateTraineeInStorage() {
        traineeMap.put(1L, testTrainee);
        testTrainee.setAddress("New Address");

        Trainee result = traineeDao.update(testTrainee);

        assertEquals("New Address", result.getAddress());
        assertEquals("New Address", traineeMap.get(1L).getAddress());
    }

    @Test
    void delete_ShouldRemoveTraineeFromStorage() {
        traineeMap.put(1L, testTrainee);

        traineeDao.delete(1L);

        assertFalse(traineeMap.containsKey(1L));
    }

    @Test
    void findById_ShouldReturnTraineeWhenExists() {
        traineeMap.put(1L, testTrainee);

        Optional<Trainee> result = traineeDao.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(testTrainee, result.get());
    }

    @Test
    void findById_ShouldReturnEmptyWhenNotExists() {
        Optional<Trainee> result = traineeDao.findById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void findByUserId_ShouldReturnTraineeWhenExists() {
        traineeMap.put(1L, testTrainee);

        Optional<Trainee> result = traineeDao.findByUserId(1L);

        assertTrue(result.isPresent());
        assertEquals(testTrainee, result.get());
    }

    @Test
    void findByUserId_ShouldReturnEmptyWhenNotExists() {
        Optional<Trainee> result = traineeDao.findByUserId(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void findAll_ShouldReturnAllTrainees() {
        Trainee trainee2 = new Trainee();
        trainee2.setTraineeId(2L);
        traineeMap.put(1L, testTrainee);
        traineeMap.put(2L, trainee2);

        List<Trainee> result = traineeDao.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void findAll_ShouldReturnEmptyListWhenNoTrainees() {
        List<Trainee> result = traineeDao.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}