package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.lib.sender.CommandSenderWrapper;

import java.util.List;
import java.util.Optional;

import static org.screamingsandals.bedwars.lib.lang.I.i18n;

public class AddholoCommand extends BaseCommand {
    public AddholoCommand(CommandManager<CommandSenderWrapper> manager) {
        super(manager, "addholo", BedWarsPermission.ADMIN_PERMISSION, false);
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .argument(manager
                                .argumentBuilder(String.class, "type")
                                .asOptional()
                                .withSuggestionsProvider((c, s) -> List.of("leaderboard", "stats"))
                        )
                        .handler(commandContext -> {
                            Optional<String> type = commandContext.getOptional("type");
                            // TODO Use Wrapper in the code - Add EyeLocation to PlayerWrapper in ScreamingLib
                            var player = commandContext.getSender().as(Player.class);
                            if (!Main.isHologramsEnabled()) {
                                player.sendMessage(i18n("holo_not_enabled"));
                            } else {
                                if (type.isPresent() && "leaderboard".equalsIgnoreCase(type.get())) {
                                    Main.getLeaderboardHolograms().addHologramLocation(player.getEyeLocation());
                                    player.sendMessage(i18n("leaderboard_holo_added"));
                                } else {
                                    Main.getHologramInteraction().addHologramLocation(player.getEyeLocation());
                                    Main.getHologramInteraction().updateHolograms();
                                    player.sendMessage(i18n("holo_added"));
                                }
                            }
                        })
        );
    }
}
