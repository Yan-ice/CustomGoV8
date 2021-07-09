package customgo.event.lobby;

import fw.group.Lobby;

public abstract class EventLobby {
	protected Lobby gro;
	public EventLobby(Lobby gro){
		this.gro = gro;
	}
	
	/**
	 * 返回触发事件的游戏。
	 * @return 游戏
	 */
	public Lobby getLobby(){
		return gro;
	}

}
