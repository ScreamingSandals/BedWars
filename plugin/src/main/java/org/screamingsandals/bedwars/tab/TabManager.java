package org.screamingsandals.bedwars.tab;

import org.bukkit.ChatColor;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.game.GamePlayer;
import org.screamingsandals.bedwars.lib.nms.accessors.IChatBaseComponent_i_ChatSerializerAccessor;
import org.screamingsandals.bedwars.lib.nms.accessors.PacketPlayOutPlayerListHeaderFooterAccessor;
import org.screamingsandals.bedwars.lib.nms.utils.ClassStorage;

import java.util.ArrayList;
import java.util.List;

import static org.screamingsandals.bedwars.lib.nms.utils.ClassStorage.*;

public class TabManager {
    private List<String> header;
    private List<String> footer;

    public TabManager() {
        if (Main.getConfigurator().config.getBoolean("tab.header.enabled")) {
            header = Main.getConfigurator().config.getStringList("tab.header.contents");
        }
        if (Main.getConfigurator().config.getBoolean("tab.footer.enabled")) {
            footer = Main.getConfigurator().config.getStringList("tab.footer.contents");
        }
    }

    public void modifyForPlayer(GamePlayer player) {
        if (player.player.isOnline() && (header != null || footer != null)) {
            try {
                Object packet = ClassStorage.forceConstruct(PacketPlayOutPlayerListHeaderFooterAccessor.getType());
                if (header != null) {
                    setField(packet, PacketPlayOutPlayerListHeaderFooterAccessor.getFieldHeader(), getMethod(IChatBaseComponent_i_ChatSerializerAccessor.getMethodM_130701_1())
                            .invokeStatic("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', String.join("\n", translate(player, header))) + "\"}"));
                } else {
                    setField(packet, PacketPlayOutPlayerListHeaderFooterAccessor.getFieldHeader(), getMethod(IChatBaseComponent_i_ChatSerializerAccessor.getMethodM_130701_1())
                            .invokeStatic("{\"text\": \"\"}"));
                }
                if (footer != null) {
                    setField(packet, PacketPlayOutPlayerListHeaderFooterAccessor.getFieldFooter(), getMethod(IChatBaseComponent_i_ChatSerializerAccessor.getMethodM_130701_1())
                            .invokeStatic("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', String.join("\n", translate(player, footer))) + "\"}"));
                } else {
                    setField(packet, PacketPlayOutPlayerListHeaderFooterAccessor.getFieldFooter(), getMethod(IChatBaseComponent_i_ChatSerializerAccessor.getMethodM_130701_1())
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
                Object packet = ClassStorage.forceConstruct(PacketPlayOutPlayerListHeaderFooterAccessor.getType());
                setField(packet, PacketPlayOutPlayerListHeaderFooterAccessor.getFieldHeader(), getMethod(IChatBaseComponent_i_ChatSerializerAccessor.getMethodM_130701_1())
                        .invokeStatic("{\"text\": \"\"}"));
                setField(packet, PacketPlayOutPlayerListHeaderFooterAccessor.getFieldFooter(), getMethod(IChatBaseComponent_i_ChatSerializerAccessor.getMethodM_130701_1())
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
