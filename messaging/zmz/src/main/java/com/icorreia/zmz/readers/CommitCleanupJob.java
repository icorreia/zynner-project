package com.icorreia.zmz.readers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author Ivo Correia (idvcorreia@gmail.com)
 * @since 0.1
 */
public class CommitCleanupJob implements Runnable {

    /** A logger for this class. */
    private static final Logger logger = LoggerFactory.getLogger(MessageReader.class);

    private final LinkedBlockingQueue<String> commitLogFileNames;

    private final String commitFolder;

    public CommitCleanupJob(String compactionFolder) {
        this(compactionFolder, Integer.MAX_VALUE);
    }


    public CommitCleanupJob(String commitFolder, int capacity) {
        commitLogFileNames = new LinkedBlockingQueue<>(capacity);
        this.commitFolder = commitFolder;
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            try {
                String fileName = commitLogFileNames.take();
                Files.delete(Paths.get(commitFolder + fileName));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("Job interrupted.", e);
            } catch (IOException e) {
                logger.warn("Could not delete specified log.", e);
            }


        }


    }

    public boolean addFileName(String fileName) {
        return this.commitLogFileNames.add(fileName);
    }
}
