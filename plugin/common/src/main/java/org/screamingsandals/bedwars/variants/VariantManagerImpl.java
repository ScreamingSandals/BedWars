/*
 * Copyright (C) 2025 ScreamingSandals
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.api.variants.VariantManager;
import org.screamingsandals.lib.plugin.ServiceManager;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnPostEnable;
import org.screamingsandals.lib.utils.annotations.methods.OnPreDisable;
import org.screamingsandals.lib.utils.annotations.parameters.DataFolder;
import org.screamingsandals.lib.utils.logger.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VariantManagerImpl implements VariantManager {
    @DataFolder("variants")
    private final @NotNull Path variantsFolder;
    private final @NotNull Logger logger;
    private final @NotNull List<VariantImpl> variants = new LinkedList<>();
    private final @NotNull VariantLoaderImpl variantLoader;

    public static @NotNull VariantManagerImpl getInstance() {
        return ServiceManager.get(VariantManagerImpl.class);
    }

    @Override
    public @Nullable VariantImpl getVariant(@NotNull String name) {
        return variants.stream().filter(variant -> variant.getName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public @NotNull List<@NotNull VariantImpl> getVariants() {
        return List.copyOf(variants);
    }

    @Override
    public boolean hasVariant(@NotNull String name) {
        return getVariant(name) != null;
    }

    @Override
    public @NotNull VariantImpl getDefaultVariant() {
        return Objects.requireNonNull(getVariant("default"), "A variant called default is not present. This is a bug!");
    }

    @Override
    public @NotNull List<@NotNull String> getVariantNames() {
        return variants.stream().map(VariantImpl::getName).collect(Collectors.toList());
    }

    @OnPostEnable
    public void onPostEnable() {
        if (!Files.exists(variantsFolder)) {
            try {
                Files.createDirectory(variantsFolder);
            } catch (IOException e) {
                logger.error("An error occurred while creating a folder {}", variantsFolder.toString(), e);
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
                    if (file.exists() && file.isFile() && !file.getName().toLowerCase(Locale.ROOT).endsWith(".disabled")) {
                        var variant = variantLoader.loadVariant(file);
                        if (variant != null) {
                            variants.add(variant);
                        }
                    }
                });
            }
        } catch (IOException e) {
            logger.error("An error occurred while loading variant files", e);
        }
    }

    @OnPreDisable
    public void onPreDisable() {
        variants.clear();
    }
}
