package nl.theepicblock.resourcelocatorapi;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.impl.resource.loader.ModResourcePackCreator;
import net.minecraft.resource.ResourceType;
import nl.theepicblock.resourcelocatorapi.api.ExtendedResourcePack;
import nl.theepicblock.resourcelocatorapi.impl.CompositeResourcePack;

public class ResourceLocatorApi implements ModInitializer {
	@Override
	public void onInitialize() {
		
	}

	public static ExtendedResourcePack getGlobalResourcePack() {
		var compositePack = new CompositeResourcePack(ResourceType.CLIENT_RESOURCES);
		ModResourcePackCreator.CLIENT_RESOURCE_PACK_PROVIDER.register(compositePack::append);
		return compositePack;
	}
}
