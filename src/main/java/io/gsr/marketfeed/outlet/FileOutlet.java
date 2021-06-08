package io.gsr.marketfeed.outlet;

import io.gsr.marketfeed.PipelineStage;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

@Log4j2
public class FileOutlet<T> extends PipelineStage<T> {
    private final Path outputFilePath;

    public FileOutlet(Path outputFilePath) {
        super();
        this.outputFilePath = outputFilePath;
    }

    @Override
    public void handle(T message) {
        try {
            Files.write(outputFilePath, List.of(message.toString()), UTF_8, APPEND, CREATE);
        } catch (IOException e) {
            log.error("Failed to write to file: " + outputFilePath, e);
        }
    }
}
