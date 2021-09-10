package nl.theepicblock.resourcelocatorapi.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;

public class TextureInputStreams {
    @NotNull
    public final InputStream texture;
    @Nullable
    public final InputStream meta;

    public TextureInputStreams(@NotNull InputStream texture, @Nullable InputStream meta) {
        this.texture = texture;
        this.meta = meta;
    }
}
