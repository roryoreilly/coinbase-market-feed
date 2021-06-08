package io.gsr.marketfeed.model.coinbase;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/*
 * {
 *     "type": "snapshot",
 *     "product_id": "BTC-USD",
 *     "bids": [["10101.10", "0.45054140"]],
 *     "asks": [["10102.55", "0.57753524"]]
 * }
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class SnapshotCoinbaseMessage extends CoinbaseMessage {
    List<List<String>> asks;
    List<List<String>> bids;
}
