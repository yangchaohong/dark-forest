package org.AbuzzHarbor9999.dark;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.TNT;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.*;
import java.io.*;
import com.google.gson.*;
import org.AbuzzHarbor9999.dark.libs;

public final class Main extends JavaPlugin implements Listener {
    public String config="";
    public static JsonObject json;
    public static File file=new File("plugins//darkconfig.json");
    public Date date = new Date();
    public JsonArray wmlist;
    public static civ[] wml =new civ[1024];
    public World world=Bukkit.getWorld("world");
    /**
     * 当插件被Load(加载)时执行的代码
     * getLogger().info() -> 代表或其控制台Log并且发送一行info信息
     */
    @Override
    public void onLoad() {
        getLogger().info("onLoad has been invoked!");

        if(file.exists()) {
            getLogger().info("文件已经存在");
        }else {
            try {
                file.createNewFile();
                JsonObject obj = new JsonObject();
                obj.addProperty("cnt", 0);
                JsonArray list=new JsonArray();
                obj.add("wmlist",list);
                getLogger().info(obj.toString());
                libs.writeJSON(obj);
                getLogger().info("文件创建成功");
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        try {
            FileInputStream f1=new FileInputStream(file);//这里需要进行抛出异常处理
            for (int i = 0; i < file.length(); i++) {
                char ch=(char)(f1.read());//循环读取字符
//                System.out.print(ch);
                config+=ch;
            }
            f1.close();//关闭文件
        } catch (Exception e) {
            // TODO: handle exception
            getLogger().info("文件打开失败");
        }
        getLogger().info(config);
        json = new JsonParser().parse(config).getAsJsonObject();
        wmlist= json.getAsJsonArray("wmlist");
        int cnt=json.get("cnt").getAsInt();
        for(int i=0;i<cnt;i++){
            civ temp=new civ();
            temp.fromJSON(json.getAsJsonArray("wmlist").get(i).getAsJsonObject());
            wml[i]=temp;
        }
    }

    /**
     * 当插件被Enable(开启)时执行的代码
     * getLogger().info() -> 代表或其控制台Log并且发送一行info信息
     */
    @Override
    public void onEnable() {
        getLogger().info("onEnable has been invoked!");
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    /**
     * 当插件被Disable(关闭)时执行的代码
     * getLogger().info() -> 代表或其控制台Log并且发送一行info信息
     */
    @Override
    public void onDisable() {
        getLogger().info("onDisable has been invoked!");
    }
    @Override
    public boolean onCommand(CommandSender s, Command cmd,String label,String[] args){
        if(cmd.getName().equalsIgnoreCase("isinwm")){
            if (!(s instanceof Player)) { // 判断输入者的类型 为了防止出现 控制台或命令方块 输入的情况
                s.sendMessage("你必须是一名玩家!");
                return true; // 这里返回true只是因为该输入者不是玩家,并不是输入错指令,所以我们直接返回true即可
            }
            int cnt=json.get("cnt").getAsInt();
            for (int i=0;i<cnt;i++){
                civ wm=new civ();
                wm.fromJSON(json.getAsJsonArray("wmlist").get(i).getAsJsonObject());
                getLogger().info(wm.name);
                if(wm.Isin(s.getName())){
                    s.sendMessage("你在"+wm.name);
                    return true;
                }
            }
            s.sendMessage("你没有加入任何 组织/文明！");
            return true;
        }
        if(cmd.getName().equalsIgnoreCase("Createwm")||cmd.getName().equalsIgnoreCase("cwm")){
            if(args.length==0){
                s.sendMessage("请输入你要创建的 组织/文明 的名称");
                return true;
            }
            for(int i=0;i<json.get("cnt").getAsInt();i++){
                if(wml[i].name==args[0]){
                    s.sendMessage("已经有（过）一个文明叫这个名字了");
                    return true;
                }
            }
            if (!(s instanceof Player)) { // 判断输入者的类型 为了防止出现 控制台或命令方块 输入的情况
                s.sendMessage("你必须是一名玩家!");
                return true; // 这里返回true只是因为该输入者不是玩家,并不是输入错指令,所以我们直接返回true即可
            }
            // 如果我们已经判断好sender是一名玩家之后,我们就可以将其强转为Player对象,把它作为一个"玩家"来处理
            Player player = (Player) s;
            if(json.get("cnt").getAsInt()>0)
                if(libs.hasbeenin(s.getName())){
                    s.sendMessage("你已经加入过某个 组织/文明 了！");
                    return true;
                }
            int cnt=json.get("cnt").getAsInt()+1;
            Block block = world.getBlockAt(player.getLocation().getBlockX(), player.getLocation().getBlockY()-1, player.getLocation().getBlockZ());
            BlockState state = block.getState();
            if(!state.getType().toString().equalsIgnoreCase("TNT")){
                s.sendMessage("你需要一个TNT放置在你的脚下 "+state.getType().toString());
                return true;
            }
            json.remove("cnt");
            json.addProperty("cnt",cnt);
            civ nwm=new civ();
            Location pos=player.getLocation();
            nwm.id=cnt-1;
            nwm.Isalive=true;
            nwm.owner=s.getName();
            nwm.startDate=date.toString();
            nwm.name=args[0];
            nwm.x=(int)pos.getBlockX();
            nwm.y=(int)pos.getBlockY()-1;
            nwm.z=(int)pos.getBlockZ();
            nwm.member[0]=s.getName();
//            TNT tnt=(TNT) state;
            json.addProperty("cnt", cnt);
            JsonObject wm = new JsonObject();
            wm.addProperty("id", nwm.id);
            wm.addProperty("name",nwm.name);
            wm.addProperty("owner", nwm.owner);
            wm.addProperty("Isalive",nwm.Isalive);
            wm.addProperty("startDate",nwm.startDate);
            wm.addProperty("x",nwm.x);
            wm.addProperty("y",nwm.y);
            wm.addProperty("z",nwm.z);
            JsonArray member=new JsonArray();
            member.add(s.getName());
            wm.add("member",member);
            wm.addProperty("memcnt",nwm.memcnt);
            wmlist.add(wm);
            json.add("wmlist",wmlist);
            libs.writeJSON(json);
            wml[cnt-1]=nwm;
            s.sendMessage("你创造了文明"+nwm.name);
            return true;
        }
        if(cmd.getName().equalsIgnoreCase("joinwm")||cmd.getName().equalsIgnoreCase("jwm")){
            if(args.length==0){
                s.sendMessage("请输入你要加入的 组织/文明 的名称");
                return true;
            }
            if (!(s instanceof Player)) { // 判断输入者的类型 为了防止出现 控制台或命令方块 输入的情况
                s.sendMessage("你必须是一名玩家!");
                return true; // 这里返回true只是因为该输入者不是玩家,并不是输入错指令,所以我们直接返回true即可
            }
            // 如果我们已经判断好sender是一名玩家之后,我们就可以将其强转为Player对象,把它作为一个"玩家"来处理
            Player player = (Player) s;
            if(libs.hasbeenin(s.getName())){
                s.sendMessage("你已经加入过某个 组织/文明 了！");
                return true;
            }
            int cnt=json.get("cnt").getAsInt(),id;
            civ wm=new civ();
            for(int i=0;i<cnt;i++){
                wm.fromJSON(json.getAsJsonArray("wmlist").get(i).getAsJsonObject());
                if(wm.name==args[0])
                    break;
            }
            if(!wm.Isalive){
                s.sendMessage("你在尝试加入一个已经毁灭的文明");
                return true;
            }
            if(libs.far(wm.x,wm.z,player.getLocation().getBlockX(),player.getLocation().getBlockZ())>50) {
                s.sendMessage("太远了");
                return true;
            }
            wml[wm.id].member[wml[wm.id].memcnt++]=s.getName();
            json.get("wmlist").getAsJsonArray().get(wm.id).getAsJsonObject().get("member").getAsJsonArray().add(s.getName());
            json.get("wmlist").getAsJsonArray().get(wm.id).getAsJsonObject().addProperty("memcnt",wm.memcnt+1);
            wmlist= json.getAsJsonArray("wmlist");
            libs.writeJSON(json);
            s.sendMessage("你加入了 组织/文明 "+wm.name);
            return true;
        }
        if(cmd.getName().equalsIgnoreCase("chatwm")){
            if(args.length==0){
                s.sendMessage("请输入聊天内容");
                return true;
            }
            int cnt = json.get("cnt").getAsInt();
            for(int i=0;i<cnt;i++){
                int memcnt=wml[i].memcnt;
                if(wml[i].Isin(s.getName()))
                    for(int j=0;j<memcnt;j++){
                        if(wml[i].member[j]!=s.getName()){
                            Player player=(Player)s;
                            player.performCommand("tell "+wml[i].member[j]+" "+args[0]);
                        }
                    }
            }
        }
        return false;
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(json.get("cnt").getAsInt()>0) {
            Player player = event.getPlayer();
//            player.sendMessage(Main.json.get("cnt").getAsInt()+"");
            int cnt = json.get("cnt").getAsInt();
            int x = (int) player.getLocation().getX();
            int z = (int) player.getLocation().getZ();
            for (int i = 0; i < cnt; i++) {
                if (libs.far(wml[i].x, wml[i].z, x, z) <= 200&&wml[i].Isalive) {
                    if (!wml[i].Isin(player.getName())) {
                        player.sendMessage("你靠近了 " + wml[i].name);
                    }
                }
            }
        }
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(json.get("cnt").getAsInt()>0) {
            int cnt=json.get("cnt").getAsInt();
            Block block=event.getBlock();
            BlockState state=block.getState();
            Location loc=state.getLocation();
            int x=loc.getBlockX();
            int y=loc.getBlockY();
            int z=loc.getBlockZ();
            if(state.getType().toString().equalsIgnoreCase("TNT")) {
                for(int i=0;i<cnt;i++) {
                    if(x==wml[i].x&&y==wml[i].y&&z==wml[i].z) {
                        event.getPlayer().sendMessage("你毁灭了 "+wml[i].name+" ！");
                        Bukkit.broadcastMessage("文明 "+wml[i].name+" 毁灭了！");
                        wml[i].Isalive=false;
                        wml[i].member=null;
                        wml[i].memcnt=0;
                        wml[i].deadDate=date.toString();
                        JsonArray temp=new JsonArray();
                        json.get("wmlist").getAsJsonArray().get(i).getAsJsonObject().addProperty("Isalive",false);
                        json.get("wmlist").getAsJsonArray().get(i).getAsJsonObject().add("member",temp);
                        json.get("wmlist").getAsJsonArray().get(i).getAsJsonObject().addProperty("memcnt",0);
                        json.get("wmlist").getAsJsonArray().get(i).getAsJsonObject().addProperty("deadDate",date.toString());
                        libs.writeJSON(json);
                        return;
                    }
                }
            }
        }
    }
    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event){
        if(json.get("cnt").getAsInt()>0) {
            int cnt=json.get("cnt").getAsInt();
            Block block=event.getBlock();
            BlockState state=block.getState();
            Location loc=state.getLocation();
            int x=loc.getBlockX();
            int y=loc.getBlockY();
            int z=loc.getBlockZ();
            if(state.getType().toString().equalsIgnoreCase("TNT")) {
                for(int i=0;i<cnt;i++) {
                    if(x==wml[i].x&&y==wml[i].y&&z==wml[i].z) {
                        Bukkit.broadcastMessage("文明 "+wml[i].name+" 毁灭了！");
                        wml[i].Isalive=false;
                        wml[i].member=null;
                        wml[i].memcnt=0;
                        wml[i].deadDate=date.toString();
                        JsonArray temp=new JsonArray();
                        json.get("wmlist").getAsJsonArray().get(i).getAsJsonObject().addProperty("Isalive",false);
                        json.get("wmlist").getAsJsonArray().get(i).getAsJsonObject().add("member",temp);
                        json.get("wmlist").getAsJsonArray().get(i).getAsJsonObject().addProperty("memcnt",0);
                        json.get("wmlist").getAsJsonArray().get(i).getAsJsonObject().addProperty("deadDate",date.toString());
                        libs.writeJSON(json);
                        return;
                    }
                }
            }
        }
    }
    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event){
        if(json.get("cnt").getAsInt()>0) {
            int cnt=json.get("cnt").getAsInt();
            Block block=event.getBlock();
            BlockState state=block.getState();
            Location loc=state.getLocation();
            int x=loc.getBlockX();
            int y=loc.getBlockY();
            int z=loc.getBlockZ();
            if(state.getType().toString().equalsIgnoreCase("TNT")) {
                for(int i=0;i<cnt;i++) {
                    if(x==wml[i].x&&y==wml[i].y&&z==wml[i].z) {
                        Bukkit.broadcastMessage("文明 "+wml[i].name+" 毁灭了！");
                        wml[i].Isalive=false;
                        wml[i].member=null;
                        wml[i].memcnt=0;
                        wml[i].deadDate=date.toString();
                        JsonArray temp=new JsonArray();
                        json.get("wmlist").getAsJsonArray().get(i).getAsJsonObject().addProperty("Isalive",false);
                        json.get("wmlist").getAsJsonArray().get(i).getAsJsonObject().add("member",temp);
                        json.get("wmlist").getAsJsonArray().get(i).getAsJsonObject().addProperty("memcnt",0);
                        json.get("wmlist").getAsJsonArray().get(i).getAsJsonObject().addProperty("deadDate",date.toString());
                        libs.writeJSON(json);
                        return;
                    }
                }
            }
        }
    }
    @EventHandler
    public void onBlockRedstone(BlockRedstoneEvent event){
        if(json.get("cnt").getAsInt()>0) {
            int cnt=json.get("cnt").getAsInt();
            Block block=event.getBlock();
            BlockState state=block.getState();
            Location loc=state.getLocation();
            int x=loc.getBlockX();
            int y=loc.getBlockY();
            int z=loc.getBlockZ();
            if(state.getType().toString().equalsIgnoreCase("TNT")) {
                for(int i=0;i<cnt;i++) {
                    if(x==wml[i].x&&y==wml[i].y&&z==wml[i].z) {
                        Bukkit.broadcastMessage("文明 "+wml[i].name+" 毁灭了！");
                        wml[i].Isalive=false;
                        wml[i].member=null;
                        wml[i].memcnt=0;
                        wml[i].deadDate=date.toString();
                        JsonArray temp=new JsonArray();
                        json.get("wmlist").getAsJsonArray().get(i).getAsJsonObject().addProperty("Isalive",false);
                        json.get("wmlist").getAsJsonArray().get(i).getAsJsonObject().add("member",temp);
                        json.get("wmlist").getAsJsonArray().get(i).getAsJsonObject().addProperty("memcnt",0);
                        json.get("wmlist").getAsJsonArray().get(i).getAsJsonObject().addProperty("deadDate",date.toString());
                        libs.writeJSON(json);
                        return;
                    }
                }
            }
        }
    }
    @EventHandler
    public void onLogin(PlayerJoinEvent event) {
        Player player=event.getPlayer();
//        getLogger().info("玩家"+player.getName()+"加入了游戏");
        player.sendMessage("欢迎来到黑暗森林服务器。\n“宇宙就是一座黑暗森林，每个文明都是带枪的猎人，像幽灵般潜行于林间……他必须小心， 因为林中到处都有与他一样潜行的猎人。在这片森林中，他人就是地狱，就是永恒的威胁，任何暴露自己存在的生命都将很快被消灭。”\n" );
        player.sendMessage("输入/isinwm查询自己在不在一个文明 在脚下放置TNT输入/Createwm <文明名称>来创建文明 在某文明中心50米以内输入/joinwm <文明名称>加入文明 输入/chatwm <聊天内容>向文明的其他成员发送私信 摧毁一个文明的中心TNT来毁灭这个文明");
    }
}
