package org.example.dao;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.Trainee;
import org.example.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class TraineeDao {
    private InMemoryStorage storage;

    @Autowired
    public void setStorage(InMemoryStorage storage) {
        this.storage = storage;
    }

    public Trainee create(Trainee trainee) {
        Long id = storage.getNextTraineeId();
        trainee.setTraineeId(id);
        storage.getTraineeStorage().put(id, trainee);
        log.info("Created trainee with id: {}", id);
        return trainee;
    }

    public Trainee update(Trainee trainee) {
        storage.getTraineeStorage().put(trainee.getTraineeId(), trainee);
        log.info("Updated trainee with id: {}", trainee.getTraineeId());
        return trainee;
    }

    public void delete(Long id) {
        storage.getTraineeStorage().remove(id);
        log.info("Deleted trainee with id: {}", id);
    }

    public Optional<Trainee> findById(Long id) {
        return Optional.ofNullable(storage.getTraineeStorage().get(id));
    }

    public Optional<Trainee> findByUserId(Long userId) {
        return storage.getTraineeStorage().values().stream()
                .filter(t -> t.getUserId().equals(userId))
                .findFirst();
    }

    public List<Trainee> findAll() {
        return new ArrayList<>(storage.getTraineeStorage().values());
    }
}