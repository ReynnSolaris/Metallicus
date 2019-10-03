package me.reynn.bots.metallicus;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import me.reynn.bots.metallicus.Assets.ImageGenerator;
import me.reynn.bots.metallicus.CommandHandlers.Introduction;
import org.json.JSONObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class Main {
    public static Connection con;
    public static Introduction intro;
    public static JSONObject ValidMessageIds = new JSONObject();
    public static ResultSet GetResultSet(String SQL) {
        ResultSet RS = null;
        try {
            Statement stmt = con.createStatement();
            RS = stmt.executeQuery(SQL);
        } catch(SQLException e) {}
        return RS;
    }

    public static void ShowQuestion(IChannel channel, int QN) {
        EmbedBuilder em = new EmbedBuilder();
        em.withTitle("Clan Evaluation");
        em.withColor(new Color(170,0,0));
        em.withDesc("**:gem: Question ["+QN+"]:** "+intro.Questions.get(QN-1)+"\n\n");
        List<String> Qs = new ArrayList<>();
        if(QN == 1)
            Qs = intro.Q1A;
        if(QN == 2)
            Qs = intro.Q2A;
        if(QN == 3)
            Qs = intro.Q3A;
        em.appendDesc(":shield: **(1.** "+Qs.get(0)+"\n");
        em.appendDesc(":shield: **(2.** "+Qs.get(1)+"\n");
        em.appendDesc(":shield: **(3.** "+Qs.get(2)+"\n");
        em.appendDesc("To select an answer please type **"+BotUtils.prefix+"s [Answer]**\n (for example - **"+BotUtils.prefix+"s 1**)");
        RequestBuffer.request(() -> channel.sendMessage(em.build()));
    }

    public static List<String> GetRandom(JSONObject RS) {

        Random mineLootTable = new Random(System.currentTimeMillis()*System.currentTimeMillis());
        boolean choseS = false;
        java.lang.String choseS_ = "";
        java.lang.String lastS = "";
        double lastD = 0d;
        double mineLoot = (double)(mineLootTable.nextInt(250))/250;

        for (Object s : RS.keySet()) {
            if (RS.getDouble(s) <= mineLoot) {
                if (lastD < RS.getDouble(s)) {
                    choseS = true;
                    choseS_ = s;
                }
                lastD = RS.getDouble(s);
            }
            lastS = s;
        }
        if (!choseS)
            choseS_ = lastS;
        List<String> a = new ArrayList<>();
        a.add(0, Boolean.toString(choseS));
        a.add(1, choseS_);
        a.add(2, lastS);
        return a;
    }

    public static void main(String[] args) {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/obfuscated_ram?useSSL=false", "toor", "root");
            System.out.printf("[SYS MSG] %s - %s\n", "SQL Connection has been Established.", "Connected to server: localhost:3306/obfuscated_ram!");
        } catch (Exception e) {}
        Bot bot = new Bot(BotUtils.botToken);
        SQLHandler SQLH = new SQLHandler();
        intro = new Introduction();
        intro.SetupVars();
        ReactionManager RM = new ReactionManager();
        //MAIN COMMANDS
        System.out.printf("[SYS MSG] %s - %s\n", "Attempting to initialize commands.", "LOADING COMMANDS");
        bot.addCommand(new Command() {
            @Override
            public String getLabel() {
                return "profile";
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public Permissions getPermission() {
                return null;
            }

            @Override
            public List<String> Whitelist() {
                return null;
            }

            @Override
            public boolean HasWhitelist() {
                return false;
            }

            @Override
            public String RequiredServer() {
                return null;
            }

            @Override
            public String RequiredChannel() {
                return null;
            }

            @Override
            public String[] AlternativeLabels() {
                String[] Labels = new String[1];
                Labels[0] = "p";
                return Labels;
            }

            @Override
            public boolean DirectMessageCommand() {
                return false;
            }

            @Override
            public void runCommand(IUser user, IChannel channel, MessageReceivedEvent event, IGuild guild, String label, List<String> args) {
                List<String> pd = PlayerData.PlayerData(user);
                if(pd!=null) {
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.withTitle("Here is your profile: " + user.getName() + "!");
                    embedBuilder.withImage("attachment://profile-" + user.getStringID() + ".png");
                    InputStream is = new ByteArrayInputStream(ImageGenerator.Profile(user));
                    channel.sendFile(embedBuilder.build(), is, "profile-" + user.getStringID() + ".png");
                } else {
                    RequestBuffer.request(() -> channel.sendMessage("You must start to use the bot before you can use this, **"+user.getName()+"**.\nUse **.start** in the __BOT'S Direct Messages__ to begin."));
                }
            }
        }); // Profile
        bot.addCommand(new Command() {
            @Override
            public String getLabel() {
                return "start";
            }

            @Override
            public String getDescription() {
                return "Start your adventure out with this command.";
            }

            @Override
            public Permissions getPermission() {
                return null;
            }

            @Override
            public List<String> Whitelist() {
                return null;
            }

            @Override
            public boolean HasWhitelist() {
                return false;
            }

            @Override
            public String RequiredServer() {
                return null;
            }

            @Override
            public String RequiredChannel() {
                return null;
            }

            @Override
            public String[] AlternativeLabels() {
                String[] Labels = new String[1];
                Labels[0] = "s";
                return Labels;
            }

            @Override
            public boolean DirectMessageCommand() {
                return true;
            }

            @Override
            public void runCommand(IUser user, IChannel channel, MessageReceivedEvent event, IGuild guild, String label, List<String> args) {
                boolean so_cold = false;
                List<String> pd;
                int QuestionNum = 1;
                try {
                    ResultSet a = GetResultSet("SELECT * FROM profile_db WHERE id ='"+user.getStringID()+"';");
                    so_cold = a.isBeforeFirst();
                    //if(!so_cold) {
                        //SQLH.UpdateData(user);
                        //a = GetResultSet("SELECT * FROM profile_db WHERE id ='"+user.getStringID()+"';");
                    //}
                    while(a.next())
                        QuestionNum = Integer.parseInt(a.getString(8));
                    if(QuestionNum > 3) {
                        RequestBuffer.request(() -> channel.sendMessage("Sorry, **"+user.getName()+"**, you have already started using the bot."));
                        return;
                    }
                } catch (Exception e) {}


                if(args.get(0).equalsIgnoreCase(".start")) {
                    pd = PlayerData.PlayerData(user);
                    ShowQuestion(channel, QuestionNum);
                    //SQLH.AddTo(user, "QN", 1);
                } else if(!args.get(0).equals(null)) {
                    pd = PlayerData.PlayerData(user);
                    if(args.get(0).matches("\\d+")) {
                        int num = Integer.parseInt(args.get(0));
                        Random random = new Random(System.currentTimeMillis());
                        if(num <= 3 && num >= 1) {
                            SQLH.AddTo(user, "QN", 1);
                            if(num == 3) {
                                SQLH.AddTo(user, "QP", (1+random.nextInt(1)));
                            } else if(num == 2) {
                                if(random.nextInt(10) < 5) {
                                    SQLH.AddTo(user, "QP", 1);
                                } else {
                                    SQLH.RemoveFrom(user, "QP", 1);
                                }
                            } else {
                                SQLH.RemoveFrom(user, "QP", (1+random.nextInt(1)));
                            }
                            if(QuestionNum + 1 != 4) {
                                ShowQuestion(channel, QuestionNum + 1);
                            } else {
                                pd = PlayerData.PlayerData(user);
                                int qp = Integer.parseInt(pd.get(7));
                                String Clan = "";
                                if(qp < 0) {
                                    Clan = "Iron Assailants";
                                } else if(qp > 0) {
                                    Clan = "Hollowdawn";
                                } else {
                                    Clan = "Banner Dragons";
                                }
                                final String toSend = "Congratulations, **"+user.getName()+"**! :tada: You have joined the Clan, **"+Clan+"**!";
                                SQLH.SetTo(user, "clan", Clan);
                                RequestBuffer.request(() -> channel.sendMessage(toSend));
                            }
                            return;
                        } else {
                            RequestBuffer.request(() -> channel.sendMessage("You must choose a valid option, **"+user.getName()+"**."));
                            return;
                        }
                    } else {
                        RequestBuffer.request(() -> channel.sendMessage("Sorry, **"+user.getName()+"**, but you didn't choose a valid number."));
                        return;
                    }
                }
                return;
            }
        }); // Start
        bot.addCommand(new Command() {
            @Override
            public String getLabel() {
                return "help";
            }

            @Override
            public String getDescription() {
                return "This shows this dialog box.";
            }

            @Override
            public Permissions getPermission() {
                return null;
            }

            @Override
            public List<String> Whitelist() {
                return null;
            }

            @Override
            public boolean HasWhitelist() {
                return false;
            }

            @Override
            public String RequiredServer() {
                return null;
            }

            @Override
            public String RequiredChannel() {
                return null;
            }

            @Override
            public String[] AlternativeLabels() {
                return new String[0];
            }

            @Override
            public boolean DirectMessageCommand() {
                return false;
            }

            @Override
            public void runCommand(IUser user, IChannel channel, MessageReceivedEvent event, IGuild guild, String label, List<String> args) {
                IPrivateChannel pm = user.getOrCreatePMChannel();
                EmbedBuilder em = new EmbedBuilder();
                em.withTitle(":tada: Help Menu :tada:");
                em.withColor(new Color(75, 255, 190, 150));
                String awoo = bot.bot.getGuildByID(Long.parseLong("480634348908052490")).getEmojiByName("AngryAwooGlitch").toString();
                String github = bot.bot.getGuildByID(Long.parseLong("480634348908052490")).getEmojiByName("GitHubLogo").toString();
                em.withDesc(" \n"+github+" [[Creator's GitHub]](https://github.com/reynnSolaris) "+github+"\n"+awoo+" **Discord** - `@Reynn#4341`"+awoo+"\n");
                String Invite = "https://discordapp.com/api/oauth2/authorize?client_id=538576961790803968&permissions=322624&scope=bot";
                em.appendDesc(":envelope: [[INVITE]]("+Invite+") :envelope:\n");
                em.appendDesc("\n:crown: Standard Bot Commands. :crown:\n");
                em.appendDesc(":diamond_shape_with_a_dot_inside:  **"+BotUtils.prefix+"help** - `This command is what is used to show this dialog box that informs the user on how to use this bot.`\n");
                em.appendDesc(":diamond_shape_with_a_dot_inside: **"+BotUtils.prefix+"start** - `The command that initializes the user to start interacting with the bot.`\n");

                RequestBuffer.request(() -> event.getMessage().delete());
                RequestBuffer.request(() -> {
                    try {
                        pm.sendMessage(em.build());
                    } catch (Exception e) {
                        channel.sendMessage(user.getName() + ", please enable private messages.");
                        return;
                    }
                });
            }
        }); // Help
        bot.addCommand(new Command() {
            @Override
            public String getLabel() {
                return "mine";
            }

            @Override
            public String getDescription() {
                return "Quarry into the deep below and mine valuable resources for later use.";
            }

            @Override
            public Permissions getPermission() {
                return null;
            }

            @Override
            public List<String> Whitelist() {
                return null;
            }

            @Override
            public boolean HasWhitelist() {
                return false;
            }

            @Override
            public String RequiredServer() {
                return null;
            }

            @Override
            public String RequiredChannel() {
                return null;
            }

            @Override
            public String[] AlternativeLabels() {
                return new String[0];
            }

            @Override
            public boolean DirectMessageCommand() {
                return false;
            }

            @Override
            public void runCommand(IUser user, IChannel channel, MessageReceivedEvent event, IGuild guild, String label, List<String> args) {
                List<String> pd = PlayerData.PlayerData(user);
                if (pd != null) {
                    long currTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
                    long mineTime = SQLH.getTimer(user);
                    if (currTime - mineTime >= 1) {
                        SQLH.updateTimer(user, Long.toString(currTime));
                        try {
                            ResultSet rs = GetResultSet("SELECT * FROM mining_loottable");
                            JSONObject RS = new JSONObject();
                            while (rs.next()) {
                                RS.put(Integer.toString(rs.getInt(1)), rs.getDouble(2));
                            }
                            RS = new JSONObject(RS.toString(4));
                            List<String> a = GetRandom(RS);
                            String choseS_ = a.get(1);
                            boolean choseS = Boolean.parseBoolean(a.get(0));
                            String lastS = a.get(2);

                            String OreList = "";
                            ResultSet lootTable = GetResultSet("SELECT * FROM mining_loottable WHERE id='" + choseS_ + "'");
                            while (lootTable.next()) {
                                OreList = lootTable.getString(3);
                            }

                            JSONObject ore = new JSONObject(OreList);
                            List<String> b = GetRandom(ore);
                            String z_ = b.get(1);
                            String oreChosen = "";
                            for (String s : ore.keySet()) {
                                if (s.equalsIgnoreCase(z_)) {
                                    oreChosen = s;
                                }
                            }
                            System.out.printf("%s\n", user.getName() + ", has mined [" + SQLH.getItemInfo(oreChosen).getString("Name") + "]");
                            Random r = new Random(currTime ^ 2);
                            int OreAmnt = 1;
                            if (r.nextInt(100) <= 3) {
                                OreAmnt = r.nextInt();
                            }
                            r = new Random(System.currentTimeMillis() + 3 ^ 2);
                            int goldenBits = r.nextInt(250);
                            if (goldenBits <= 29)
                                goldenBits = 30;
                            EmbedBuilder eb = new EmbedBuilder();
                            eb.withTitle("Mining Operation");
                            eb.withDesc(user.getName() + ", has mined [**" + SQLH.getItemInfo(oreChosen).getString("Name") + "**] **x" + OreAmnt + "** from their expedition, they also earned " + goldenBits + " Golden Bits.\nYou have :clock1: **45 Seconds** left to mine again.");
                            eb.withColor(Color.GREEN);
                            SQLH.addItem(user, oreChosen, OreAmnt);
                            SQLH.AddTo(user, "golden_bits", goldenBits);
                            RequestBuffer.request(() -> channel.sendMessage(eb.build()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        EmbedBuilder eb = new EmbedBuilder();
                        eb.withTitle("Mining Operation");
                        eb.withDesc(user.getName() + ", you have :clock1: **" + (45 - (currTime - mineTime)) + " Seconds** left to mine again.");
                        eb.withColor(Color.GREEN);
                        RequestBuffer.request(() -> channel.sendMessage(eb.build()));
                    }
                    RequestBuffer.request(() -> event.getMessage().delete());
                } else {
                    RequestBuffer.request(() -> channel.sendMessage("You must start to use the bot before you can use this, **"+user.getName()+"**.\nUse **.start** in the __BOT'S Direct Messages__ to begin."));
                }
            }
        });
        bot.addCommand(new Command() {
            @Override
            public String getLabel() {
                return "inventory";
            }

            @Override
            public String getDescription() {
                return "Shows your inventory full of mystery.";
            }

            @Override
            public Permissions getPermission() {
                return null;
            }

            @Override
            public List<String> Whitelist() {
                return null;
            }

            @Override
            public boolean HasWhitelist() {
                return false;
            }

            @Override
            public String RequiredServer() {
                return null;
            }

            @Override
            public String RequiredChannel() {
                return null;
            }

            @Override
            public String[] AlternativeLabels() {
                String[] Labels = new String[1];
                Labels[0] = "inv";
                return Labels;
            }

            @Override
            public boolean DirectMessageCommand() {
                return false;
            }

            @Override
            public void runCommand(IUser user, IChannel channel, MessageReceivedEvent event, IGuild guild, String label, List<String> args) {
                try {
                    JSONObject pd = SQLH.getInventory(user);
                    if(pd!=null) {
                        EmbedBuilder inv = new EmbedBuilder();
                        inv.withTitle(user.getName() + "'s Inventory");
                        inv.withColor(Color.GREEN);
                        int inv_size = 10;
                        int curentItem = 0;
                        for (String s : pd.keySet()) {
                            curentItem++;
                            if(curentItem == inv_size)
                                break;
                            //ID = s | amn = pd.get(s);
                            String ItemName;
                            try {
                                ItemName = SQLH.getItemInfo(s).getString("Name");
                            } catch (Exception e) {
                                ItemName = "[{UNKNOWN ITEM}] - ??";
                            }
                            inv.appendDesc("["+pd.get(s)+"] "+ItemName+"\n");
                        }
                        int total_items = pd.keySet().size();
                        int total_pages = (total_items / inv_size) + 1;
                        int currentPage = 1;
                        inv.appendDesc("\n\n        [**"+currentPage+"**/**"+total_pages+"**]");
                        String Left, Right;
                        Left = "⬅";
                        Right = "➡";
                        IMessage msg;
                        msg = channel.sendMessage(inv.build());
                        RequestBuffer.request(() -> msg.addReaction(ReactionEmoji.of(Left)));
                        Thread.sleep(5);
                        RequestBuffer.request(() -> msg.addReaction(ReactionEmoji.of(Right)));
                        ValidMessageIds.put(msg.getStringID(), user.getStringID());
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //DEBUGGING
        bot.addCommand(new Command() {
            @Override
            public String getLabel() {
                return "sqlCheck";
            }

            @Override
            public String getDescription() {
                return "Retrieves SQL Data for %Variable%";
            }

            @Override
            public Permissions getPermission() {
                return null;
            }

            @Override
            public List<String> Whitelist() {
                ArrayList<String> wl = new ArrayList<String>(
                        Arrays.asList("522104653790576652"));
                return wl;
            }

            @Override
            public boolean HasWhitelist() {
                return true;
            }

            @Override
            public String RequiredServer() {
                return null;
            }

            @Override
            public String RequiredChannel() {
                return null;
            }

            @Override
            public String[] AlternativeLabels() {
                return new String[0];
            }

            @Override
            public boolean DirectMessageCommand() {
                return false;
            }

            @Override
            public void runCommand(IUser user, IChannel channel, MessageReceivedEvent event, IGuild guild, String label, List<String> args) {
                if(args.get(0).equalsIgnoreCase("."+this.getLabel()))
                    return;
                if(args.get(0).equals(null))
                    return;
                EmbedBuilder eb = new EmbedBuilder();
                eb.withColor(Color.CYAN);
                eb.withTitle("SQL Evaluation Result");
                List<IUser> grabbed = event.getMessage().getMentions();
                if(grabbed.size() != 0) {
                    user = grabbed.get(0);
                }
                eb.withDesc(":asterisk: __Retreived__: **"+args.get(0)+"**\n:crown: __User__: [*"+user.getName()+"*]\n:asterisk: __Value__: **"+SQLH.GetValue(user, args.get(0)).toString()+"**");
                RequestBuffer.request(() -> channel.sendMessage(eb.build()));

            }
        }); // SQL CHECK
        bot.addCommand(new Command() {
            @Override
            public String getLabel() {
                return "addItem";
            }

            @Override
            public String getDescription() {
                return "Adds an item to a player.";
            }

            @Override
            public Permissions getPermission() {
                return null;
            }

            @Override
            public List<String> Whitelist() {
                ArrayList<String> wl = new ArrayList<String>(
                        Arrays.asList("522104653790576652"));
                return wl;
            }

            @Override
            public boolean HasWhitelist() {
                return true;
            }

            @Override
            public String RequiredServer() {
                return null;
            }

            @Override
            public String RequiredChannel() {
                return null;
            }

            @Override
            public String[] AlternativeLabels() {
                return new String[0];
            }

            @Override
            public boolean DirectMessageCommand() {
                return false;
            }

            @Override
            public void runCommand(IUser user, IChannel channel, MessageReceivedEvent event, IGuild guild, String label, List<String> args) {
                if(args.get(0).equalsIgnoreCase("."+this.getLabel()))
                    return;
                int amnt = 1;
                if(!args.get(1).equals(null))
                    amnt = Integer.parseInt(args.get(1));
                List<IUser> grabbed = event.getMessage().getMentions();
                if(grabbed.size() != 0) {
                    user = grabbed.get(0);
                }
                final IUser user_ = user;
                final int amnt_ = amnt;
                SQLH.addItem(user, args.get(0), amnt);
                RequestBuffer.request(() -> {
                    channel.sendMessage("Successfully added the item ["+args.get(0)+"]("+amnt_+") to "+user_.getName()+"!");
                });
            }
        }); // Add item
        bot.addCommand(new Command() {
            @Override
            public String getLabel() {
                return "removeItem";
            }

            @Override
            public String getDescription() {
                return "Removes an item to a player.";
            }

            @Override
            public Permissions getPermission() {
                return null;
            }

            @Override
            public List<String> Whitelist() {
                ArrayList<String> wl = new ArrayList<String>(
                        Arrays.asList("522104653790576652"));
                return wl;
            }

            @Override
            public boolean HasWhitelist() {
                return true;
            }

            @Override
            public String RequiredServer() {
                return null;
            }

            @Override
            public String RequiredChannel() {
                return null;
            }

            @Override
            public String[] AlternativeLabels() {
                return new String[0];
            }

            @Override
            public boolean DirectMessageCommand() {
                return false;
            }

            @Override
            public void runCommand(IUser user, IChannel channel, MessageReceivedEvent event, IGuild guild, String label, List<String> args) {
                if(args.get(0).equalsIgnoreCase("."+this.getLabel()))
                    return;
                int amnt = 1;
                if(!args.get(1).equals(null))
                    amnt = Integer.parseInt(args.get(1));
                List<IUser> grabbed = event.getMessage().getMentions();
                if(grabbed.size() != 0) {
                    user = grabbed.get(0);
                }
                final IUser user_ = user;
                final int amnt_ = amnt;
                SQLH.removeItem(user, args.get(0), amnt);
                RequestBuffer.request(() -> {
                    channel.sendMessage("Successfully removed the item ["+args.get(0)+"]("+amnt_+") to "+user_.getName()+"!");
                });
            }
        }); // Remove item
        bot.addCommand(new Command() {
            @Override
            public String getLabel() {
                return "roll";
            }

            @Override
            public String getDescription() {
                return "";
            }

            @Override
            public Permissions getPermission() {
                return null;
            }

            @Override
            public List<String> Whitelist() {
                ArrayList<String> wl = new ArrayList<String>(
                        Arrays.asList("522104653790576652"));
                return wl;
            }

            @Override
            public boolean HasWhitelist() {
                return true;
            }

            @Override
            public String RequiredServer() {
                return null;
            }

            @Override
            public String RequiredChannel() {
                return null;
            }

            @Override
            public String[] AlternativeLabels() {
                return new String[0];
            }

            @Override
            public boolean DirectMessageCommand() {
                return false;
            }

            @Override
            public void runCommand(IUser user, IChannel channel, MessageReceivedEvent event, IGuild guild, String label, List<String> args) {
                if(args.get(0).equals("."+this.getLabel()))
                    return;
                Random rdn = new Random(System.currentTimeMillis()*System.currentTimeMillis());
                double num = (double) rdn.nextInt(Integer.parseInt(args.get(0)));
                double numa = num;
                //String NumberList = "{"+(num/250);
                /*for (int i = 0; i < 101; i++) {
                    rdn = new Random((System.currentTimeMillis()+i)*(System.currentTimeMillis()+i));
                    num = rdn.nextInt(Integer.parseInt(args.get(0)));
                    NumberList = NumberList + ",\n"+(num/250);
                    numa=+num;
                }
                *///System.out.print(NumberList+"}\nAverage: "+(numa/250)+"\n");
                final double num_ = num;
                RequestBuffer.request(() -> channel.sendMessage(user.getName()+", you just rolled a "+ num_ + " out of "+args.get(0)));
            }
        });
        System.out.printf("[SYS MSG] %s - %s\n", "Attempting to initialize commands.", "LOADED COMMANDS");
    }
}