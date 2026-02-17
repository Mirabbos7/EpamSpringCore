package org.example.storage;

import lombok.Getter;
import org.example.entity.Trainee;
import org.example.entity.Trainer;
import org.example.entity.Training;
import org.example.entity.TrainingType;
import org.example.entity.User;
import org.springframework.stereotype.Component;

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

    // TODO:
    //  1. This looks quite repetitive. Path prefix `src/main/resources` is the same,
    //  .txt is the same, loadFile() call is also the same, only file name is different.
    //  Can you think of a generalized solution?
    //  OR
    //  2. You could try using @Value + org.springframework.core.io.Resource instead of hardcoded paths.
    //  In Spring applications, Resource is preferred over File because it abstracts the actual source
    //  (classpath, filesystem, URL, etc.) and decouples code from environment assumptions:
    //  Our app can be deployed in a way where file system is not accessible, but classpath is, or vice versa

    private static final String TRAINING_TYPE_FILE = "src/main/resources/TrainingType.txt";
    private static final String USERS_FILE = "src/main/resources/Users.txt";
    private static final String TRAINEES_FILE = "src/main/resources/Trainees.txt";
    private static final String TRAINERS_FILE = "src/main/resources/Trainers.txt";
    private static final String TRAININGS_FILE = "src/main/resources/Trainings.txt";

    @PostConstruct
    public void init() {
        loadTrainingTypes();
        loadUsers();
        loadTrainees();
        loadTrainers();
        loadTrainings();
    }

    private void loadTrainingTypes() {
        loadFile(TRAINING_TYPE_FILE);
    }

    private void loadUsers() {
        loadFile(USERS_FILE);
    }

    private void loadTrainees() {
        loadFile(TRAINEES_FILE);
    }

    private void loadTrainers() {
        loadFile(TRAINERS_FILE);
    }

    private void loadTrainings() {
        loadFile(TRAININGS_FILE);
    }

    private void loadFile(String filePath) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                parseLine(line.trim());
            }
        } catch (IOException e) {
            // TODO:
            //  [Optional]
            //  This works AS IS, but let’s think it through
            //  Do we still want to start the app if the initial data failed to load?
            //  Which one is better here fail-fast vs graceful degradation?
            e.printStackTrace();
        }
    }

    private boolean parseLine(String line) {
        if (line.isEmpty() || line.startsWith("#")) {
            return false;
        }

        String[] parts = line.split(",");
        if (parts.length < 2) {
            return false;
        }

        // TODO:
        //  1. You don't need else branch when you have `return` on the previous branch
        //  Similarly to what you've done above you can use guarding ifs here as well
        //  2. [Optional] you can also try switch: 2 -> parseTrainingType, 3-> parseUser, ... , default -> return false
        try {
            if (parts.length == 2) {
                parseTrainingType(parts);
                return true;
            } else if (parts.length == 6) {
                parseUser(parts);
                return true;
            } else if (parts.length == 4) {
                parseTrainee(parts);
                return true;
            } else if (parts.length == 3) {
                parseTrainer(parts);
                return true;
            } else if (parts.length == 7) {
                parseTraining(parts);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
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
            System.out.println("ERROR: User " + userId + " not found for Trainee");
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
            System.out.println("ERROR: User " + userId + " not found for Trainer");
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
            System.out.println("ERROR: Trainee " + traineeId + " not found for Training");
            return;
        }
        if (!trainerStorage.containsKey(trainerId)) {
            System.out.println("ERROR: Trainer " + trainerId + " not found for Training");
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