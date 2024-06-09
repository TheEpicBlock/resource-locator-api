package nl.theepicblock.resourcelocatorapi.mixin;

import net.fabricmc.fabric.impl.resource.loader.ModNioResourcePack;
import nl.theepicblock.resourcelocatorapi.impl.MoreContextPack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ModNioResourcePack.class)
public abstract class MoreContextFapi implements MoreContextPack {
    @Shadow public abstract String getId();

    @Override
    public String resourcelocatorapi$getFullName() {
        return this.getId();
    }
}
