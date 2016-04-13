package me.vexil.prime.data;

import me.vexil.prime.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandContext {

    private List<String> arguments;

    public CommandContext(String... arguments) {
        this.arguments = Collections.unmodifiableList(Arrays.asList(arguments));
    }

    public String getJoinedArgs() {
        return StringUtil.join(arguments, " ");
    }

    public String getJoinedArgs(int start) {
        return StringUtil.join(arguments.subList(start, arguments.size()), " ");
    }

    public List<String> getArguments() {
        return arguments;
    }

    public String getString(int index) {
        try {
            return arguments.get(index);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public int getInteger(int index) {
        return StringUtil.toInteger(getString(index) == null ? "-1" : getString(index));
    }

    public double getDouble(int index) {
        return StringUtil.toDouble(getString(index) == null ? "-1.0" : getString(index));
    }

    public Player getPlayer(int index) {
        return getString(index) == null ? null : Bukkit.getPlayer(getString(index));
    }
}
