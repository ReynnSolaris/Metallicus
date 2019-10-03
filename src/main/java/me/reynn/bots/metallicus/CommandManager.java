package me.reynn.bots.metallicus;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {
    private String prefix;
    private List<Command> commands;
    public CommandManager(String prefix) {
        this.prefix = prefix;
        commands = new ArrayList<Command>();
    }
    public void addCommand(Command command) {
        commands.add(command);
    }

    public List<Command> getCommands() {
        return commands;
    }

    @EventSubscriber
    public void commandRan(MessageReceivedEvent event) {
        try {
            String message = event.getMessage().getContent();
            if (message.toLowerCase().startsWith(prefix)) {
                if (event.getAuthor().isBot())
                    return;
                //IUser user, IChannel channel, IGuild guild, String label, List<String> args
                IUser user = event.getAuthor();
                IChannel channel = event.getChannel();
                IGuild guild = event.getGuild();
                boolean TestingGuild = false;
                String label = message.toLowerCase().replace(prefix, "").split(" ")[0];
                String[] argus = message.replace(prefix + label + " ", "").split(" ");
                List<String> args = new ArrayList<>();
                for (String arg : argus) {
                    args.add(arg);
                }
                boolean worked = false;
                boolean hasPermission = true;
                boolean isWhitelisted = false;
                boolean requiredServer = true;
                boolean privateDM = false;
                boolean requiredChannel = true;
                ///IGuild testingGuild = Bot.ibot.getGuildByID(Long.parseLong("190237475313156096"));
                //IRole testingRole = testingGuild.getRolesByName("Member").get(0);
                for (Command command : commands) {
                    boolean UsedAlt = false;
                    if(command.AlternativeLabels().length != 0) {
                        for (int i = 0; i < command.AlternativeLabels().length; i++) {
                            if(command.AlternativeLabels()[i].equalsIgnoreCase(label)) {
                                UsedAlt = true;
                                break;
                            }
                        }
                    }
                    if (command.getLabel().equalsIgnoreCase(label) || UsedAlt) {
                        EmbedBuilder eb = new EmbedBuilder();
                        eb.withAuthorName("Command Priority");
                        if(command.DirectMessageCommand()) {
                            if(!channel.isPrivate()) {
                                eb.withDesc("The command you tried to use isn't available to be used outside the bots direct messages!");
                                RequestBuffer.request(() -> channel.sendMessage(eb.build()));
                                return;
                            }
                        }
                        if(!command.DirectMessageCommand()) {
                            if(channel.isPrivate()) {
                                eb.withDesc("The command you tried to use isn't available to be used in the bots direct messages!");
                                RequestBuffer.request(() -> channel.sendMessage(eb.build()));
                                return;
                            }
                        }
                        worked = true;
                        if(command.RequiredServer() != null) {
                            if(!guild.getStringID().equalsIgnoreCase(command.RequiredServer())) {
                                requiredServer = false;
                            }
                            if(!channel.getStringID().equalsIgnoreCase(command.RequiredChannel()) && command.RequiredChannel() != null) {
                                requiredChannel = false;
                            }
                        }
                        if(requiredServer) {
                            if(requiredChannel) {
                                if (!command.HasWhitelist() || (command.HasWhitelist() && command.Whitelist().contains(user.getStringID()))) { //user.getRolesForGuild(testingGuild).contains(testingRole))
                                    if (command.getPermission() != null && user.getPermissionsForGuild(guild).contains(command.getPermission()) || command.getPermission() == null || user.getStringID().equalsIgnoreCase(BotUtils.BO_ID)) {
                                        command.runCommand(user, channel, event, guild, label, args);
                                    } else {
                                        hasPermission = false;
                                    }
                                } else {
                                    isWhitelisted = true;
                                }
                            } else {
                                RequestBuffer.request(() -> channel.sendMessage("This command isn't whitelisted for this channel!"));
                            }
                        } else {
                            RequestBuffer.request(() -> channel.sendMessage("This command isn't whitelisted for this server!"));
                        }
                    }
                }
                if(isWhitelisted) {
                    RequestBuffer.request(() -> channel.sendMessage("User isn't whitelisted for this command!"));
                }
                if (!hasPermission) {
                    RequestBuffer.request(() -> channel.sendMessage("User doesn't have required permissions!"));
                }
                if (!worked) {
                    return;
                }
            }
        }
        catch(Exception e) {
            throw e;
        }
    }
}
