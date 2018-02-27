package de.set.tools.artifactorycloud.tasks;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.concurrent.RecursiveAction;

import org.jfrog.artifactory.client.Artifactory;
import org.jfrog.artifactory.client.RepositoryHandle;
import org.jfrog.artifactory.client.model.LightweightRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

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
        LOG.info("Downloading {} in repository {}", this.file.getUri(), this.repository.getKey());
        final RepositoryHandle handle = this.artifactory.repository(this.repository.getKey());
        try (InputStream input = handle.download(this.file.getUri()).doDownload()) {

            final Path target = this.repoDir.resolve("." + this.file.getUri());
            Files.createDirectories(target.getParent());
            Files.copy(input, target);
            Files.setLastModifiedTime(target, FileTime.from(this.file.getLastModified().toInstant()));
            this.verify(target);

        } catch (final IOException e) {
            throw new RuntimeException(String.format("Cannot download file %s of repository",
                    this.file.getUri(),
                    this.repository.getKey()), e);
        }

    }

    @SuppressWarnings("nls")
    private void verify(final Path target) throws IOException {
        final long fileSize = Files.size(target);
        if (fileSize != this.file.getSize()) {
            throw new IOException(
                String.format("Size mismatch: expected %d but was %d %s", this.file.getSize(), fileSize, target));
        }

        final HashCode sha1 = this.sha1(target);
        if (!HashCode.fromString(this.file.getSha1()).equals(sha1)) {
             throw new IOException(
                String.format("Sha-1 mismatch: expected %s but was %s %s", this.file.getSha1(), sha1, target));
        }

    }

    @SuppressWarnings("deprecation")
    private HashCode sha1(final Path file) throws IOException {
        return com.google.common.io.Files.asByteSource(file.toFile()).hash(Hashing.sha1());
    }

}
