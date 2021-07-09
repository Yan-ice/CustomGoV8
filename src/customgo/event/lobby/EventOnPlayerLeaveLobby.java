package customgo.event.lobby;

import org.bukkit.entity.Player;

import fw.group.Lobby;

public final class EventOnPlayerLeaveLobby extends EventLobby{
	private boolean waiting = false;
	private Player p;
	public EventOnPlayerLeaveLobby(Lobby gro,Player p,boolean inWaiting){
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
