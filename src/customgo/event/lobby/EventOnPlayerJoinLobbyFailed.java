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
	 * ��ȡ�������ҡ�
	 * @return ���
	 */
	public Player getPlayer(){
		return p;
	}
}
