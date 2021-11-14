package org.screamingsandals.bedwars.utils;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.api.TeamColor;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.events.ApplyPropertyToItemEventImpl;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.lib.block.BlockTypeHolder;
import org.screamingsandals.lib.container.PlayerContainer;
import org.screamingsandals.lib.item.Item;
import org.screamingsandals.lib.player.PlayerMapper;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.sender.SenderMessage;
import org.screamingsandals.lib.utils.BlockFace;
import org.screamingsandals.lib.utils.MathUtils;
import org.screamingsandals.lib.world.LocationHolder;
import org.screamingsandals.lib.world.WorldHolder;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@UtilityClass
public class MiscUtils {
    private final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)\u00A7[0-9A-FK-ORX]");
    public final Component BW_PREFIX = Component.text("[")
            .color(NamedTextColor.WHITE)
            .append(
                    Component.text("B")
                            .color(NamedTextColor.RED)
            )
            .append(
                    Component.text("W] ")
                            .color(NamedTextColor.WHITE)
            );

    /**
     * From BedWarsRel
     */
    public int randInt(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }

    public String stripColor(String input) {
        return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
    }

    public @Nullable String getFirstColorCode(String input) {
        final var matcher = STRIP_COLOR_PATTERN.matcher(input);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    public NamedTextColor fromLegacyColorCode(String colorCode) {
        return NamedTextColor.ofExact(Integer.parseInt(colorCode.replace(String.valueOf(LegacyComponentSerializer.SECTION_CHAR), ""), 16));
    }

    public BlockFace getCardinalDirection(LocationHolder location) {
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
    public void sendActionBarMessage(PlayerWrapper player, SenderMessage senderMessage) {
        if (MainConfig.getInstance().node("specials", "action-bar-messages").getBoolean()) {
            player.sendActionBar(senderMessage);
        } else {
            // TODO: custom game prefix
            player.sendMessage(senderMessage);
        }
    }

    public int getIntFromProperty(String name, String fallback, ApplyPropertyToItemEventImpl event) {
        try {
            return event.getIntProperty(name);
        } catch (NullPointerException e) {
            return MainConfig.getInstance().node((Object[]) fallback.split("\\.")).getInt();
        }
    }

    public double getDoubleFromProperty(String name, String fallback, ApplyPropertyToItemEventImpl event) {
        try {
            return event.getDoubleProperty(name);
        } catch (NullPointerException e) {
            return MainConfig.getInstance().node((Object[]) fallback.split("\\.")).getDouble();
        }
    }

    public boolean getBooleanFromProperty(String name, String fallback, ApplyPropertyToItemEventImpl event) {
        try {
            return event.getBooleanProperty(name);
        } catch (NullPointerException e) {
            return MainConfig.getInstance().node((Object[]) fallback.split("\\.")).getBoolean();
        }
    }

    public String getStringFromProperty(String name, String fallback, ApplyPropertyToItemEventImpl event) {
        try {
            return event.getStringProperty(name);
        } catch (NullPointerException e) {
            return MainConfig.getInstance().node((Object[]) fallback.split("\\.")).getString();
        }
    }

    public String getMaterialFromProperty(String name, String fallback, ApplyPropertyToItemEventImpl event) {
        try {
            return event.getStringProperty(name);
        } catch (NullPointerException e) {
            return MainConfig.getInstance().node((Object[]) fallback.split("\\.")).getString(BedWarsPlugin.isLegacy() ? "SANDSTONE" : "CUT_SANDSTONE");
        }
    }

    public BlockTypeHolder getBlockTypeFromString(String name, String fallback) {
        if (name != null) {
            var result = BlockTypeHolder.ofOptional(name);
            if (result.isEmpty()) {
                Debug.warn("Wrong material configured: " + name, true);
            } else {
                return result.get();
            }
        }

        return BlockTypeHolder.of(fallback);
    }

    public PlayerWrapper findTarget(GameImpl game, PlayerWrapper player, double maxDist) {
        PlayerWrapper playerTarget = null;
        var team = game.getPlayerTeam(player.as(BedWarsPlayer.class));

        var foundTargets = new ArrayList<>(game.getConnectedPlayers());
        foundTargets.removeAll(team.getPlayers());


        for (PlayerWrapper p : foundTargets) {
            var gamePlayer = PlayerManagerImpl.getInstance().getPlayer(p.getUuid());
            if (gamePlayer.isEmpty()) {
                continue;
            }

            if (!player.getLocation().getWorld().equals(p.getLocation().getWorld())) {
                continue;
            }

            if (gamePlayer.get().isSpectator()) {
                continue;
            }

            double realDistance = player.getLocation().getDistanceSquared(p.getLocation());
            if (realDistance < MathUtils.square(maxDist)) {
                playerTarget = p;
                maxDist = realDistance;
            }
        }
        return playerTarget;
    }

    /* End of Special Items */

    public LocationHolder readLocationFromString(WorldHolder world, String location) {
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
        return new LocationHolder(x, y, z, yaw, pitch, world);
    }

    public String setLocationToString(LocationHolder location) {
        return location.getX() + ";" + location.getY() + ";" + location.getZ() + ";" + location.getYaw() + ";"
                + location.getPitch();
    }

    public String convertColorToNewFormat(String oldColor, boolean isNewColor) {
        String newColor = oldColor;

        if (isNewColor) {
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

    public void giveItemsToPlayer(List<Item> itemStackList, PlayerWrapper player, TeamColor teamColor) {
        for (Item itemStack : itemStackList) {
            final String materialName = itemStack.getMaterial().platformName();
            final PlayerContainer playerInventory = player.getPlayerInventory();

            if (materialName.contains("HELMET")) {
                playerInventory.setHelmet(BedWarsPlugin.getInstance().getColorChanger().applyColor(teamColor, itemStack));
            } else if (materialName.contains("CHESTPLATE")) {
                playerInventory.setChestplate(BedWarsPlugin.getInstance().getColorChanger().applyColor(teamColor, itemStack));
            } else if (materialName.contains("LEGGINGS")) {
                playerInventory.setLeggings(BedWarsPlugin.getInstance().getColorChanger().applyColor(teamColor, itemStack));
            } else if (materialName.contains("BOOTS")) {
                playerInventory.setBoots(BedWarsPlugin.getInstance().getColorChanger().applyColor(teamColor, itemStack));
            } else {
                playerInventory.addItem(BedWarsPlugin.getInstance().getColorChanger().applyColor(teamColor, itemStack));
            }
        }
    }

    public List<PlayerWrapper> getOnlinePlayers(Collection<UUID> uuids) {
        if (uuids == null || uuids.isEmpty()) {
            return Collections.emptyList();
        }

        return uuids.stream()
                .map(PlayerMapper::getPlayer)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public List<LocationHolder> getLocationsBetween(LocationHolder loc1, LocationHolder loc2){
        int lowX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int lowY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int lowZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());

        final var locationList = new ArrayList<LocationHolder>();

        for(int x = 0; x<Math.abs(loc1.getBlockX()-loc2.getBlockX()); x++){
            for(int y = 0; y<Math.abs(loc1.getBlockY()-loc2.getBlockY()); y++){
                for(int z = 0; z<Math.abs(loc1.getBlockZ()-loc2.getBlockZ()); z++){
                    locationList.add(new LocationHolder(lowX+x, lowY+y, lowZ+z, 0, 0, loc1.getWorld()));
                }
            }
        }

        return locationList;
    }

    public static LocationHolder findEmptyLocation(LocationHolder respawnLocation) {
        if (respawnLocation.getY() > (respawnLocation.getWorld().getMaxY() - 1) || (!respawnLocation.getBlock().getType().isSolid() && !respawnLocation.clone().add(0,1,0).getBlock().getType().isSolid())) {
            return respawnLocation;
        } else {
            return findEmptyLocation(respawnLocation.clone().add(0, 2, 0));
        }
    }

    public static TextColor getColor(@Nullable String color) {
        if (color == null) {
            return NamedTextColor.WHITE;
        }

        if (color.startsWith("#")) {
            var val = TextColor.fromCSSHexString(color);
            if (val == null) {
                return NamedTextColor.WHITE;
            }
            return val;
        } else {
            var val = NamedTextColor.NAMES.value(color.toLowerCase());
            if (val == null) {
                return NamedTextColor.WHITE;
            }
            return val;
        }
    }

    public Path getPluginsFolder(String name) {
        return Paths.get(BedWarsPlugin.getInstance().getDataFolder().getParent().toAbsolutePath().toString(), name);
    }

    /**
     * From SBA
     */
    private static final BlockFace[] axis = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
    private static final BlockFace[] radial = { BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST };

    public static BlockFace yawToFace(float yaw, boolean useSubCardinalDirections) {
        if (useSubCardinalDirections)
            return radial[Math.round(yaw / 45f) & 0x7].getOppositeFace();

        return axis[Math.round(yaw / 90f) & 0x3].getOppositeFace();
    }
}
