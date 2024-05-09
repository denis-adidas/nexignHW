package ru.bootcamp.brt.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import ru.bootcamp.brt.db.Subscribers;
import ru.bootcamp.brt.db.SubscribersRepo;
import ru.bootcamp.brt.producer.TariffKafkaProducer;

import java.util.function.BiConsumer;

@Slf4j
@RequiredArgsConstructor
@Service
public class MsisdnService {

    private final SubscribersRepo repo;
    private final TariffKafkaProducer producer;

    public void checkMsisdn(String msisdn, String lineCdr) {
        Subscribers sub = repo.findByMsisdn(msisdn);
        if (sub != null) {
            var result = lineCdr + "," + sub.getTariffId();
            System.out.println(result);
            producer.sendMessage(result);
            log.info("Msisdn {} получен, отправляем в Kafka результат {}", msisdn, result);
        }
    }

}
