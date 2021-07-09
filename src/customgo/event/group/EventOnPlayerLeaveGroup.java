package customgo.event.group;

import org.bukkit.entity.Player;

import fw.group.Group;

public final class EventOnPlayerLeaveGroup extends EventGroup{
	private boolean waiting = false;
	private Player p;
	public EventOnPlayerLeaveGroup(Group gro,Player p,boolean inWaiting){
		super(gro);
		this.p = p;
		this.waiting = inWaiting;
	}
	
	/**
	 * 判断玩家离开时，游戏是否在等待。
	 * @return 游戏是否在等待
	 */
	public boolean isInWaiting(){
		return waiting;
	}

	/**
	 * 获取离开的玩家。
	 * @return 玩家
	 */
	public Player getPlayer(){
		return p;
	}
}
