package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import org.screamingsandals.bedwars.commands.AdminCommand;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.variants.VariantImpl;
import org.screamingsandals.bedwars.variants.VariantManagerImpl;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class AddCommand extends BaseAdminSubCommand {
    public AddCommand() {
        super();
    }

    @Override
    public void construct(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .argument(StringArgument
                                .<CommandSenderWrapper>newBuilder("variant")
                                .withSuggestionsProvider((c, s) -> VariantManagerImpl.getInstance().getVariantNames())
                                .asOptional()
                        )
                        .handler(commandContext -> {
                            String gameName = commandContext.get("game");
                            var variant = commandContext.<String>getOptional("variant");
                            var sender = commandContext.getSender();

                            if (GameManagerImpl.getInstance().hasGame(gameName)) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_ERROR_ALREADY_EXISTS).defaultPrefix());
                            } else if (AdminCommand.gc.containsKey(gameName)) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_ERROR_ALREADY_WORKING_ON_IT).defaultPrefix());
                            } else {
                                VariantImpl variantObj = null;
                                if (variant.isPresent() && "null".equalsIgnoreCase(variant.get())) {
                                    var variantOpt = VariantManagerImpl.getInstance().getVariant(variant.get());
                                    if (variantOpt.isPresent()) {
                                        variantObj = variantOpt.get();
                                    } else {
                                        sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_VARIANT)
                                                .placeholder("variant", variant.get()).defaultPrefix());
                                    }
                                }

                                var creator = GameImpl.createGame(gameName);
                                AdminCommand.gc.put(gameName, creator);

                                if (variantObj != null) {
                                    creator.setGameVariant(variantObj);
                                    sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_SUCCESS_ADDED_WITH_VARIANT)
                                            .placeholder("arena", gameName)
                                            .placeholder("variant", variantObj.getName())
                                            .defaultPrefix());
                                } else {
                                    sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_SUCCESS_ADDED)
                                            .placeholder("arena", gameName)
                                            .defaultPrefix());
                                }
                            }
                        })
        );
    }
}
