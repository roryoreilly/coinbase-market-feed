package io.gsr.marketfeed.inlet.coinbase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gsr.marketfeed.inlet.WebSocketInlet;
import io.gsr.marketfeed.model.coinbase.CoinbaseChannel;
import lombok.extern.log4j.Log4j2;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Queue;

@Log4j2
public class CoinbaseWebSocketInlet extends WebSocketInlet {
    private final ObjectMapper mapper;
    private final List<String> products;

    public CoinbaseWebSocketInlet(final String websocketServer, final List<Queue<String>> nextStageQueues, final List<String> products) throws URISyntaxException {
        super(websocketServer, nextStageQueues);
        this.products = products;
        mapper = new ObjectMapper();
    }

    @Override
    protected void sendMessageOnOpen() {
        CoinbaseSubscribeMessage subscribeMessage = CoinbaseSubscribeMessage.builder()
                .type("subscribe")
                .product_ids(products)
                .channels(List.of(CoinbaseChannel.builder().name("level2").build()))
                .build();

        try {
            final String subscribeMessageAsString = mapper.writeValueAsString(subscribeMessage);
            log.debug("Sending message after open of: {}", subscribeMessageAsString);
            webSocketClient.send(subscribeMessageAsString);
        } catch (JsonProcessingException e) {
            log.error("Failed to subscribe to Coinbase websocket", e);
        }
    }
}
