package dev.tr7zw.exordium;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.network.chat.Component;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import dev.tr7zw.exordium.config.ScoreboardEditorScreen;

public class ExordiumMod extends ExordiumModBase implements ClientModInitializer {

    private static boolean exordium$openScoreboardEditorNextTick = false;

    @Override
    public void onInitializeClient() {
        super.onInitialize();
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            markInitialized();
            if (exordium$openScoreboardEditorNextTick) {
                exordium$openScoreboardEditorNextTick = false;
                client.setScreen(new ScoreboardEditorScreen(client.screen));
            }
        });
    }

    @Override
    public void initModloader() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                literal("exordium")
                        .then(literal("scoreboard")
                                .executes(context -> openScoreboardEditor())
                                .then(literal("edit").executes(context -> openScoreboardEditor()))
                                .then(literal("reset").executes(context -> {
                                    ExordiumModBase.instance.config.scoreboardCustomPosition = false;
                                    ExordiumModBase.instance.config.scoreboardX = 0;
                                    ExordiumModBase.instance.config.scoreboardY = 0;
                                    ExordiumModBase.instance.config.scoreboardScale = 100;
                                    ExordiumModBase.instance.config.scoreboardOpacity = 100;
                                    ExordiumModBase.instance.writeConfig();
                                    context.getSource().sendFeedback(
                                            Component.translatable("text.exordium.command.scoreboard.reset"));
                                    return 1;
                                })))));
    }

    private static int openScoreboardEditor() {
        exordium$openScoreboardEditorNextTick = true;
        return 1;
    }

}
