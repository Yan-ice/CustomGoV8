package fw.papi;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import fw.group.Lobby;
import me.clip.placeholderapi.external.EZPlaceholderHook;

public class ValuePlaceholder extends EZPlaceholderHook{
	Lobby lobby;
	public ValuePlaceholder(Plugin plugin, Lobby l) {
		super(plugin, "fw".concat(l.getName()));
		lobby = l;
		// TODO 自动生成的构造函数存根
	}

	public void CustomPlaceHolder(String key,String value){
		if(m.containsKey(key)){
			m.replace(key, value);
		}else{
			m.put(key, value);
		}
	}
	
	Map<String,String> m = new HashMap<>();
	
	@Override
	public String onPlaceholderRequest(Player arg0, String arg1) {
		// TODO 自动生成的方法存根
		if(lobby.isComplete()){
			if (arg1.equals("playeramount")) {
	            return lobby.getPlayerAmount()+""; //如果PAPI要的变量是 %customplaceholder_Vlele_hello% ，就会给你回复一个 大家好！！！
	        }
			if (arg1.equals("maxplayer")) {
	            return lobby.getDefaultGroup().getMaxPlayer()+""; //如果PAPI要的变量是 %customplaceholder_Vlele_hello% ，就会给你回复一个 大家好！！！
	        }
			if (arg1.equals("minplayer")) {
	            return lobby.getDefaultGroup().getMinPlayer()+""; //如果PAPI要的变量是 %customplaceholder_Vlele_hello% ，就会给你回复一个 大家好！！！
	        }
			if (arg1.equals("statu")) {
				if(lobby.canJoin()){
					return ChatColor.GREEN+"等待中";
				}else{
					return ChatColor.RED+"游戏中";
				}
	        }
			if (arg1.equals("time")) {
				if(lobby.canJoin()){
					return lobby.getDefaultGroup().getTimer().LobbyTimer().getTime()+"";
				}else{
					return lobby.getDefaultGroup().getTimer().ArenaTimer().getTime()+"";
				}
	        }
			for(String s : m.keySet()){
				if (arg1.equals(s)) {
					return m.get(s);
		        }
			}
			
			return "未知的命令";
		}else{
			return "未正常开启";
		}
		
	}

}
