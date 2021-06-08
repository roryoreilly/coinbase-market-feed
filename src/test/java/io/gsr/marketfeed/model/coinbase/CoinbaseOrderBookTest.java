package io.gsr.marketfeed.model.coinbase;

import org.hamcrest.collection.IsMapContaining;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

class CoinbaseOrderBookTest {
    @Test
    void createFromSnapshot() {
        CoinbaseOrderBook orderBook = new CoinbaseOrderBook("BTC/USDT");
        SnapshotCoinbaseMessage message = new SnapshotCoinbaseMessage();
        message.setBids(List.of(createOrder("999.5", "1"), createOrder("1000", "10")));
        message.setAsks(List.of(createOrder("1000.01", "0.5"), createOrder("1001", "1")));
        orderBook.createFromSnapshot(message);

        assertThat(orderBook.getBids(), IsMapContaining.hasEntry(new BigDecimal("1000"), new BigDecimal("10")));
        assertThat(orderBook.getBids(), IsMapContaining.hasEntry(new BigDecimal("999.5"), new BigDecimal("1")));

        assertThat(orderBook.getAsks(), IsMapContaining.hasEntry(new BigDecimal("1000.01"), new BigDecimal("0.5")));
        assertThat(orderBook.getAsks(), IsMapContaining.hasEntry(new BigDecimal("1001"), new BigDecimal("1")));
    }

    private List<String> createOrder(final String price, final String size) {
        return List.of(price, size);
    }

    @Test
    void createNewOrderBookForLevels() {
        SortedMap<BigDecimal, BigDecimal> bids = new TreeMap<>(Collections.reverseOrder());
        bids.put(new BigDecimal("999.5"), new BigDecimal("1"));
        bids.put(new BigDecimal("1000"), new BigDecimal("10"));
        SortedMap<BigDecimal, BigDecimal> asks = new TreeMap<>();
        asks.put(new BigDecimal("1000.01"), new BigDecimal("0.5"));
        asks.put(new BigDecimal("1001"), new BigDecimal("1"));
        CoinbaseOrderBook orderBook = new CoinbaseOrderBook("BTC/USDT", bids, asks);

        CoinbaseOrderBook reducedOrderBook = orderBook.createNewOrderBookForLevels(1);

        assertThat(orderBook.getBids(), equalTo(bids));
        assertThat(orderBook.getAsks(), equalTo(asks));
        assertThat(reducedOrderBook.getBids(), not(equalTo(bids)));
        assertThat(reducedOrderBook.getAsks(), not(equalTo(asks)));

        assertThat(reducedOrderBook.getBids().size(), equalTo(1));
        assertThat(reducedOrderBook.getAsks().size(), equalTo(1));

        assertThat(reducedOrderBook.getBids().keySet(), hasItem(new BigDecimal("1000")));
        assertThat(reducedOrderBook.getAsks().keySet(), hasItem(new BigDecimal("1000.01")));
    }

    @Test
    void updateBookReturnsExpectedResultForUpdateOnPreviousPrice() {
        CoinbaseOrderBook orderBook = createBasicOrderBook();

        UpdateCoinbaseMessage updateCoinbaseMessage = new UpdateCoinbaseMessage();
        updateCoinbaseMessage.setChanges(List.of(List.of("buy", "1000", "5")));
        orderBook.updateBook(updateCoinbaseMessage);

        assertThat(orderBook.getBids().get(new BigDecimal("1000")), equalTo(new BigDecimal("5")));
    }

    @Test
    void updateBookReturnsExpectedResultForUpdateOnNewPrice() {
        CoinbaseOrderBook orderBook = createBasicOrderBook();

        UpdateCoinbaseMessage updateCoinbaseMessage = new UpdateCoinbaseMessage();
        updateCoinbaseMessage.setChanges(List.of(List.of("buy", "88", "100")));
        orderBook.updateBook(updateCoinbaseMessage);

        assertThat(orderBook.getBids().get(new BigDecimal("88")), equalTo(new BigDecimal("100")));
        assertThat(orderBook.getBids().size(), equalTo(3));
    }

    @Test
    void updateBookRemovesPriceIfUpdateIsZero() {
        CoinbaseOrderBook orderBook = createBasicOrderBook();

        UpdateCoinbaseMessage updateCoinbaseMessage = new UpdateCoinbaseMessage();
        updateCoinbaseMessage.setChanges(List.of(List.of("buy", "1000", "0.00000000")));
        orderBook.updateBook(updateCoinbaseMessage);

        assertThat(orderBook.getBids().keySet(), not(hasItem(new BigDecimal("1000"))));
        assertThat(orderBook.getBids().size(), equalTo(1));
    }

    @NotNull
    private CoinbaseOrderBook createBasicOrderBook() {
        SortedMap<BigDecimal, BigDecimal> bids = new TreeMap<>(Collections.reverseOrder());
        bids.put(new BigDecimal("999.5"), new BigDecimal("1"));
        bids.put(new BigDecimal("1000"), new BigDecimal("10"));
        SortedMap<BigDecimal, BigDecimal> asks = new TreeMap<>();
        asks.put(new BigDecimal("1000.01"), new BigDecimal("0.5"));
        asks.put(new BigDecimal("1001"), new BigDecimal("1"));
        CoinbaseOrderBook orderBook = new CoinbaseOrderBook("BTC/USDT", bids, asks);
        return orderBook;
    }
}