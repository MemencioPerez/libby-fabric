package net.byteflux.libby;

import net.byteflux.libby.logging.adapters.FabricLogAdapter;
import net.byteflux.libby.classloader.URLClassLoaderHelper;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import org.slf4j.Logger;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

/**
 * A runtime dependency manager for Fabric mods.
 */
public class FabricLibraryManager<T> extends LibraryManager {
    /**
     * Mod classpath helper
     */
    private final URLClassLoaderHelper classLoader;

    /**
     * Creates a new Fabric library manager.
     *
     * @param logger        the mod logger
     * @param dataDirectory mod's data directory
     * @param mod        the mod to manage
     * @param directoryName download directory name
     */
    public FabricLibraryManager(Logger logger,
                                Path dataDirectory,
                                T mod,
                                String directoryName) {

        super(new FabricLogAdapter(logger), dataDirectory, directoryName);
        boolean useCompatibilityClassLoader = FabricLoaderImpl.INSTANCE.getGameProvider().requiresUrlClassLoader() || Boolean.parseBoolean(System.getProperty("fabric.loader.useCompatibilityClassLoader", "false"));
        classLoader = new URLClassLoaderHelper(useCompatibilityClassLoader ? (URLClassLoader) requireNonNull(mod, "mod").getClass().getClassLoader() : (URLClassLoader) getDynamicURLClassLoader(requireNonNull(mod, "mod").getClass().getClassLoader()), this);
    }

    /**
     * Creates a new Fabric library manager.
     *
     * @param logger        the mod logger
     * @param dataDirectory mod's data directory
     * @param mod        the mod to manage
     */
    public FabricLibraryManager(Logger logger,
                                  Path dataDirectory,
                                  T mod) {
        this(logger, dataDirectory, mod, "lib");
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
        return getClass().getClassLoader().getResourceAsStream(path);
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