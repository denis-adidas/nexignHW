package ru.bootcamp.brt.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Сущность звонка
 */
@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Calls {
    @Id
    @Column(name = "id")
    private Integer id;
    @Column(name = "subscriber_id")
    private Long subscriberId;
    @Column(name = "start_time")
    private LocalDateTime startTime;
    @Column(name = "end_time")
    private LocalDateTime endTime;
    @Column(name = "type")
    private Integer type;

}