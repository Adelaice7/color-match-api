package com.rmeunier.colormatchapi.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;


public class ItemCountListener implements ChunkListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemCountListener.class);

    @Override
    public void beforeChunk(ChunkContext chunkContext) {

    }

    @Override
    public void afterChunk(ChunkContext chunkContext) {
        String sum = chunkContext.getStepContext().getStepExecution().getSummary();

        LOGGER.info("Summary: {}", sum);
    }

    @Override
    public void afterChunkError(ChunkContext chunkContext) {

    }
}
