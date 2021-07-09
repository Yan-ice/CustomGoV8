package customgo.event.lobby;

import org.bukkit.entity.Player;

import fw.group.Lobby;

public final class EventOnPlayerJoinLobbyFailed extends EventLobby{

	private Player p;
	public EventOnPlayerJoinLobbyFailed(Lobby gro,Player p){
		super(gro);
		this.p = p;
	}
	
	/**
	 * 获取加入的玩家。
	 * @return 玩家
	 */
	public Player getPlayer(){
		return p;
	}
}
