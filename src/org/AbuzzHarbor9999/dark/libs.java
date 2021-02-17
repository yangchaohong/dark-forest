package org.AbuzzHarbor9999.dark;
import com.google.gson.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.*;

import static org.bukkit.Bukkit.getLogger;

public class libs {
    public static String toPrettyFormat(String json) {
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(jsonObject);
    }
    public static void writeJSON(JsonObject json){
        FileOutputStream f1= null;
        try {
            f1 = new FileOutputStream(Main.file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        byte[] buff=libs.toPrettyFormat(json.toString()).getBytes();//将字符串转换为字节数组
        try {
            f1.write(buff);//把字节数组的内容写进去文件
        } catch (Exception e) {
            // TODO: handle exception
        }finally {
            try {
                f1.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        getLogger().info("文件写入成功");
    }
    public static boolean hasbeenin(String name){
        boolean flag=false;
        int cnt=Main.json.get("cnt").getAsInt();
//        getLogger().info(Main.json.get("cnt").getAsInt()+"");
        if(cnt==0)
            return false;
        for (int i=0;i<cnt;i++){
            civ wm=new civ();
            wm.fromJSON(Main.json.getAsJsonArray("wmlist").get(i).getAsJsonObject());
            getLogger().info(wm.name);
            if(wm.Isin(name)){
                flag=true;
            }
        }
        return flag;
    }
    public static int far(int x1,int y1,int x2,int y2){
//        getLogger().info(Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2))+"");
        return (int) Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
    }
    public static void UpdateJson(){
        int cnt=Main.json.get("cnt").getAsInt();
        for(int i=0;i<cnt;i++){
            civ temp=new civ();
            temp.fromJSON(Main.json.getAsJsonArray("wmlist").get(i).getAsJsonObject());
            Main.wml[i]=temp;
        }
    }
}
