/*
 * Copyright (C) 2024 ScreamingSandals
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

package org.screamingsandals.bedwars.lang;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.screamingsandals.lib.lang.container.TranslationContainer;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class LayeredTranslationContainer implements TranslationContainer {
    private TranslationContainer fallbackContainer;
    private final ConfigurationNode shadedNode;
    private ConfigurationNode node = BasicConfigurationNode.root();
    private ConfigurationNode customNode = BasicConfigurationNode.root();

    @Override
    public List<String> translate(Collection<String> key) {
        return translate(key.toArray(String[]::new));
    }

    @Override
    public List<String> translate(String... key) {
        var node = this.customNode.node((Object[]) key);
        if (node.empty()) {
            node = this.node.node((Object[]) key);
            if (node.empty()) {
                node = this.shadedNode.node((Object[]) key);
            }
        }
        if (node.isList()) {
            return node.childrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
        } else if (!node.empty()) {
            return List.of(node.getString(""));
        }
        return fallbackContainer != null ? fallbackContainer.translate(key) : List.of();
    }

    @Override
    public boolean isEmpty() {
        return shadedNode.empty() && node.empty() && customNode.empty();
    }
}
