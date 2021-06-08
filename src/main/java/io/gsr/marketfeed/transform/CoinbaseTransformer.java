package io.gsr.marketfeed.transform;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.gsr.marketfeed.PipelineStage;
import io.gsr.marketfeed.model.coinbase.CoinbaseMessage;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Queue;

@Log4j2
public class CoinbaseTransformer extends PipelineStage<String> {
    private final List<Queue<CoinbaseMessage>> nextStageQueues;
    private final ObjectMapper mapper;

    public CoinbaseTransformer(final List<Queue<CoinbaseMessage>> nextStageQueues) {
        super();
        this.nextStageQueues = nextStageQueues;
        mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Override
    public void handle(String message) {
        try {
            CoinbaseMessage coinbaseMessage = mapper.readValue(message, CoinbaseMessage.class);
            nextStageQueues.forEach(q -> q.add(coinbaseMessage));
        } catch (JsonProcessingException e) {
            log.error("Failed to transform message " + message, e);
        }
    }

    @Override
    public Queue<String> getMessageQueue() {
        return messageQueue;
    }
}
