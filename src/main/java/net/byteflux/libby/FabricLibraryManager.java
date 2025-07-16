package net.byteflux.libby;

import net.byteflux.libby.logging.adapters.FabricLogAdapter;
import net.byteflux.libby.classloader.URLClassLoaderHelper;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.fabricmc.loader.api.ModContainer;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

/**
 * A runtime dependency manager for Fabric mods.
 */
public class FabricLibraryManager extends LibraryManager {
    /**
     * Mod container classpath helper
     */
    private final URLClassLoaderHelper classLoader;

    private final ModContainer modContainer;

    /**
     * Creates a new Fabric library manager.
     *
     * @param logger        the mod logger
     * @param dataDirectory mod's data directory
     * @param modContainer  the mod container to manage
     * @param directoryName download directory name
     */
    public FabricLibraryManager(Logger logger,
                                Path dataDirectory,
                                ModContainer modContainer,
                                String directoryName) {
        super(new FabricLogAdapter(logger), dataDirectory, directoryName);
        boolean useCompatibilityClassLoader = FabricLoaderImpl.INSTANCE.getGameProvider().requiresUrlClassLoader() || Boolean.parseBoolean(System.getProperty("fabric.loader.useCompatibilityClassLoader", "false"));
        this.modContainer = requireNonNull(modContainer, "modContainer");
        classLoader = new URLClassLoaderHelper(useCompatibilityClassLoader ? (URLClassLoader) modContainer.getClass().getClassLoader() : (URLClassLoader) getDynamicURLClassLoader(modContainer.getClass().getClassLoader()), this);
    }

    /**
     * Creates a new Fabric library manager.
     *
     * @param logger        the mod logger
     * @param dataDirectory mod's data directory
     * @param modContainer  the mod container to manage
     */
    public FabricLibraryManager(Logger logger,
                                Path dataDirectory,
                                ModContainer modContainer) {
        this(logger, dataDirectory, modContainer, "lib");
    }

    /**
     * Adds a file to the Fabric mod's classpath.
     *
     * @param file the file to add
     */
    @Override
    protected void addToClasspath(Path file) {
        classLoader.addToClasspath(file);
    }

    @Override
    protected InputStream getPluginResourceAsInputStream(String path) {
        return modContainer.findPath(path)
                .map(p -> {
                    try {
                        return Files.newInputStream(p);
                    } catch (IOException e) {
                        throw new RuntimeException("Could not open resource: " + path, e);
                    }
                })
                .orElse(null);
    }

    private ClassLoader getDynamicURLClassLoader(ClassLoader classLoader) {
        try {
            Field urlLoaderField = classLoader.getClass().getDeclaredField("urlLoader");
            urlLoaderField.setAccessible(true);
            return (ClassLoader) urlLoaderField.get(classLoader);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}