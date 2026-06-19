package nl.theepicblock.resourcelocatorapi.api;

import com.mojang.datafixers.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.List;
import java.util.Set;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.IoSupplier;

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
    @Nullable IoSupplier<InputStream> getAsset(String namespace, String path);

    /**
     * Retrieves all versions of an asset. This is useful if multiple packs declare the same file.
     * (For example, you can have multiple resource packs defining a 'lang/en_us.json')
     */
    @NotNull List<IoSupplier<InputStream>> getAllAssets(String namespace, String path);

    /**
     * Retrieves a list of all the namespaces defined in this pack.
     */
    @NotNull Set<String> getNamespaces();

    /**
     * Checks if an asset exists. Uses the same lookup logic as {@link #getAsset(String, String)}
     */
    boolean containsAsset(String namespace, String path);

    default @NotNull Set<Pair<Identifier, IoSupplier<InputStream>>> locateLanguageFiles() {
        return locateFiles("lang");
    };

    @NotNull Set<Pair<Identifier, IoSupplier<InputStream>>> locateFiles(String prefix);
}
