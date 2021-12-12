package nl.theepicblock.resourcelocatorapi.impl;

import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import nl.theepicblock.resourcelocatorapi.api.AssetContainer;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompositeResourcePack implements AssetContainer {
    /**
     * All packs contained in this compositepack
     */
    private final List<ResourcePack> resourcePacks = new ArrayList<>();
    /**
     * For every namespace, this map contains a list of {@link ResourcePack}'s that contain assets in that namespace. For optimization purposes.
     */
    private final Map<String, List<ResourcePack>> packsPerNamespace = new HashMap<>();
    /**
     * The {@link ResourceType} that this resource pack is. (data packs are a type of resource pack)
     */
    private final ResourceType type;

    public CompositeResourcePack(ResourceType type) {
        this.type = type;
    }

    public void append(ResourcePackProfile packProfile) {
        append(packProfile.createResourcePack());
    }

    /**
     * Adds a {@link ResourcePack} to the composite pack, making all of it's assets available
     */
    public void append(ResourcePack pack) {
        resourcePacks.add(pack);

        for (var namespace : pack.getNamespaces(type)) {
            packsPerNamespace.computeIfAbsent(namespace, value -> new ArrayList<>()).add(pack);
        }
    }

    @Override
    public @NotNull InputStream getAsset(String namespace, String path) throws IOException {
        var packs = packsPerNamespace.get(namespace);
        if (packs == null) throw new FileNotFoundException();

        var id = new Identifier(namespace, path);
        for (var pack : packs) {
            if (pack.contains(type, id)) {
                return pack.open(type, id);
            }
        }
        throw new FileNotFoundException();
    }

    @Override
    public boolean containsAsset(String namespace, String path) {
        var packs = packsPerNamespace.get(namespace);
        if (packs == null) return false;

        var id = new Identifier(namespace, path);
        for (var pack : packs) {
            if (pack.contains(type, id)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void close() throws Exception {
        for (var pack : resourcePacks) {
            pack.close();
        }
    }
}

