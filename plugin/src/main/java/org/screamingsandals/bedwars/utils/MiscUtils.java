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

package org.screamingsandals.bedwars.utils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.api.TeamColor;
import org.screamingsandals.bedwars.api.game.GameStatus;
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
import org.screamingsandals.lib.spectator.AudienceComponentLike;
import org.screamingsandals.lib.spectator.Color;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.spectator.mini.MiniMessageParser;
import org.screamingsandals.lib.utils.BlockFace;
import org.screamingsandals.lib.utils.MathUtils;
import org.screamingsandals.lib.world.LocationHolder;
import org.screamingsandals.lib.world.WorldHolder;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@UtilityClass
public class MiscUtils {
    private final Random RANDOM = new Random();
    private final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)\u00A7[0-9A-FK-ORX]");
    public final LocationHolder MAX_LOCATION = new LocationHolder(Double.MAX_VALUE, 256D, Double.MAX_VALUE);
    public final LocationHolder MIN_LOCATION = new LocationHolder(Double.MIN_VALUE, 0D, Double.MIN_VALUE);
    public final Component BW_PREFIX = Component.text()
            .content("[")
            .color(Color.WHITE)
            .append(
                    Component.text("B", Color.RED),
                    Component.text("W] ", Color.WHITE)
            )
            .build();


    public static final Map<Integer, Color> ID_TO_COLOR_MAP = Map.ofEntries(
            Map.entry(0, Color.BLACK),
            Map.entry(1, Color.DARK_BLUE),
            Map.entry(2, Color.DARK_GREEN),
            Map.entry(3, Color.DARK_AQUA),
            Map.entry(4, Color.DARK_RED),
            Map.entry(5, Color.DARK_PURPLE),
            Map.entry(6, Color.GOLD),
            Map.entry(7, Color.GRAY),
            Map.entry(8, Color.DARK_GRAY),
            Map.entry(9, Color.BLUE),
            Map.entry(10, Color.GREEN),
            Map.entry(11, Color.AQUA),
            Map.entry(12, Color.RED),
            Map.entry(13, Color.LIGHT_PURPLE),
            Map.entry(14, Color.YELLOW),
            Map.entry(15, Color.WHITE)
    );

    public static final Map<Color, Integer> COLOR_TO_ID_MAP = Map.ofEntries(
            Map.entry(Color.BLACK, 0),
            Map.entry(Color.DARK_BLUE, 1),
            Map.entry(Color.DARK_GREEN, 2),
            Map.entry(Color.DARK_AQUA, 3),
            Map.entry(Color.DARK_RED, 4),
            Map.entry(Color.DARK_PURPLE, 5),
            Map.entry(Color.GOLD, 6),
            Map.entry(Color.GRAY, 7),
            Map.entry(Color.DARK_GRAY, 8),
            Map.entry(Color.BLUE, 9),
            Map.entry(Color.GREEN, 10),
            Map.entry(Color.AQUA, 11),
            Map.entry(Color.RED, 12),
            Map.entry(Color.LIGHT_PURPLE, 13),
            Map.entry(Color.YELLOW, 14),
            Map.entry(Color.WHITE, 15)
    );

    /**
     * From BedWarsRel (Tweaked to use same instance each time)
     */
    public int randInt(int min, int max) {
        return RANDOM.nextInt((max - min) + 1) + min;
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

    public Color fromLegacyColorCode(String colorCode) {
        return ID_TO_COLOR_MAP.getOrDefault(Integer.parseInt(colorCode.replace("§", ""), 16), Color.WHITE);
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
    public void sendActionBarMessage(PlayerWrapper player, AudienceComponentLike senderMessage) {
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

    public String writeLocationToString(LocationHolder location) {
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

    public static Color getColor(@Nullable String color) {
        var val = Color.hexOrName(color);
        if (val == null) {
            return Color.WHITE;
        }
        return val;
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


    @NotNull
    public String translateAlternateColorCodes(char altColorChar, @NotNull String textToTranslate) {
        char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx".indexOf(b[i + 1]) > -1) {
                b[i] = '§';
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }

    public String toLegacyColorCode(Color color) {
        return "§" + Integer.toString(COLOR_TO_ID_MAP.getOrDefault(Color.nearestNamedTo(color), 15), 16);
    }

    public String getFormattedDate(String format) {
        try {
            return new SimpleDateFormat(format).format(new Date());
        } catch (Throwable ignored) {
            return DateTimeFormatter.ISO_DATE.format(LocalDate.now());
        }
    }

    public String roundForMainLobbySidebar(double toRound) {
        if (toRound >= 1000) {
            var num = Math.round(toRound / 100) / 10.0;
            return (num == (int) num ? (int) num : num) + "k";
        }
        return String.valueOf(toRound);
    }

    public String toMiniMessage(String legacy) {
        if (legacy == null) {
            return null;
        }
        return MiniMessageParser.INSTANCE.serialize(Component.fromLegacy(legacy));
    }

    public List<String> toMiniMessage(List<String> legacy) {
        if (legacy == null) {
            return null;
        }
        return legacy.stream()
                .map(l -> MiniMessageParser.INSTANCE.serialize(Component.fromLegacy(l)))
                .collect(Collectors.toList());
    }

    public Optional<GameImpl> getGameWithHighestPlayers(List<GameImpl> games, boolean fee) {  // If tie choose random one
        var biggest = games.stream()
                .filter(waitingGame -> waitingGame.getStatus() == GameStatus.WAITING)
                .filter(waitingGame -> waitingGame.getFee() > 0 || !fee)
                .filter(game -> game.countConnectedPlayers() < game.getMaxPlayers())
                .max(Comparator.comparingInt(GameImpl::countConnectedPlayers));

        if (biggest.isEmpty()) {
            return Optional.empty();
        }

        var biggestGames = games.stream()
                .filter(game -> game.countPlayers() == biggest.get().countPlayers())
                .filter(waitingGame -> waitingGame.getStatus() == GameStatus.WAITING)
                .filter(waitingGame -> waitingGame.getFee() > 0 || !fee)
                .filter(game -> game.countConnectedPlayers() < game.getMaxPlayers())
                .collect(Collectors.toList());

        return Optional.of(biggestGames.get(MiscUtils.randInt(0, biggestGames.size()-1)));
    }

    public Optional<GameImpl> getGameWithLowestPlayers(List<GameImpl> games, boolean fee) {
        return games.stream()
                .filter(game -> game.getStatus() == GameStatus.WAITING)
                .filter(game -> game.countConnectedPlayers() < game.getMaxPlayers())
                .filter(game -> {
                    if (fee) {
                        return game.getFee() > 0;
                    }
                    return true;
                })
                .min(Comparator.comparingInt(GameImpl::countConnectedPlayers));
    }

    public Optional<GameImpl> getFirstWaitingGame(List<GameImpl> games, boolean fee) {
        return games.stream()
                .filter(game -> game.getStatus() == GameStatus.WAITING)
                .filter(game -> {
                    if (fee) {
                        return game.getFee() > 0;
                    }
                    return true;
                })
                .max(Comparator.comparingInt(GameImpl::countConnectedPlayers));
    }

    public Optional<GameImpl> getFirstRunningGame(List<GameImpl> games, boolean fee) {
        return games.stream()
                .filter(game -> game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING)
                .filter(game -> {
                    if (fee) {
                        return game.getFee() > 0;
                    }
                    return true;
                })
                .max(Comparator.comparingInt(GameImpl::countConnectedPlayers));
    }
}
