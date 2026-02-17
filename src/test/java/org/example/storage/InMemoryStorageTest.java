package org.example.storage;

import org.example.entity.Trainee;
import org.example.entity.Trainer;
import org.example.entity.Training;
import org.example.entity.TrainingType;
import org.example.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryStorageTest {

    private InMemoryStorage storage;

    @BeforeEach
    void setUp() {
        storage = new InMemoryStorage(List.of());
    }

    @Test
    void getNextUserId_ShouldIncrementEachCall() {
        assertEquals(1L, storage.getNextUserId());
        assertEquals(2L, storage.getNextUserId());
        assertEquals(3L, storage.getNextUserId());
    }

    @Test
    void getNextTraineeId_ShouldIncrementEachCall() {
        assertEquals(1L, storage.getNextTraineeId());
        assertEquals(2L, storage.getNextTraineeId());
    }

    @Test
    void getNextTrainerId_ShouldIncrementEachCall() {
        assertEquals(1L, storage.getNextTrainerId());
        assertEquals(2L, storage.getNextTrainerId());
    }

    @Test
    void getNextTrainingId_ShouldIncrementEachCall() {
        assertEquals(1L, storage.getNextTrainingId());
        assertEquals(2L, storage.getNextTrainingId());
    }

    @Test
    void getNextTrainingTypeId_ShouldIncrementEachCall() {
        assertEquals(1L, storage.getNextTrainingTypeId());
        assertEquals(2L, storage.getNextTrainingTypeId());
    }

    @Test
    void userStorage_ShouldStoreAndRetrieveUser() {
        User user = new User(1L, "John", "Doe", "John.Doe", "pass", true);
        storage.getUserStorage().put(1L, user);

        assertEquals(user, storage.getUserStorage().get(1L));
        assertEquals(1, storage.getUserStorage().size());
    }

    @Test
    void traineeStorage_ShouldStoreAndRetrieveTrainee() {
        Trainee trainee = new Trainee(1L, LocalDate.of(1990, 1, 1), "address", 1L);
        storage.getTraineeStorage().put(1L, trainee);

        assertEquals(trainee, storage.getTraineeStorage().get(1L));
    }

    @Test
    void trainerStorage_ShouldStoreAndRetrieveTrainer() {
        Trainer trainer = new Trainer(1L, "Fitness", 1L);
        storage.getTrainerStorage().put(1L, trainer);

        assertEquals(trainer, storage.getTrainerStorage().get(1L));
    }

    @Test
    void trainingStorage_ShouldStoreAndRetrieveTraining() {
        Training training = new Training(1L, 1L, 1L, "name", "type", 60, LocalDate.now());
        storage.getTrainingStorage().put(1L, training);

        assertEquals(training, storage.getTrainingStorage().get(1L));
    }

    @Test
    void trainingTypeStorage_ShouldStoreAndRetrieveTrainingType() {
        TrainingType type = new TrainingType(1L, "Cardio");
        storage.getTrainingTypeStorage().put(1L, type);

        assertEquals(type, storage.getTrainingTypeStorage().get(1L));
    }

    @Test
    void loadFile_ShouldThrowRuntimeExceptionWhenFileNotFound() {
        assertThrows(RuntimeException.class,
                () -> invokeLoadFile(storage, "nonexistent/file.txt"));
    }

    @Test
    void parseLine_ShouldSkipEmptyLines() {
        invokeParseLine(storage, "");
        assertTrue(storage.getUserStorage().isEmpty());
        assertTrue(storage.getTraineeStorage().isEmpty());
    }

    @Test
    void parseLine_ShouldSkipCommentLines() {
        invokeParseLine(storage, "# this is a comment");
        assertTrue(storage.getUserStorage().isEmpty());
    }

    @Test
    void parseLine_ShouldParseTrainingType() {
        invokeParseLine(storage, "1,Cardio");
        assertEquals(1, storage.getTrainingTypeStorage().size());
        assertEquals("Cardio", storage.getTrainingTypeStorage().get(1L).getTrainingTypeName());
    }

    @Test
    void parseLine_ShouldParseUser() {
        invokeParseLine(storage, "1,John,Doe,John.Doe,pass123,true");
        assertEquals(1, storage.getUserStorage().size());
        assertEquals("John", storage.getUserStorage().get(1L).getFirstName());
    }

    @Test
    void parseLine_ShouldParseTrainee_WhenUserExists() {
        invokeParseLine(storage, "1,John,Doe,John.Doe,pass123,true");
        invokeParseLine(storage, "1,1990-01-01,Test Address,1");
        assertEquals(1, storage.getTraineeStorage().size());
    }

    @Test
    void parseLine_ShouldSkipTrainee_WhenUserNotExists() {
        invokeParseLine(storage, "1,1990-01-01,Test Address,999");
        assertTrue(storage.getTraineeStorage().isEmpty());
    }

    @Test
    void parseLine_ShouldParseTrainer_WhenUserExists() {
        invokeParseLine(storage, "1,John,Doe,John.Doe,pass123,true");
        invokeParseLine(storage, "1,Fitness,1");
        assertEquals(1, storage.getTrainerStorage().size());
    }

    @Test
    void parseLine_ShouldSkipTrainer_WhenUserNotExists() {
        invokeParseLine(storage, "1,Fitness,999");
        assertTrue(storage.getTrainerStorage().isEmpty());
    }

    @Test
    void parseLine_ShouldParseTraining_WhenTraineeAndTrainerExist() {
        // user
        invokeParseLine(storage, "1,John,Doe,John.Doe,pass123,true");
        invokeParseLine(storage, "2,Jane,Doe,Jane.Doe,pass456,true");
        // trainee
        invokeParseLine(storage, "1,1990-01-01,Address1,1");
        // trainer
        invokeParseLine(storage, "1,Fitness,2");
        // training
        invokeParseLine(storage, "1,1,1,Morning Workout,Fitness,60,2024-01-15");
        assertEquals(1, storage.getTrainingStorage().size());
    }

    @Test
    void parseLine_ShouldSkipTraining_WhenTraineeNotExists() {
        invokeParseLine(storage, "1,999,1,Morning Workout,Fitness,60,2024-01-15");
        assertTrue(storage.getTrainingStorage().isEmpty());
    }

    @Test
    void parseLine_ShouldSkipUnknownFormat() {
        invokeParseLine(storage, "1,2,3,4,5");
        assertTrue(storage.getUserStorage().isEmpty());
        assertTrue(storage.getTraineeStorage().isEmpty());
    }

    @Test
    void parseLine_ShouldSkipInvalidData() {
        invokeParseLine(storage, "notANumber,Cardio");
        assertTrue(storage.getTrainingTypeStorage().isEmpty());
    }

    // TODO:
    //  Using reflection in tests is usually a signal of a design issue!
    //  You are testing implementation details instead of behavior
    //  If the parsing/loading logic is important enough to have dedicated unit tests,
    //  consider extracting it into a separate class or making methods package-private as a compromise.
    private void invokeParseLine(InMemoryStorage storage, String line) {
        try {
            var method = InMemoryStorage.class.getDeclaredMethod("parseLine", String.class);
            method.setAccessible(true);
            method.invoke(storage, line);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void invokeLoadFile(InMemoryStorage storage, String filePath) {
        try {
            var method = InMemoryStorage.class.getDeclaredMethod("loadFile", String.class);
            method.setAccessible(true);
            method.invoke(storage, filePath);
        } catch (java.lang.reflect.InvocationTargetException e) {
            throw (RuntimeException) e.getCause();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}