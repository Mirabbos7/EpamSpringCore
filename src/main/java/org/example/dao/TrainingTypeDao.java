package org.example.dao;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.TrainingType;
import org.example.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class TrainingTypeDao {

    private InMemoryStorage storage;

    @Autowired
    public void setStorage(InMemoryStorage storage) {
        this.storage = storage;
    }

    public TrainingType create(TrainingType trainingType) {
        Long id = storage.getNextTrainingTypeId();
        trainingType.setTrainingTypeId(id);
        storage.getTrainingTypeStorage().put(id, trainingType);
        log.info("Created trainingType with id: {}", id);
        return trainingType;
    }

    public TrainingType update(TrainingType trainingType) {
        storage.getTrainingTypeStorage().put(trainingType.getTrainingTypeId(), trainingType);
        log.info("Updated trainingType with id: {}", trainingType.getTrainingTypeId());
        return trainingType;
    }

    public void delete(Long id) {
        storage.getTrainingTypeStorage().remove(id);
        log.info("Deleted trainingType with id: {}", id);
    }

    public Optional<TrainingType> findById(Long id) {
        return Optional.ofNullable(storage.getTrainingTypeStorage().get(id));
    }

    public Optional<TrainingType> findByName(String name) {
        return storage.getTrainingTypeStorage().values().stream()
                .filter(t -> t.getTrainingTypeName().equals(name))
                .findFirst();
    }

    public List<TrainingType> findAll() {
        return new ArrayList<>(storage.getTrainingTypeStorage().values());
    }
}