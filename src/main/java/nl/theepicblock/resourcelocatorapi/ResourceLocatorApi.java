package nl.theepicblock.resourcelocatorapi;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.impl.resource.loader.ModResourcePackCreator;
import net.minecraft.resource.ResourceType;
import nl.theepicblock.resourcelocatorapi.api.AssetContainer;
import nl.theepicblock.resourcelocatorapi.impl.CompositeResourcePack;

public class ResourceLocatorApi {
	public static AssetContainer createGlobalAssetContainer() {
		// Create empty composite pack
		var compositePack = new CompositeResourcePack(ResourceType.CLIENT_RESOURCES);

		// Append all mod resource packs via fapi
		ModResourcePackCreator.CLIENT_RESOURCE_PACK_PROVIDER.register(compositePack::append);

		return compositePack;
	}
}
