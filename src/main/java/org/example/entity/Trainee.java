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
public class Trainee {
    // TODO:
    //  [Optional]
    //  You can leave it AS IS for now, but later when integrating a database
    //  note that the entity own identifier is usually named just 'id'.
    private Long traineeId;
    private LocalDate dateOfBirth;
    private String address;
    private Long userId;
}
