package nl.theepicblock.resourcelocatorapi.testrunner;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import nl.theepicblock.resourcelocatorapi.ResourceLocatorApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.List;

public class RLATest implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("RLA-Testrunner");
    @Override
    public void onInitialize() {
        var nonce = System.getenv("TESTRUNNER_NONCE");
        if (nonce == null) {
            throw new NullPointerException("TESTRUNNER_NONCE needs to be set");
        }
        var expects_unparsed = System.getenv("TESTRUNNER_EXPECTS");
        if (expects_unparsed == null) {
            throw new NullPointerException("TESTRUNNER_EXPECTS needs to be set");
        }

        LOGGER.info("Starting tests");
        try (var assets = ResourceLocatorApi.createGlobalAssetContainer()) {
            for (var entry : expects_unparsed.split(";")) {
                var key = entry.split("=", 2)[0];
                var v = entry.split("=",2)[1];

                var keyId = Identifier.parse(key);
                var vSet = new HashSet<>(List.of(v.split(",")));
                var resultSet = new HashSet<String>();

                for (var f : assets.getAllAssets(keyId.getNamespace(), keyId.getPath())) {
                    var s = f.get();
                    var b = new BufferedInputStream(s);
                    var r = new String(b.readAllBytes(), StandardCharsets.UTF_8);
                    resultSet.add(r);
                }
                if (!vSet.equals(resultSet)) {
                    LOGGER.error("Test failed! Expected {} found {}", vSet, resultSet);
                    System.exit(1);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        LOGGER.info("Tests succeeded");
        try {
            Files.writeString(Path.of("./runner_result"), nonce, StandardOpenOption.CREATE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.exit(0);
    }
}
