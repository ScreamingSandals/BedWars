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
