package br.com.marcone.Aws_Project.service;

import br.com.marcone.Aws_Project.enums.EventType;
import br.com.marcone.Aws_Project.model.Envelope;
import br.com.marcone.Aws_Project.model.Product;
import br.com.marcone.Aws_Project.model.ProductEvent;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.Topic;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ProductPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(ProductPublisher.class);

    private AmazonSNS amazonSNS;
    private Topic topic;
    private ObjectMapper mapper;

    public ProductPublisher(AmazonSNS snsClient,
                            @Qualifier("productEventsTopic") Topic productEventsTopic,
                            ObjectMapper mapper){
        this.amazonSNS = snsClient;
        this.topic = productEventsTopic;
        this.mapper = mapper;

    }

    public void publishProductEvent(Product prod, EventType event, String username){
        ProductEvent prdEvent = new ProductEvent();
        prdEvent.setProductId(prod.getId());
        prdEvent.setCode(prod.getCode());
        prdEvent.setUsername(username);

        Envelope env = new Envelope();
        env.setEvent(event);

        try {
            env.setData(mapper.writeValueAsString(prdEvent));
            PublishResult result = amazonSNS.publish(topic.getTopicArn(), mapper.writeValueAsString(env));

            LOG.info("Product event send. Event: {}  - Product id: {} - Message Id {} - ",
                    env.getEvent(),
                    prdEvent.getProductId(),
                    result.getMessageId());

        } catch (JsonProcessingException e) {
            LOG.error("Erro");
        }

    }
}
