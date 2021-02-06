package org.screamingsandals.bedwars.tab;

import org.bukkit.ChatColor;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.game.GamePlayer;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.screamingsandals.bedwars.lib.nms.utils.ClassStorage.NMS.*;
import static org.screamingsandals.bedwars.lib.nms.utils.ClassStorage.*;

public class TabManager {
    private List<String> header;
    private List<String> footer;

    public TabManager() {
        if (Main.getConfigurator().node("tab", "header", "enabled").getBoolean()) {
            header = Main.getConfigurator().node("tab", "header", "contents").childrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
        }
        if (Main.getConfigurator().node("tab", "footer", "enabled").getBoolean()) {
            footer = Main.getConfigurator().node("tab", "footer", "contents").childrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
        }
    }

    public void modifyForPlayer(GamePlayer player) {
        if (player.player.isOnline() && (header != null || footer != null)) {
            try {
                Object packet = PacketPlayOutPlayerListHeaderFooter.getConstructor().newInstance();
                if (header != null) {
                    setField(packet, "header, a, field_179703_a", getMethod(ChatSerializer, "a,field_150700_a", String.class)
                            .invokeStatic("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', String.join("\n", translate(player, header))) + "\"}"));
                } else {
                    setField(packet, "header, a, field_179703_a", getMethod(ChatSerializer, "a,field_150700_a", String.class)
                            .invokeStatic("{\"text\": \"\"}"));
                }
                if (footer != null) {
                    setField(packet, "footer, b, field_179702_b", getMethod(ChatSerializer, "a,field_150700_a", String.class)
                            .invokeStatic("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', String.join("\n", translate(player, footer))) + "\"}"));
                } else {
                    setField(packet, "footer, b, field_179702_b", getMethod(ChatSerializer, "a,field_150700_a", String.class)
                            .invokeStatic("{\"text\": \"\"}"));
                }
                sendPacket(player.player, packet);
            } catch (Exception ignored) {

            }
        }
    }

    public void clear(GamePlayer player) {
        if (player.player.isOnline() && (header != null || footer != null)) {
            try {
                Object packet = PacketPlayOutPlayerListHeaderFooter.getConstructor().newInstance();
                setField(packet, "header, a, field_179703_a", getMethod(ChatSerializer, "a,field_150700_a", String.class)
                        .invokeStatic("{\"text\": \"\"}"));
                setField(packet, "footer, b, field_179702_b", getMethod(ChatSerializer, "a,field_150700_a", String.class)
                        .invokeStatic("{\"text\": \"\"}"));
                sendPacket(player.player, packet);
            } catch (Exception ignored) {
            }
        }
    }

    public List<String> translate(GamePlayer gamePlayer, List<String> origin) {
        List<String> list = new ArrayList<>();
        origin.forEach(a -> list.add(a.replace("%players%", String.valueOf(gamePlayer.getGame().countPlayers()))
                .replace("%alive%", String.valueOf(gamePlayer.getGame().countAlive()))
                .replace("%spectating%", String.valueOf(gamePlayer.getGame().countSpectating()))
                .replace("%spectators%", String.valueOf(gamePlayer.getGame().countSpectators()))
                .replace("%respawnable%", String.valueOf(gamePlayer.getGame().countRespawnable()))
                .replace("%max%", String.valueOf(gamePlayer.getGame().getMaxPlayers()))
                .replace("%map%", gamePlayer.getGame().getName())));
        return list;
    }
}
