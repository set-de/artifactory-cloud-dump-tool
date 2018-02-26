package de.set.tools.artifactorycloud;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import org.jfrog.artifactory.client.Artifactory;
import org.jfrog.artifactory.client.ArtifactoryClientBuilder;
import org.jfrog.artifactory.client.ProxyConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.google.common.base.Strings;

import de.set.tools.artifactorycloud.tasks.BackupConfiguration;
import de.set.tools.artifactorycloud.tasks.BackupRepositories;

@SpringBootApplication
public class ArtifactoryCloudBackup {

    @Value("${artifactory.url}")
    private String artifactoryUrl;

    @Value("${artifactory.user}")
    private String artifactoryUser;

    @Value("${artifactory.password}")
    private String artifactoryPassword;

    @Value("${output.dir}")
    private final File outputDir = new File("backup"); //$NON-NLS-1$

    public static void main(final String[] args) {
        SpringApplication.run(ArtifactoryCloudBackup.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(final ApplicationContext ctx) {
        return args -> {
            final Artifactory artifactory = ctx.getBean(Artifactory.class);

            final ForkJoinPool pool = ForkJoinPool.commonPool();
            pool.invoke(new RecursiveAction() {

                private static final long serialVersionUID = 1328540978178667582L;

                @Override
                protected void compute() {
                    try {
                        final Path backupPath = ArtifactoryCloudBackup.this.outputDir.toPath();
                        Files.createDirectories(backupPath);

                        invokeAll(
                                new BackupConfiguration(artifactory, backupPath),
                                new BackupRepositories(artifactory, backupPath));
                    } catch (final IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

        };
    }

    @SuppressWarnings("nls")
    @Bean
    ProxyConfig proxyConfig() {
        final String host = System.getProperty("http.proxyHost");
        final String port = System.getProperty("http.proxyPort");
        if (!Strings.isNullOrEmpty(host) && !Strings.isNullOrEmpty(port)) {
            return new HttpProxyConfig(host, Integer.valueOf(port), null, null);
        } else {
            return null;
        }
    }

    @Bean
    Artifactory artifactory(final ProxyConfig proxy) {
        final Artifactory artifactory = ArtifactoryClientBuilder.create()
                .setUrl(this.artifactoryUrl)
                .setUsername(this.artifactoryUser)
                .setPassword(this.artifactoryPassword)
                .setProxy(proxy)
                .build();

        return artifactory;
    }
}
