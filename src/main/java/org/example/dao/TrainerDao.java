package org.example.dao;

import org.example.entity.Trainee;
import org.example.entity.Trainer;
import org.example.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class TrainerDao {
    private InMemoryStorage storage;

    @Autowired
    public void setStorage(InMemoryStorage storage){
        this.storage = storage;
    }

    public Trainer create(Trainer trainer){
        Long id = storage.getNextTrainerId();
        trainer.setTrainerId(id);
        storage.getTrainerStorage().put(id, trainer);
        return trainer;
    }

    public Trainer update(Trainer trainer){
        storage.getTrainerStorage().put(trainer.getTrainerId(), trainer);
        return trainer;
    }

    public void delete(Long id) {
        storage.getTrainerStorage().remove(id);
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
