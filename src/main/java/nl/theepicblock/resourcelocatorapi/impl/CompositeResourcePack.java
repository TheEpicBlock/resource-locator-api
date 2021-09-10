package nl.theepicblock.resourcelocatorapi.impl;

import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import nl.theepicblock.resourcelocatorapi.api.ExtendedResourcePack;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompositeResourcePack implements ExtendedResourcePack, AutoCloseable {
    private final List<ResourcePack> resourcePacks = new ArrayList<>();
    private final Map<String, List<ResourcePack>> packsPerNamespace = new HashMap<>();
    private final ResourceType type;

    public CompositeResourcePack(ResourceType type) {
        this.type = type;
    }

    public void append(ResourcePackProfile packProfile) {
        append(packProfile.createResourcePack());
    }

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
