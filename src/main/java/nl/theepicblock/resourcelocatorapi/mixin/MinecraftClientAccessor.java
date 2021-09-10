package nl.theepicblock.resourcelocatorapi.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.Supplier;

@Mixin(MinecraftClient.class)
public interface MinecraftClientAccessor {
    @Invoker
    static ResourcePackProfile callCreateResourcePackProfile(String name, Text displayName, boolean alwaysEnabled, Supplier<ResourcePack> packFactory, PackResourceMetadata metadata, ResourcePackProfile.InsertionPosition insertionPosition, ResourcePackSource source) {
        throw new IllegalStateException();
    }
}
