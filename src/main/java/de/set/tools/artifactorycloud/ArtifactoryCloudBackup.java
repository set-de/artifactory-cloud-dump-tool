package de.set.tools.artifactorycloud;

import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.concurrent.ForkJoinPool;

import org.jfrog.artifactory.client.Artifactory;
import org.jfrog.artifactory.client.ArtifactoryClientBuilder;
import org.jfrog.artifactory.client.ProxyConfig;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.google.common.base.Strings;

import de.set.tools.artifactorycloud.output.FileSystemFactory;
import de.set.tools.artifactorycloud.tasks.BackupArtifactory;

@SpringBootApplication
@EnableConfigurationProperties({ ArtifactoryConfig.class })
public class ArtifactoryCloudBackup {

    public static void main(final String[] args) {
        SpringApplication.run(ArtifactoryCloudBackup.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(final Artifactory artifactory, final FileSystemFactory fileSystemFactory) {
        return args -> {
            try (FileSystem fs = fileSystemFactory.openFileSystem()) {
                final Path outputPath = fileSystemFactory.getEntryPath();

                ForkJoinPool.commonPool().invoke(
                    new BackupArtifactory(outputPath, artifactory));
            }
        };
    }

    @Bean
    Artifactory artifactory(final ProxyConfig proxy, final ArtifactoryConfig config) {
        final Artifactory artifactory = ArtifactoryClientBuilder.create()
                .setUrl(config.getUrl())
                .setUsername(config.getUser())
                .setPassword(config.getPassword())
                .setProxy(proxy)
                .build();

        return artifactory;
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
}
