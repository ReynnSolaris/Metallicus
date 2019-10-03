package me.reynn.bots.metallicus;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.StatusType;
import sx.blah.discord.util.DiscordException;

import java.util.List;

public class Bot {
    IDiscordClient bot;
    CommandManager commands;
    ReactionManager reactions;
    public static IDiscordClient ibot;
    public Bot(String token) {
        bot = createClient(token);
        commands = new CommandManager(BotUtils.prefix);
        bot.getDispatcher().registerListener(commands);
        reactions = new ReactionManager();
        bot.getDispatcher().registerListener(reactions);
        while(!bot.isReady()) {
            if(bot.isReady())
                break;
        }
        bot.changePresence(StatusType.DND, ActivityType.PLAYING, BotUtils.prefix+"help | " + bot.getGuilds().size()+" Servers");
        ibot = bot;
    }

    public void addCommand(Command command) {
        commands.addCommand(command);
    }
    public List<Command> getCommands() {
        return commands.getCommands();
    }
    private IDiscordClient createClient(String token) {
        ClientBuilder clientBuilder = new ClientBuilder();
        clientBuilder.withToken(token);
        try {
            return clientBuilder.login();
        } catch (DiscordException e) {
            e.printStackTrace();
            return null;
        }
    }
}
