package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.TrainerDao;
import org.example.entity.Trainer;
import org.example.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TrainerService {

    private TrainerDao trainerDao;
    private UserService userService;

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public Trainer create(String firstName, String lastName, String specialization) {
        User user = userService.createUser(firstName, lastName);

        Trainer trainer = new Trainer();
        trainer.setUserId(user.getUserId());
        trainer.setSpecialization(specialization);

        log.info("Creating trainer for user: {}", user.getUsername());
        return trainerDao.create(trainer);
    }

    public Trainer update(Long trainerId, String specialization) {
        Trainer trainer = trainerDao.findById(trainerId)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found: " + trainerId));

        trainer.setSpecialization(specialization);

        log.info("Updating trainer with id: {}", trainerId);
        return trainerDao.update(trainer);
    }

    public Optional<Trainer> select(Long trainerId) {
        return trainerDao.findById(trainerId);
    }

    public List<Trainer> selectAll() {
        return trainerDao.findAll();
    }
}