package io.gsr.marketfeed;

import io.gsr.marketfeed.inlet.InletStage;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Log4j2
public class Orchestrator {
    private final List<PipelineStage<?>> pipelineStages;
    private final ExecutorService executorService;

    public Orchestrator(final List<PipelineStage<?>> pipelineStages) {
        this.pipelineStages = pipelineStages;
        this.executorService = Executors.newFixedThreadPool(pipelineStages.size());
    }

    public void start() {
        pipelineStages.forEach(this::beginStage);

        try {
            executorService.awaitTermination(100000, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            log.error("Interruption on executor service", e);
        }
    }

    private void beginStage(PipelineStage<?> stage) {
        if (stage instanceof InletStage) {
            ((InletStage<?>) stage).start();
        }
        executorService.submit(stage);
    }
}
