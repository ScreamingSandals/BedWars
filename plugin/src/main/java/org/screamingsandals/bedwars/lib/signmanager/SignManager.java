package org.screamingsandals.bedwars.lib.signmanager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.utils.PreparedLocation;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignManager {

    private final ConfigurationLoader<?> loader;
    private final Map<PreparedLocation, SignBlock> signs = new HashMap<>();
    private boolean modify = false;
    private SignOwner owner;

    public SignManager(SignOwner owner, ConfigurationLoader<?> loader) {
        this.owner = owner;
        this.loader = loader;

        Bukkit.getScheduler().runTaskLater(Main.getInstance(), this::loadConfig, 5L);
    }

    public void loadConfig() {
        try {
            var config = loader.load();

            config.node("sign").childrenList().forEach(sign -> {
                var name = sign.node("name").getString();
                if (name == null || name.isBlank()) {
                    name = sign.node("game").getString("invalid"); // Compatibility with old BedWars sign.yml
                }
                try {
                    var loc = sign.node("location").get(PreparedLocation.class);
                    signs.put(loc, new SignBlock(loc, name));
                    owner.updateSign(signs.get(loc));
                } catch (SerializationException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean isSignRegistered(Location location) {
        return signs.containsKey(new PreparedLocation(location));
    }

    public void unregisterSign(Location location) {
        if (signs.containsKey(new PreparedLocation(location))) {
            signs.remove(new PreparedLocation(location));
            modify = true;
        }
    }

    public boolean registerSign(Location location, String game) {
        if (owner.isNameExists(game)) {
        	SignBlock block = new SignBlock(new PreparedLocation(location), game);
            signs.put(new PreparedLocation(location), block);
            modify = true;
            owner.updateSign(block);
            return true;
        }
        return false;
    }

    public SignBlock getSign(Location location) {
        return signs.get(new PreparedLocation(location));
    }

    public List<SignBlock> getSignsForName(String name) {
        List<SignBlock> list = new ArrayList<>();
        for (SignBlock sign : signs.values()) {
            if (sign.getName().equals(name)) {
                list.add(sign);
            }
        }
        return list;
    }

    public void save() {
        save(false);
    }

    public void save(boolean force) {
        if (modify || force) {
            var config = loader.createNode();

            signs.values().forEach(sign -> {
                try {
                    var signNode = config.node("sign").appendListNode();
                    signNode.node("location").set(sign.getLocation());
                    signNode.node("name").set(sign.getName());
                } catch (SerializationException e) {
                    e.printStackTrace();
                }
            });

            try {
                loader.save(config);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
