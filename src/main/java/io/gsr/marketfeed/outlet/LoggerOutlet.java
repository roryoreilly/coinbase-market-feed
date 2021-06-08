package io.gsr.marketfeed.outlet;

import io.gsr.marketfeed.PipelineStage;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class LoggerOutlet<T> extends PipelineStage<T> {
    public LoggerOutlet() {
        super();
    }

    @Override
    public void handle(T message) {
        log.info(message.toString());
    }
}
