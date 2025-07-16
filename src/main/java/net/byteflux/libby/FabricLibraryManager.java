package net.byteflux.libby;

import net.byteflux.libby.logging.adapters.FabricLogAdapter;
import net.fabricmc.loader.impl.launch.FabricLauncherBase;
import org.slf4j.Logger;

import java.io.InputStream;
import java.nio.file.Path;

/**
 * A runtime dependency manager for Fabric mods.
 */
public class FabricLibraryManager extends LibraryManager {
    /**
     * Creates a new Fabric library manager.
     *
     * @param logger        the mod logger
     * @param dataDirectory mod's data directory
     * @param directoryName download directory name
     */
    public FabricLibraryManager(Logger logger,
                                Path dataDirectory,
                                String directoryName) {
        super(new FabricLogAdapter(logger), dataDirectory, directoryName);
    }

    /**
     * Creates a new Fabric library manager.
     *
     * @param logger        the mod logger
     * @param dataDirectory mod's data directory
     */
    public FabricLibraryManager(Logger logger,
                                Path dataDirectory) {
        this(logger, dataDirectory, "lib");
    }

    /**
     * Adds a file to the Fabric mod's classpath.
     *
     * @param file the file to add
     */
    @Override
    protected void addToClasspath(Path file) {
        FabricLauncherBase.getLauncher().addToClassPath(file);
    }

    @Override
    protected InputStream getPluginResourceAsInputStream(String path) {
        return getClass().getClassLoader().getResourceAsStream(path);
    }
}