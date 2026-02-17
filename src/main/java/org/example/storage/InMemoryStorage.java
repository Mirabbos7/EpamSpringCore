package org.example.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Trainee;
import org.example.entity.Trainer;
import org.example.entity.Training;
import org.example.entity.TrainingType;
import org.example.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Getter
@Slf4j
public class InMemoryStorage {

    private final Map<Long, User> userStorage = new HashMap<>();
    private final Map<Long, Trainee> traineeStorage = new HashMap<>();
    private final Map<Long, Trainer> trainerStorage = new HashMap<>();
    private final Map<Long, Training> trainingStorage = new HashMap<>();
    private final Map<Long, TrainingType> trainingTypeStorage = new HashMap<>();

    private final AtomicLong userIdCounter = new AtomicLong(1);
    private final AtomicLong traineeIdCounter = new AtomicLong(1);
    private final AtomicLong trainerIdCounter = new AtomicLong(1);
    private final AtomicLong trainingIdCounter = new AtomicLong(1);
    private final AtomicLong trainingTypeIdCounter = new AtomicLong(1);

    @Value("${storage.file.trainingTypes}")
    private String trainingTypeFile;

    @Value("${storage.file.users}")
    private String usersFile;

    @Value("${storage.file.trainees}")
    private String traineesFile;

    @Value("${storage.file.trainers}")
    private String trainersFile;

    @Value("${storage.file.trainings}")
    private String trainingsFile;

    @PostConstruct
    public void init() {
        loadFile(trainingTypeFile);
        loadFile(usersFile);
        loadFile(traineesFile);
        loadFile(trainersFile);
        loadFile(trainingsFile);
    }

    private void loadFile(String filePath) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                parseLine(line.trim());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load file: " + filePath, e);
        }
    }

    private void parseLine(String line) {
        if (line.isEmpty() || line.startsWith("#")) {
            return;
        }

        String[] parts = line.split(",");
        if (parts.length < 2) {
            return;
        }

        try {
            switch (parts.length) {
                case 2 -> parseTrainingType(parts);
                case 3 -> parseTrainer(parts);
                case 4 -> parseTrainee(parts);
                case 6 -> parseUser(parts);
                case 7 -> parseTraining(parts);
                default -> log.warn("Unknown line format, skipping: {}", line);
            }
        } catch (Exception e) {
            log.error("Failed to parse line: {}", line, e);
        }
    }

    private void parseTrainingType(String[] parts) {
        if (parts.length < 2) return;

        Long id = Long.parseLong(parts[0].trim());
        String typeName = parts[1].trim();

        TrainingType trainingType = new TrainingType(id, typeName);
        trainingTypeStorage.put(id, trainingType);
        trainingTypeIdCounter.set(Math.max(trainingTypeIdCounter.get(), id + 1));
    }

    private void parseUser(String[] parts) {
        if (parts.length < 6) return;

        Long userId = Long.parseLong(parts[0].trim());
        String firstName = parts[1].trim();
        String lastName = parts[2].trim();
        String username = parts[3].trim();
        String password = parts[4].trim();
        boolean isActive = Boolean.parseBoolean(parts[5].trim());

        User user = new User(userId, firstName, lastName, username, password, isActive);
        userStorage.put(userId, user);
        userIdCounter.set(Math.max(userIdCounter.get(), userId + 1));
    }

    private void parseTrainee(String[] parts) {
        if (parts.length < 4) return;

        Long traineeId = Long.parseLong(parts[0].trim());
        LocalDate dateOfBirth = LocalDate.parse(parts[1].trim());
        String address = parts[2].trim();
        Long userId = Long.parseLong(parts[3].trim());

        if (!userStorage.containsKey(userId)) {
            log.error("User {} not found for Trainee {}, skipping", userId, traineeId);
            return;
        }

        Trainee trainee = new Trainee(traineeId, dateOfBirth, address, userId);
        traineeStorage.put(traineeId, trainee);
        traineeIdCounter.set(Math.max(traineeIdCounter.get(), traineeId + 1));
    }

    private void parseTrainer(String[] parts) {
        if (parts.length < 3) return;

        Long trainerId = Long.parseLong(parts[0].trim());
        String specialization = parts[1].trim();
        Long userId = Long.parseLong(parts[2].trim());

        if (!userStorage.containsKey(userId)) {
            log.error("User {} not found for Trainer {}, skipping", userId, trainerId);
            return;
        }

        Trainer trainer = new Trainer(trainerId, specialization, userId);
        trainerStorage.put(trainerId, trainer);
        trainerIdCounter.set(Math.max(trainerIdCounter.get(), trainerId + 1));
    }

    private void parseTraining(String[] parts) {
        if (parts.length < 7) return;

        Long trainingId = Long.parseLong(parts[0].trim());
        Long traineeId = Long.parseLong(parts[1].trim());
        Long trainerId = Long.parseLong(parts[2].trim());
        String name = parts[3].trim();
        String type = parts[4].trim();
        int duration = Integer.parseInt(parts[5].trim());
        LocalDate date = LocalDate.parse(parts[6].trim());

        if (!traineeStorage.containsKey(traineeId)) {
            log.error("Trainee {} not found for Training {}, skipping", traineeId, trainingId);
            return;
        }
        if (!trainerStorage.containsKey(trainerId)) {
            log.error("Trainer {} not found for Training {}, skipping", trainerId, trainingId);
            return;
        }

        Training training = new Training(trainingId, traineeId, trainerId, name, type, duration, date);
        trainingStorage.put(trainingId, training);
        trainingIdCounter.set(Math.max(trainingIdCounter.get(), trainingId + 1));
    }

    public Long getNextUserId() {
        return userIdCounter.getAndIncrement();
    }

    public Long getNextTraineeId() {
        return traineeIdCounter.getAndIncrement();
    }

    public Long getNextTrainerId() {
        return trainerIdCounter.getAndIncrement();
    }

    public Long getNextTrainingId() {
        return trainingIdCounter.getAndIncrement();
    }

    public Long getNextTrainingTypeId() {
        return trainingTypeIdCounter.getAndIncrement();
    }
}