package com.icorreia.zmz.readers.commitlog;

import com.icorreia.commons.messaging.Message;
import com.icorreia.commons.serialization.Encoder;
import com.icorreia.zmz.readers.MessageReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author Ivo Correia (idvcorreia@gmail.com)
 * @since 0.1
 */
public class CommitLogHandler<T extends Message> {

    /** A logger for this class. */
    private static final Logger logger = LoggerFactory.getLogger(CommitLogHandler.class);

    private long commitLogSize;

    private String commitLogFolder;

    private CommitCleanupJob commitCleanupJob;

    private Thread cleanupThread;

    private Encoder<T> encoder;

    private long cunrretLogSize = 0;

    private String currentCommitLog = "";

    public CommitLogHandler(long commitLogSize, String commitLogFolder, Class<T> clazz) throws IOException {
        this.commitLogSize = commitLogSize;
        this.cunrretLogSize = 0;
        this.commitLogFolder = commitLogFolder;
        this.encoder = new Encoder<>();
        this.encoder.registerClass(clazz);

        currentCommitLog = genNewCommitLogName();
        File commitLog = new File(currentCommitLog);
        commitLog.createNewFile();
        if (!commitLog.exists()) {
            throw new IOException("Could not create new commit log.");
        }
        encoder.setOutput(currentCommitLog);
    }

    private String genNewCommitLogName() {
        return commitLogFolder + File.separatorChar + System.currentTimeMillis();
    }

    public void addMessage(T message) {
        try {
            if (cunrretLogSize >= commitLogSize) {
                commitCleanupJob.addFileName(currentCommitLog);
                currentCommitLog = genNewCommitLogName();
                encoder.setOutput(currentCommitLog);
                cunrretLogSize = 0;
                logger.info("Created new commit log file '{}'.", currentCommitLog);
            }

            encoder.encode(message);

            //TODO Maybe change the addition / name

        } catch (IOException e) {
            logger.error("Could not write to commit log.", e);
        }

    }

    public void start() {
        //TODO: Initial cleanup of non-empty folder.
        commitCleanupJob = new CommitCleanupJob();
        cleanupThread = new Thread(commitCleanupJob);
        cleanupThread.start();
    }

    public void stop() {
        try {
            encoder.close();
            cleanupThread.interrupt();
            cleanupThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Interrupted while stopping.", e);
        }
    }
}
