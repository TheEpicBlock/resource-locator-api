package nl.theepicblock.resourcelocatorapi.mixin;

import nl.theepicblock.resourcelocatorapi.impl.MoreContextPack;
//import org.quiltmc.qsl.resource.loader.api.GroupResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

//@Mixin(GroupResourcePack.class)
public abstract class MoreContextQsl implements MoreContextPack {
    //@Shadow
    public abstract String getFullName();

    @Override
    public String resourcelocatorapi$getFullName() {
        return this.getFullName();
    }
}
