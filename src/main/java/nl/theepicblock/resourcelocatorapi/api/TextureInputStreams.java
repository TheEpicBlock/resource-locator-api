package nl.theepicblock.resourcelocatorapi.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;

public record TextureInputStreams(@NotNull InputStream texture, @Nullable InputStream meta) {}
