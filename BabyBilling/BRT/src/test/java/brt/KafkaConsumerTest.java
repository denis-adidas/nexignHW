package brt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.annotation.KafkaListener;
import ru.bootcamp.brt.KafkaConsumer;
import ru.bootcamp.brt.service.MsisdnService;

public class KafkaConsumerTest {

    @Mock
    private MsisdnService msisdnService;

    private KafkaConsumer kafkaConsumer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        kafkaConsumer = new KafkaConsumer(msisdnService);
    }

    @Test
    public void testListenMethod() {
        String testMessage =
                "02,79876543221, 79123456789, 1709798657, 1709799601\n" +
                "01,79996667755, 79876543221, 1709899870, 1709905806";

        kafkaConsumer.listen(testMessage);

        Mockito.verify(msisdnService, Mockito.times(2)).checkMsisdn(Mockito.anyString(), Mockito.anyString());
    }
}
