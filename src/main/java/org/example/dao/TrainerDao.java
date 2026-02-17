package org.example.dao;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.Trainer;
import org.example.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class TrainerDao {
    private InMemoryStorage storage;

    @Autowired
    public void setStorage(InMemoryStorage storage) {
        this.storage = storage;
    }

    public Trainer create(Trainer trainer) {
        Long id = storage.getNextTrainerId();
        trainer.setTrainerId(id);
        storage.getTrainerStorage().put(id, trainer);
        log.info("Created trainer with id: {}", id);
        return trainer;
    }

    public Trainer update(Trainer trainer) {
        storage.getTrainerStorage().put(trainer.getTrainerId(), trainer);
        log.info("Updated trainer with id: {}", trainer.getTrainerId());
        return trainer;
    }

    public void delete(Long id) {
        storage.getTrainerStorage().remove(id);
        log.info("Deleted trainer with id: {}", id);
    }

    public Optional<Trainer> findById(Long id) {
        return Optional.ofNullable(storage.getTrainerStorage().get(id));
    }

    public Optional<Trainer> findByUserId(Long userId) {
        return storage.getTrainerStorage().values().stream()
                .filter(t -> t.getUserId().equals(userId))
                .findFirst();
    }

    public List<Trainer> findAll() {
        return new ArrayList<>(storage.getTrainerStorage().values());
    }
}