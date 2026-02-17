package org.example.facade;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.Trainee;
import org.example.entity.Trainer;
import org.example.entity.Training;
import org.example.service.TraineeService;
import org.example.service.TrainerService;
import org.example.service.TrainingService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public GymFacade(TraineeService traineeService,
                     TrainerService trainerService,
                     TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    public Trainee createTrainee(String firstName, String lastName, LocalDate dateOfBirth, String address) {
        log.info("Facade: creating trainee {} {}", firstName, lastName);
        return traineeService.create(firstName, lastName, dateOfBirth, address);
    }

    public Trainee updateTrainee(Long traineeId, LocalDate dateOfBirth, String address) {
        log.info("Facade: updating trainee with id: {}", traineeId);
        return traineeService.update(traineeId, dateOfBirth, address);
    }

    public void deleteTrainee(Long traineeId) {
        log.info("Facade: deleting trainee with id: {}", traineeId);
        traineeService.delete(traineeId);
    }

    public Optional<Trainee> selectTrainee(Long traineeId) {
        return traineeService.select(traineeId);
    }

    public List<Trainee> selectAllTrainees() {
        return traineeService.selectAll();
    }

    public Trainer createTrainer(String firstName, String lastName, String specialization) {
        log.info("Facade: creating trainer {} {}", firstName, lastName);
        return trainerService.create(firstName, lastName, specialization);
    }

    public Trainer updateTrainer(Long trainerId, String specialization) {
        log.info("Facade: updating trainer with id: {}", trainerId);
        return trainerService.update(trainerId, specialization);
    }

    public Optional<Trainer> selectTrainer(Long trainerId) {
        return trainerService.select(trainerId);
    }

    public List<Trainer> selectAllTrainers() {
        return trainerService.selectAll();
    }

    public Training createTraining(Long traineeId, Long trainerId, String name,
                                   String type, int duration, LocalDate date) {
        log.info("Facade: creating training '{}'", name);
        return trainingService.create(traineeId, trainerId, name, type, duration, date);
    }

    public Optional<Training> selectTraining(Long trainingId) {
        return trainingService.select(trainingId);
    }

    public List<Training> selectAllTrainings() {
        return trainingService.selectAll();
    }
}
