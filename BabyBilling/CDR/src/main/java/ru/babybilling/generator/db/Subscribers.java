package ru.babybilling.generator.db;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Сущность абонента
 */
@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subscribers {
    @Id
    private Integer id;
    private String msisdn;
}
