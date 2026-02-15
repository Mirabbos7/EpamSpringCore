package org.example.entity;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode

public class Training {
    private Long trainingId;
    private Long traineeId;
    private Long trainerId;
    private String trainingName;
    private String trainingType;
    private int trainingDuration;
    private LocalDate trainingDate;
}
