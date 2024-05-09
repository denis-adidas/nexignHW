package ru.bootcamp.brt;


import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.bootcamp.brt.service.MsisdnService;

@RequiredArgsConstructor
@Service
public class KafkaConsumer {
    private final MsisdnService msisdnService;

    @KafkaListener(id = "myId", topics = "cdr_events")
    public void listen(String in) {
        String[] callLine = in.split("\n");
        for (String part : callLine) {
            String[] parts = part.split(",");
            var msisdnCall = parts[1];
            msisdnService.checkMsisdn(msisdnCall, part);
        }
    }
}
