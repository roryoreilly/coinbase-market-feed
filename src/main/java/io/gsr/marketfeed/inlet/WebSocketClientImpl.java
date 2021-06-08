package io.gsr.marketfeed.inlet;

import lombok.extern.log4j.Log4j2;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Queue;

@Log4j2
public class WebSocketClientImpl extends WebSocketClient {
    private final Queue<String> messageQueue;
    private final Runnable sendMessageOnOpen;

    public WebSocketClientImpl(final URI uri, final Queue<String> messageQueue, final Runnable sendMessageOnOpen) {
        super(uri);
        this.messageQueue = messageQueue;
        this.sendMessageOnOpen = sendMessageOnOpen;
    }

    @Override
    public void onOpen(final ServerHandshake serverHandshake) {
        log.debug("Got server handshake for web socket inlet of: {}", serverHandshake.getHttpStatusMessage());
        sendMessageOnOpen.run();
    }

    @Override
    public void onMessage(final String message) {
        messageQueue.add(message);
        log.debug("Websocket inlet received message: {}", message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        if (remote) {
            log.warn("WebsocketInlet closed by the remote peer with code {}. The reason was '{}'", code, reason);
        } else {
            log.info("WebsocketInlet closed with code {}. The reason was '{}'", code, reason);
        }
    }

    @Override
    public void onError(final Exception e) {
        log.error("Error on websocket", e);
    }
}
