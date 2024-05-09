package ru.babybilling.generator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.babybilling.generator.db.SubscribersRepo;
import ru.babybilling.generator.model.Cdr;
import ru.babybilling.generator.producer.KafkaProducer;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;

import static java.util.concurrent.ThreadLocalRandom.current;

@Slf4j
@Service
@RequiredArgsConstructor
public class Commutator {
    private static final String outComingCallCode = "01";
    private static final String inComingCallCode = "02";
    private final SubscribersRepo repo;
    private final KafkaProducer producer;

    private String getSubscriber() {
        return repo.findAll().get(current().nextInt(0, 20)).getMsisdn();
    }

    public void generate() {
        try {

            var currentTime = LocalDateTime.now();
            var stopTime = currentTime.plusYears(1);
            ArrayList<Cdr> pullCDR = new ArrayList<>(10);
            while (currentTime.isBefore(stopTime)) {
                var callType = current().nextLong(1, 3); //генерация типа звонка
                var len = current().nextLong(1, 361); //длительность звонка от 1 до 360 секунд
                var firstNum = getSubscriber();
                var secondNum = getSubscriber();
                Long startTime = currentTime.toEpochSecond(ZoneOffset.UTC);
                String endTime = Long.toString(currentTime.toEpochSecond(ZoneOffset.UTC) + len);
                var callBreak = current().nextLong(0, 2);
                if (callBreak == 1)
                    currentTime = currentTime.plusSeconds(len + current().nextLong(1, 6000)); //подписать
                else currentTime = currentTime.minusSeconds(len + current().nextLong(1, 100));
                var lineCDR = "CDR: " + callType + "," + firstNum + "," + secondNum + "," + startTime + "," + endTime + "\n";
                var cdr = new Cdr();
                cdr.setCallType(callType);
                cdr.setFirstNum(firstNum);
                cdr.setSecondNum(secondNum);
                cdr.setStartTime(startTime);
                cdr.setEndTime(endTime);
                pullCDR.add(cdr);
                log.info("CDR: {}, {}, {}, {}, {}", callType, firstNum, secondNum, startTime, endTime);
                // обратная запись звонка
                var secondCdr = new Cdr();
                long secondCallType = 1;
                if (callType == 1) {
                    secondCallType = 2;
                }
                secondCdr.setCallType(secondCallType);
                secondCdr.setFirstNum(secondNum);
                secondCdr.setSecondNum(firstNum);
                secondCdr.setStartTime(startTime);
                secondCdr.setEndTime(endTime);
                pullCDR.add(secondCdr);
                log.info("CDR: {}, {}, {}, {}, {}", callType, secondNum, firstNum, startTime, endTime);
                if (pullCDR.size() == 10) {
                    pullCDR.sort(new Comparator<Cdr>() {
                        @Override
                        public int compare(Cdr o1, Cdr o2) {
                            return o1.getStartTime().compareTo(o2.getStartTime());
                        }
                    });
                    writeToCDR(pullCDR);
                    pullCDR.clear();
//                    break; //TODO: убрать брейк//
                }
            }
            writeToCDR(pullCDR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeToCDR(ArrayList<Cdr> pullCDR) {
        try {
            if (!pullCDR.isEmpty()) {
                var writer = new PrintWriter("cdr/CDR_" + pullCDR.get(0).getStartTime() + ".txt", StandardCharsets.UTF_8);
                StringBuilder msgCDR = new StringBuilder();
                pullCDR.forEach(c -> {
                    msgCDR.append(c.getCallType());
                    msgCDR.append(",");
                    msgCDR.append(c.getFirstNum());
                    msgCDR.append(",");
                    msgCDR.append(c.getSecondNum());
                    msgCDR.append(",");
                    msgCDR.append(c.getStartTime());
                    msgCDR.append(",");
                    msgCDR.append(c.getEndTime());
                    msgCDR.append("\n");
                });
                writer.print(msgCDR.toString().trim());
                producer.sendMessage(msgCDR.toString());
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
