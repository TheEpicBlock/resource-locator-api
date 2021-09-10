package nl.theepicblock.resourcelocatorapi.api;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

public interface ExtendedResourcePack {
    String MODELS = "models/";
    String TEXTURES = "textures/";
    String SOUNDS = "sounds/";
    String BLOCKSTATES = "blockstates/";

    /**
     * @param path  Example: "testsound" will get "assets/minecraft/sounds/testsound.ogg".
     */
    default @NotNull InputStream getSoundFile(String namespace, String path) throws IOException {
        return getAsset(namespace, SOUNDS + path + ".ogg");
    }

    /**
     * @param path  Example: "testblock" will get "assets/minecraft/blockstates/testblock.json".
     */
    default @NotNull InputStream getBlockStateDefinition(String namespace, String path) throws IOException {
        return getAsset(namespace, BLOCKSTATES + path + ".json");
    }

    /**
     * @param path  Example: "testblock" will get "assets/minecraft/models/block/testblock.json".
     */
    default @NotNull InputStream getBlockModel(String namespace, String path) throws IOException {
        return getModel(namespace, "block/" + path);
    }

    /**
     * @param path  Example: "testitem" will get "assets/minecraft/models/item/testitem.json".
     */
    default @NotNull InputStream getItemModel(String namespace, String path) throws IOException {
        return getModel(namespace, "item/" + path);
    }

    /**
     * @param path  Example: "item/testitem" will get "assets/minecraft/models/item/testitem.json".
     */
    default @NotNull InputStream getModel(String namespace, String path) throws IOException {
        return getAsset(namespace, MODELS + path + ".json");
    }

    /**
     * @param path  Example: "item/testtexture" will get "assets/minecraft/textures/item/testtexture.png".
     */
    default @NotNull TextureInputStreams getTexture(String namespace, String path) throws IOException {
        InputStream texture = getAsset(namespace, TEXTURES + path + ".png");
        
        var metaPath = TEXTURES + path + ".png.mcmeta";
        if (this.containsAsset(namespace, metaPath)) {
            return new TextureInputStreams(texture, getAsset(namespace, path));
        } else {
            return new TextureInputStreams(texture, null);
        }
    }

    /**
     * @param path  Example: "models/item/testitem.json" will get "assets/minecraft/models/item/testitem.json".
     */
    @NotNull InputStream getAsset(String namespace, String path) throws IOException;

    boolean containsAsset(String namespace, String path);
}
