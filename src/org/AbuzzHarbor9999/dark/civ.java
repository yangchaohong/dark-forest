package org.AbuzzHarbor9999.dark;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import static org.bukkit.Bukkit.getLogger;

public class civ {
    public int id;
    public String name;
    public String owner;
    public String startDate;
    public String deadDate=null;
    public boolean Isalive;
    public int x;
    public int y;
    public int z;
    public String member[]=new String[256];
    public int memcnt=1;
    public void fromJSON(JsonObject json){
        id=json.get("id").getAsInt();
        name=json.get("name").getAsString();
        owner=json.get("owner").getAsString();
        startDate=json.get("startDate").getAsString();
        Isalive=json.get("Isalive").getAsBoolean();
        x= json.get("x").getAsInt();
        y= json.get("y").getAsInt();
        z= json.get("z").getAsInt();
        memcnt= json.get("memcnt").getAsInt();
        for(int i=0;i<memcnt;i++)
            member[i]=json.getAsJsonArray("member").get(i).getAsString();
    }
    public boolean Isin(String player){
        boolean flag=false;
//        getLogger().info(player);
        for(int i=0;i<memcnt;i++) {
//            getLogger().info(member[i]);
            if (member[i].equalsIgnoreCase(player))
                flag = true;
        }
        return flag;
    }
    public JsonObject toJSON() {
        JsonObject wm=new JsonObject();
        wm.addProperty("id", id);
        wm.addProperty("name",name);
        wm.addProperty("owner", owner);
        wm.addProperty("Isalive",Isalive);
        wm.addProperty("startDate",startDate);
        wm.addProperty("deadDate",deadDate);
        wm.addProperty("x",x);
        wm.addProperty("y",y);
        wm.addProperty("z",z);
        wm.addProperty("memcnt",memcnt);
        JsonArray mem=new JsonArray();
        for(int i=0;i<memcnt;i++) {
            mem.add(member[i]);
        }
        wm.add("member",mem);
        return wm;
    }
}
