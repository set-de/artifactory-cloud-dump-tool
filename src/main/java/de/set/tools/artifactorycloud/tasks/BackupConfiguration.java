package de.set.tools.artifactorycloud.tasks;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.RecursiveAction;

import org.jfrog.artifactory.client.Artifactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackupConfiguration extends RecursiveAction {


    private static final long serialVersionUID = -2261110588795990077L;
    private static final Logger LOG = LoggerFactory.getLogger(BackupConfiguration.class);

    private final Artifactory artifactory;
    private final Path backupDir;

    public BackupConfiguration(
            final Artifactory artifactory,
            final Path backupDir) {
        this.artifactory = artifactory;
        this.backupDir = backupDir;
    }

    @SuppressWarnings("nls")
    @Override
    public void compute() {
        LOG.info("Creating backup of configuration");
        final String configuration = this.artifactory.system().configuration();
        final Path configFile = this.backupDir.resolve("system-configuration.xml"); //$NON-NLS-1$
        try {
            Files.write(configFile,
                    configuration.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE_NEW);
            LOG.info("configuration was written to {}", configFile);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }



}
