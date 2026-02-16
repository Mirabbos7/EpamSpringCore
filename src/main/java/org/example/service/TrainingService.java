package org.example.service;

import org.example.dao.TrainingDao;
import org.example.entity.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TrainingService {
    private TrainingDao trainingDao;

    @Autowired
    public void setTrainingDao(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    public Training create(Long traineeId, Long trainerId, String trainingName,
                           String trainingType, int duration, LocalDate date) {

        Training training = new Training();
        training.setTraineeId(traineeId);
        training.setTrainerId(trainerId);
        training.setTrainingName(trainingName);
        training.setTrainingType(trainingType);
        training.setTrainingDuration(duration);
        training.setTrainingDate(date);

        training = trainingDao.create(training);

        return training;
    }

    public Optional<Training> select(Long trainingId) {
        return trainingDao.findById(trainingId);
    }

    public List<Training> selectAll() {
        return trainingDao.findAll();
    }

    public List<Training> selectByTraineeId(Long traineeId) {
        return trainingDao.findByTraineeId(traineeId);
    }

    public List<Training> selectByTrainerId(Long trainerId) {
        return trainingDao.findByTrainerId(trainerId);
    }
}