package io.gsr.marketfeed.model.coinbase;

import lombok.Data;

import java.util.List;

@Data
public class SubscriptionsCoinbaseMessage extends CoinbaseMessage {
    private List<CoinbaseChannel> channels;
}
