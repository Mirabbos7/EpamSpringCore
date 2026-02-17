package org.example.dao;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.Training;
import org.example.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class TrainingDao {

    private InMemoryStorage storage;

    @Autowired
    public void setStorage(InMemoryStorage storage) {
        this.storage = storage;
    }

    public Training create(Training training) {
        Long id = storage.getNextTrainingId();
        training.setTrainingId(id);
        storage.getTrainingStorage().put(id, training);
        log.info("Created training with id: {}", id);
        return training;
    }

    public Training update(Training training) {
        storage.getTrainingStorage().put(training.getTrainingId(), training);
        log.info("Updated training with id: {}", training.getTrainingId());
        return training;
    }

    public void delete(Long id) {
        storage.getTrainingStorage().remove(id);
        log.info("Deleted training with id: {}", id);
    }

    public Optional<Training> findById(Long id) {
        return Optional.ofNullable(storage.getTrainingStorage().get(id));
    }

    public List<Training> findByTraineeId(Long traineeId) {
        return storage.getTrainingStorage().values().stream()
                .filter(t -> t.getTraineeId().equals(traineeId))
                .toList();
    }

    public List<Training> findByTrainerId(Long trainerId) {
        return storage.getTrainingStorage().values().stream()
                .filter(t -> t.getTrainerId().equals(trainerId))
                .toList();
    }

    public List<Training> findAll() {
        return new ArrayList<>(storage.getTrainingStorage().values());
    }
}