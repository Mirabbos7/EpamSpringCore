package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.TraineeDao;
import org.example.entity.Trainee;
import org.example.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TraineeService {

    private TraineeDao traineeDao;
    private UserService userService;

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public Trainee create(String firstName, String lastName, LocalDate dateOfBirth, String address) {
        User user = userService.createUser(firstName, lastName);

        Trainee trainee = new Trainee();
        trainee.setUserId(user.getUserId());
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);

        log.info("Creating trainee for user: {}", user.getUsername());
        return traineeDao.create(trainee);
    }

    public Trainee update(Long traineeId, LocalDate dateOfBirth, String address) {
        Trainee trainee = traineeDao.findById(traineeId)
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found: " + traineeId));

        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);

        log.info("Updating trainee with id: {}", traineeId);
        return traineeDao.update(trainee);
    }

    public void delete(Long traineeId) {
        log.info("Deleting trainee with id: {}", traineeId);
        traineeDao.delete(traineeId);
    }

    public Optional<Trainee> select(Long traineeId) {
        return traineeDao.findById(traineeId);
    }

    public List<Trainee> selectAll() {
        return traineeDao.findAll();
    }
}