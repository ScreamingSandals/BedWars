package org.screamingsandals.bedwars.special;

import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.RunningTeam;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.bedwars.utils.Sounds;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.Map;

import static misat11.lib.lang.I18n.i18nonly;

public class Trap extends SpecialItem implements org.screamingsandals.bedwars.api.special.Trap {
    private List<Map<String, Object>> trapData;
    private Location location;
    private Player player;

    public Trap(Game game, Player player, Team team, List<Map<String, Object>> trapData) {
        super(game, player, team);
        this.trapData = trapData;
        this.player = player;
        this.team = team;

        game.registerSpecialItem(this);
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public boolean isPlaced() {
        return location != null;
    }

    public void place(Location loc) {
        this.location = loc;
    }

    public void process(Player player, RunningTeam runningTeam, boolean forceDestroy) {
        if (runningTeam == game.getTeamOfPlayer(this.player) || forceDestroy) {
            game.unregisterSpecialItem(this);
            location.getBlock().setType(Material.AIR);
            return;
        }

        if (runningTeam != game.getTeamOfPlayer(this.player)) {
            for (Map<String, Object> data : trapData) {
                if (data.containsKey("sound")) {
                    String sound = (String) data.get("sound");
                    Sounds.playSound(player, location, sound, null, 1, 1);
                }

                if (data.containsKey("effect")) {
                    PotionEffect effect = (PotionEffect) data.get("effect");
                    player.addPotionEffect(effect);
                }

                if (data.containsKey("damage")) {
                    double damage = (double) data.get("damage");
                    player.damage(damage);
                }
            }

            for (Player p : game.getTeamOfPlayer(this.player).getConnectedPlayers()) {
                MiscUtils.sendActionBarMessage(p, i18nonly("specials_trap_caught_team").replace("%player%", player.getDisplayName()));
            }
        }
        MiscUtils.sendActionBarMessage(player, i18nonly("specials_trap_caught").replace("%team%", getTeam().getName()));
    }
}
