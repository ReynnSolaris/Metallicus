package me.reynn.bots.metallicus;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.util.List;

public interface Command {
    public String getLabel();
    public String getDescription();
    public Permissions getPermission();
    public List<String> Whitelist();
    public boolean HasWhitelist();
    public String RequiredServer();
    public String RequiredChannel();
    public String[] AlternativeLabels();
    public boolean DirectMessageCommand();

    public void runCommand(IUser user, IChannel channel, MessageReceivedEvent event, IGuild guild, String label, List<String> args);
}
