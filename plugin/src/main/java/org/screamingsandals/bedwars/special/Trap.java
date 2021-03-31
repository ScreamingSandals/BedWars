package org.screamingsandals.bedwars.special;

import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.RunningTeam;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.bedwars.utils.Sounds;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerMapper;
import org.screamingsandals.lib.utils.AdventureHelper;

import java.util.List;
import java.util.Map;

public class Trap extends SpecialItem implements org.screamingsandals.bedwars.api.special.Trap {
    private List<Map<String, Object>> trapData;
    private Location location;
    private RunningTeam runningTeam;

    public Trap(Game game, Player player, RunningTeam team, List<Map<String, Object>> trapData) {
        super(game, player, team);
        this.trapData = trapData;
        this.runningTeam = team;

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
        if (runningTeam == this.runningTeam || forceDestroy) {
            game.unregisterSpecialItem(this);
            location.getBlock().setType(Material.AIR);
            return;
        }

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

        for (Player p : this.runningTeam.getConnectedPlayers()) {
            MiscUtils.sendActionBarMessage(PlayerMapper.wrapPlayer(p), Message.of(LangKeys.SPECIALS_TRAP_CAUGHT_TEAM).placeholder("player", AdventureHelper.toComponent(player.getDisplayName())));
        }
        MiscUtils.sendActionBarMessage(PlayerMapper.wrapPlayer(player), Message.of(LangKeys.SPECIALS_TRAP_CAUGHT).placeholder("team", getTeam().getName()));
    }
}
