package me.reynn.bots.metallicus.Assets;

import me.reynn.bots.metallicus.BotUtils;
import me.reynn.bots.metallicus.Main;
import me.reynn.bots.metallicus.PlayerData;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.AttributedCharacterIterator;
import java.util.*;
import java.util.List;

/**
 * Created by Aaron on 9/14/2018.
 */
public class ImageGenerator {

    public static BufferedImage imageFor(String url) throws IOException {
        if (url == null)
            return null;
        URL urll = new URL(url);
        URLConnection connection = urll.openConnection();
        connection.addRequestProperty("User-Agent", "Mozilla/5.0 Apache the Attack Helicopter");
        connection.connect();
        return ImageIO.read(connection.getInputStream());
    }

    public static byte[] Profile(IUser user) {
        final int width = 512;
        final int height = 512;
        File resourcesDirectory = new File("src/main/java/me/reynn/bots/metallicus/Assets");
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        String nameOfProfile = "profiletest";

        if(user.getStringID().equalsIgnoreCase(BotUtils.BO_ID))
            nameOfProfile = "profilecreator";

        Image background = null;
        try {
            background = ImageIO.read(new File(resourcesDirectory.getAbsolutePath() + "/"+nameOfProfile+ ".png"));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        background = background.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        graphics.drawImage(background, 0, 0, width, height, null);

        Image avatar = null;
        try {
            BufferedImage temp = imageFor(user.getAvatarURL().replaceAll(".webp", ".png"));
            avatar = temp.getScaledInstance(105, 105, Image.SCALE_SMOOTH);
        } catch(Exception e) {
            e.printStackTrace();
        }

        graphics.setClip(new Rectangle2D.Double(203, 150, 105, 105));
        graphics.drawImage(avatar, 203, 150, 105, 105, null);
        graphics.setClip(null);

        List<String> pd = PlayerData.PlayerData(user);
        Font font = new Font("Arial", Font.BOLD, 16);
        graphics.setFont(font);
        graphics.setColor(new Color(220, 220, 220));
        int stringLen = (int)
                graphics.getFontMetrics(font).getStringBounds(user.getName(), graphics).getWidth();

        if(user.getStringID().equalsIgnoreCase(BotUtils.BO_ID)){
            graphics.setColor(new Color(20, 20, 20));
            graphics.drawString(user.getName(), ((width / 12) - (stringLen / 2)) + 215.5f, 278.5f);
            GradientPaint gp = new GradientPaint(
                    ((width / 12) - (stringLen / 2)) + 215,
                    275,
                    new Color(60, 60, 170),
                    ((width / 2) - (stringLen / 2)) + 195,
                    275,
                    new Color(60, 240, 150));
            graphics.setPaint(gp);
        }

        graphics.drawString(user.getName(), ((width / 12) - (stringLen / 2)) + 215, 278);

        graphics.setColor(new Color(210, 185, 25));
        //graphics.drawString("$"+pd.get(2), 45, 240);


        graphics.dispose();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "png", bytes);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return bytes.toByteArray();
    }
}