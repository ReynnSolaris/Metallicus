package me.reynn.bots.metallicus.CommandHandlers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aaron on 1/27/2019.
 */
public class Introduction {
    public List<String> Clans = new ArrayList<>();
    public List<String> Questions = new ArrayList<>();
    public List<String> Q1A = new ArrayList<>();
    public List<String> Q2A = new ArrayList<>();
    public List<String> Q3A = new ArrayList<>();
    public void SetupVars() {
        /*
        -Clans-
            ~Hollowdawn~ A magical clan that allows ??
            ~Banner Dragons~ A Sly and witty clan that allows ??
            ~Iron Assailants~ A structured militant clan that allows ??
         */
        Clans.add(0, "Hollowdawn");
        Clans.add(1, "Banner Dragons");
        Clans.add(2, "Iron Assailants");
        /*
            Questions
         */
        Questions.add(0,"If you had found a rare crystal in a mine, what would you do?");
        Questions.add(1,"While you were mining a demonic creature was summoned, it offers to trade you golden crystalite for some ore, what's your response?");
        Questions.add(2,"You come across a treasure chest with writing, 'Those whom steal shall pay'. What are you going to do?");
        /*
            Question Answers
         */
        //QUESTION 1
        Q1A.add(0, "Tell your friends that you have found the gem? (Just Kidding! You don't have friends!)");
        Q1A.add(1, "Try and crack the magical essence pattern reminiscing off the crystal to infuse your pickaxe.");
        Q1A.add(2, "Sell the crystal to a nearby dark merchant for Ethereal Shards.");

        //QUESTION 2
        Q2A.add(0, "Avoid the creature and run away from it.");
        Q2A.add(1, "Trade with the creature because money is money.");
        Q2A.add(2, "Slay the demonic creature!");

        //QUESTION 3
        Q3A.add(0, "Open the mysterious chest because you don't care about an old sign.");
        Q3A.add(1, "You take a second to use detection magic on the chest.");
        Q3A.add(2, "Ignore the chest and continue on your way.");
    }

}
