package nl.theepicblock.resourcelocatorapi.api;

import net.minecraft.resource.InputSupplier;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
    @Nullable InputSupplier<InputStream> getAsset(String namespace, String path);

    /**
     * Retrieves all versions of an asset. This is useful if multiple packs declare the same file.
     * (For example, you can have multiple resource packs defining a 'lang/en_us.json')
     */
    @NotNull List<InputSupplier<InputStream>> getAllAssets(String namespace, String path);

    /**
     * Retrieves a list of all the namespaces defined in this pack.
     */
    @NotNull Set<String> getNamespaces();

    /**
     * Checks if an asset exists. Uses the same lookup logic as {@link #getAsset(String, String)}
     */
    boolean containsAsset(String namespace, String path);
    default @NotNull Set<Pair<Identifier, InputSupplier<InputStream>>> locateLanguageFiles() {
        return locateFiles("lang");
    };
    @NotNull Set<Pair<Identifier, InputSupplier<InputStream>>> locateFiles(String prefix);
}
