package io.gsr.marketfeed;

import io.gsr.marketfeed.inlet.InletStage;
import io.gsr.marketfeed.inlet.coinbase.CoinbaseWebSocketInlet;
import io.gsr.marketfeed.model.coinbase.CoinbaseMessage;
import io.gsr.marketfeed.model.coinbase.CoinbaseOrderBook;
import io.gsr.marketfeed.outlet.SystemOutOutlet;
import io.gsr.marketfeed.transform.CoinbaseOrderBookHandler;
import io.gsr.marketfeed.transform.CoinbaseTransformer;
import lombok.extern.log4j.Log4j2;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

@Log4j2
public class MarketFeedApplication {

    public static void main(String[] args) {
        Config config = new Config(args);
        try {
            final List<PipelineStage<?>> createCoinbasePipeline = createCoinbasePipeline(config);
            Orchestrator orchestrator = new Orchestrator(createCoinbasePipeline);
            orchestrator.start();
        } catch (Exception e) {
            log.error("Failed to run market feed application ", e);
        }
    }

    private static List<PipelineStage<?>> createCoinbasePipeline(final Config config) throws InvalidKeyException, URISyntaxException {
        List<String> products = config.getCoinbaseProducts();

        List<PipelineStage<?>> pipelineStages = new ArrayList<>();
        List<Queue<CoinbaseMessage>> orderBookMessageQueue = new ArrayList<>();
        for (String product : products) {
            if (!config.getCoinbaseProductsAll().contains(product)) {
                throw new IllegalArgumentException(product + " is not a valid coinbase pro product");
            }
            PipelineStage<CoinbaseOrderBook> systemOutOutlet = new SystemOutOutlet<>();
            PipelineStage<CoinbaseMessage> coinbaseOrderBookStage = new CoinbaseOrderBookHandler(List.of(systemOutOutlet.getMessageQueue()), config.getCoinbaseOrderBookLevels(), product);
            pipelineStages.addAll(List.of(systemOutOutlet, coinbaseOrderBookStage));
            orderBookMessageQueue.add(coinbaseOrderBookStage.getMessageQueue());
        }

        PipelineStage<String> coinbaseOrderBookTransformer = new CoinbaseTransformer(orderBookMessageQueue);
        InletStage<String> coinbaseWebSocketInlet = new CoinbaseWebSocketInlet(config.getCoinbaseWebsocketUrl(), List.of(coinbaseOrderBookTransformer.getMessageQueue()), products);
        pipelineStages.addAll(List.of(coinbaseOrderBookTransformer, coinbaseWebSocketInlet));
        return pipelineStages;
    }

}
