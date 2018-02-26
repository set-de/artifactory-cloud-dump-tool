package de.set.tools.artifactorycloud.model;

import java.util.Date;
import java.util.List;

import org.jfrog.artifactory.client.model.Item;
import org.jfrog.artifactory.client.model.impl.ItemImpl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FileList {

    @JsonIgnore
    private List<Item> files;
    private String uri;
    private Date created;

    public List<Item> getFiles() {
        return this.files;
    }

    @JsonDeserialize(contentAs = ItemImpl.class)
    public void setFiles(final List<Item> files) {
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
