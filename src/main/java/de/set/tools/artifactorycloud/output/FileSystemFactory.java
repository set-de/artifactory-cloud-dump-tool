package de.set.tools.artifactorycloud.output;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;

public interface FileSystemFactory {

    FileSystem openFileSystem() throws IOException;

    Path getEntryPath() throws IOException;

}
