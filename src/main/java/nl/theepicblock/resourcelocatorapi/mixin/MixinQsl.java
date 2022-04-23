package nl.theepicblock.resourcelocatorapi.mixin;

import net.fabricmc.api.EnvType;
import org.quiltmc.qsl.resource.loader.impl.ResourceLoaderImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ResourceLoaderImpl.class)
public class MixinQsl {
    /**
     * Forces the env type to be server
     */
    @Redirect(method = "registerBuiltinResourcePack(Lnet/minecraft/util/Identifier;Ljava/lang/String;Lorg/quiltmc/loader/api/ModContainer;Lorg/quiltmc/qsl/resource/loader/api/ResourcePackActivationType;Lnet/minecraft/text/Text;)Z",
            at = @At(value = "INVOKE", target = "Lorg/quiltmc/loader/api/minecraft/MinecraftQuiltLoader;getEnvironmentType()Lnet/fabricmc/api/EnvType;"))
    private static EnvType getEnvironmentType() {
        return EnvType.CLIENT;
    }
}
