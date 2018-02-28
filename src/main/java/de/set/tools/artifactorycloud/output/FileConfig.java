package de.set.tools.artifactorycloud.output;

import java.io.File;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "local")
public class FileConfig {

    private File path;

    public File getPath() {
        return this.path;
    }

    public void setPath(final File path) {
        this.path = path;
    }
}
