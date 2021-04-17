package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.holograms.StatisticsHolograms;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class RemoveholoCommand extends BaseCommand {
    public RemoveholoCommand() {
        super("removeholo", BedWarsPermission.ADMIN_PERMISSION, false);
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder, CommandManager<CommandSenderWrapper> manager) {
        manager.command(
                commandSenderWrapperBuilder
                    .handler(commandContext -> {
                        var player = commandContext.getSender().as(Player.class);
                        if (!StatisticsHolograms.isEnabled()) {
                            commandContext.getSender().sendMessage(Message.of(LangKeys.ADMIN_HOLO_NOT_ENABLED).defaultPrefix());
                        } else {
                            player.setMetadata("bw-remove-holo", new FixedMetadataValue(Main.getInstance().getPluginDescription().as(JavaPlugin.class), true));
                            commandContext.getSender().sendMessage(Message.of(LangKeys.ADMIN_HOLO_CLICK_TO_REMOVE).defaultPrefix());
                        }
                    })
        );
    }
}
