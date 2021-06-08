package io.gsr.marketfeed.outlet;

import io.gsr.marketfeed.PipelineStage;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SystemOutOutlet<T> extends PipelineStage<T> {
    public SystemOutOutlet() {
        super();
    }

    @Override
    public void handle(T message) {
        System.out.println(message.toString());
    }
}
