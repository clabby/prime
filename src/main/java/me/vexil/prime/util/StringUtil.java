package me.vexil.prime.util;

import org.bukkit.ChatColor;

import java.util.Collection;

public class StringUtil {

    private static char colorCodeTranslate = '&';

    public static void setColorCodeChar(char colorCodeChar) {
        colorCodeTranslate = colorCodeChar;
    }

    public static String colorize(String string) {
        return ChatColor.translateAlternateColorCodes(colorCodeTranslate, string);
    }

    public static String join(String[] args, String delimiter) {
        if (args.length < 1) {
            throw new IllegalArgumentException("args is empty");
        }
        StringBuilder stringBuilder = new StringBuilder(args[0]);

        if (args.length > 1) {
            for (int x = 1; x < args.length; x++) {
                stringBuilder.append(delimiter).append(args[x]);
            }
            return stringBuilder.toString();
        } else {
            return stringBuilder.toString();
        }
    }

    public static String join(Collection<String> args, String delimiter) {
        return join(args.toArray(new String[args.size()]), delimiter);
    }

    public static boolean isInt(String toTest) {
        try {
            Integer.parseInt(toTest);
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }

    public static boolean isDouble(String toTest) {
        try {
            Double.parseDouble(toTest);
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }

    public static int toInteger(String string) throws NumberFormatException {
        try {
            return Integer.parseInt(string.replaceAll("[^\\d]", ""));
        } catch (NumberFormatException e) {
            throw new NumberFormatException(string + " isn't a number!");
        }
    }

    public static double toDouble(String string) {
        try {
            return Double.parseDouble(string.replaceAll(".*?([\\d.]+).*", "$1"));
        } catch (NumberFormatException e) {
            throw new NumberFormatException(string + " isn't a number!");
        }
    }

    private StringUtil() {
    }
}
