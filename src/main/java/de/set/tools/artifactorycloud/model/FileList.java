package de.set.tools.artifactorycloud.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FileList {

    private List<FileListEntry> files;
    private String uri;
    private Date created;

    public List<FileListEntry> getFiles() {
        return this.files;
    }

    public void setFiles(final List<FileListEntry> files) {
        this.files = files;
    }

    public String getUri() {
        return this.uri;
    }

    public void setUri(final String uri) {
        this.uri = uri;
    }

    public Date getCreated() {
        return this.created;
    }

    public void setCreated(final Date created) {
        this.created = created;
    }
}
