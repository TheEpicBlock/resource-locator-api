package nl.theepicblock.resourcelocatorapi;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourceType;
import nl.theepicblock.resourcelocatorapi.api.AssetContainer;
import nl.theepicblock.resourcelocatorapi.impl.ArrpProvider;
import nl.theepicblock.resourcelocatorapi.impl.CompositeResourcePack;
import nl.theepicblock.resourcelocatorapi.impl.FapiProvider;
import nl.theepicblock.resourcelocatorapi.impl.QslProvider;

public class ResourceLocatorApi {
	public static AssetContainer createGlobalAssetContainer() {
		var loader = FabricLoader.getInstance();

		// Create empty composite pack
		var compositePack = new CompositeResourcePack(ResourceType.CLIENT_RESOURCES);

		if (loader.isModLoaded("advanced_runtime_resource_pack")) {
			ArrpProvider.addPacksBeforeVanilla(compositePack);
		}

		if (loader.isModLoaded("quilt_resource_loader")) {
			QslProvider.addPacks(compositePack);
		} else {
			FapiProvider.addPacks(compositePack);
		}

		if (loader.isModLoaded("advanced_runtime_resource_pack")) {
			ArrpProvider.addPacksAfterVanilla(compositePack);
		}

		return compositePack;
	}
}
