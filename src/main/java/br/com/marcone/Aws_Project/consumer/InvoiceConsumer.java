package br.com.marcone.Aws_Project.consumer;


import br.com.marcone.Aws_Project.model.Invoice;
import br.com.marcone.Aws_Project.model.SnsMessage;
import br.com.marcone.Aws_Project.repository.InvoiceRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;


@Service
public class InvoiceConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(InvoiceConsumer.class);

    private ObjectMapper mapper;
    private InvoiceRepository invoiceRepository;
    private AmazonS3 s3;

    @Autowired
    public InvoiceConsumer(ObjectMapper mapper, InvoiceRepository invoiceRepository, AmazonS3 s3) {
        this.mapper = mapper;
        this.invoiceRepository = invoiceRepository;
        this.s3 = s3;
    }

    @JmsListener(destination = "${aws.sqs.queue.invoice.events.name}")
    public void receiveS3Event(TextMessage textMessage) throws JMSException, IOException {
        SnsMessage snsMessage = mapper.readValue(textMessage.getText(), SnsMessage.class);

        S3EventNotification s3EventNotification = mapper.readValue(snsMessage.getMessage(), S3EventNotification.class);

        processInvoiceNotification(s3EventNotification);

    }

    private void processInvoiceNotification(S3EventNotification s3EventNotification) throws IOException {
        List<S3EventNotification.S3EventNotificationRecord> list = s3EventNotification.getRecords();
        if (list.size() > 0) {
            for (S3EventNotification.S3EventNotificationRecord
                    s3EventNotificationRecord : list ) {
                S3EventNotification.S3Entity s3Entity = s3EventNotificationRecord.getS3();

                String bucketName = s3Entity.getBucket().getName();
                String objectKey = s3Entity.getObject().getKey();

                String invoiceFile = downloadObject(bucketName, objectKey);

                Invoice invoice = mapper.readValue(invoiceFile, Invoice.class);
                LOG.info("Invoice received: {}", invoice.getInvoiceNumber());

                invoiceRepository.save(invoice);
                s3.deleteObject(bucketName, objectKey);

            }
        }

    }

    private String downloadObject(String bucketName, String objectKey) throws IOException {
        S3Object object = s3.getObject(bucketName, objectKey);
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bfr = new BufferedReader(new InputStreamReader(object.getObjectContent()));

        String content = null;
        while ((content = bfr.readLine()) != null){
            stringBuilder.append(content);
        }
        return stringBuilder.toString();
    }

}
