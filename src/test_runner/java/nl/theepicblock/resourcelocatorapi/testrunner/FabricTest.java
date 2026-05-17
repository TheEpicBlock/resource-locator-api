package nl.theepicblock.resourcelocatorapi.testrunner;

import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;

public class FabricTest {
    public static void init() {
        var loader = FabricLoader.getInstance();
        var c = loader.getModContainer(RLATest.MODID).orElseThrow();
        var b1 = ResourceLoader.registerBuiltinPack(RLATest.id("normal"), c, Component.literal("normal"), PackActivationType.NORMAL);
        var b2 = ResourceLoader.registerBuiltinPack(RLATest.id("always_enabled"), c, Component.literal("always_enabled"), PackActivationType.ALWAYS_ENABLED);
        var b3 = ResourceLoader.registerBuiltinPack(RLATest.id("default_enabled"), c, Component.literal("default_enabled"), PackActivationType.DEFAULT_ENABLED);
        if (!b1 || !b2 || !b3) {
            RLATest.LOGGER.warn("resource registration failed");
        }
    }
}
