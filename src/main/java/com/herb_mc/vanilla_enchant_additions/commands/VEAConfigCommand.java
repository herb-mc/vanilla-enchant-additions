package com.herb_mc.vanilla_enchant_additions.commands;

import com.herb_mc.vanilla_enchant_additions.VEAMod;
import com.herb_mc.vanilla_enchant_additions.helper.ConfigOpt;
import com.herb_mc.vanilla_enchant_additions.helper.VEAConfigHelper;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

import java.io.IOException;

public class VEAConfigCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
    {
        dispatcher.register((CommandManager.literal("vea-config")
                .requires((serverCommandSource) -> serverCommandSource.hasPermissionLevel(VEAMod.configMaps.get("configGetPermLevel").getInt())))
                .then(CommandManager.literal("get")
                        .executes((context) -> {
                            for (String option : VEAMod.optionList)
                                context.getSource().sendFeedback(Text.literal(option + ": " + VEAMod.configMaps.get(option).getAsString()), false);
                            return 1;
                        })
                        .then(CommandManager.argument("option", StringArgumentType.string())
                                .suggests((context,builder) -> CommandSource.suggestMatching(VEAMod.optionList,builder))
                                .executes((context) -> {
                                    context.getSource().sendFeedback(Text.literal(context.getArgument("option", String.class) + ": " + VEAMod.configMaps.get(context.getArgument("option", String.class)).getAsString()), false);
                                    return 1;
                                })
                        ))
                .then(CommandManager.literal("reload")
                        .requires((serverCommandSource) -> serverCommandSource.hasPermissionLevel(4))
                        .executes((context) -> {
                            context.getSource().sendFeedback(Text.literal("Reloading configurations"), true);
                            VEAConfigHelper.loadConf(context.getSource().getServer());
                            return 1;
                        }))
                .then(CommandManager.literal("set")
                        .requires((serverCommandSource) -> serverCommandSource.hasPermissionLevel(4))
                        .then(CommandManager.argument("option", StringArgumentType.string())
                                .suggests((context,builder) -> CommandSource.suggestMatching(VEAMod.optionList,builder))
                                .then(CommandManager.argument("value", StringArgumentType.string())
                                        .executes((context) -> {
                                            String temp = context.getArgument("value", String.class);
                                            boolean success = setValue(context, temp);
                                            if (success) {
                                                context.getSource().sendFeedback(Text.literal(context.getArgument("option", String.class) + " set to " + VEAMod.configMaps.get(context.getArgument("option", String.class)).getAsString()), false);
                                                VEAMod.LOGGER.info("{}: value of '{}' set to {}", context.getSource().getDisplayName().getString(), context.getArgument("option", String.class), context.getArgument("value", String.class));
                                                try {
                                                    VEAConfigHelper.overwriteSettingToConf(context.getArgument("option", String.class), temp, context.getSource().getServer());
                                                } catch (IOException e) {
                                                    VEAMod.LOGGER.error("Failed write value for '{}' to vanilla_enchant_additions.conf", context.getArgument("option", String.class), e);
                                                }
                                            } else {
                                                VEAMod.LOGGER.info("Failed to write '{}' to setting {} in vanilla_enchant_additions.conf", temp, context.getArgument("option", String.class));
                                            }
                                            return 1;
                                        })
                                )
                        )
                )
        );
    }

    private static boolean setValue(CommandContext<ServerCommandSource> context, String arg) {
        ConfigOpt c = VEAMod.configMaps.get(context.getArgument("option", String.class));
        boolean success = true;
        Object t = null;
        if (!c.getCondition().satisfiesCondition(arg)) {
            context.getSource().sendFeedback(Text.literal("Value " + arg + " not accepted, " + c.failMessage), false);
            return false;
        }
        if (c.type == boolean.class) {
            t = Boolean.parseBoolean(arg);
        } else if (c.type == int.class) {
            try {
                t = Integer.parseInt(arg);
            } catch (NumberFormatException e) {
                context.getSource().sendFeedback(Text.literal("Unable to parse value " + arg + " as int"), false);
                success = false;
            }
        } else if (c.type == double.class) {
            try {
                t = Double.parseDouble(arg);
            } catch (NumberFormatException e) {
                context.getSource().sendFeedback(Text.literal("Unable to parse value " + arg + " as double"), false);
                success = false;
            }
        } else {
            try {
                t = Float.parseFloat(arg);
            } catch (NumberFormatException e) {
                context.getSource().sendFeedback(Text.literal("Unable to parse value " + arg + " as float"), false);
                success = false;
            }
        }
        if (success) c.setValue(t);
        return success;
    }

}
