package org.example.dao;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.User;
import org.example.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class UserDao {

    private InMemoryStorage storage;

    @Autowired
    public void setStorage(InMemoryStorage storage) {
        this.storage = storage;
    }

    public User create(User user) {
        Long id = storage.getNextUserId();
        user.setUserId(id);
        storage.getUserStorage().put(id, user);
        log.info("Created user with id: {}, username: {}", id, user.getUsername());
        return user;
    }

    public User update(User user) {
        storage.getUserStorage().put(user.getUserId(), user);
        log.info("Updated user with id: {}", user.getUserId());
        return user;
    }

    public void delete(Long id) {
        storage.getUserStorage().remove(id);
        log.info("Deleted user with id: {}", id);
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(storage.getUserStorage().get(id));
    }

    public Optional<User> findByUsername(String username) {
        return storage.getUserStorage().values().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();
    }

    public List<User> findAll() {
        return new ArrayList<>(storage.getUserStorage().values());
    }

    public boolean existsByUsername(String username) {
        return storage.getUserStorage().values().stream()
                .anyMatch(u -> u.getUsername().equals(username));
    }
}