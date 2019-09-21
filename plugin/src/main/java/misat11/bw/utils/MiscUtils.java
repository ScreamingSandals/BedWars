package misat11.bw.utils;

import misat11.bw.Main;
import misat11.bw.api.Game;
import misat11.bw.api.RunningTeam;
import misat11.bw.api.events.BedwarsApplyPropertyToBoughtItem;
import misat11.bw.game.GamePlayer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Random;

public class MiscUtils {
    /** From BedWarsRel */
    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
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
                    player.sendMessage(message);
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
            return Main.getConfigurator().config.getString(fallback, "CUT_SANDSTONE");
        }
    }

    public static Material getMaterialFromString(String path, String fallback) {
        Material material = Material.getMaterial(fallback);
        if (path != null) {
            try {
                Material mat = Material.getMaterial(path);
                if (mat != null) {
                    material = mat;
                }
            } catch (NullPointerException e) {
                System.out.println("Wrong material configured: " + path);
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
}
