package org.example.service;

import org.example.dao.TraineeDao;
import org.example.dao.UserDao;
import org.example.entity.Trainee;
import org.example.entity.User;
import org.example.utils.PasswordGenerator;
import org.example.utils.UsernameGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TraineeService {

    private TraineeDao traineeDao;
    private UserDao userDao;
    private UsernameGenerator usernameGenerator;
    private PasswordGenerator passwordGenerator;

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Autowired
    public void setUsernameGenerator(UsernameGenerator usernameGenerator) {
        this.usernameGenerator = usernameGenerator;
    }

    @Autowired
    public void setPasswordGenerator(PasswordGenerator passwordGenerator) {
        this.passwordGenerator = passwordGenerator;
    }

    public Trainee create(String firstName, String lastName, LocalDate dateOfBirth, String address) {

        String username = usernameGenerator.generateUsername(firstName, lastName, userDao::existsByUsername);
        String password = passwordGenerator.generatePassword();

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword(password);
        user.setActive(true);
        user = userDao.create(user);

        Trainee trainee = new Trainee();
        trainee.setUserId(user.getUserId());
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);
        trainee = traineeDao.create(trainee);

        return trainee;
    }

    public Trainee update(Long traineeId, LocalDate dateOfBirth, String address) {

        Optional<Trainee> existing = traineeDao.findById(traineeId);
        if (!existing.isPresent()) {
            throw new IllegalArgumentException("Trainee not found");
        }

        Trainee trainee = existing.get();
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);

        return traineeDao.update(trainee);
    }

    public void delete(Long traineeId) {
        traineeDao.delete(traineeId);
    }

    public Optional<Trainee> select(Long traineeId) {
        return traineeDao.findById(traineeId);
    }

    public List<Trainee> selectAll() {
        return traineeDao.findAll();
    }
}
