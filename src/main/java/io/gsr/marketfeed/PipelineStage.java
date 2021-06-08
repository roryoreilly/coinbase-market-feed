package io.gsr.marketfeed;

import lombok.extern.log4j.Log4j2;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Log4j2
public abstract class PipelineStage<T> implements Runnable {
    protected final Queue<T> messageQueue;
    private boolean shouldComplete;

    protected PipelineStage() {
        this.messageQueue = new ConcurrentLinkedQueue<>();
        shouldComplete = false;
    }

    @Override
    public void run() {
        log.info("Beginning stage {}", this.getClass().getName());
        while (!shouldComplete) {
            if (!messageQueue.isEmpty()) {
                T message = messageQueue.poll();
                try {
                    handle(message);
                } catch (Exception ex) {
                    onException(message, ex);
                }
            }
        }
    }

    public Queue<T> getMessageQueue() {
        return messageQueue;
    }

    public void complete() {
        shouldComplete = true;
    }

    protected void onException(final T message, final Exception ex) {
        log.warn("Failed on message " + message + ". Will try to handle message again", ex);
        try {
            handle(message);
        } catch (Exception e) {
            log.error("Failed on message " + message, e);
        }
    }

    protected abstract void handle(T message);
}
