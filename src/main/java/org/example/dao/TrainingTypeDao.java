package org.example.dao;

import org.example.entity.TrainingType;
import org.example.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TrainingTypeDao {

    private InMemoryStorage storage;

    @Autowired
    public void setStorage(InMemoryStorage storage){
        this.storage = storage;
    }

    public TrainingType create(TrainingType training){
        Long id = storage.getNextTrainingTypeId();
        training.setTrainingTypeId(id);
        storage.getTrainingTypeStorage().put(id, training);
        return training;
    }

    public TrainingType update(TrainingType trainingType){
        storage.getTrainingTypeStorage().put(trainingType.getTrainingTypeId(), trainingType);
        return trainingType;
    }

    public void delete(Long id) {
        storage.getTrainingTypeStorage().remove(id);
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
