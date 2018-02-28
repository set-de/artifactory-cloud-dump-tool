package de.set.tools.artifactorycloud.output;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;

public class DefaultFileSystemFactory implements FileSystemFactory {

    private final FileConfig fileConfig;

    @Autowired
    public DefaultFileSystemFactory(final FileConfig fileConfig) {
        this.fileConfig = fileConfig;
    }

    @Override
    public FileSystem openFileSystem() {
        return FileSystems.getDefault();
    }

    @Override
    public Path getEntryPath() {
        return this.fileConfig.getPath().toPath();
    }

}
