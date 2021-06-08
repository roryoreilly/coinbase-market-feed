package io.gsr.marketfeed.transform;

import io.gsr.marketfeed.PipelineStage;
import io.gsr.marketfeed.model.coinbase.CoinbaseMessage;
import io.gsr.marketfeed.model.coinbase.CoinbaseOrderBook;
import io.gsr.marketfeed.model.coinbase.SnapshotCoinbaseMessage;
import io.gsr.marketfeed.model.coinbase.SubscriptionsCoinbaseMessage;
import io.gsr.marketfeed.model.coinbase.UpdateCoinbaseMessage;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Queue;

@Log4j2
public class CoinbaseOrderBookHandler extends PipelineStage<CoinbaseMessage> {
    private final List<Queue<CoinbaseOrderBook>> nextStageQueues;
    private final int orderBookLevels;
    private final CoinbaseOrderBook orderBook;
    private final String product;

    public CoinbaseOrderBookHandler(final List<Queue<CoinbaseOrderBook>> nextStageQueues, final int orderBookLevels, final String product) {
        super();
        this.nextStageQueues = nextStageQueues;
        this.orderBookLevels = orderBookLevels;
        this.product = product;
        orderBook = new CoinbaseOrderBook(product);
    }

    @Override
    public void handle(CoinbaseMessage coinbaseMessage) {
        if (coinbaseMessage instanceof SubscriptionsCoinbaseMessage) {
            return;
        }
        if (coinbaseMessage.getProduct_id().equals(product)) {
            updateOrderBook(coinbaseMessage);
            CoinbaseOrderBook reducedOrderBook = orderBook.createNewOrderBookForLevels(orderBookLevels);
            nextStageQueues.forEach(q -> q.add(reducedOrderBook));
        }
    }

    private void updateOrderBook(final CoinbaseMessage coinbaseMessage) {
        if (coinbaseMessage instanceof SnapshotCoinbaseMessage) {
            orderBook.createFromSnapshot((SnapshotCoinbaseMessage) coinbaseMessage);
            log.debug("Created order book from snapshot: {}", coinbaseMessage);
        } else if (coinbaseMessage instanceof UpdateCoinbaseMessage) {
            orderBook.updateBook((UpdateCoinbaseMessage) coinbaseMessage);
            log.debug("Updated order book from snapshot: {}", coinbaseMessage);
        }
    }

    @Override
    public Queue<CoinbaseMessage> getMessageQueue() {
        return messageQueue;
    }
}
