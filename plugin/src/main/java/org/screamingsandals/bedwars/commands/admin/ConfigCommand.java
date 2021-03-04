package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import org.screamingsandals.bedwars.commands.AdminCommand;
import org.screamingsandals.lib.sender.CommandSenderWrapper;

import java.util.List;

import static org.screamingsandals.bedwars.lib.lang.I.i18n;

public class ConfigCommand extends BaseAdminSubCommand {
    public ConfigCommand(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        super(manager, commandSenderWrapperBuilder, "config");
    }

    @Override
    public void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .argument(StringArgument.<CommandSenderWrapper>newBuilder("key")
                                .withSuggestionsProvider((c, s) -> {
                                    if (AdminCommand.gc.containsKey(c.<String>get("game"))) {
                                        return AdminCommand.gc.get(c.<String>get("game")).getConfigurationContainer().getRegisteredKeys();
                                    }
                                    return List.of();
                                })
                        )
                        .argument(StringArgument.<CommandSenderWrapper>newBuilder("value")
                            .withSuggestionsProvider((c, s) -> List.of("true", "false", "inherit"))
                        )
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String keyString = commandContext.get("key");
                            String value = commandContext.get("value");

                            var key = game.getConfigurationContainer().getRegisteredKeys()
                                    .stream()
                                    .filter(t -> t.equalsIgnoreCase(keyString))
                                    .findFirst();

                            if (key.isEmpty()) {
                                sender.sendMessage(i18n("admin_command_invalid_config_variable_name"));
                            } else {
                                var type = game.getConfigurationContainer().getType(key.get());
                                if (type.isAssignableFrom(Boolean.class)) {
                                    switch (value.toLowerCase()) {
                                        case "t":
                                        case "tr":
                                        case "tru":
                                        case "true":
                                        case "y":
                                        case "ye":
                                        case "yes":
                                        case "1":
                                            game.getConfigurationContainer().update(key.get(), Boolean.TRUE);
                                            value = "true";
                                            break;
                                        case "f":
                                        case "fa":
                                        case "fal":
                                        case "fals":
                                        case "false":
                                        case "n":
                                        case "no":
                                        case "0":
                                            game.getConfigurationContainer().update(key.get(), Boolean.FALSE);
                                            value = "false";
                                            break;
                                        case "i":
                                        case "in":
                                        case "inh":
                                        case "inhe":
                                        case "inher":
                                        case "inheri":
                                        case "inherit":
                                        case "d":
                                        case "de":
                                        case "def":
                                        case "defa":
                                        case "defau":
                                        case "defaul":
                                        case "default":
                                            game.getConfigurationContainer().update(key.get(), null);
                                            value = "inherit";
                                            break;
                                        default:
                                            sender.sendMessage(i18n("admin_command_invalid_config_value"));
                                            return;
                                    }
                                } else {
                                    // here we need to somehow determinate which type is it
                                    game.getConfigurationContainer().update(key.get(), value);
                                }
                                sender.sendMessage(i18n("admin_command_config_variable_set_to").replace("%config%", keyString).replace("%value%", value));
                            }
                        }))
        );
    }
}
