package de.set.tools.artifactorycloud.tasks;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.Collectors;

import org.jfrog.artifactory.client.Artifactory;
import org.jfrog.artifactory.client.model.LightweightRepository;
import org.jfrog.artifactory.client.model.impl.RepositoryTypeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackupRepositories extends BackupAction {

    private static final long serialVersionUID = 4028274842311700214L;
    private static final Logger LOG = LoggerFactory.getLogger(BackupRepositories.class);

    private final Artifactory artifactory;
    private final Path backupDir;

    public BackupRepositories(final Artifactory artifactory, final Path backupDir) {
        this.artifactory = artifactory;
        this.backupDir = backupDir;
    }

    @Override
    protected void compute() {
        final Path repoDir = this.backupDir.resolve("repositories"); //$NON-NLS-1$
        LOG.info("Start backups of repositories into {}", repoDir); //$NON-NLS-1$
        final List<LightweightRepository> repositories = this.artifactory.repositories().list(null);
        ForkJoinTask.invokeAll(repositories
            .stream()
            .filter((repo) -> repo.getType() != RepositoryTypeImpl.VIRTUAL)
            .map((repo) -> new BackupRepository(repo, this.artifactory, repoDir))
            .collect(Collectors.toList()));
        LOG.info("Finished repository backup"); //$NON-NLS-1$
    }

}
