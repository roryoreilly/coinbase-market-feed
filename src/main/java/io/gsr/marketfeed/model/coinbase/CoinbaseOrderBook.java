package io.gsr.marketfeed.model.coinbase;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@EqualsAndHashCode
@Log4j2
@AllArgsConstructor
public class CoinbaseOrderBook {
    private final String product;
    private SortedMap<BigDecimal, BigDecimal> bids;
    private SortedMap<BigDecimal, BigDecimal> asks;

    public CoinbaseOrderBook(String product) {
        this.product = product;
        bids = new TreeMap<>(Collections.reverseOrder());
        asks = new TreeMap<>();
    }

    public void createFromSnapshot(final SnapshotCoinbaseMessage message) {
        for (List<String> order : message.getBids()) {
            final BigDecimal price = new BigDecimal(order.get(0));
            final BigDecimal size = new BigDecimal(order.get(1));
            bids.put(price, size);
        }

        for (List<String> order : message.getAsks()) {
            final BigDecimal price = new BigDecimal(order.get(0));
            final BigDecimal size = new BigDecimal(order.get(1));
            asks.put(price, size);
        }
    }

    public CoinbaseOrderBook createNewOrderBookForLevels(int levels) {
        SortedMap<BigDecimal, BigDecimal> bids = this.bids.entrySet().stream()
                .limit(levels)
                .collect(TreeMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), Map::putAll);
        SortedMap<BigDecimal, BigDecimal> asks = this.asks.entrySet().stream()
                .limit(levels)
                .collect(TreeMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), Map::putAll);
        return new CoinbaseOrderBook(product, bids, asks);
    }

    public void updateBook(final UpdateCoinbaseMessage message) {
        for (List<String> change : message.getChanges()) {
            String side = change.get(0);
            BigDecimal price = new BigDecimal(change.get(1));
            BigDecimal size = new BigDecimal(change.get(2));
            SortedMap<BigDecimal, BigDecimal> book;
            if (side.equals("buy")) {
                book = bids;
            } else {
                book = asks;
            }
            if (size.compareTo(BigDecimal.ZERO) == 0) {
                book.remove(price);
            } else {
                book.put(price, size);
            }
        }
    }

    public CoinbaseOrderBook clone() {
        SortedMap<BigDecimal, BigDecimal> asks = (TreeMap<BigDecimal, BigDecimal>) ((TreeMap<BigDecimal, BigDecimal>) this.asks).clone();
        return new CoinbaseOrderBook(product, bids, asks);
    }

    @Override
    public String toString() {
        return product +
                ": bids=" + bids +
                ", asks=" + asks;
    }

    SortedMap<BigDecimal, BigDecimal> getBids() {
        return bids;
    }

    SortedMap<BigDecimal, BigDecimal> getAsks() {
        return asks;
    }
}
