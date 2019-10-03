package me.reynn.bots.metallicus;

import org.json.JSONObject;
import sx.blah.discord.handle.obj.IUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static me.reynn.bots.metallicus.Main.GetResultSet;
import static me.reynn.bots.metallicus.Main.con;

/**
 * Created by Aaron on 1/26/2019.
 */
public class SQLHandler {
    public static Connection con = Main.con;

    public void RemoveFrom(IUser user, String Var, int Amount) {
        try {
            ResultSet Data = GetResultSet("SELECT "+Var+" FROM profile_db WHERE id = '"+user.getStringID()+"';");
            if(Data.isBeforeFirst()) {
                long Value = 0;
                while(Data.next()) { Value = Long.parseLong(Data.getString(1)); }
                Value -= Amount;
                if(Value < 0)
                    Value = 0;
                PreparedStatement removeUpdate = con.prepareStatement("UPDATE profile_db SET " + Var + " = '" + Value + "' WHERE id = '" + user.getStringID() + "';");
                removeUpdate.executeUpdate();
                removeUpdate.close();
                System.out.printf("[%s]: %s", "MYSQL", "Changed Value: [" + Var + " By -" + Amount + "] REQUEST_SEED: "+user.getStringID()+"\n");
            }
        } catch(Exception e) {}
    }

    public void AddTo(IUser user, String Var, int Amount) {
        try {
            ResultSet Data = GetResultSet("SELECT "+Var+" FROM profile_db WHERE id = '"+user.getStringID()+"';");
            if(Data.isBeforeFirst()) {
                long Value = 0;
                while(Data.next()) { Value = Long.parseLong(Data.getString(1)); }
                Value += Amount;
                if(Value < 0 || Value == Long.MAX_VALUE)
                    Value = 0;
                PreparedStatement addUpdate = con.prepareStatement("UPDATE profile_db SET " + Var + " = '" + Value + "' WHERE id = '" + user.getStringID() + "';");
                addUpdate.executeUpdate();
                addUpdate.close();
                System.out.printf("[%s]: %s", "MYSQL", "Changed Value: [" + Var + " By " + Amount + "] REQUEST_SEED: "+user.getStringID()+"\n");
            }
        } catch(Exception e) {}
    }

    public void SetTo(IUser user, String Var, int Amount) {
        try {
            ResultSet Data = GetResultSet("SELECT "+Var+" FROM profile_db WHERE id = '"+user.getStringID()+"';");
            if(Data.isBeforeFirst()) {
                PreparedStatement removeUpdate = con.prepareStatement("UPDATE profile_db SET " + Var + " = '" + Amount + "' WHERE id = '" + user.getStringID() + "';");
                removeUpdate.executeUpdate();
                removeUpdate.close();
                System.out.printf("[%s]: %s", "MYSQL", "Changed Value: [" + Var + " To " + Amount + "] REQUEST_SEED: "+user.getStringID()+"\n");
            }
        } catch(Exception e) {}
    }

    public void SetTo(IUser user, String Var, String Amount) {
        try {
            ResultSet Data = GetResultSet("SELECT "+Var+" FROM profile_db WHERE id = '"+user.getStringID()+"';");
            if(Data.isBeforeFirst()) {
                PreparedStatement removeUpdate = con.prepareStatement("UPDATE profile_db SET " + Var + " = '" + Amount + "' WHERE id = '" + user.getStringID() + "';");
                removeUpdate.executeUpdate();
                removeUpdate.close();
                System.out.printf("[%s]: %s", "MYSQL", "Changed Value: [" + Var + " To " + Amount + "] REQUEST_SEED: "+user.getStringID()+"\n");
            }
        } catch(Exception e) {}
    }

