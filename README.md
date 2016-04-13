# Prime

Simple commands for Spigot.

**NOTE**: This is a very early/experimental version with little features, and has not been tested very much at all. Feel free to fork and mess with it.

# How To Use

### Main Class Example
```java
public class MyPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        PrimeManager manager = new PrimeManager(this, new PrimePermissionHandler());

        manager.registerCommands(MyCommand.class);
    }
}
```

### Command Example

```java
public class MyCommand {

    @Command(
            aliases = {"mycommand", "alias", "otheralias"},
            description = "My command",
            min = 1,
            max = 1,
            usage = "<word>"
    )
    public static void myCommand(CommandContext args, CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.sendMessage(ChatColor.RED + "Your word: " + ChatColor.GOLD + args.getString(0));
        }
    }
}
```

### Nested Command Example

**What are nested commands?**: Nested commands allow for things such as `/parent command <args>`. This is useful for organizing sub-commands under one command rather than creating new base commands.

```java
public class MyPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        PrimeManager manager = new PrimeManager(this, new PrimePermissionHandler());

        manager.registerCommands(SubCommands.class);
    }

    @ParentCommand(
            aliases = {"parent"},
            description = "Parent command"
    )
    public static class SubCommands {

        @Command(
                aliases = {"mycommand", "alias", "otheralias"},
                description = "My command",
                min = 1,
                max = 1,
                usage = "<word>"
        )
        public static void myCommand(CommandContext args, CommandSender sender) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                player.sendMessage(ChatColor.RED + "Your word: " + ChatColor.GOLD + args.getString(0));
            }
        }
    }
}
```

# Licence

MIT License

Copyright (c) 2016 Ben Clabby

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.