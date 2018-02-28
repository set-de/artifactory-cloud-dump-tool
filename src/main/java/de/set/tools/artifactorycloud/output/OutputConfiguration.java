package de.set.tools.artifactorycloud.output;

import java.util.Optional;

import org.jfrog.artifactory.client.ProxyConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.set.tools.artifactorycloud.output.DestinationConfig.Type;

@EnableConfigurationProperties({
    DestinationConfig.class,
    FileConfig.class,
    S3Config.class
})
@Configuration
public class OutputConfiguration {

    @Bean
    FileSystemFactory fileSystemFactory(
            final DestinationConfig destinationConfig,
            final ProxyConfig proxyConfig,
            final Optional<S3Config> s3,
            final Optional<FileConfig> file) {
        return destinationConfig.getType() == Type.S3 ?
                new S3FileSystemFactory(proxyConfig, s3.get()) :
                new DefaultFileSystemFactory(file.get());
    }

}
