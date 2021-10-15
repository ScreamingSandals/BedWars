package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import org.screamingsandals.bedwars.commands.AdminCommand;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.List;

@Service
public class ConfigCommand extends BaseAdminSubCommand {
    public ConfigCommand() {
        super();
    }

    @Override
    public void construct(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
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
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_CONSTANT_NAME).defaultPrefix());
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
                                            sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_CONSTANT_VALUE).defaultPrefix());
                                            return;
                                    }
                                } else {
                                    // here we need to somehow determinate which type is it
                                    game.getConfigurationContainer().update(key.get(), value);
                                }
                                Message
                                        .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_CONSTANT_SET)
                                        .placeholder("config", keyString)
                                        .placeholder("value", value)
                                        .defaultPrefix()
                                        .send(sender);
                            }
                        }))
        );
    }
}
