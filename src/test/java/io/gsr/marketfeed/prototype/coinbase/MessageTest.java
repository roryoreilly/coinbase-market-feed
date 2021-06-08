package io.gsr.marketfeed.prototype.coinbase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gsr.marketfeed.model.coinbase.CoinbaseMessage;
import io.gsr.marketfeed.model.coinbase.SubscriptionsCoinbaseMessage;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

@Log4j2
class MessageTest {

    @Test
    public void testJsonMapperConvertsToSubscription() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        String jsonMessage = "{\"type\":\"subscriptions\",\"channels\":[{\"name\":\"level2\",\"product_ids\":[\"BTC-USD\"]}]}";
        CoinbaseMessage coinbaseMessage = mapper.readValue(jsonMessage, CoinbaseMessage.class);
        assertThat(coinbaseMessage, instanceOf(SubscriptionsCoinbaseMessage.class));
    }
}