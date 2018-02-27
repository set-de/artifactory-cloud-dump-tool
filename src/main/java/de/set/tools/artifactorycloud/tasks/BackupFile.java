package de.set.tools.artifactorycloud.tasks;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.RecursiveAction;

import org.jfrog.artifactory.client.Artifactory;
import org.jfrog.artifactory.client.RepositoryHandle;
import org.jfrog.artifactory.client.model.LightweightRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.set.tools.artifactorycloud.model.FileListEntry;

@SuppressWarnings("serial")
public class BackupFile extends RecursiveAction {

    private static final Logger LOG = LoggerFactory.getLogger(BackupFile.class);

    private final FileListEntry file;
    private final Artifactory artifactory;
    private final Path repoDir;
    private final LightweightRepository repository;

    public BackupFile(
            final FileListEntry file,
            final LightweightRepository repository,
            final Artifactory artifactory,
            final Path repoDir) {
        this.file = file;
        this.repository = repository;
        this.artifactory = artifactory;
        this.repoDir = repoDir;
    }

    @SuppressWarnings("nls")
    @Override
    protected void compute() {
        try {
            LOG.info("Downloading {} in repository {}", this.file.getUri(), this.repository.getKey());
            final RepositoryHandle handle = this.artifactory.repository(this.repository.getKey());
            final Path target = this.repoDir.resolve("." + this.file.getUri());
            Files.createDirectories(target.getParent());
            try (InputStream input = handle.download(this.file.getUri()).doDownload()) {
                Files.copy(input, target);
            }

        } catch (final IOException e) {
            throw new RuntimeException(String.format("Cannot download file %s of repository",
                    this.file.getUri(),
                    this.repository.getKey()), e);
        }

    }

}
