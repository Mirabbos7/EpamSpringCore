package org.example;

import org.example.config.AppConfig;
import org.example.entity.Trainee;
import org.example.entity.Trainer;
import org.example.entity.Training;
import org.example.service.TraineeService;
import org.example.service.TrainerService;
import org.example.service.TrainingService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;

public class Gym {

    public static void main(String[] args) {
        System.out.println("=== Starting Gym CRM System ===");

        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        TraineeService traineeService = context.getBean(TraineeService.class);
        TrainerService trainerService = context.getBean(TrainerService.class);
        TrainingService trainingService = context.getBean(TrainingService.class);

        System.out.println("\n=== Data loaded from files ===");
        System.out.println("Trainees: " + traineeService.selectAll().size());
        System.out.println("Trainers: " + trainerService.selectAll().size());
        System.out.println("Trainings: " + trainingService.selectAll().size());

        System.out.println("\n=== Creating new Trainee ===");
        Trainee trainee = traineeService.create("Alex", "Brown", LocalDate.of(1995, 3, 15), "456 New St");
        System.out.println("Created Trainee ID: " + trainee.getTraineeId());
        System.out.println("Check logs for username and password!");

        System.out.println("\n=== Creating new Trainer ===");
        Trainer trainer = trainerService.create("Mike", "Johnson", "Cardio");
        System.out.println("Created Trainer ID: " + trainer.getTrainerId());
        System.out.println("Check logs for username and password!");

        System.out.println("\n=== Creating new Training ===");
        Training training = trainingService.create(
                trainee.getTraineeId(),
                trainer.getTrainerId(),
                "Cardio Session",
                "Cardio",
                45,
                LocalDate.now()
        );
        System.out.println("Created Training ID: " + training.getTrainingId());

        System.out.println("\n=== Updating Trainee ===");
        Trainee updated = traineeService.update(trainee.getTraineeId(), LocalDate.of(1995, 3, 15), "789 Updated St");
        System.out.println("Updated address: " + updated.getAddress());

        System.out.println("\n=== Selecting all Trainees ===");
        traineeService.selectAll().forEach(t ->
                System.out.println("Trainee ID: " + t.getTraineeId() + ", User ID: " + t.getUserId())
        );

        System.out.println("\n=== Selecting all Trainers ===");
        trainerService.selectAll().forEach(t ->
                System.out.println("Trainer ID: " + t.getTrainerId() + ", Specialization: " + t.getSpecialization())
        );

        System.out.println("\n=== Selecting all Trainings ===");
        trainingService.selectAll().forEach(t ->
                System.out.println("Training ID: " + t.getTrainingId() + ", Name: " + t.getTrainingName())
        );

        System.out.println("\n=== Deleting Trainee ===");
        traineeService.delete(trainee.getTraineeId());
        System.out.println("Deleted Trainee ID: " + trainee.getTraineeId());

        System.out.println("\n=== Gym CRM System finished ===");

        ((AnnotationConfigApplicationContext) context).close();
    }
}