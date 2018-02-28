package de.set.tools.artifactorycloud.output;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("destination")
public class DestinationConfig {

    public enum Type {
        LOCAL,
        S3
    }

    private Type type = Type.LOCAL;

    public Type getType() {
        return this.type;
    }

    public void setType(final Type type) {
        this.type = type;
    }
}
