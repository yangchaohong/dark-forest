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
     * �������Load(����)ʱִ�еĴ���
     * getLogger().info() -> ����������̨Log���ҷ���һ��info��Ϣ
     */
    @Override
    public void onLoad() {
        getLogger().info("onLoad has been invoked!");

        if(file.exists()) {
            getLogger().info("�ļ��Ѿ�����");
        }else {
            try {
                file.createNewFile();
                JsonObject obj = new JsonObject();
                obj.addProperty("cnt", 0);
                JsonArray list=new JsonArray();
                obj.add("wmlist",list);
                getLogger().info(obj.toString());
                libs.writeJSON(obj);
                getLogger().info("�ļ������ɹ�");
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        try {
            FileInputStream f1=new FileInputStream(file);//������Ҫ�����׳��쳣����
            for (int i = 0; i < file.length(); i++) {
                char ch=(char)(f1.read());//ѭ����ȡ�ַ�
//                System.out.print(ch);
                config+=ch;
            }
            f1.close();//�ر��ļ�
        } catch (Exception e) {
            // TODO: handle exception
            getLogger().info("�ļ���ʧ��");
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
     * �������Enable(����)ʱִ�еĴ���
     * getLogger().info() -> ����������̨Log���ҷ���һ��info��Ϣ
     */
    @Override
    public void onEnable() {
        getLogger().info("onEnable has been invoked!");
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    /**
     * �������Disable(�ر�)ʱִ�еĴ���
     * getLogger().info() -> ����������̨Log���ҷ���һ��info��Ϣ
     */
    @Override
    public void onDisable() {
        getLogger().info("onDisable has been invoked!");
    }
    @Override
    public boolean onCommand(CommandSender s, Command cmd,String label,String[] args){
        if(cmd.getName().equalsIgnoreCase("isinwm")){
            if (!(s instanceof Player)) { // �ж������ߵ����� Ϊ�˷�ֹ���� ����̨������� ��������
                s.sendMessage("�������һ�����!");
                return true; // ���ﷵ��trueֻ����Ϊ�������߲������,�����������ָ��,��������ֱ�ӷ���true����
            }
            int cnt=json.get("cnt").getAsInt();
            for (int i=0;i<cnt;i++){
                civ wm=new civ();
                wm.fromJSON(json.getAsJsonArray("wmlist").get(i).getAsJsonObject());
                getLogger().info(wm.name);
                if(wm.Isin(s.getName())){
                    s.sendMessage("����"+wm.name);
                    return true;
                }
            }
            s.sendMessage("��û�м����κ� ��֯/������");
            return true;
        }
        if(cmd.getName().equalsIgnoreCase("Createwm")||cmd.getName().equalsIgnoreCase("cwm")){
            if(args.length==0){
                s.sendMessage("��������Ҫ������ ��֯/���� ������");
                return true;
            }
            for(int i=0;i<json.get("cnt").getAsInt();i++){
                if(wml[i].name==args[0]){
                    s.sendMessage("�Ѿ��У�����һ�����������������");
                    return true;
                }
            }
            if (!(s instanceof Player)) { // �ж������ߵ����� Ϊ�˷�ֹ���� ����̨������� ��������
                s.sendMessage("�������һ�����!");
                return true; // ���ﷵ��trueֻ����Ϊ�������߲������,�����������ָ��,��������ֱ�ӷ���true����
            }
            // ��������Ѿ��жϺ�sender��һ�����֮��,���ǾͿ��Խ���ǿתΪPlayer����,������Ϊһ��"���"������
            Player player = (Player) s;
            if(json.get("cnt").getAsInt()>0)
                if(libs.hasbeenin(s.getName())){
                    s.sendMessage("���Ѿ������ĳ�� ��֯/���� �ˣ�");
                    return true;
                }
            int cnt=json.get("cnt").getAsInt()+1;
            Block block = world.getBlockAt(player.getLocation().getBlockX(), player.getLocation().getBlockY()-1, player.getLocation().getBlockZ());
            BlockState state = block.getState();
            if(!state.getType().toString().equalsIgnoreCase("TNT")){
                s.sendMessage("����Ҫһ��TNT��������Ľ��� "+state.getType().toString());
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
            s.sendMessage("�㴴��������"+nwm.name);
            return true;
        }
        if(cmd.getName().equalsIgnoreCase("joinwm")||cmd.getName().equalsIgnoreCase("jwm")){
            if(args.length==0){
                s.sendMessage("��������Ҫ����� ��֯/���� ������");
                return true;
            }
            if (!(s instanceof Player)) { // �ж������ߵ����� Ϊ�˷�ֹ���� ����̨������� ��������
                s.sendMessage("�������һ�����!");
                return true; // ���ﷵ��trueֻ����Ϊ�������߲������,�����������ָ��,��������ֱ�ӷ���true����
            }
            // ��������Ѿ��жϺ�sender��һ�����֮��,���ǾͿ��Խ���ǿתΪPlayer����,������Ϊһ��"���"������
            Player player = (Player) s;
            if(libs.hasbeenin(s.getName())){
                s.sendMessage("���Ѿ������ĳ�� ��֯/���� �ˣ�");
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
                s.sendMessage("���ڳ��Լ���һ���Ѿ����������");
                return true;
            }
            if(libs.far(wm.x,wm.z,player.getLocation().getBlockX(),player.getLocation().getBlockZ())>50) {
                s.sendMessage("̫Զ��");
                return true;
            }
            wml[wm.id].member[wml[wm.id].memcnt++]=s.getName();
            json.get("wmlist").getAsJsonArray().get(wm.id).getAsJsonObject().get("member").getAsJsonArray().add(s.getName());
            json.get("wmlist").getAsJsonArray().get(wm.id).getAsJsonObject().addProperty("memcnt",wm.memcnt+1);
            wmlist= json.getAsJsonArray("wmlist");
            libs.writeJSON(json);
            s.sendMessage("������� ��֯/���� "+wm.name);
            return true;
        }
        if(cmd.getName().equalsIgnoreCase("chatwm")){
            if(args.length==0){
                s.sendMessage("��������������");
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
                        player.sendMessage("�㿿���� " + wml[i].name);
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
                        event.getPlayer().sendMessage("������� "+wml[i].name+" ��");
                        Bukkit.broadcastMessage("���� "+wml[i].name+" �����ˣ�");
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
                        Bukkit.broadcastMessage("���� "+wml[i].name+" �����ˣ�");
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
                        Bukkit.broadcastMessage("���� "+wml[i].name+" �����ˣ�");
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
                        Bukkit.broadcastMessage("���� "+wml[i].name+" �����ˣ�");
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
//        getLogger().info("���"+player.getName()+"��������Ϸ");
        player.sendMessage("��ӭ�����ڰ�ɭ�ַ�������\n���������һ���ڰ�ɭ�֣�ÿ���������Ǵ�ǹ�����ˣ��������Ǳ�����ּ䡭��������С�ģ� ��Ϊ���е�����������һ��Ǳ�е����ˡ�����Ƭɭ���У����˾��ǵ����������������в���κα�¶�Լ����ڵ����������ܿ챻���𡣡�\n" );
        player.sendMessage("����/isinwm��ѯ�Լ��ڲ���һ������ �ڽ��·���TNT����/Createwm <��������>���������� ��ĳ��������50����������/joinwm <��������>�������� ����/chatwm <��������>��������������Ա����˽�� �ݻ�һ������������TNT�������������");
    }
}
