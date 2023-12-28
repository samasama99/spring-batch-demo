package org.samasama.batchdemo.subject;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Subject {
    @Id
    @GeneratedValue
    private Long id;
    String firstName;
    String lastName;
    Integer age;
}
