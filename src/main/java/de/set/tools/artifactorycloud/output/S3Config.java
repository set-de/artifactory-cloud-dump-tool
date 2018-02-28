package de.set.tools.artifactorycloud.output;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "s3")
public class S3Config {

    private String accessKeyId;

    private String secretKey;

    private String bucket;

    private String region;

    public String getAccessKeyId() {
        return this.accessKeyId;
    }

    public void setAccessKeyId(final String accessKey) {
        this.accessKeyId = accessKey;
    }

    public String getSecretKey() {
        return this.secretKey;
    }

    public void setSecretKey(final String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucket() {
        return this.bucket;
    }

    public void setBucket(final String bucket) {
        this.bucket = bucket;
    }

    public String getRegion() {
        return this.region;
    }

    public void setRegion(final String region) {
        this.region = region;
    }
}
