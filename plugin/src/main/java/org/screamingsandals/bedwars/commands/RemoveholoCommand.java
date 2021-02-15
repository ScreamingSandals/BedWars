package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.lib.sender.CommandSenderWrapper;

import static org.screamingsandals.bedwars.lib.lang.I.i18n;

public class RemoveholoCommand extends BaseCommand {
    public RemoveholoCommand(CommandManager<CommandSenderWrapper> manager) {
        super(manager, "removeholo", BedWarsPermission.ADMIN_PERMISSION, false);
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                    .handler(commandContext -> {
                        // TODO Use Wrapper in the code - Add EyeLocation to PlayerWrapper in ScreamingLib
                        var player = commandContext.getSender().as(Player.class);
                        if (!Main.isHologramsEnabled()) {
                            player.sendMessage(i18n("holo_not_enabled"));
                        } else {
                            player.setMetadata("bw-remove-holo", new FixedMetadataValue(Main.getInstance().getPluginDescription().as(JavaPlugin.class), true));
                            player.sendMessage(i18n("click_to_holo_for_remove"));
                        }
                    })
        );
    }
}
