package io.gsr.marketfeed.inlet.coinbase;

import io.gsr.marketfeed.model.coinbase.CoinbaseChannel;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Builder
@Data
@ToString
public class CoinbaseSubscribeMessage {
    private String type;
    private List<String> product_ids;
    private List<CoinbaseChannel> channels;
}
