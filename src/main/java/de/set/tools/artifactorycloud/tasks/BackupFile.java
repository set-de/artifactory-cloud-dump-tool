package de.set.tools.artifactorycloud.tasks;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.util.concurrent.RecursiveAction;

import org.jfrog.artifactory.client.Artifactory;
import org.jfrog.artifactory.client.RepositoryHandle;
import org.jfrog.artifactory.client.model.LightweightRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.hash.Funnels;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
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
        LOG.info("Downloading {} from repository {} [size: {}; sha-1: {}]",
                this.file.getUri(),
                this.repository.getKey(),
                this.file.getSize(),
                this.file.getSha1());

        final Path target = this.getPath(this.file.getUri());

        if (!this.isUpToDate(target)) {
            this.downloadFile(target);
        } else {
            LOG.info("{} is up to date", target);
        }
    }

    protected void downloadFile(final Path target) {
        final RepositoryHandle handle = this.artifactory.repository(this.repository.getKey());
        try (InputStream input = handle.download(this.file.getUri()).doDownload()) {


            Files.createDirectories(target.getParent());
            Files.copy(input, target, StandardCopyOption.REPLACE_EXISTING);
            Files.setLastModifiedTime(target, FileTime.from(this.file.getLastModified().toInstant()));
            this.verify(target);

        } catch (final IOException e) {
            throw new RuntimeException(String.format("Cannot download file %s of repository", //$NON-NLS-1$
                    this.file.getUri(),
                    this.repository.getKey()), e);
        }
    }

    private boolean isUpToDate(final Path target) {
        try {
            return Files.exists(target)
                && this.file.getSize() == Files.size(target)
                && HashCode.fromString(this.file.getSha1()).equals(this.sha1(target));
        } catch (final IOException e) {
            LOG.warn("Unable to verify file", e); //$NON-NLS-1$
            return false;
        }
    }

    @SuppressWarnings("nls")
    private void verify(final Path target) throws IOException {
        final long fileSize = Files.size(target);
        if (fileSize != this.file.getSize()) {
            LOG.warn("Size mismatch: expected {} but was {} {}", this.file.getSize(), fileSize, target);
        }

        final HashCode sha1 = this.sha1(target);
        if (!HashCode.fromString(this.file.getSha1()).equals(sha1)) {
            LOG.warn("Sha-1 mismatch: expected {} but was {} {}", this.file.getSha1(), sha1, target);
        }

    }

    @SuppressWarnings("deprecation")
    private HashCode sha1(final Path file) throws IOException {
        final HashFunction hashFunction = Hashing.sha1();
        final Hasher hasher = hashFunction.newHasher();
        Files.copy(file, Funnels.asOutputStream(hasher));
        return hasher.hash();
    }

    private Path getPath(final String uri) {
        if (!Strings.isNullOrEmpty(uri) && uri.startsWith("/")) { //$NON-NLS-1$
            return this.getPath(uri.substring(1));
        } else {
            return this.repoDir.resolve(uri);
        }
    }

}
