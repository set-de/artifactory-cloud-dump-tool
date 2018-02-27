package de.set.tools.artifactorycloud.tasks;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.RecursiveAction;
import java.util.stream.Collectors;

import org.jfrog.artifactory.client.Artifactory;
import org.jfrog.artifactory.client.ArtifactoryRequest.ContentType;
import org.jfrog.artifactory.client.ArtifactoryRequest.Method;
import org.jfrog.artifactory.client.ArtifactoryResponse;
import org.jfrog.artifactory.client.impl.ArtifactoryRequestImpl;
import org.jfrog.artifactory.client.model.LightweightRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.set.tools.artifactorycloud.model.FileList;

public class BackupRepository extends RecursiveAction {

    private static final long serialVersionUID = -1372343545486875451L;
    private static final Logger LOG = LoggerFactory.getLogger(BackupRepository.class);

    private final LightweightRepository repo;
    private final Artifactory artifactory;
    private final Path backupDir;

    public BackupRepository(
            final LightweightRepository repo,
            final Artifactory artifactory,
            final Path backupDir) {
                this.repo = repo;
                this.artifactory = artifactory;
                this.backupDir = backupDir;
    }

    @SuppressWarnings("nls")
    @Override
    protected void compute() {
        try {
            final Path repoDir = this.backupDir.resolve(this.repo.getKey());
            LOG.info("Creating backup of repository {} into {}", this.repo.getKey(), repoDir);
            Files.createDirectories(repoDir);
            final ArtifactoryRequestImpl request = new ArtifactoryRequestImpl()
                .method(Method.GET)
                .apiUrl("api/storage/" + this.repo.getKey())
                .responseType(ContentType.JSON)
                .addQueryParam("list", "1")
                .addQueryParam("deep", "1");
            final ArtifactoryResponse response = this.artifactory.restCall(request);
            if (response.isSuccessResponse()) {
                this.saveData(response.getRawBody());
                final FileList fileList = response.parseBody(FileList.class);
                invokeAll(fileList.getFiles().stream()
                        .filter((entry) -> !entry.isFolder())
                        .map((file) -> new BackupFile(
                                file,
                                this.repo,
                                this.artifactory,
                                repoDir))
                .collect(Collectors.toList()));
            }
            LOG.info("Finished backup of repository {}", this.repo.getKey());
        } catch (final Exception e) {
            throw new RuntimeException(String.format("Cannot backup repository %s", this.repo.getKey()), e);
        }
    }

    private void saveData(final String rawBody) throws IOException {
        final Path path = this.backupDir.resolve(String.format("%s-file-list.json", this.repo.getKey())); //$NON-NLS-1$
        Files.write(path, rawBody.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE_NEW);
    }

}
