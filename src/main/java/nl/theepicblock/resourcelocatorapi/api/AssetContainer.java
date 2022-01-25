package nl.theepicblock.resourcelocatorapi.api;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * Manages mod assets.
 */
public interface AssetContainer extends AutoCloseable {
    /**
     * Retrieves an {@link InputStream} to an asset.
     *
     * Example:
     * <pre>{@code
     * var assetContainer = ..;
     * var inputstream = assetContainer.getAsset("mymod", "models/item/testitem.json");
     * }</pre>
     *
     * @param namespace Namespace containing the resource
     * @param path  Path to the resource
     */
    @NotNull InputStream getAsset(String namespace, String path) throws IOException;

    /**
     * Checks if an asset exists. Uses the same lookup logic as {@link #getAsset(String, String)}
     */
    boolean containsAsset(String namespace, String path);
}
