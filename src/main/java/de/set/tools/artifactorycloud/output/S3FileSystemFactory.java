package de.set.tools.artifactorycloud.output;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Map;

import org.jfrog.artifactory.client.ProxyConfig;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.upplication.s3fs.AmazonS3Factory;

public class S3FileSystemFactory implements FileSystemFactory {

    private final ProxyConfig proxyConfig;
    private final S3Config s3config;
    private FileSystem fileSystem;

    @Autowired
    public S3FileSystemFactory(final ProxyConfig proxyConfig, final S3Config s3config) {
        this.proxyConfig = proxyConfig;
        this.s3config = s3config;
    }

    @Override
    public FileSystem openFileSystem() throws IOException {
        if (this.fileSystem != null && this.fileSystem.isOpen()) {
            return this.fileSystem;
        }

        final Map<String, Object> env = Maps.newHashMap();
        env.put(AmazonS3Factory.ACCESS_KEY, this.s3config.getAccessKeyId());
        env.put(AmazonS3Factory.SECRET_KEY, this.s3config.getSecretKey());

        if (this.proxyConfig != null) {
            env.put(AmazonS3Factory.PROXY_HOST, this.proxyConfig.getHost());
            env.put(AmazonS3Factory.PROXY_PORT, String.valueOf(this.proxyConfig.getPort()));
            env.put(AmazonS3Factory.PROXY_USERNAME, this.proxyConfig.getUser());
            env.put(AmazonS3Factory.PROXY_PASSWORD, this.proxyConfig.getPassword());
        }

        final String region = this.s3config.getRegion();
        final String uri = this.getUri(region);
        this.fileSystem = FileSystems.newFileSystem(
                URI.create(uri),
                env,
                Thread.currentThread().getContextClassLoader());
        return this.fileSystem;
    }

    @SuppressWarnings("nls")
    private String getUri(final String region) {
        String uri = "s3://";
        if (!Strings.isNullOrEmpty(region)) {
            uri += "s3.";
            uri += region;
            uri += ".amazonaws.com";
        }
        uri += "/";
        return uri;
    }

    @Override
    public Path getEntryPath() throws IOException {
        return this.openFileSystem().getPath("/" + this.s3config.getBucket()); //$NON-NLS-1$
    }

}
