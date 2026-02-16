package org.example.service;

import org.example.dao.TrainingDao;
import org.example.entity.Training;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    private TrainingDao trainingDao;

    @InjectMocks
    private TrainingService trainingService;

    private Training testTraining;

    @BeforeEach
    void setUp() {
        testTraining = new Training();
        testTraining.setTrainingId(1L);
        testTraining.setTraineeId(1L);
        testTraining.setTrainerId(1L);
        testTraining.setTrainingName("Morning Workout");
        testTraining.setTrainingType("Fitness");
        testTraining.setTrainingDuration(60);
        testTraining.setTrainingDate(LocalDate.of(2024, 1, 15));
    }

    @Test
    void create_ShouldCreateTrainingSuccessfully() {
        // Arrange
        Long traineeId = 1L;
        Long trainerId = 1L;
        String trainingName = "Morning Workout";
        String trainingType = "Fitness";
        int duration = 60;
        LocalDate date = LocalDate.of(2024, 1, 15);

        when(trainingDao.create(any(Training.class))).thenReturn(testTraining);

        // Act
        Training result = trainingService.create(traineeId, trainerId, trainingName, trainingType, duration, date);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getTrainingId());
        assertEquals(traineeId, result.getTraineeId());
        assertEquals(trainerId, result.getTrainerId());
        assertEquals(trainingName, result.getTrainingName());
        assertEquals(trainingType, result.getTrainingType());
        assertEquals(duration, result.getTrainingDuration());
        assertEquals(date, result.getTrainingDate());

        verify(trainingDao, times(1)).create(any(Training.class));
    }

    @Test
    void create_ShouldSetAllFieldsCorrectly() {
        // Arrange
        Long traineeId = 2L;
        Long trainerId = 3L;
        String trainingName = "Evening Yoga";
        String trainingType = "Yoga";
        int duration = 90;
        LocalDate date = LocalDate.of(2024, 2, 20);

        when(trainingDao.create(any(Training.class))).thenAnswer(invocation -> {
            Training training = invocation.getArgument(0);
            assertEquals(traineeId, training.getTraineeId());
            assertEquals(trainerId, training.getTrainerId());
            assertEquals(trainingName, training.getTrainingName());
            assertEquals(trainingType, training.getTrainingType());
            assertEquals(duration, training.getTrainingDuration());
            assertEquals(date, training.getTrainingDate());
            training.setTrainingId(5L);
            return training;
        });

        // Act
        Training result = trainingService.create(traineeId, trainerId, trainingName, trainingType, duration, date);

        // Assert
        assertEquals(5L, result.getTrainingId());
        verify(trainingDao, times(1)).create(argThat(training ->
                training.getTraineeId().equals(traineeId) &&
                        training.getTrainerId().equals(trainerId) &&
                        training.getTrainingName().equals(trainingName) &&
                        training.getTrainingType().equals(trainingType) &&
                        training.getTrainingDuration() == duration &&
                        training.getTrainingDate().equals(date)
        ));
    }

    @Test
    void select_ShouldReturnTrainingWhenExists() {
        // Arrange
        Long trainingId = 1L;

        when(trainingDao.findById(trainingId)).thenReturn(Optional.of(testTraining));

        // Act
        Optional<Training> result = trainingService.select(trainingId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testTraining, result.get());
        verify(trainingDao, times(1)).findById(trainingId);
    }

    @Test
    void select_ShouldReturnEmptyWhenTrainingNotExists() {
        // Arrange
        Long trainingId = 999L;

        when(trainingDao.findById(trainingId)).thenReturn(Optional.empty());

        // Act
        Optional<Training> result = trainingService.select(trainingId);

        // Assert
        assertFalse(result.isPresent());
        verify(trainingDao, times(1)).findById(trainingId);
    }

    @Test
    void selectAll_ShouldReturnAllTrainings() {
        // Arrange
        Training training2 = new Training();
        training2.setTrainingId(2L);
        training2.setTraineeId(2L);
        training2.setTrainerId(2L);
        training2.setTrainingName("Evening Run");
        training2.setTrainingType("Cardio");
        training2.setTrainingDuration(45);
        training2.setTrainingDate(LocalDate.of(2024, 1, 16));

        List<Training> trainings = Arrays.asList(testTraining, training2);

        when(trainingDao.findAll()).thenReturn(trainings);

        // Act
        List<Training> result = trainingService.selectAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testTraining, result.get(0));
        assertEquals(training2, result.get(1));
        verify(trainingDao, times(1)).findAll();
    }

    @Test
    void selectAll_ShouldReturnEmptyListWhenNoTrainings() {
        // Arrange
        when(trainingDao.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Training> result = trainingService.selectAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(trainingDao, times(1)).findAll();
    }

    @Test
    void selectByTraineeId_ShouldReturnTrainingsForTrainee() {
        // Arrange
        Long traineeId = 1L;

        Training training2 = new Training();
        training2.setTrainingId(2L);
        training2.setTraineeId(1L);
        training2.setTrainerId(2L);
        training2.setTrainingName("Afternoon Yoga");
        training2.setTrainingType("Yoga");
        training2.setTrainingDuration(75);
        training2.setTrainingDate(LocalDate.of(2024, 1, 17));

        List<Training> trainings = Arrays.asList(testTraining, training2);

        when(trainingDao.findByTraineeId(traineeId)).thenReturn(trainings);

        // Act
        List<Training> result = trainingService.selectByTraineeId(traineeId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(t -> t.getTraineeId().equals(traineeId)));
        verify(trainingDao, times(1)).findByTraineeId(traineeId);
    }

    @Test
    void selectByTraineeId_ShouldReturnEmptyListWhenNoTrainingsForTrainee() {
        // Arrange
        Long traineeId = 999L;

        when(trainingDao.findByTraineeId(traineeId)).thenReturn(Arrays.asList());

        // Act
        List<Training> result = trainingService.selectByTraineeId(traineeId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(trainingDao, times(1)).findByTraineeId(traineeId);
    }

    @Test
    void selectByTrainerId_ShouldReturnTrainingsForTrainer() {
        // Arrange
        Long trainerId = 1L;

        Training training2 = new Training();
        training2.setTrainingId(3L);
        training2.setTraineeId(3L);
        training2.setTrainerId(1L);
        training2.setTrainingName("Morning CrossFit");
        training2.setTrainingType("CrossFit");
        training2.setTrainingDuration(50);
        training2.setTrainingDate(LocalDate.of(2024, 1, 18));

        List<Training> trainings = Arrays.asList(testTraining, training2);

        when(trainingDao.findByTrainerId(trainerId)).thenReturn(trainings);

        // Act
        List<Training> result = trainingService.selectByTrainerId(trainerId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(t -> t.getTrainerId().equals(trainerId)));
        verify(trainingDao, times(1)).findByTrainerId(trainerId);
    }

    @Test
    void selectByTrainerId_ShouldReturnEmptyListWhenNoTrainingsForTrainer() {
        // Arrange
        Long trainerId = 999L;

        when(trainingDao.findByTrainerId(trainerId)).thenReturn(Arrays.asList());

        // Act
        List<Training> result = trainingService.selectByTrainerId(trainerId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(trainingDao, times(1)).findByTrainerId(trainerId);
    }
}