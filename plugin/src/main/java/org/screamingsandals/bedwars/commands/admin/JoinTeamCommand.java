package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.commands.AdminCommand;
import org.screamingsandals.bedwars.game.Team;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.utils.TeamJoinMetaDataValue;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;

import java.util.List;
import java.util.stream.Collectors;

public class JoinTeamCommand extends BaseAdminSubCommand {
    public static final String BEDWARS_TEAM_JOIN_METADATA = "bw-addteamjoin";

    public JoinTeamCommand(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        super(manager, commandSenderWrapperBuilder, "jointeam");
    }

    @Override
    public void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .argument(StringArgument.<CommandSenderWrapper>newBuilder("team")
                                    .withSuggestionsProvider((c, s) -> {
                                        if (AdminCommand.gc.containsKey(c.<String>get("game"))) {
                                            return AdminCommand.gc.get(c.<String>get("game")).getTeams()
                                                    .stream()
                                                    .map(Team::getName)
                                                    .collect(Collectors.toList());
                                        }
                                        return List.of();
                                    })
                        )
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String team = commandContext.get("team");

                            var player = sender.as(Player.class);

                            for (var t : game.getTeams()) {
                                if (t.name.equals(team)) {
                                    if (player.hasMetadata(BEDWARS_TEAM_JOIN_METADATA)) {
                                        player.removeMetadata(BEDWARS_TEAM_JOIN_METADATA, Main.getInstance().getPluginDescription().as(JavaPlugin.class));
                                    }
                                    player.setMetadata(BEDWARS_TEAM_JOIN_METADATA, new TeamJoinMetaDataValue(t));

                                    new BukkitRunnable() {
                                        public void run() {
                                            if (!player.hasMetadata(BEDWARS_TEAM_JOIN_METADATA)) {
                                                return;
                                            }

                                            player.removeMetadata(BEDWARS_TEAM_JOIN_METADATA, Main.getInstance().getPluginDescription().as(JavaPlugin.class));
                                        }
                                    }.runTaskLater(Main.getInstance().getPluginDescription().as(JavaPlugin.class), 200L);
                                    sender.sendMessage(Message.of(LangKeys.ADMIN_TEAM_JOIN_ENTITY_CLICK_RIGHT_ON_ENTITY).defaultPrefix().placeholder("team", t.name));
                                    return;
                                }
                            }
                            sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_TEAM_DOES_NOT_EXIST).defaultPrefix());
                        }))
        );
    }
}
