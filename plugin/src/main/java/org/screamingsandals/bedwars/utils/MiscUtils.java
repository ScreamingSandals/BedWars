package org.screamingsandals.bedwars.utils;

import misat11.lib.lang.I;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.RunningTeam;
import org.screamingsandals.bedwars.api.TeamColor;
import org.screamingsandals.bedwars.api.events.BedwarsApplyPropertyToBoughtItem;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.game.GamePlayer;
import org.screamingsandals.bedwars.game.Team;
import org.screamingsandals.lib.debug.Debug;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static misat11.lib.lang.I18n.i18nonly;

public class MiscUtils {
    /**
     * From BedWarsRel
     */
    public static int randInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    public static BlockFace getCardinalDirection(Location location) {
        double rotation = (location.getYaw() - 90) % 360;
        if (rotation < 0) {
            rotation += 360.0;
        }
        if (0 <= rotation && rotation < 22.5) {
            return BlockFace.NORTH;
        } else if (22.5 <= rotation && rotation < 67.5) {
            return BlockFace.NORTH_EAST;
        } else if (67.5 <= rotation && rotation < 112.5) {
            return BlockFace.EAST;
        } else if (112.5 <= rotation && rotation < 157.5) {
            return BlockFace.SOUTH_EAST;
        } else if (157.5 <= rotation && rotation < 202.5) {
            return BlockFace.SOUTH;
        } else if (202.5 <= rotation && rotation < 247.5) {
            return BlockFace.SOUTH_WEST;
        } else if (247.5 <= rotation && rotation < 292.5) {
            return BlockFace.WEST;
        } else if (292.5 <= rotation && rotation < 337.5) {
            return BlockFace.NORTH_WEST;
        } else if (337.5 <= rotation && rotation < 360.0) {
            return BlockFace.NORTH;
        } else {
            return BlockFace.NORTH;
        }
    }
    /* End of BedWarsRel */

