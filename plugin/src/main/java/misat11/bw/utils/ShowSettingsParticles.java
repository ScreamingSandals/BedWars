package misat11.bw.utils;

import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import misat11.bw.Main;
import misat11.bw.api.GameStore;
import misat11.bw.game.Game;
import misat11.bw.game.Team;

public class ShowSettingsParticles extends BukkitRunnable {
	
	public final Player player;
	public final Game game;
	
	public ShowSettingsParticles(Player player, Game game) {
		this.player = player;
		this.game = game;
		
		this.runTaskTimer(Main.getInstance(), 0L, 20L);
	}

	@Override
	public void run() {
		try {
			// TODO in some future version
			
			for (GameStore store : game.getGameStores()) {
				
			}
			
			for (Team team : game.getTeams()) {
				player.spawnParticle(Particle.BARRIER, team.getTargetBlock(), 1);
				player.spawnParticle(Particle.HEART, team.getTeamSpawn(), 1);
			}
			
			player.spawnParticle(Particle.CRIT, game.getSpectatorSpawn(), 10);
			player.spawnParticle(Particle.DRAGON_BREATH, game.getLobbySpawn(), 10);
		} catch (Throwable e) {
			// Maybe not supported
			this.cancel();
		}
	}
	
}
