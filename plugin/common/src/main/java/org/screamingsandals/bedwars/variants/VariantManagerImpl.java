/*
 * Copyright (C) 2022 ScreamingSandals
 *
 * This file is part of Screaming BedWars.
 *
 * Screaming BedWars is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Screaming BedWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Screaming BedWars. If not, see <https://www.gnu.org/licenses/>.
 */

package org.screamingsandals.bedwars.variants;

import lombok.RequiredArgsConstructor;
import org.screamingsandals.bedwars.BedWarsPlugin;
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
    public VariantImpl getDefaultVariant() {
        return getVariant("default").orElseThrow();
    }

    @Override
    public List<String> getVariantNames() {
        return variants.stream().map(VariantImpl::getName).collect(Collectors.toList());
    }

    @OnPostEnable
    public void onPostEnable() {
        if (!Files.exists(variantsFolder)) {
            try {
                Files.createDirectory(variantsFolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Copy files related to default variant if they don't exist
        if (!Files.exists(variantsFolder.resolve("default.yml"))) {
            BedWarsPlugin.getInstance().saveResource("variants/default.yml", false);
        }

        // Copy files related to certain-popular-server variant if they don't exist
        if (!Files.exists(variantsFolder.resolve("certain-popular-server.yml"))) {
            BedWarsPlugin.getInstance().saveResource("variants/certain-popular-server.yml", false);
        }
        if (!Files.exists(variantsFolder.resolve("../shop/certain-popular-server/shop.yml"))) {
            BedWarsPlugin.getInstance().saveResource("shop/certain-popular-server/shop.yml", false);
        }
        if (!Files.exists(variantsFolder.resolve("../shop/certain-popular-server/upgrade-shop.yml"))) {
            BedWarsPlugin.getInstance().saveResource("shop/certain-popular-server/upgrade-shop.yml", false);
        }

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
    }

    @OnPreDisable
    public void onPreDisable() {
        variants.clear();
    }
}
