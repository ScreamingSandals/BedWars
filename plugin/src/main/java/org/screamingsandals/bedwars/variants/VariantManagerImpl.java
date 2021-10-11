package org.screamingsandals.bedwars.variants;

import lombok.RequiredArgsConstructor;
import org.screamingsandals.bedwars.api.variants.VariantManager;
import org.screamingsandals.lib.plugin.ServiceManager;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnPostEnable;
import org.screamingsandals.lib.utils.annotations.methods.OnPreDisable;
import org.screamingsandals.lib.utils.annotations.parameters.DataFolder;
import org.screamingsandals.lib.utils.logger.LoggerWrapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VariantManagerImpl implements VariantManager {
    @DataFolder("variants")
    private final Path variantsFolder;
    private final LoggerWrapper logger;
    private final List<VariantImpl> variants = new LinkedList<>();

    public static VariantManagerImpl getInstance() {
        return ServiceManager.get(VariantManagerImpl.class);
    }

    @Override
    public Optional<VariantImpl> getVariant(String name) {
        return variants.stream().filter(variant -> variant.getName().equals(name)).findFirst();
    }

    @Override
    public List<VariantImpl> getVariants() {
        return List.copyOf(variants);
    }

    @Override
    public boolean hasVariant(String name) {
        return getVariant(name).isPresent();
    }

    @Override
    public List<String> getVariantNames() {
        return variants.stream().map(VariantImpl::getName).collect(Collectors.toList());
    }

    @OnPostEnable
    public void onPostEnable() {
        if (Files.exists(variantsFolder)) {
            try (var stream = Files.walk(variantsFolder.toAbsolutePath())) {
                final var results = stream.filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .collect(Collectors.toList());
                if (results.isEmpty()) {
                    logger.debug("No variants have been found!");
                } else {
                    results.forEach(file -> {
                        if (file.exists() && file.isFile() && !file.getName().toLowerCase().endsWith(".disabled")) {
                            var variant = VariantImpl.loadVariant(file);
                            if (variant != null) {
                                variants.add(variant);
                            }
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Files.createDirectory(variantsFolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnPreDisable
    public void onPreDisable() {
        variants.clear();
    }
}
