package me.vexil.prime;

import me.vexil.prime.annotations.CommandPermission;
import me.vexil.prime.annotations.ParentCommand;
import me.vexil.prime.data.CommandContext;
import me.vexil.prime.data.CommandInformation;
import me.vexil.prime.exception.CommandMethodInvocationException;
import me.vexil.prime.handler.PermissionHandler;
import me.vexil.prime.handler.PrimeExecutor;
import me.vexil.prime.util.PrimeLocale;
import me.vexil.prime.util.ReflectionUtil;
import me.vexil.prime.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import me.vexil.prime.annotations.Command;
import me.vexil.prime.exception.CommandException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class PrimeManager implements CommandExecutor {

    private static PluginCommand getNewCommand(String name, Plugin plugin) {
        PluginCommand command = null;
        try {
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);
            command = constructor.newInstance(name, plugin);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return command;
    }

    private static CommandMap getCommandMap() {
        CommandMap commandMap = null;
        try {
            if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
                commandMap = (CommandMap) ReflectionUtil.getFieldValue(SimplePluginManager.class, Bukkit.getPluginManager(), "commandMap");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return commandMap;
    }

    private Plugin plugin;
    private List<CommandInformation> commands = new ArrayList<>();
    private Map<ParentCommand, List<CommandInformation>> nestedCommands = new HashMap<>();
    private PermissionHandler permissionHandler;

    public PrimeManager(Plugin plugin, PermissionHandler permissionHandler) {
        this.plugin = plugin;
        this.permissionHandler = permissionHandler;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public List<CommandInformation> getCommands() {
        return commands;
    }

    public Map<ParentCommand, List<CommandInformation>> getNestedCommands() {
        return nestedCommands;
    }

    public PermissionHandler getPermissionHandler() {
        return permissionHandler;
    }

    public void setPermissionHandler(PermissionHandler permissionHandler) {
        this.permissionHandler = permissionHandler;
    }

    public void registerCommands(Class<?> clazz) {
        if (clazz.isAnnotationPresent(ParentCommand.class)) {
            ParentCommand parentCommand = clazz.getAnnotation(ParentCommand.class);

            List<CommandInformation> commands = new ArrayList<>();
            for (Method method : clazz.getMethods()) {
                if (Modifier.isStatic(method.getModifiers()) && method.isAnnotationPresent(Command.class)) {
                    Command annotationInfo = method.getAnnotation(Command.class);

                    CommandInformation commandInfo = new CommandInformation(
                            annotationInfo.aliases(),
                            annotationInfo.description(),
                            annotationInfo.minArgs(),
                            annotationInfo.maxArgs(),
                            annotationInfo.usage(),
                            new PrimeExecutor(method),
                            method.isAnnotationPresent(CommandPermission.class) ? method.getAnnotation(CommandPermission.class) : null
                    );

                    commands.add(commandInfo);
                }
            }

            PluginCommand command = getNewCommand(parentCommand.aliases()[0], getPlugin());
            command.setAliases(Arrays.asList(parentCommand.aliases()));
            command.setDescription(parentCommand.description());

            StringBuilder stringBuilder = new StringBuilder("<" + StringUtil.join(commands.get(0).getAliases(), "|"));
            for (int x = 1; x < commands.size(); x++) {
                CommandInformation commandInformation = commands.get(x);

                stringBuilder.append("|");
                stringBuilder.append(StringUtil.join(commandInformation.getAliases(), "|"));
            }
            stringBuilder.append(">");

            command.setUsage(stringBuilder.toString());
            command.setExecutor(this);

            getCommandMap().register(plugin.getName(), command);

            nestedCommands.put(parentCommand, commands);
        } else {
            for (Method method : clazz.getMethods()) {
                if (Modifier.isStatic(method.getModifiers()) && method.isAnnotationPresent(Command.class)) {
                    Command annotationInfo = method.getAnnotation(Command.class);

                    CommandInformation commandInfo = new CommandInformation(
                            annotationInfo.aliases(),
                            annotationInfo.description(),
                            annotationInfo.minArgs(),
                            annotationInfo.maxArgs(),
                            annotationInfo.usage(),
                            new PrimeExecutor(method),
                            method.isAnnotationPresent(CommandPermission.class) ? method.getAnnotation(CommandPermission.class) : null
                    );

                    PluginCommand command = getNewCommand(commandInfo.getAliases()[0], getPlugin());
                    command.setAliases(Arrays.asList(commandInfo.getAliases()));
                    command.setDescription(commandInfo.getDesc());
                    command.setUsage(commandInfo.getUsage());
                    command.setExecutor(this);

                    getCommandMap().register(plugin.getName(), command);
                    commands.add(commandInfo);
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        for (ParentCommand parentCommand : nestedCommands.keySet()) {
            for (String parentAlias : parentCommand.aliases()) {
                if (parentAlias.equals(label)) {
                    List <CommandInformation> commands = nestedCommands.get(parentCommand);

                    if (args.length < 1) {
                        sender.sendMessage(PrimeLocale.INVALID_ARGS.replaceAll("%usage%", command.getUsage()));
                        return true;
                    }

                    for (CommandInformation commandInfo : commands) {
                        for (String alias : commandInfo.getAliases()) {
                            if (alias.equals(args[0])) {
                                String[] shortArgs = new String[args.length - 1];
                                System.arraycopy(args, 1, shortArgs, 0, args.length - 1);

                                if (commandInfo.getPermission() != null && !permissionHandler.hasPermission(sender, commandInfo.getPermission())) {
                                    sender.sendMessage(PrimeLocale.NO_PERMS);
                                    return true;
                                }
                                if (commandInfo.getMinArgs() > shortArgs.length || (!(commandInfo.getMaxArgs() < 0) && commandInfo.getMaxArgs() < shortArgs.length)) {
                                    sender.sendMessage(PrimeLocale.INVALID_ARGS.replaceAll("%usage%", label + " " + args[0] + (commandInfo.getUsage().isEmpty() ? "" : " " + commandInfo.getUsage())));
                                    return true;
                                }

                                try {
                                    commandInfo.getCommandExecutor().execute(new CommandContext(shortArgs), sender);
                                } catch (CommandMethodInvocationException e) {
                                    sender.sendMessage(PrimeLocale.ERROR);
                                    e.printStackTrace();
                                } catch (CommandException e) {
                                    sender.sendMessage(e.getMessage());
                                    e.printStackTrace();
                                }
                                return true;
                            }
                        }
                    }
                }
            }
        }

        for (CommandInformation commandInfo : commands) {
            for (String alias : commandInfo.getAliases()) {
                if (alias.equals(label)) {
                    if (commandInfo.getPermission() != null && !permissionHandler.hasPermission(sender, commandInfo.getPermission())) {
                        sender.sendMessage(PrimeLocale.NO_PERMS);
                        return true;
                    }
                    if (commandInfo.getMinArgs() > args.length || (!(commandInfo.getMaxArgs() < 0) && commandInfo.getMaxArgs() < args.length)) {
                        sender.sendMessage(PrimeLocale.INVALID_ARGS.replaceAll("%usage%", label + (commandInfo.getUsage().isEmpty() ? "" : " " + commandInfo.getUsage())));
                        return true;
                    }

                    try {
                        commandInfo.getCommandExecutor().execute(new CommandContext(args), sender);
                    } catch (CommandMethodInvocationException e) {
                        sender.sendMessage(PrimeLocale.ERROR);
                        e.printStackTrace();
                    } catch (CommandException e) {
                        sender.sendMessage(e.getMessage());
                        e.printStackTrace();
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
