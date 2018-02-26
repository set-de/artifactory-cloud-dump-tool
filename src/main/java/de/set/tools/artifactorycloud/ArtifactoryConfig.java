package de.set.tools.artifactorycloud;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "artifactory")
public class ArtifactoryConfig {

    private String user;
    private String password;
    private String url;

    public String getUser() {
        return this.user;
    }
    public void setUser(final String user) {
        this.user = user;
    }
    public String getPassword() {
        return this.password;
    }
    public void setPassword(final String password) {
        this.password = password;
    }
    public String getUrl() {
        return this.url;
    }
    public void setUrl(final String url) {
        this.url = url;
    }
}
