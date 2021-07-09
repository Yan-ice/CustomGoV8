package fw;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import fw.Data;
import fw.group.Lobby;
import fw.location.FLocation;

public class JoinSign implements Listener,ConfigurationSerializable{
	private static File SignDir = new File(Data.fmain.getDataFolder(),"sign");
	
	String name;
	FLocation Loc;
	List<String> info;
	Lobby divider;
	
	boolean isLoad = false;
	static Set<JoinSign> signlist = new HashSet<>();
	public boolean isLoad(){
		return isLoad;
	}
	
	public static void RefreshSign(Lobby by){
		for(JoinSign s : signlist){
			if(s.divider==by){
				s.LoadSign();
			}
		}
	}
/**
 * ����/�����������õ����ӡ�
 */
	public static List<JoinSign> DisposeAllSign(){
		signlist.clear();
		if (!SignDir.exists()) {
			SignDir.mkdir();
		}
		File[] a = SignDir.listFiles();
		for(File f : a){
			FileConfiguration config = Data.fmain.load(f);
			signlist.add((JoinSign)config.get("Data"));
		}
		return null;
	}
	
	public static List<JoinSign> SaveAllSign(){
		if (!SignDir.exists()) {
			SignDir.mkdir();
		}
		for(JoinSign sign : signlist){
			File f = new File(SignDir, sign.name+".yml");
			if(f.exists()){
				f.delete();
			}
			try {
				f.createNewFile();
				FileConfiguration fi = YamlConfiguration.loadConfiguration(f);
				fi.set("Data", sign);
				fi.save(f);
			} catch (IOException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
		}
		signlist.clear();
		return null;
	}
	public JoinSign(String name,FLocation Loc,Lobby divider,List<String> info) {
		this.info = info;
		this.name = name;
		this.divider = divider;
		this.Loc = Loc;
		if(LoadSign()){
			Data.Debug("����"+name+"��ȡ�ɹ���");
		}else{
			Data.Debug("����"+name+"��ȡʧ�ܡ�");
		}
	}
	
	@SuppressWarnings("unchecked")
	public JoinSign(Map<String,Object> map) {
		if(map.containsKey("Location")){
			Loc = new FLocation((String)map.get("Location"),null);
		}
		if(map.containsKey("Info")){
			info = (List<String>)map.get("Info");
			while(info.size()>3){
				info.remove(info.size()-1);
			}
		}else{
			info.add("&l��ǰ����: <pl>");
			info.add("&4��ӭ���棡~");
			info.add("<cjoin>");
		}
		if(map.containsKey("Join")){
			if(Lobby.getLobby((String)map.get("Join"))!=null){
				divider = Lobby.getLobby((String)map.get("Join"));
			}
		}
		if(map.containsKey("Name")){
			name = (String) map.get("Name");
		}
		if(LoadSign()){
			Data.Debug("����"+name+"��ȡ�ɹ���");
		}else{
			Data.Debug("����"+name+"��ȡʧ�ܡ�");
		}
	}
/**
 * ���¶�ȡľ�Ʋ�ˢ��ľ�Ƶ��֡�
 * @return
 */
	public boolean LoadSign() {
		HandlerList.unregisterAll(this);
		if(signlist.contains(this)){
			signlist.remove(this);
		}
		if (Loc == null || !Loc.isComplete()) {
			isLoad = false;
			return false;
		}
		if(divider==null || !divider.isComplete()){
			isLoad = false;
			return false;
		}
		if (Loc.ToMC(null).getBlock().getState() instanceof Sign) {
			Sign sign = (Sign) Loc.ToMC(null).getBlock().getState();
			sign.setLine(0, ChatColor.BLUE + divider.getDefaultGroup().GetDisplay()+"["+name+"]");
			sign.setLine(1,valueChange(info.get(0)));
			sign.setLine(2, valueChange(info.get(1)));
			sign.setLine(3, valueChange(info.get(2)));
			sign.update();
			Data.fmain.getServer().getPluginManager().registerEvents(this, Data.fmain);
			signlist.add(this);
			isLoad = true;
			return true;
		} else {
			isLoad = false;
			return false;
		}
	}
	
	private String valueChange(String value){
		value = value.replace("&", "��");
		value = value.replaceAll("<pl>", divider.getPlayerAmount()+"");
		if(divider.canJoin()){
			value = value.replaceAll("<cjoin>", ChatColor.DARK_BLUE+"�ȴ���");
		}else{
			value = value.replaceAll("<cjoin>", ChatColor.DARK_RED+"��Ϸ��");
		}
		
		return value;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void BreakListen(BlockBreakEvent evt) {
		if(evt.isCancelled()){
			return;
		}
		if ((new FLocation(evt.getBlock().getLocation())).equals(Loc)) {
			HandlerList.unregisterAll(this);
			isLoad = false;
			signlist.remove(this);
			if(getFile()!=null && getFile().exists()){
				getFile().delete();
			}
			evt.getPlayer().sendMessage("��������ɾ���ɹ���");
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void InteractListen(PlayerInteractEvent evt) {
		if (evt.getClickedBlock() != null && evt.getClickedBlock().getState() != null
				&& evt.getClickedBlock().getState() instanceof Sign) {
			if ((new FLocation(evt.getClickedBlock().getState().getLocation())).equals(Loc)) {
				if(evt.getAction()==Action.RIGHT_CLICK_BLOCK){
					if (isLoad) {
						divider.Join(evt.getPlayer());
						LoadSign();
					} else {
						evt.getPlayer().sendMessage(ChatColor.RED +"������û�б�������ȡ��");
					}
				}
			}
		}

	}
	
	@Override
	public Map<String, Object> serialize() {
		// TODO �Զ����ɵķ������
		Map<String, Object> map = new HashMap<>();
		map.put("Location", Loc.ToString());
		map.put("Info", info);
		if(divider!=null){
			map.put("Join", divider.getName());
		}else{
			map.put("Join", "none");
		}
		map.put("Name", name);
		return map;
	}
	
	private File getFile(){
		File[] f = SignDir.listFiles();
		for(int a=0;a<f.length;a++){
			if(f[a].getName().equals(name+".yml")){
				return f[a];
			}
		}
		return null;
	}
}
