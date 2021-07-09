package fw.player;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import fw.Data;
import fw.group.Group;
import fw.timer.BaseTimer;

public class OfflineListen extends BaseTimer{
	private static Set<OfflineListen> o = new HashSet<>();
	Group byGroup;
	Player pl;
	private boolean hasConnect = false;
	
	public OfflineListen(Player pl,Group byGroup) {
		this.byGroup = byGroup;
		this.pl = pl;
		setMaxTime(Data.optionFileConf.getInt("ReconnectTime"));
		o.add(this);
		Start();
	}
	public static void ReConnect(Player pl){
		for(OfflineListen x : o){
			if(x.pl.getName().equals(pl.getName())){
				x.ReConnect();
				return;
			}
		}
		pl.sendMessage(ChatColor.RED+"您当前没有可重新连接的游戏！");
	}
	public static OfflineListen getOfflineListener(Player pl){
		for(OfflineListen x : o){
			if(x.pl.getName().equals(pl.getName())){
				return x;
			}
		}
		return null;
	}
	public Group getGroup(){
		return byGroup;
	}
	public Player getPlayer(){
		return pl;
	}
	
	private void ReConnect(){
		byGroup.JoinGroup(pl,true);
		hasConnect = true;
		pl.sendMessage("您已重新连接。");
	}
	
	@Override
	public boolean ShutDown() {
		return hasConnect || byGroup.canJoin();
	}

	@Override
	public void whenShutDown() {
		o.remove(this);
	}

	@Override
	public void EveryTick() {
		
	}

	@Override
	public void TimeUp() {
		o.remove(this);
	}
}
