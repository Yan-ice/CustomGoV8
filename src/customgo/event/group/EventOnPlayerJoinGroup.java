package customgo.event.group;


import org.bukkit.entity.Player;

import fw.group.Group;

public final class EventOnPlayerJoinGroup extends EventGroup{
	private boolean cancel = false;
	private Player p;
	public EventOnPlayerJoinGroup(Group gro,Player p){
		super(gro);
		this.p = p;
	}
	
	/**
	 * 该玩家的加入是否被阻止。
	 * @return 玩家是否被阻止加入
	 */
	public boolean isPrevented(){
		return cancel;
	}
	
	/**
	 * 设置阻止玩家加入。
	 * @param c 玩家是否应被阻止加入
	 */
	public void setPrevent(boolean c){
		cancel = c;
	}
	/**
	 * 获取加入的玩家。
	 * @return 玩家
	 */
	public Player getPlayer(){
		return p;
	}
}
