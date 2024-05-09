package brt.service;

import jdk.jfr.Description;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.bootcamp.brt.db.Subscribers;
import ru.bootcamp.brt.db.SubscribersRepo;
import ru.bootcamp.brt.producer.TariffKafkaProducer;
import org.mockito.ArgumentCaptor;
import ru.bootcamp.brt.service.MsisdnService;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import static org.mockito.Mockito.*;

class MsisdnServiceTest {

    @Mock
    private SubscribersRepo repo;

    @Mock
    private TariffKafkaProducer producer;

    private MsisdnService msisdnService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        msisdnService = new MsisdnService(repo, producer);
    }
    @Description("lineCdr - каждый параметр CDR. В данном случае, нам безразницы что мы передаем, так как основная" +
            "задача - проверить идентификацию msisdn. Если будет ошибка в CDR - мы пройдем верификацию, но получим ошибку данных")
    @Test
    void checkMsisdn_SubscriberExists_MessageSentToKafka() throws IOException {
        Reader in = new FileReader("data.csv");
        CSVParser csvParser = CSVFormat.DEFAULT.withHeader().parse(in);


        for (CSVRecord record : csvParser) {
            String code = record.get("code");
            String number = record.get("number");
            String msisdn = code + number;

            String tariffId = record.get("tariffId");
            String lineCdr = "someCDR";

            Subscribers subscriber = new Subscribers();
            subscriber.setMsisdn(msisdn);
            subscriber.setTariffId(Integer.valueOf(tariffId));

            when(repo.findByMsisdn(msisdn)).thenReturn(subscriber);

            msisdnService.checkMsisdn(msisdn, lineCdr);

            verify(producer).sendMessage(lineCdr + "," + tariffId);
        }
    }
    @Test
    void checkMsisdn_SubscriberDoesNotExist_NoMessageSentToKafka() throws IOException {
        Reader in = new FileReader("data.csv");
        CSVParser csvParser = CSVFormat.DEFAULT.withHeader().parse(in);

        for (CSVRecord record : csvParser) {
            String code = record.get("code");
            String number = record.get("number");
            String msisdn = code + number;

            String tariffId = record.get("tariffId");
            String lineCdr = "someCDR";

            Subscribers subscriber = new Subscribers();
            subscriber.setMsisdn(msisdn);
            subscriber.setTariffId(Integer.valueOf(tariffId));

            when(repo.findByMsisdn(msisdn)).thenReturn(null);

            msisdnService.checkMsisdn(msisdn, lineCdr);

            verify(producer, never()).sendMessage(anyString());
        }
    }

    @Test
    void checkMsisdn_SubscriberExists_LogsInfo() throws IOException {
        Reader in = new FileReader("data.csv");
        CSVParser csvParser = CSVFormat.DEFAULT.withHeader().parse(in);

        for (CSVRecord record : csvParser) {
            String code = record.get("code");
            String number = record.get("number");
            String msisdn = code + number;

            String tariffId = record.get("tariffId");
            String lineCdr = "someCDR";

            Subscribers subscriber = new Subscribers();
            subscriber.setMsisdn(msisdn);
            subscriber.setTariffId(Integer.valueOf(tariffId));

            when(repo.findByMsisdn(msisdn)).thenReturn(subscriber);

            msisdnService.checkMsisdn(msisdn, lineCdr);
        }
    }

}
