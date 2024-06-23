package nl.theepicblock.resourcelocatorapi.impl;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.*;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import nl.theepicblock.resourcelocatorapi.ResourceLocatorApi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class RawFileProvider {
    public static void addPacks(CompositeResourcePack pack) {
        var modsDir = FabricLoader.getInstance().getGameDir().resolve("mods");

        if (!Files.isDirectory(modsDir)) {
            ResourceLocatorApi.LOGGER.error("Mods folder isn't a directory");
        }

        try (var stream = Files.walk(modsDir, FileVisitOption.FOLLOW_LINKS)) {
            stream.forEach(path -> {
                if (isValidFile(path)) {
                    try (var inputStream = new FileInputStream(path.toFile());
                        var bufStream = new BufferedInputStream(inputStream)) {
                        handleFile(pack, bufStream, path.toString());
                    } catch (FileNotFoundException e) {
                        ResourceLocatorApi.LOGGER.error("File seems to have disappeared "+path, e);
                    } catch (IOException e) {
                        ResourceLocatorApi.LOGGER.error("IO exception reading "+path, e);
                    }
                }
            });
        } catch (IOException e) {
            ResourceLocatorApi.LOGGER.error("IO exception whilst trying to walk files", e);
        }
    }

    /**
     * @author FabricMC
     */
    private static boolean isValidFile(Path path) {
        if (!Files.isRegularFile(path)) return false;

        try {
            if (Files.isHidden(path)) return false;
        } catch (IOException e) {
            ResourceLocatorApi.LOGGER.error("Error checking if file is hidden: "+path, e);
            return false;
        }

        String fileName = path.getFileName().toString();

        return fileName.endsWith(".jar") && !fileName.startsWith(".");
    }

    private static void handleFile(CompositeResourcePack pack, InputStream stream, String path) {
        var newPack = new BufferResourcePack("Rawfile ("+path+")");
        try {
            var zipStream = new ZipInputStream(stream);
            ZipEntry entry;
            while ((entry = zipStream.getNextEntry()) != null) {
                if (entry.isDirectory()) continue;

                if (entry.getName().startsWith("META-INF/jars")) {
                    handleFile(pack, zipStream, entry.getName() + " in " + path);
                } else if (entry.getName().startsWith("assets/")) {
                    var buffer = readMod(zipStream);
                    newPack.putAsset(entry.getName(), buffer);
                }
            }
        } catch (IOException e) {
            ResourceLocatorApi.LOGGER.error("Error whilst reading zip "+path, e);
        }
        pack.append(newPack);
    }

    /**
     * @author FabricMC
     */
    static ByteBuffer readMod(InputStream is) throws IOException {
        int available = is.available();
        boolean availableGood = available > 1;
        byte[] buffer = new byte[availableGood ? available : 30_000];
        int offset = 0;
        int len;

        while ((len = is.read(buffer, offset, buffer.length - offset)) >= 0) {
            offset += len;

            if (offset == buffer.length) {
                if (availableGood) {
                    int val = is.read();
                    if (val < 0) break;

                    availableGood = false;
                    buffer = Arrays.copyOf(buffer, Math.max(buffer.length * 2, 30_000));
                    buffer[offset++] = (byte) val;
                } else {
                    buffer = Arrays.copyOf(buffer, buffer.length * 2);
                }
            }
        }

        return ByteBuffer.wrap(buffer, 0, offset);
    }

    private static final class BufferResourcePack extends AbstractFileResourcePack implements MoreContextPack {
        private final Map<String, ByteBuffer> files = new HashMap<>();

        private BufferResourcePack(String name) {
            super(new ResourcePackInfo(name, Text.literal(name), ResourcePackSource.NONE, Optional.empty()));
        }

        @Nullable
        @Override
        public InputSupplier<InputStream> openRoot(String... segments) {
            return this.createSupplier(String.join("/", segments));
        }

        @Nullable
        @Override
        public InputSupplier<InputStream> open(ResourceType type, Identifier id) {
            return this.createSupplier(type.getDirectory()+"/"+id.getNamespace()+"/"+id.getPath());
        }

        @Override
        public void findResources(ResourceType type, String namespace, String prefix, ResultConsumer consumer) {
            var bigPrefix = type.getDirectory()+"/"+namespace+"/"+prefix+"/";
            var smollPrefix = type.getDirectory()+"/"+namespace+"/";
            this.files.forEach((path, buf) -> {
                if (path.startsWith(bigPrefix)) {
                    var idPath = path.substring(smollPrefix.length());
                    try {
                        var id = Identifier.of(namespace, idPath);
                        consumer.accept(id, () -> new ByteBufferInputStream(buf));
                    } catch (InvalidIdentifierException e) {
                        ResourceLocatorApi.LOGGER.warn("Invalid path in pack, ignoring: "+namespace+":"+idPath);
                    }
                }
            });
        }

        @Override
        public Set<String> getNamespaces(ResourceType type) {
            var re = Pattern.compile("assets/([^/]+).*");
            return this.files.keySet().stream()
                    .map(fullPath -> {
                        var matcher = re.matcher(fullPath);
                        matcher.matches();
                        return matcher.group(1);
                    })
                    .collect(Collectors.toSet());
        }

        @Override
        public void close() {

        }

        private @Nullable InputSupplier<InputStream> createSupplier(String path) {
            var buf = files.get(path);
            if (buf == null) return null;
            return () -> new ByteBufferInputStream(files.get(path));
        }

        public void putAsset(String path, ByteBuffer buf) {
            this.files.put(path, buf);
        }

        @Override
        public String resourcelocatorapi$getFullName() {
            return this.getId();
        }
    }

    private static final class ByteBufferInputStream extends InputStream {
        private final ByteBuffer buffer;
        private int pos;

        private ByteBufferInputStream(ByteBuffer buffer) {
            this.buffer = buffer;
        }

        /**
         * @author FabricMC
         */
        @Override
        public int read() throws IOException {
            if (pos >= buffer.limit()) {
                return -1;
            } else {
                return buffer.get(pos++) & 0xff;
            }
        }

        /**
         * @author FabricMC
         */
        @Override
        public int read(byte @NotNull [] b, int off, int len) throws IOException {
            int rem = buffer.limit() - pos;

            if (rem <= 0) {
                return -1;
            } else {
                len = Math.min(len, rem);
                System.arraycopy(buffer.array(), pos, b, off, len);
                pos += len;

                return len;
            }
        }
    }
}
