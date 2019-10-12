package misat11.bw.listener;

import misat11.bw.Main;
import misat11.bw.api.events.BedwarsPlayerKilledEvent;
import misat11.bw.api.events.BedwarsTeamChestOpenEvent;
import misat11.bw.api.game.GameStatus;
import misat11.bw.commands.BaseCommand;
import misat11.bw.game.*;
import misat11.bw.inventories.TeamSelectorInventory;
import misat11.bw.statistics.PlayerStatistic;
import misat11.bw.utils.*;
import misat11.lib.nms.NMSUtils;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static misat11.lib.lang.I18n.i18n;
import static misat11.lib.lang.I18n.i18nonly;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        final Player victim = event.getEntity();

        if (Main.isPlayerInGame(victim)) {
            GamePlayer gVictim = Main.getPlayerGameProfile(victim);
            Game game = gVictim.getGame();
            CurrentTeam victimTeam = game.getPlayerTeam(gVictim);
            ChatColor victimColor = victimTeam.teamInfo.color.chatColor;
            List<ItemStack> drops = new ArrayList<>(event.getDrops());
            int respawnTime = Main.getConfigurator().config.getInt("respawn-cooldown-time", 5);

            event.setKeepInventory(game.getOriginalOrInheritedKeepInventory());
            event.setDroppedExp(0);

            if (game.getStatus() == GameStatus.RUNNING) {
                if (!game.getOriginalOrInheritedPlayerDrops()) {
                    event.getDrops().clear();
                }

                if (Main.getConfigurator().config.getBoolean("chat.send-death-messages-just-in-game")) {
                    String deathMessage = event.getDeathMessage();
                    if (Main.getConfigurator().config.getBoolean("chat.send-custom-death-messages")) {
                        if (event.getEntity().getKiller() != null) {
                            Player killer = event.getEntity().getKiller();
                            GamePlayer gKiller = Main.getPlayerGameProfile(killer);
                            CurrentTeam killerTeam = game.getPlayerTeam(gKiller);
                            ChatColor killerColor = killerTeam.teamInfo.color.chatColor;

                            deathMessage = i18n("player_killed")
                                .replace("%victim%", victimColor + victim.getDisplayName())
                                .replace("%killer%", killerColor + killer.getDisplayName())
                                .replace("%victimTeam%", victimColor + victimTeam.getName())
                                .replace("%killerTeam%", killerColor + killerTeam.getName());
                        } else {
                            deathMessage = i18n("player_self_killed")
                                .replace("%victim%", victimColor + victim.getDisplayName())
                                .replace("%victimTeam%", victimColor + victimTeam.getName());
                        }

                    }
                    if (deathMessage != null) {
                        event.setDeathMessage(null);
                        for (Player player : game.getConnectedPlayers()) {
                            player.sendMessage(deathMessage);
                        }
                    }
                }

                CurrentTeam team = game.getPlayerTeam(gVictim);
                SpawnEffects.spawnEffect(game, victim, "game-effects.kill");
                if (!team.isBed) {
                    game.updateScoreboard();
                    gVictim.isSpectator = true;
                    team.players.remove(gVictim);
                    team.getScoreboardTeam().removeEntry(victim.getName());
                    if (Main.isPlayerStatisticsEnabled()) {
                        PlayerStatistic statistic = Main.getPlayerStatisticsManager().getStatistic(victim);
                        statistic.setCurrentLoses(statistic.getCurrentLoses() + 1);
                        statistic.setCurrentScore(statistic.getCurrentScore()
                            + Main.getConfigurator().config.getInt("statistics.scores.lose", 0));

                    }
                }

                boolean onlyOnBedDestroy = Main.getConfigurator().config.getBoolean("statistics.bed-destroyed-kills",
                    false);

                Player killer = victim.getKiller();
                if (Main.isPlayerInGame(killer)) {
                    GamePlayer gKiller = Main.getPlayerGameProfile(killer);
                    if (gKiller.getGame() == game) {
                        if (!onlyOnBedDestroy || !team.isBed) {
                            game.dispatchRewardCommands("player-kill", killer,
                                Main.getConfigurator().config.getInt("statistics.scores.kill", 10));
                        }
                        if (team.isDead()) {
                            SpawnEffects.spawnEffect(game, victim, "game-effects.teamkill");
                            Sounds.playSound(killer, killer.getLocation(),
                                Main.getConfigurator().config.getString("sounds.on_team_kill"),
                                Sounds.ENTITY_PLAYER_LEVELUP, 1, 1);
                        } else {
                            Sounds.playSound(killer, killer.getLocation(),
                                Main.getConfigurator().config.getString("sounds.on_player_kill"),
                                Sounds.ENTITY_PLAYER_BIG_FALL, 1, 1);
                            Main.depositPlayer(killer, Main.getVaultKillReward());
                        }

                    }
                }

                BedwarsPlayerKilledEvent killedEvent = new BedwarsPlayerKilledEvent(game, victim,
                    Main.isPlayerInGame(killer) ? killer : null, drops);
                Main.getInstance().getServer().getPluginManager().callEvent(killedEvent);

                if (Main.isPlayerStatisticsEnabled()) {
                    PlayerStatistic diePlayer = Main.getPlayerStatisticsManager().getStatistic(victim);
                    PlayerStatistic killerPlayer;

                    boolean teamIsDead = !team.isBed;

                    if (!onlyOnBedDestroy || teamIsDead) {
                        diePlayer.setCurrentDeaths(diePlayer.getCurrentDeaths() + 1);
                        diePlayer.setCurrentScore(diePlayer.getCurrentScore()
                            + Main.getConfigurator().config.getInt("statistics.scores.die", 0));
                    }

                    if (killer != null) {
                        if (!onlyOnBedDestroy || teamIsDead) {
                            killerPlayer = Main.getPlayerStatisticsManager().getStatistic(killer);
                            if (killerPlayer != null) {
                                killerPlayer.setCurrentKills(killerPlayer.getCurrentKills() + 1);
                                killerPlayer.setCurrentScore(killerPlayer.getCurrentScore()
                                    + Main.getConfigurator().config.getInt("statistics.scores.kill", 10));
                            }
                        }
                    }
                }
            }
            NMSUtils.respawn(Main.getInstance(), victim, 5L);
            if (Main.getConfigurator().config.getBoolean("respawn-cooldown.enabled")) {
                game.makeSpectator(gVictim, false);

                new BukkitRunnable() {
                    int livingTime = respawnTime;
                    GamePlayer gamePlayer = gVictim;
                    Player player = gamePlayer.player;

                    @Override
                    public void run() {
                        livingTime--;
                        if (livingTime > 0) {
                            Title.send(player,
                                i18nonly("respawn_cooldown_title").replace("%time%", String.valueOf(livingTime)), "");
                            Sounds.playSound(player, player.getLocation(),
                                Main.getConfigurator().config.getString("sounds.on_respawn_cooldown_wait"),
                                Sounds.BLOCK_STONE_BUTTON_CLICK_ON, 1, 1);
                        }

                        if (livingTime == 0) {
                            Sounds.playSound(player, player.getLocation(),
                                Main.getConfigurator().config.getString("sounds.on_respawn_cooldown_done"),
                                Sounds.UI_BUTTON_CLICK, 1, 1);
                            game.makePlayerFromSpectator(gamePlayer);

                            this.cancel();
                        }
                    }
                }.runTaskTimer(Main.getInstance(), 20L, 20L);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (Main.isPlayerGameProfileRegistered(event.getPlayer())) {
            GamePlayer gPlayer = Main.getPlayerGameProfile(event.getPlayer());
            if (gPlayer.isInGame())
                gPlayer.changeGame(null);
            Main.unloadPlayerGameProfile(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (Main.isPlayerInGame(event.getPlayer())) {
            GamePlayer gPlayer = Main.getPlayerGameProfile(event.getPlayer());
            Game game = gPlayer.getGame();
            CurrentTeam team = game.getPlayerTeam(gPlayer);

            if (game.getStatus() == GameStatus.WAITING) {
                event.setRespawnLocation(gPlayer.getGame().getLobbySpawn());
                return;
            }
            if (gPlayer.isSpectator) {
                event.setRespawnLocation(gPlayer.getGame().makeSpectator(gPlayer, true));
            } else {
                event.setRespawnLocation(gPlayer.getGame().getPlayerTeam(gPlayer).teamInfo.spawn);

                if (Main.getConfigurator().config.getBoolean("respawn.protection-enabled", true)) {
                    RespawnProtection respawnProtection = game.addProtectedPlayer(gPlayer.player);
                    respawnProtection.runProtection();
                }

                SpawnEffects.spawnEffect(gPlayer.getGame(), gPlayer.player, "game-effects.respawn");
                if (gPlayer.getGame().getOriginalOrInheritedPlayerRespawnItems()) {
                    List<ItemStack> givedGameStartItems = (List<ItemStack>) Main.getConfigurator().config
                        .getList("gived-player-respawn-items");
                    for (ItemStack stack : givedGameStartItems) {
                        gPlayer.player.getInventory().addItem(Main.applyColor(team.getColor(), stack));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
        if (Main.isPlayerInGame(event.getPlayer())) {
            GamePlayer gPlayer = Main.getPlayerGameProfile(event.getPlayer());
            Game game = gPlayer.getGame();
            if (game.getWorld() != event.getPlayer().getWorld()
                && game.getLobbySpawn().getWorld() != event.getPlayer().getWorld()) {
                gPlayer.changeGame(null);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled())
            return;
        if (Main.isPlayerInGame(event.getPlayer())) {
            Game game = Main.getPlayerGameProfile(event.getPlayer()).getGame();
            if (game.getStatus() == GameStatus.WAITING) {
                event.setCancelled(true);
                return;
            }
            if (!game.blockPlace(Main.getPlayerGameProfile(event.getPlayer()), event.getBlock(),
                event.getBlockReplacedState(), event.getItemInHand())) {
                event.setCancelled(true);
            }

            if (game.getStatus() == GameStatus.RUNNING
                && GameCreator.isInArea(event.getBlock().getLocation(), game.getPos1(), game.getPos2())) {
                Block block = event.getBlock();
                int explosionTime = Main.getConfigurator().config.getInt("tnt.explosion-time", 8) * 20;

                if (block.getType() == Material.TNT
                    && Main.getConfigurator().config.getBoolean("tnt.auto-ignite", false)) {
                    block.setType(Material.AIR);
                    Location location = block.getLocation().add(0.5, 0.5, 0.5);
                    ;

                    TNTPrimed tnt = (TNTPrimed) location.getWorld().spawnEntity(location, EntityType.PRIMED_TNT);
                    tnt.setFuseTicks(explosionTime);

                    Main.registerGameEntity(tnt, game);

                    new BukkitRunnable() {
                        public void run() {
                            Main.unregisterGameEntity(tnt);
                        }
                    }.runTaskLater(Main.getInstance(), explosionTime + 10);
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled())
            return;
        if (Main.isPlayerInGame(event.getPlayer())) {
            Game game = Main.getPlayerGameProfile(event.getPlayer()).getGame();
            if (game.getStatus() == GameStatus.WAITING) {
                event.setCancelled(true);
                return;
            }
            if (!game.blockBreak(Main.getPlayerGameProfile(event.getPlayer()), event.getBlock(), event)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommandExecuted(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled())
            return;
        if (Main.isPlayerInGame(event.getPlayer())) {
            if (!Main.isCommandAllowedInGame(event.getMessage().split(" ")[0])) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(i18n("command_is_not_allowed"));
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getClickedInventory() == null) {
            return;
        }

        if (event.getClickedInventory().getType() == InventoryType.PLAYER) {
            Player p = (Player) event.getWhoClicked();
            if (Main.isPlayerInGame(p)) {
                GamePlayer gPlayer = Main.getPlayerGameProfile(p);
                Game game = gPlayer.getGame();
                if (game.getStatus() == GameStatus.WAITING || gPlayer.isSpectator) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onHunger(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player) || event.isCancelled()) {
            return;
        }

        Player player = (Player) event.getEntity();
        if (Main.isPlayerInGame(player)) {
            GamePlayer gPlayer = Main.getPlayerGameProfile(player);
            Game game = gPlayer.getGame();
            if (game.getStatus() == GameStatus.WAITING || gPlayer.isSpectator) {
                event.setCancelled(true);
            }

            if (game.getStatus() == GameStatus.RUNNING && Main.getConfigurator().config.getBoolean("disable-hunger")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (event.isCancelled() || !(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        if (Main.isPlayerInGame(player)) {
            GamePlayer gPlayer = Main.getPlayerGameProfile(player);
            if (gPlayer.getGame().getStatus() != GameStatus.RUNNING) {
                event.setCancelled(true);
            } else if (!gPlayer.getGame().getOriginalOrInheritedCrafting()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) {
            if (event instanceof EntityDamageByEntityEvent) {
                Game game = Main.getInGameEntity(event.getEntity());
                if (game != null) {
                    if (game.isEntityShop(event.getEntity()) && game.getOriginalOrInheritedPreventKillingVillagers()) {
                        event.setCancelled(true);
                    }
                }

                if (event.getEntity() instanceof ArmorStand) {
                    Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
                    if (damager instanceof Player) {
                        Player player = (Player) damager;
                        if (Main.isPlayerInGame(player)) {
                            GamePlayer gPlayer = Main.getPlayerGameProfile(player);
                            if (gPlayer.getGame().getStatus() == GameStatus.WAITING || gPlayer.isSpectator) {
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            }

            return;
        }

        Player player = (Player) event.getEntity();
        if (Main.isPlayerInGame(player)) {
            GamePlayer gPlayer = Main.getPlayerGameProfile(player);
            Game game = gPlayer.getGame();
            if (gPlayer.isSpectator) {
                if (event.getCause() == DamageCause.VOID) {
                    player.teleport(game.getSpecSpawn());
                }
                event.setCancelled(true);
            } else if (game.getStatus() == GameStatus.WAITING) {
                if (event.getCause() == DamageCause.VOID) {
                    player.teleport(game.getLobbySpawn());
                }
                event.setCancelled(true);
            } else if (game.getStatus() == GameStatus.RUNNING) {
                if (gPlayer.isSpectator) {
                    event.setCancelled(true);
                }
                if (game.isProtectionActive(player) && event.getCause() != DamageCause.VOID) {
                    event.setCancelled(true);
                    return;
                }

                if (event.getCause() == DamageCause.VOID) {
                    player.setHealth(0.5);
                }

            } else if (event instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent) event;
                if (edbee.getDamager() instanceof Player) {
                    Player damager = (Player) edbee.getDamager();
                    if (Main.isPlayerInGame(damager)) {
                        if (Main.getPlayerGameProfile(damager).isSpectator) {
                            event.setCancelled(true);
                        }
                    }
                    /*
                     * Used onLaunchProjectile instead of this, remove this comment after testing
                     *
                     * } else if (edbee.getDamager() instanceof Projectile) { Projectile projectile
                     * = (Projectile) edbee.getDamager(); if (projectile.getShooter() instanceof
                     * Player) { Player damager = (Player) projectile.getShooter(); if
                     * (Main.isPlayerInGame(damager)) { if
                     * (Main.getPlayerGameProfile(damager).isSpectator) { event.setCancelled(true);
                     * } } }
                     */
                } else if (edbee.getDamager() instanceof Firework) {
                    if (Main.isPlayerInGame(player)) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onLaunchProjectile(ProjectileLaunchEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Projectile projectile = event.getEntity();
        if (projectile.getShooter() instanceof Player) {
            Player damager = (Player) projectile.getShooter();
            if (Main.isPlayerInGame(damager)) {
                if (Main.getPlayerGameProfile(damager).isSpectator) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Player player = event.getPlayer();
        if (Main.isPlayerInGame(player)) {
            GamePlayer gPlayer = Main.getPlayerGameProfile(player);
            if (gPlayer.getGame().getStatus() != GameStatus.RUNNING || gPlayer.isSpectator) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFly(PlayerToggleFlightEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Player player = event.getPlayer();
        if (Main.isPlayerInGame(player)) {
            if (!Main.getPlayerGameProfile(player).isSpectator) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.isCancelled() && event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }

        Player player = event.getPlayer();
        if (Main.isPlayerInGame(player)) {
            GamePlayer gPlayer = Main.getPlayerGameProfile(player);
            Game game = gPlayer.getGame();
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (game.getStatus() == GameStatus.WAITING || gPlayer.isSpectator) {
                    event.setCancelled(true);
                    if (event.getMaterial() == Material
                        .valueOf(Main.getConfigurator().config.getString("items.jointeam", "COMPASS"))) {
                        if (game.getStatus() == GameStatus.WAITING) {
                            TeamSelectorInventory inv = game.getTeamSelectorInventory();
                            if (inv == null) {
                                return;
                            }
                            inv.openForPlayer(player);
                        } else if (gPlayer.isSpectator) {
                            // TODO
                        }
                    } else if (event.getMaterial() == Material
                        .valueOf(Main.getConfigurator().config.getString("items.startgame", "DIAMOND"))) {
                        if (game.getStatus() == GameStatus.WAITING && (player.hasPermission("bw.vip.startitem")
                            || player.hasPermission("misat11.bw.vip.startitem"))) {
                            if (game.checkMinPlayers()) {
                                game.gameStartItem = true;
                            } else {
                                player.sendMessage(i18n("vip_not_enough_players"));
                            }
                        }
                    } else if (event.getMaterial() == Material
                        .valueOf(Main.getConfigurator().config.getString("items.leavegame", "SLIME_BALL"))) {
                        game.leaveFromGame(player);
                    }
                }

                if (game.getStatus() == GameStatus.RUNNING) {
                    if (event.getClickedBlock() != null) {
                        if (event.getClickedBlock().getType() == Material.ENDER_CHEST) {
                            Block chest = event.getClickedBlock();
                            CurrentTeam team = game.getTeamOfChest(chest);

                            if (team == null) {
                                return;
                            }
                            event.setCancelled(true);

                            if (!team.players.contains(gPlayer)) {
                                player.sendMessage(i18n("team_chest_is_not_your"));
                                return;
                            }

                            BedwarsTeamChestOpenEvent teamChestOpenEvent = new BedwarsTeamChestOpenEvent(game, player,
                                team);
                            Main.getInstance().getServer().getPluginManager().callEvent(teamChestOpenEvent);

                            if (teamChestOpenEvent.isCancelled()) {
                                return;
                            }

                            player.openInventory(team.getTeamChestInventory());
                        } else if (event.getClickedBlock().getType() == Material.CHEST) {
                            game.addChestForFutureClear(event.getClickedBlock().getLocation());
                        }
                    }
                }

                if (event.getClickedBlock() != null) {
                    if (game.getRegion().isBedBlock(event.getClickedBlock().getState())) {
                        // prevent Essentials to set home in arena
                        event.setCancelled(true);

                        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                            ItemStack stack = event.getItem();
                            if (stack.getType().isBlock()) {
                                BlockFace face = event.getBlockFace();
                                Block block = event.getClickedBlock().getLocation().clone().add(face.getDirection())
                                    .getBlock();
                                if (block.getType() == Material.AIR) {
                                    BlockState originalState = block.getState();
                                    block.setType(stack.getType());
                                    try {
                                        // The method is no longer in API, but in legacy versions exists
                                        Block.class.getMethod("setData", byte.class).invoke(block,
                                            (byte) stack.getDurability());
                                    } catch (Exception e) {
                                    }
                                    BlockPlaceEvent bevent = new BlockPlaceEvent(block, originalState,
                                        event.getClickedBlock(), stack, player, true);
                                    Bukkit.getPluginManager().callEvent(bevent);

                                    if (bevent.isCancelled()) {
                                        originalState.update(true, false);
                                    } else {
                                        // TODO get right block place sound
                                        Sounds.BLOCK_STONE_PLACE.playSound(player, block.getLocation(), 1, 1);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        if (Main.isPlayerInGame(player)) {
            GamePlayer gPlayer = Main.getPlayerGameProfile(player);
            Game game = gPlayer.getGame();
            if (game.getStatus() == GameStatus.WAITING || gPlayer.isSpectator) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        if (Main.isPlayerInGame(player)) {
            GamePlayer gPlayer = Main.getPlayerGameProfile(player);
            Game game = gPlayer.getGame();
            if (game.getStatus() == GameStatus.WAITING || gPlayer.isSpectator) {
                event.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onSleep(PlayerBedEnterEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (Main.isPlayerInGame(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.isCancelled() || !(event.getPlayer() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getPlayer();
        if (Main.isPlayerInGame(player)) {
            GamePlayer gProfile = Main.getPlayerGameProfile(player);
            if (gProfile.getGame().getStatus() == GameStatus.RUNNING) {
                if (gProfile.isSpectator) {
                    // TODO spectator compass exclude
                    event.setCancelled(true);
                    return;
                }
                if (event.getInventory().getType() == InventoryType.ENCHANTING
                    || event.getInventory().getType() == InventoryType.CRAFTING
                    || event.getInventory().getType() == InventoryType.ANVIL
                    || event.getInventory().getType() == InventoryType.BREWING
                    || event.getInventory().getType() == InventoryType.FURNACE
                    || event.getInventory().getType() == InventoryType.WORKBENCH) {
                    if (!gProfile.getGame().getOriginalOrInheritedCrafting()) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if (Main.isPlayerInGame(player)) {
            Game game = Main.getPlayerGameProfile(player).getGame();
            if (!(entity instanceof LivingEntity)) {
                return;
            }

            if (game.getStatus() != GameStatus.WAITING) {
                return;
            }
            LivingEntity living = (LivingEntity) entity;
            String displayName = ChatColor.stripColor(living.getCustomName());

            for (Team team : game.getTeams()) {
                if (team.name.equals(displayName)) {
                    event.setCancelled(true);
                    game.selectTeam(Main.getPlayerGameProfile(player), displayName);
                    return;
                }
            }
        } else if (player.hasPermission(BaseCommand.ADMIN_PERMISSION)) {
            List<MetadataValue> values = player.getMetadata(GameCreator.BEDWARS_TEAM_JOIN_METADATA);
            if (values.size() == 0) {
                return;
            }

            event.setCancelled(true);
            TeamJoinMetaDataValue value = (TeamJoinMetaDataValue) values.get(0);
            if (!((boolean) value.value())) {
                return;
            }

            if (!(entity instanceof LivingEntity)) {
                player.sendMessage(i18n("admin_command_jointeam_entitynotcompatible"));
                return;
            }

            LivingEntity living = (LivingEntity) entity;
            living.setRemoveWhenFarAway(false);
            living.setCanPickupItems(false);
            living.setCustomName(value.getTeam().color.chatColor + value.getTeam().name);
            living.setCustomNameVisible(Main.getConfigurator().config.getBoolean("jointeam-entity-show-name", true));

            if (living instanceof ArmorStand) {
                ArmorStandUtils.equipArmorStand((ArmorStand) living, value.getTeam());
            }

            player.removeMetadata(GameCreator.BEDWARS_TEAM_JOIN_METADATA, Main.getInstance());
            player.sendMessage(i18n("admin_command_jointeam_entity_added"));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent je) {

        final Player player = je.getPlayer();

        if (Main.isHologramsEnabled()) {
            Main.getHologramInteraction().updateHolograms(player, 60L);

        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled() || !Main.getConfigurator().config.getBoolean("chat.override")) {
            return;
        }

        Player player = event.getPlayer();
        if (Main.isPlayerInGame(player)) {
            GamePlayer gPlayer = Main.getPlayerGameProfile(player);
            Game game = gPlayer.getGame();
            CurrentTeam team = game.getPlayerTeam(gPlayer);
            String message = event.getMessage();
            boolean spectator = gPlayer.isSpectator;

            String playerName = player.getName();
            String displayName = player.getDisplayName();
            String playerListName = player.getPlayerListName();

            String format = Main.getConfigurator().config.getString("chat.format", "<%teamcolor%%name%§r> ");
            if (team != null) {
                format = format.replace("%teamcolor%", team.teamInfo.color.chatColor.toString());
                format = format.replace("%team%", team.teamInfo.name);
                format = format.replace("%coloredteam%", team.teamInfo.color.chatColor + team.teamInfo.name);
            } else if (spectator) {
                format = format.replace("%teamcolor%", ChatColor.GRAY.toString());
                format = format.replace("%team%", "SPECTATOR");
                format = format.replace("%coloredteam%", ChatColor.GRAY.toString() + "SPECTATOR");
            } else {
                format = format.replace("%teamcolor%", ChatColor.GRAY.toString());
                format = format.replace("%team%", "");
                format = format.replace("%coloredteam%", ChatColor.GRAY.toString());
            }
            format = format.replace("%name%", playerName);
            format = format.replace("%displayName%", displayName);
            format = format.replace("%playerListName%", playerListName);

            if (Main.isVault()) {
                Chat chat = Bukkit.getServer().getServicesManager().load(Chat.class);
                if (chat != null) {
                    format = format.replace("%prefix%", chat.getPlayerPrefix(player));
                    format = format.replace("%suffix%", chat.getPlayerSuffix(player));
                }
            }

            format = format.replace("%prefix%", "");
            format = format.replace("%suffix%", "");

            format = ChatColor.translateAlternateColorCodes('&', format);

            boolean teamChat = Main.getConfigurator().config.getBoolean("chat.default-team-chat-while-running", true)
                && game.getStatus() == GameStatus.RUNNING && (team != null || spectator);

            String allChat = Main.getConfigurator().config.getString("chat.all-chat-prefix", "@a");
            String tChat = Main.getConfigurator().config.getString("chat.team-chat-prefix", "@t");

            if (message.startsWith(allChat)) {
                teamChat = false;
                message = message.substring(allChat.length()).trim();
            } else if (message.startsWith(tChat) && (team != null || spectator)) {
                teamChat = true;
                message = message.substring(tChat.length()).trim();
            }

            if (teamChat) {
                if (spectator) {
                    format = Main.getConfigurator().config.getString("chat.death-chat", "[DEATH] ") + format;
                } else {
                    format = Main.getConfigurator().config.getString("chat.team-chat", "[TEAM] ") + format;
                }
            } else {
                format = Main.getConfigurator().config.getString("chat.all-chat", "[ALL] ") + format;
            }

            event.setFormat(format + message.replaceAll("%", "%%")); // Fix using % in chat
            Iterator<Player> recipients = event.getRecipients().iterator();
            while (recipients.hasNext()) {
                Player recipient = recipients.next();
                GamePlayer recipientgPlayer = Main.getPlayerGameProfile(recipient);
                Game recipientGame = recipientgPlayer.getGame();
                if (recipientGame != game && Main.getConfigurator().config.getBoolean("chat.separate-game-chat")) {
                    recipients.remove();
                } else if (game.getPlayerTeam(recipientgPlayer) != team && teamChat) {
                    recipients.remove();
                }
            }
        } else {
            if (Main.getConfigurator().config.getBoolean("chat.separate-game-chat")) {
                Iterator<Player> recipients = event.getRecipients().iterator();
                while (recipients.hasNext()) {
                    Player recipient = recipients.next();
                    GamePlayer recipientgPlayer = Main.getPlayerGameProfile(recipient);
                    Game recipientGame = recipientgPlayer.getGame();
                    if (recipientGame != null) {
                        recipients.remove();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        if (Main.isPlayerInGame(player)) {
            GamePlayer gPlayer = Main.getPlayerGameProfile(player);
            Game game = gPlayer.getGame();
            if (game.getOriginalOrInheritedDamageWhenPlayerIsNotInArena() && game.getStatus() == GameStatus.RUNNING
                && !gPlayer.isSpectator) {
                if (!GameCreator.isInArea(event.getTo(), game.getPos1(), game.getPos2())) {
                    player.damage(5);
                }
            }
        }
    }

    @EventHandler
    public void onPlaceLiquid(PlayerBucketEmptyEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        if (Main.isPlayerInGame(player)) {
            GamePlayer gPlayer = Main.getPlayerGameProfile(player);
            Game game = gPlayer.getGame();
            Location loc = event.getBlockClicked().getLocation();

            loc.add(event.getBlockFace().getDirection());

            Block block = loc.getBlock();
            if (game.getStatus() == GameStatus.RUNNING) {
                if (block.getType() == Material.AIR || game.getRegion().isBlockAddedDuringGame(block.getLocation())) {
                    game.getRegion().addBuiltDuringGame(block.getLocation());
                } else {
                    event.setCancelled(true);
                }
            } else if (game.getStatus() != GameStatus.DISABLED) {
                event.setCancelled(true);
            }
        }
    }

    /* BungeeCord */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (!(event.getResult() == PlayerLoginEvent.Result.ALLOWED)) {
            return;
        }
        Player player = event.getPlayer();
        Game game = (Game) Main.getInstance().getFirstWaitingGame();

        if (Game.isBungeeEnabled() && Main.getConfigurator().config.getBoolean("bungee.auto-game-connect", false)) {
            new BukkitRunnable() {
                public void run() {
                    try {
                        game.joinToGame(player);
                    } catch (NullPointerException ignored) {
                        BungeeUtils.movePlayerToBungeeServer(player);
                    }
                }
            }.runTaskLater(Main.getInstance(), 1L);
        }
    }
}
