package io.gsr.marketfeed.model.coinbase;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

import java.time.Instant;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SubscriptionsCoinbaseMessage.class, name = "subscriptions"),
        @JsonSubTypes.Type(value = SnapshotCoinbaseMessage.class, name = "snapshot"),
        @JsonSubTypes.Type(value = UpdateCoinbaseMessage.class, name = "l2update")
})
public abstract class CoinbaseMessage {
    private String type;
    private Long sequence;
    private Instant time;
    private String product_id;
}
