package com.icorreia.zmz.readers.commitlog;

import com.icorreia.zmz.readers.MessageReader;
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

    public CommitCleanupJob() {
        this(Integer.MAX_VALUE);
    }


    public CommitCleanupJob(int capacity) {
        commitLogFileNames = new LinkedBlockingQueue<>(capacity);
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            try {
                String fileName = commitLogFileNames.take();
                logger.info("Deleting file '{}'.", fileName);
                Files.delete(Paths.get(fileName));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("Job interrupted.", e);
            } catch (IOException e) {
                logger.warn("Could not delete specified log.", e);
            }
        }

        logger.info("Terminating commit log cleaner.");
    }

    public boolean addFileName(String fileName) {
        return this.commitLogFileNames.add(fileName);
    }
}
