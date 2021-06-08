package io.gsr.marketfeed.inlet;

import lombok.extern.log4j.Log4j2;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Queue;

@Log4j2
public class WebSocketInlet extends InletStage<String> {
    private final List<Queue<String>> nextStageQueues;
    protected WebSocketClientImpl webSocketClient;

    public WebSocketInlet(final String websocketServer, List<Queue<String>> nextStageQueues) throws URISyntaxException {
        this.nextStageQueues = nextStageQueues;
        webSocketClient = new WebSocketClientImpl(new URI(websocketServer), messageQueue, this::sendMessageOnOpen);
    }

    @Override
    public void start() {
        log.info("Starting inlet {}", this.getClass().getName());
        webSocketClient.connect();
        addShutdownHook();
    }

    @Override
    public void handle(String message) {
        nextStageQueues.forEach(q -> q.add(message));
    }

    @Override
    public void close() {
        log.info("Closing websocket inlet");
        webSocketClient.close();
    }

    protected void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    protected void sendMessageOnOpen() {
        log.info("Nothing required after open for this websocket inlet");
    }
}
