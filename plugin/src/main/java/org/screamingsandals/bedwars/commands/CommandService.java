package org.screamingsandals.bedwars.commands;

import cloud.commandframework.CommandManager;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import lombok.experimental.UtilityClass;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.command.CloudConstructor;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.Provider;

@Service(dependsOn = {
        CloudConstructor.class
}, initAnother = {
        AddholoCommand.class,
        AdminCommand.class,
        AlljoinCommand.class,
        AutojoinCommand.class,
        CheatCommand.class,
        DumpCommand.class,
        HelpCommand.class,
        JoinCommand.class,
        LanguageCommand.class,
        LeaderboardCommand.class,
        LeaveCommand.class,
        ListCommand.class,
        MainlobbyCommand.class,
        PartyCommand.class,
        RejoinCommand.class,
        ReloadCommand.class,
        RemoveHoloCommand.class,
        StatsCommand.class,
        GamesInventoryCommand.class,
        LobbyNPCCommand.class
})
@UtilityClass
public class CommandService {

    @Provider(level = Provider.Level.POST_ENABLE)
    public static CommandManager<CommandSenderWrapper> provideCommandManager() {
        try {
            var manager = CloudConstructor.construct(CommandExecutionCoordinator.simpleCoordinator());

            new MinecraftExceptionHandler<CommandSenderWrapper>()
                    .withDefaultHandlers()
                    .withHandler(MinecraftExceptionHandler.ExceptionType.NO_PERMISSION, (senderWrapper, e) ->
                            Message.of(LangKeys.NO_PERMISSIONS).defaultPrefix().getForJoined(senderWrapper)
                    )
                    .withHandler(MinecraftExceptionHandler.ExceptionType.INVALID_SYNTAX, (senderWrapper, e) ->
                            Message.of(LangKeys.UNKNOWN_USAGE).defaultPrefix().getForJoined(senderWrapper)
                    )
                    .apply(manager, s -> s);

            return manager;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
