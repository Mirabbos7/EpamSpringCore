package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.UserDao;
import org.example.entity.User;
import org.example.utils.PasswordGenerator;
import org.example.utils.UsernameGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    private UserDao userDao;
    private UsernameGenerator usernameGenerator;
    private PasswordGenerator passwordGenerator;

    @Autowired
    public void setUserDao(UserDao userDao) { this.userDao = userDao; }

    @Autowired
    public void setUsernameGenerator(UsernameGenerator usernameGenerator) { this.usernameGenerator = usernameGenerator; }

    @Autowired
    public void setPasswordGenerator(PasswordGenerator passwordGenerator) { this.passwordGenerator = passwordGenerator; }

    public User createUser(String firstName, String lastName) {
        String username = usernameGenerator.generateUsername(firstName, lastName, userDao::existsByUsername);
        String password = passwordGenerator.generatePassword();

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword(password);
        user.setActive(true);

        log.info("Creating user with username: {}", username);
        return userDao.create(user);
    }
}