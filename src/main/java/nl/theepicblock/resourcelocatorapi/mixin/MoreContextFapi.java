package nl.theepicblock.resourcelocatorapi.mixin;

import net.fabricmc.fabric.impl.resource.loader.GroupResourcePack;
import nl.theepicblock.resourcelocatorapi.impl.MoreContextPack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GroupResourcePack.class)
public abstract class MoreContextFapi implements MoreContextPack {
    @Shadow public abstract String getFullName();

    @Override
    public String resourcelocatorapi$getFullName() {
        return this.getFullName();
    }
}
