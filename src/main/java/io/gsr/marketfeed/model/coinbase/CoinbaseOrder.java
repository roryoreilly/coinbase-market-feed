package io.gsr.marketfeed.model.coinbase;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@AllArgsConstructor
@EqualsAndHashCode
public class CoinbaseOrder {
    String side;
    BigDecimal size;

    public void applyChange(String side, BigDecimal size) {
        if (this.side.equals(side)) {
            this.size = this.size.add(size);
        }
    }
}
