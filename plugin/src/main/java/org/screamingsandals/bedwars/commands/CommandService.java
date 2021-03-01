package org.screamingsandals.bedwars.commands;

import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import lombok.experimental.UtilityClass;
import org.screamingsandals.lib.command.CloudConstructor;
import org.screamingsandals.lib.entity.EntityMapper;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.AdventureHelper;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnPostEnable;

import static org.screamingsandals.bedwars.lib.lang.I.i18n;

@Service(dependsOn = {
        CloudConstructor.class,
        EntityMapper.class // AddholoCommand
})
@UtilityClass
public class CommandService {

    @OnPostEnable
    public void postEnable() {
        try {
            var manager = CloudConstructor.construct(CommandExecutionCoordinator.simpleCoordinator());

            new MinecraftExceptionHandler<CommandSenderWrapper>()
                    .withDefaultHandlers()
                    .withHandler(MinecraftExceptionHandler.ExceptionType.NO_PERMISSION, (senderWrapper, e) ->
                            AdventureHelper.toComponent(i18n("no_permissions"))
                    )
                    .withHandler(MinecraftExceptionHandler.ExceptionType.INVALID_SYNTAX, (senderWrapper, e) ->
                            AdventureHelper.toComponent(i18n("unknown_usage"))
                    )
                    .apply(manager, s -> s);

            new AddholoCommand(manager).construct();
            new AdminCommand(manager).construct();
            new AlljoinCommand(manager).construct();
            new AutojoinCommand(manager).construct();
            new DumpCommand(manager).construct();
            new HelpCommand(manager).construct();
            new JoinCommand(manager).construct();
            new LanguageCommand(manager).construct();
            new LeaderboardCommand(manager).construct();
            new LeaveCommand(manager).construct();
            new ListCommand(manager).construct();
            new MainlobbyCommand(manager).construct();
            new PartyCommand(manager).construct();
            new RejoinCommand(manager).construct();
            new ReloadCommand(manager).construct();
            new RemoveholoCommand(manager).construct();
            new StatsCommand(manager).construct();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
