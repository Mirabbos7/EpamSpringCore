package org.example.service;

import org.example.dao.TraineeDao;
import org.example.entity.Trainee;
import org.example.entity.User;
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
    private UserService userService;

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
        when(userService.createUser("John", "Doe")).thenReturn(testUser);
        when(traineeDao.create(any(Trainee.class))).thenReturn(testTrainee);

        Trainee result = traineeService.create("John", "Doe", LocalDate.of(1990, 1, 1), "123 Test St");

        assertNotNull(result);
        assertEquals(1L, result.getTraineeId());
        assertEquals(1L, result.getUserId());

        verify(userService, times(1)).createUser("John", "Doe");
        verify(traineeDao, times(1)).create(any(Trainee.class));
    }

    @Test
    void update_ShouldUpdateTraineeSuccessfully() {
        when(traineeDao.findById(1L)).thenReturn(Optional.of(testTrainee));
        when(traineeDao.update(any(Trainee.class))).thenAnswer(i -> i.getArgument(0));

        Trainee result = traineeService.update(1L, LocalDate.of(1991, 2, 2), "456 New St");

        assertNotNull(result);
        assertEquals(LocalDate.of(1991, 2, 2), result.getDateOfBirth());
        assertEquals("456 New St", result.getAddress());

        verify(traineeDao, times(1)).findById(1L);
        verify(traineeDao, times(1)).update(any(Trainee.class));
    }

    @Test
    void update_ShouldThrowExceptionWhenTraineeNotFound() {
        when(traineeDao.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> traineeService.update(999L, LocalDate.of(1990, 1, 1), "Test Address"));

        verify(traineeDao, never()).update(any(Trainee.class));
    }

    @Test
    void delete_ShouldDeleteTraineeSuccessfully() {
        traineeService.delete(1L);
        verify(traineeDao, times(1)).delete(1L);
    }

    @Test
    void select_ShouldReturnTraineeWhenExists() {
        when(traineeDao.findById(1L)).thenReturn(Optional.of(testTrainee));

        Optional<Trainee> result = traineeService.select(1L);

        assertTrue(result.isPresent());
        assertEquals(testTrainee, result.get());
    }

    @Test
    void select_ShouldReturnEmptyWhenNotExists() {
        when(traineeDao.findById(999L)).thenReturn(Optional.empty());

        Optional<Trainee> result = traineeService.select(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void selectAll_ShouldReturnAllTrainees() {
        List<Trainee> trainees = Arrays.asList(testTrainee, new Trainee());
        when(traineeDao.findAll()).thenReturn(trainees);

        List<Trainee> result = traineeService.selectAll();

        assertEquals(2, result.size());
        verify(traineeDao, times(1)).findAll();
    }
}