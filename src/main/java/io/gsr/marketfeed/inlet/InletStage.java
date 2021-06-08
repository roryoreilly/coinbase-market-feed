package io.gsr.marketfeed.inlet;

import io.gsr.marketfeed.PipelineStage;

public abstract class InletStage<T> extends PipelineStage<T> {
    public abstract void start();

    public abstract void close();
}