    public List<String> GetValue(IUser user, String Variable) {
       List<String> Return_Data = new ArrayList<>();
       try {
        ResultSet Data = GetResultSet("SELECT "+Variable+" FROM profile_db WHERE id = '"+user.getStringID()+"';");
        if(Data.isBeforeFirst()) {
            while(Data.next())
                Return_Data.add(0, Data.getString(1));
        }
        if(Return_Data.size() == 0)
            Return_Data.add(0, "NOTHING RETRIEVED!");
       } catch (Exception e) {}
       return Return_Data;
    }

    public JSONObject getInventory(IUser user) {
        JSONObject invJSON = new JSONObject();
        try {
            ResultSet inv = Main.GetResultSet("SELECT inventory FROM profile_db WHERE id='"+user.getStringID()+"';");
            if(inv.isBeforeFirst())
                while(inv.next()) {
                    invJSON = new JSONObject(inv.getString(1));
                }
        } catch (Exception e){};
        return invJSON;
    }

    public void setInventory(IUser user, JSONObject inv) {
        try {
            PreparedStatement pstmt = con.prepareStatement("UPDATE profile_db SET inventory='"+inv.toString()+"' WHERE id='"+user.getStringID()+"';");
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) { e.printStackTrace(); }
    }


    public void addItem(IUser user, String ItemId, int Amnt) {
        JSONObject invJSON = getInventory(user);
        if(invJSON.has(ItemId)) {
            invJSON.put(ItemId, Amnt + invJSON.getInt(ItemId));
        } else {
            invJSON.put(ItemId, Amnt);
        }
        setInventory(user, invJSON);
    }

    public void removeItem(IUser user, String ItemId, int Amnt) {
        JSONObject invJSON = getInventory(user);
        if(invJSON.has(ItemId)) {
            invJSON.put(ItemId, invJSON.getInt(ItemId) - Amnt);
            if(invJSON.getInt(ItemId) == 0) {
                invJSON.remove(ItemId);
            }
        }
        setInventory(user, invJSON);
    }

    public JSONObject getItemInfo(String ItemId) {
        JSONObject return_ = new JSONObject();
        try {
            ResultSet Data = GetResultSet("SELECT * FROM item_db WHERE entry = '"+ItemId+"';");
            while(Data.next()) {
                return_.put("Name", Data.getString(2));
                return_.put("Description", Data.getString(3));
                return_.put("Rarity", Data.getString(4));
            }
        } catch (Exception e) {}
        return return_;
    }

    public long getTimer(IUser user) {
        long time = 0;
        try {
            ResultSet Data = GetResultSet("SELECT mine_timer FROM profile_db WHERE id = '"+user.getStringID()+"';");
            if(Data.isBeforeFirst())
                while(Data.next())
                    time = Long.parseLong(Data.getString(1));
        } catch(Exception e) {}
        return time;
    }

    public void updateTimer(IUser user, String time) {
        try {
            PreparedStatement pstmt = con.prepareStatement("UPDATE profile_db SET mine_timer='" + time + "' WHERE id='" + user.getStringID() + "';");
            pstmt.executeUpdate();
            pstmt.close();
        } catch(Exception e) {e.printStackTrace();}
    }

    public static void UpdateData(IUser user) {
        try {
            PreparedStatement pstmt = con.prepareStatement("INSERT INTO profile_db (id, golden_bits, ethereal_bits, equipped, inventory) VALUES (?, ?, ?, ?, ?)");
            pstmt.setString(1, user.getStringID());
            pstmt.setString(2, Long.toString(500));
            pstmt.setString(3, Long.toString(25));
            JSONObject EmptyJson = new JSONObject();

            pstmt.setString(4, EmptyJson.toString()); // EQUIPPED
            pstmt.setString(5, EmptyJson.toString()); // INVENTORY

            pstmt.executeUpdate();
            pstmt.close();
            System.out.printf("[%s]: %s", "MYSQL", "MADE DATA FOR: ["+user.getStringID()+" | "+user.getName()+"] REQUEST_SEED: "+user.getStringID()+"\n");
        } catch (Exception e) { e.printStackTrace();}
    }
}
