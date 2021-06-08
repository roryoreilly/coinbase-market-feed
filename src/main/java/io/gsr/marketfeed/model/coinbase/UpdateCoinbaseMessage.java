package io.gsr.marketfeed.model.coinbase;

import lombok.Data;

import java.util.List;

/*
{
    "changes": [
        [
            "buy",
            "36354.85",
            "0.02530000"
        ]
    ],
    "product_id": "BTC-USD",
    "time": "2021-05-31T09:34:47.029145Z",
    "type": "l2update"
}
 */
@Data
public class UpdateCoinbaseMessage extends CoinbaseMessage {
    private List<List<String>> changes;
}
