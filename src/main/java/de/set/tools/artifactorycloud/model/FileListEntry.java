package de.set.tools.artifactorycloud.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FileListEntry {

    private String uri;
    private long size;
    private Date lastModified;
    private boolean folder;
    private String sha1;
    private String sha2;

    public String getUri() {
        return this.uri;
    }
    public void setUri(final String uri) {
        this.uri = uri;
    }
    public long getSize() {
        return this.size;
    }
    public void setSize(final long size) {
        this.size = size;
    }
    public Date getLastModified() {
        return this.lastModified;
    }
    public void setLastModified(final Date lastModified) {
        this.lastModified = lastModified;
    }
    public boolean isFolder() {
        return this.folder;
    }
    public void setFolder(final boolean folder) {
        this.folder = folder;
    }
    public String getSha1() {
        return this.sha1;
    }
    public void setSha1(final String sha1) {
        this.sha1 = sha1;
    }
    public String getSha2() {
        return this.sha2;
    }
    public void setSha2(final String sha2) {
        this.sha2 = sha2;
    }
}
