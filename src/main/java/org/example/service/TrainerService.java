package org.example.service;

import org.example.dao.TrainerDao;
import org.example.dao.UserDao;
import org.example.entity.Trainer;
import org.example.entity.User;
import org.example.utils.PasswordGenerator;
import org.example.utils.UsernameGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainerService {

    private TrainerDao trainerDao;
    private UserDao userDao;
    private UsernameGenerator usernameGenerator;
    private PasswordGenerator passwordGenerator;

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
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

    public Trainer create(String firstName, String lastName, String specialization) {
        String username = usernameGenerator.generateUsername(firstName, lastName, userDao::existsByUsername);
        String password = passwordGenerator.generatePassword();

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword(password);
        user.setActive(true);
        user = userDao.create(user);

        Trainer trainer = new Trainer();
        trainer.setUserId(user.getUserId());
        trainer.setSpecialization(specialization);
        trainer = trainerDao.create(trainer);

        return trainer;
    }

    public Trainer update(Long trainerId, String specialization) {
        Optional<Trainer> existing = trainerDao.findById(trainerId);
        if (!existing.isPresent()) {
            throw new IllegalArgumentException("Trainer not found");
        }

        Trainer trainer = existing.get();
        trainer.setSpecialization(specialization);

        return trainerDao.update(trainer);
    }

    public Optional<Trainer> select(Long trainerId) {
        return trainerDao.findById(trainerId);
    }

    public List<Trainer> selectAll() {
        return trainerDao.findAll();
    }
}