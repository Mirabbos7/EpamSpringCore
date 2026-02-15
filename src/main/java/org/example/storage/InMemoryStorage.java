package org.example.storage;

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

@Component
public class InMemoryStorage {

    private final Map<Long, User> userStorage = new HashMap<>();
    private final Map<Long, Trainee> traineeStorage = new HashMap<>();
    private final Map<Long, Trainer> trainerStorage = new HashMap<>();
    private final Map<Long, Training> trainingStorage = new HashMap<>();
    private final Map<Long, TrainingType> trainingTypeStorage = new HashMap<>();


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
        loadFile();
    }

    private void loadUsers() {

    }

    private void loadTrainees() {

    }

    private void loadTrainers() {

    }

    private void loadTrainings() {

    }

    private void loadFile(String filePath) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                parseLine(line.trim());
            }

        } catch (IOException e) {
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

        String type = parts[0].trim();

        try {
            switch (type.toUpperCase()) {
                case "TRAININGTYPE":
                    parseTrainingType(parts);
                    return true;
                case "USER":
                    parseUser(parts);
                    return true;
                case "TRAINEE":
                    parseTrainee(parts);
                    return true;
                case "TRAINER":
                    parseTrainer(parts);
                    return true;
                case "TRAINING":
                    parseTraining(parts);
                    return true;
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private void parseUser(String[] parts) {
        if (parts.length < 7) return;

        Long userId = Long.parseLong(parts[1].trim());
        String firstName = parts[2].trim();
        String lastName = parts[3].trim();
        String username = parts[4].trim();
        String password = parts[5].trim();
        boolean isActive = Boolean.parseBoolean(parts[6].trim());

        User user = new User(userId, firstName, lastName, username, password, isActive);
        userStorage.put(userId, user);
    }

    private void parseTrainingType(String[] parts){
        if(parts.length < 2) return;
        Long id = Long.parseLong(parts[0].trim());
        String typeName = parts[1].trim();

        TrainingType trainingType = new TrainingType(id, typeName);
        trainingTypeStorage.put(1L, trainingType);
    }

    private void parseTrainee(String[] parts) {
        if (parts.length < 5) return;

        Long traineeId = Long.parseLong(parts[1].trim());
        LocalDate dateOfBirth = LocalDate.parse(parts[2].trim());
        String address = parts[3].trim();
        Long userId = Long.parseLong(parts[4].trim());

        if (!userStorage.containsKey(userId)) {
            return;
        }

        Trainee trainee = new Trainee(traineeId, dateOfBirth, address, userId);
        traineeStorage.put(traineeId, trainee);
    }

    private void parseTrainer(String[] parts) {
        if (parts.length < 4) return;

        Long trainerId = Long.parseLong(parts[1].trim());
        String specialization = parts[2].trim();
        Long userId = Long.parseLong(parts[3].trim());

        if (!userStorage.containsKey(userId)) {
            return;
        }

        Trainer trainer = new Trainer(trainerId, specialization, userId);
        trainerStorage.put(trainerId, trainer);
    }

    private void parseTraining(String[] parts) {
        if (parts.length < 8) return;

        Long trainingId = Long.parseLong(parts[1].trim());
        Long traineeId = Long.parseLong(parts[2].trim());
        Long trainerId = Long.parseLong(parts[3].trim());
        String name = parts[4].trim();
        String type = parts[5].trim();
        LocalDate date = LocalDate.parse(parts[7].trim());
        int duration = Integer.parseInt(parts[6].trim());

        if (!traineeStorage.containsKey(traineeId)) {
            System.out.println("ERROR PARSE TRAINING");
            return;
        }
        if (!trainerStorage.containsKey(trainerId)) {
            System.out.println("ERROR PARSE TRAINING");
            return;
        }

        Training training = new Training(trainingId, traineeId, trainerId, name, type, date, duration);
        trainingStorage.put(trainingId, training);
    }

}
