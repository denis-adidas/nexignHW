package generator.service;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.babybilling.generator.db.SubscribersRepo;
import ru.babybilling.generator.model.Cdr;
import ru.babybilling.generator.producer.KafkaProducer;
import ru.babybilling.generator.service.Commutator;

import java.util.ArrayList;


public class CommutatorTest {

    @Mock
    private SubscribersRepo subscribersRepo;

    @Mock
    private KafkaProducer kafkaProducer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGenerateCDR() {
        Commutator commutator = new Commutator(subscribersRepo, kafkaProducer);

        commutator.generate();

        verify(subscribersRepo, atLeastOnce()).findAll();
        verify(kafkaProducer, atLeastOnce()).sendMessage(anyString());
    }

    @Test
    public void testWriteToCDR() {
        Commutator commutator = new Commutator(subscribersRepo, kafkaProducer);
        ArrayList<Cdr> mockCdrList = new ArrayList<>();
        Cdr cdr1 = new Cdr();
        cdr1.setCallType(01L);
        cdr1.setFirstNum("1234567890");
        cdr1.setSecondNum("0987654321");
        cdr1.setStartTime(1234567890L);
        cdr1.setEndTime("1234567900");
        mockCdrList.add(cdr1);

        commutator.writeToCDR(mockCdrList);

        verify(kafkaProducer, times(1)).sendMessage(anyString());
    }

}
