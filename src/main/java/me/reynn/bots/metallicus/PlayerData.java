package me.reynn.bots.metallicus;

import sx.blah.discord.handle.obj.IUser;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static me.reynn.bots.metallicus.Main.GetResultSet;

/**
 * Created by Aaron on 1/26/2019.
 */
public abstract class PlayerData {
    public static List<String> PlayerData(IUser user) {
        List<String> ToSend = new ArrayList<>();
        try {
            ResultSet Info = GetResultSet("SELECT * FROM profile_db WHERE id='" + user.getStringID() + "';");
            if(!Info.isBeforeFirst()) {
                //SQLHandler.UpdateData(user);
                Info = GetResultSet("SELECT * FROM profile_db WHERE id='" + user.getStringID() + "';");
                return null;
            }
            while(Info.next()) {
                for (int i = 0; i < 8; i++) {
                    ToSend.add(i, Info.getString(i+2));
                }
                /*
                    * ID
                    * Ethernal Bits
                    * Golden Bits
                    * Equipped JSON
                    * Inventory JSON
                    * Clan
                    * QN
                    * QP
                 */
            }
            return ToSend;
        } catch (Exception e) { }
        ToSend.add(0,"NO DATA");
        return ToSend;
    }
}
