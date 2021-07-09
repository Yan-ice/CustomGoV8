package customgo.event.lobby;

import fw.group.Lobby;

public final class EventOnLobbyLoaded extends EventLobby{
	boolean suc;
	public EventOnLobbyLoaded(Lobby g,boolean success){
		super(g);
		suc = success;
	}
	
	/**
	 * 返回游戏是否读取成功。
	 * @return 是否读取成功
	 */
	public boolean isSucceed(){
		return suc;
	}
}