    /* Special items  - CEPH*/
    public static void sendActionBarMessage(Player player, String message) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (Main.isSpigot() && !Main.isLegacy() && Main.getConfigurator().config.getBoolean("specials.action-bar-messages")) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                            TextComponent.fromLegacyText(message));
                } else {
                    player.sendMessage(i18nonly("prefix") + " " + message);
                }
            }
        }.runTask(Main.getInstance());
    }

    public static int getIntFromProperty(String name, String fallback, BedwarsApplyPropertyToBoughtItem event) {
        try {
            return event.getIntProperty(name);
        } catch (NullPointerException e) {
            return Main.getConfigurator().config.getInt(fallback);
        }
    }

    public static double getDoubleFromProperty(String name, String fallback, BedwarsApplyPropertyToBoughtItem event) {
        try {
            return event.getDoubleProperty(name);
        } catch (NullPointerException e) {
            return Main.getConfigurator().config.getDouble(fallback);
        }
    }

    public static boolean getBooleanFromProperty(String name, String fallback, BedwarsApplyPropertyToBoughtItem event) {
        try {
            return event.getBooleanProperty(name);
        } catch (NullPointerException e) {
            return Main.getConfigurator().config.getBoolean(fallback);
        }
    }

    public static String getStringFromProperty(String name, String fallback, BedwarsApplyPropertyToBoughtItem event) {
        try {
            return event.getStringProperty(name);
        } catch (NullPointerException e) {
            return Main.getConfigurator().config.getString(fallback);
        }
    }

    public static String getMaterialFromProperty(String name, String fallback, BedwarsApplyPropertyToBoughtItem event) {
        try {
            return event.getStringProperty(name);
        } catch (NullPointerException e) {
            return Main.getConfigurator().config.getString(fallback, Main.isLegacy() ? "SANDSTONE" : "CUT_SANDSTONE");
        }
    }

    public static Material getMaterialFromString(String name, String fallback) {
        Material material = Material.getMaterial(fallback);
        if (name != null) {
            try {
                Material mat = Material.getMaterial(name);
                if (mat != null) {
                    material = mat;
                }
            } catch (NullPointerException e) {
                Debug.warn("Wrong material configured: " + name, true);
                e.printStackTrace();
            }
        }
        return material;
    }

    public static Player findTarget(Game game, Player player, double maxDist) {
        Player playerTarget = null;
        RunningTeam team = game.getTeamOfPlayer(player);

        ArrayList<Player> foundTargets = new ArrayList<>(game.getConnectedPlayers());
        foundTargets.removeAll(team.getConnectedPlayers());


        for (Player p : foundTargets) {
            GamePlayer gamePlayer = Main.getPlayerGameProfile(p);
            if (player.getWorld() != p.getWorld()) {
                continue;
            }

            if (gamePlayer.isSpectator) {
                continue;
            }

            double realDistance = player.getLocation().distance(p.getLocation());
            if (realDistance < maxDist) {
                playerTarget = p;
                maxDist = realDistance;
            }
        }
        return playerTarget;
    }

    /* End of Special Items */

    public static Location readLocationFromString(World world, String location) {
        int lpos = 0;
        double x = 0;
        double y = 0;
        double z = 0;
        float yaw = 0;
        float pitch = 0;
        for (String pos : location.split(";")) {
            lpos++;
            switch (lpos) {
                case 1:
                    x = Double.parseDouble(pos);
                    break;
                case 2:
                    y = Double.parseDouble(pos);
                    break;
                case 3:
                    z = Double.parseDouble(pos);
                    break;
                case 4:
                    yaw = Float.parseFloat(pos);
                    break;
                case 5:
                    pitch = Float.parseFloat(pos);
                    break;
                default:
                    break;
            }
        }
        return new Location(world, x, y, z, yaw, pitch);
    }

    public static String setLocationToString(Location location) {
        return location.getX() + ";" + location.getY() + ";" + location.getZ() + ";" + location.getYaw() + ";"
                + location.getPitch();
    }

    public static String convertColorToNewFormat(String oldColor, Team team) {
        String newColor = oldColor;

        if (team.isNewColor()) {
            return oldColor;
        }

        switch (oldColor) {
            case "DARK_BLUE":
                newColor = "BLUE";
                break;
            case "DARK_GREEN":
                newColor = "GREEN";
                break;
            case "DARK_PURPLE":
                newColor = "MAGENTA";
                break;
            case "GOLD":
                newColor = "ORANGE";
                break;
            case "GRAY":
                newColor = "LIGHT_GRAY";
                break;
            case "BLUE":
                newColor = "LIGHT_BLUE";
                break;
            case "GREEN":
                newColor = "LIME";
                break;
            case "AQUA":
                newColor = "CYAN";
                break;
            case "LIGHT_PURPLE":
                newColor = "PINK";
                break;
            case "DARK_RED":
                newColor = "BROWN";
                break;
            case "DARK_GRAY":
            	newColor = "GRAY";
            	break;
        }
        return newColor;
    }
    
    public static Vector getDirection(BlockFace face) {
    	int modX = face.getModX();
    	int modY = face.getModY();
    	int modZ = face.getModZ();
        Vector direction = new Vector(modX, modY, modZ);
        if (modX != 0 || modY != 0 || modZ != 0) {
            direction.normalize();
        }
        return direction;
    }

    public static void giveItemsToPlayer(List<ItemStack> itemStackList, Player player, TeamColor teamColor) {
        for (ItemStack itemStack : itemStackList) {
            final String materialName = itemStack.getType().toString();
            final PlayerInventory playerInventory = player.getInventory();

            if (materialName.contains("HELMET")) {
                playerInventory.setHelmet(Main.applyColor(teamColor, itemStack));
            } else if (materialName.contains("CHESTPLATE")) {
                playerInventory.setChestplate(Main.applyColor(teamColor, itemStack));
            } else if (materialName.contains("LEGGINGS")) {
                playerInventory.setLeggings(Main.applyColor(teamColor, itemStack));
            } else if (materialName.contains("BOOTS")) {
                playerInventory.setBoots(Main.applyColor(teamColor, itemStack));
            } else {
                playerInventory.addItem(Main.applyColor(teamColor, itemStack));
            }
        }
    }
}
