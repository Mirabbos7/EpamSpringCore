package org.example.entity;

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
// TODO:
//  [Optional]
//  If TrainingType represents a fixed set of predefined types (e.g., "CARDIO", "STRENGTH", "YOGA"),
//  using an enum is preferable. Enums provide type safety, are easier to maintain,
//  make the code more readable and are faster to compare than strings.
//  You can keep the strings approach though - it also works here.
public class TrainingType {
    private Long trainingTypeId;
    private String trainingTypeName;
}
