package nl.theepicblock.resourcelocatorapi.impl;


import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import org.quiltmc.qsl.resource.loader.impl.ModResourcePackProvider;
import org.quiltmc.qsl.resource.loader.impl.ResourceLoaderImpl;

import java.util.ArrayList;

public class QslProvider {
    public static void addPacks(CompositeResourcePack pack) {
        var packs = new ArrayList<ResourcePack>();
        ResourceLoaderImpl.appendModResourcePacks(packs, ResourceType.CLIENT_RESOURCES, null);
        packs.forEach(pack::append);

        ModResourcePackProvider.CLIENT_RESOURCE_PACK_PROVIDER.register(pack::append);
    }
}
