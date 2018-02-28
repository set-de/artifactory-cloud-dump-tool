package de.set.tools.artifactorycloud.tasks;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.concurrent.RecursiveAction;

import org.jfrog.artifactory.client.Artifactory;

public final class BackupArtifactory extends RecursiveAction {

    private static final long serialVersionUID = 1328540978178667582L;
    private final Artifactory artifactory;
    private final Path outputDir;

    public BackupArtifactory(final Path outputDir, final Artifactory artifactory) {
        this.outputDir = outputDir;
        this.artifactory = artifactory;
    }

    @SuppressWarnings("nls")
    @Override
    protected void compute() {
        try {
            final Path backupInfos = this.outputDir.resolve("backup.properties");
            if (!Files.exists(this.outputDir)) {
                Files.createDirectories(this.outputDir);
            }
            this.createInitialInfos(backupInfos);

            invokeAll(
                    new BackupConfiguration(this.artifactory, this.outputDir),
                    new BackupRepositories(this.artifactory, this.outputDir));
            this.appendFinishedBackupInformations(backupInfos, true);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("nls")
    private void createInitialInfos(final Path backupInfos) throws IOException {
        final Properties properties = new Properties();
        properties.put("backup.started", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        properties.put("user", System.getProperty("user.name"));
        properties.put("os", System.getProperty("os.name"));
        properties.put("backup.path", this.outputDir.toString());

        try (OutputStream os = Files.newOutputStream(backupInfos, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)) {
            properties.storeToXML(os, "");
        }
    }

    @SuppressWarnings("nls")
    private void appendFinishedBackupInformations(final Path backupInfos, final boolean successful) throws IOException {
        final Properties properties = new Properties();
        try (InputStream in = Files.newInputStream(backupInfos, StandardOpenOption.READ)) {
            properties.loadFromXML(in);
        }

        properties.put("result", successful ? "success" : "failure");
        properties.put("backup.finished", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));

        try (OutputStream os = Files.newOutputStream(backupInfos, StandardOpenOption.TRUNCATE_EXISTING)) {
            properties.storeToXML(os, "");
        }
    }
}