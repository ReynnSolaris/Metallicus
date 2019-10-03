package me.reynn.bots.metallicus;

import org.json.JSONObject;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static me.reynn.bots.metallicus.Main.ValidMessageIds;

/**
 * Created by Aaron on 9/20/2018.
 */
public class ReactionManager {

    @EventSubscriber
    public void onReactionEvent(ReactionAddEvent e) {
        if (e.getMessage().getClient() != Bot.ibot)
            return;
        if(e.getUser().isBot())
            return;
        if(e.getMessage().getAuthor() != Bot.ibot.getOurUser())
            return;
        String Left,Right;
        Left = "⬅";
        Right = "➡";
        if(ValidMessageIds.keySet().size() <= 0)
            return;
        if(!e.getReaction().getEmoji().toString().equals(Left) && !e.getReaction().getEmoji().toString().equals(Right))
            return;
        for (String s : ValidMessageIds.keySet()) {
            System.out.print(e.getMessage().getTimestamp().toEpochMilli()+" - "+System.currentTimeMillis()+"\n");
            if((TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) - TimeUnit.MILLISECONDS.toSeconds(e.getMessage().getTimestamp().toEpochMilli())) >= 15) {
                ValidMessageIds.remove(s);
                System.out.print("REMOVED ["+s+"] REASON MORE THAN 5 SECONDS PAST\n");
                return;
            }
            if(e.getMessage().getStringID().equalsIgnoreCase(s)) {
                if(e.getUser().getStringID().equalsIgnoreCase(ValidMessageIds.getString(s))) {

                } else {
                    return;
                }
            } else {
                return;
            }
        }

        //e.getChannel().sendMessage(e.getUser().getName()+" just added a reaction to post`("+e.getReaction().toString()+")` !");
    }
}
