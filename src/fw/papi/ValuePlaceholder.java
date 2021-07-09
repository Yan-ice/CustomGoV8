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
		// TODO �Զ����ɵĹ��캯�����
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
		// TODO �Զ����ɵķ������
		if(lobby.isComplete()){
			if (arg1.equals("playeramount")) {
	            return lobby.getPlayerAmount()+""; //���PAPIҪ�ı����� %customplaceholder_Vlele_hello% ���ͻ����ظ�һ�� ��Һã�����
	        }
			if (arg1.equals("maxplayer")) {
	            return lobby.getDefaultGroup().getMaxPlayer()+""; //���PAPIҪ�ı����� %customplaceholder_Vlele_hello% ���ͻ����ظ�һ�� ��Һã�����
	        }
			if (arg1.equals("minplayer")) {
	            return lobby.getDefaultGroup().getMinPlayer()+""; //���PAPIҪ�ı����� %customplaceholder_Vlele_hello% ���ͻ����ظ�һ�� ��Һã�����
	        }
			if (arg1.equals("statu")) {
				if(lobby.canJoin()){
					return ChatColor.GREEN+"�ȴ���";
				}else{
					return ChatColor.RED+"��Ϸ��";
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
			
			return "δ֪������";
		}else{
			return "δ��������";
		}
		
	}

}
