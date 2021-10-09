package com.rmeunier.colormatchapi.config;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;


public class ItemCountListener implements ChunkListener {
    @Override
    public void beforeChunk(ChunkContext chunkContext) {

    }

    @Override
    public void afterChunk(ChunkContext chunkContext) {
        int count = chunkContext.getStepContext().getStepExecution().getReadCount();
        System.out.println("ItemCount: " + count);
    }

    @Override
    public void afterChunkError(ChunkContext chunkContext) {

    }
}
