package de.set.tools.artifactorycloud.tasks;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.jfrog.artifactory.client.Artifactory;
import org.jfrog.artifactory.client.RepositoryHandle;
import org.jfrog.artifactory.client.model.Item;
import org.jfrog.artifactory.client.model.LightweightRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class BackupFile extends BackupAction {

    private static final Logger LOG = LoggerFactory.getLogger(BackupFile.class);

    private final Item file;
    private final Artifactory artifactory;
    private final Path repoDir;
    private final LightweightRepository repository;

    public BackupFile(
            final Item file,
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
                    this.file.getPath(),
                    this.repository.getKey()), e);
        }

    }

}
