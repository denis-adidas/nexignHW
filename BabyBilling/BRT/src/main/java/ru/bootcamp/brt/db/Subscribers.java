package ru.bootcamp.brt.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Сущность абонента "Ромашка"
 */
@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subscribers {
    @Id
    @Column(name = "id")
    private Integer id;
    @Column(name = "msisdn")
    private String msisdn;
    @Column(name = "balance")
    private Integer balance;
    @Column(name = "balance_minutes")
    private Integer balanceMinutes;
    @Column(name = "balance_sms")
    private Integer balanceSms;
    @Column(name = "balance_kilobytes")
    private Integer balanceKilobytes;
    @Column(name = "tariff_id")
    private Integer tariffId;
}