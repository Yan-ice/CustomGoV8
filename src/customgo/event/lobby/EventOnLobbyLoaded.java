package customgo.event.lobby;

import fw.group.Lobby;

public final class EventOnLobbyLoaded extends EventLobby{
	boolean suc;
	public EventOnLobbyLoaded(Lobby g,boolean success){
		super(g);
		suc = success;
	}
	
	/**
	 * ������Ϸ�Ƿ��ȡ�ɹ���
	 * @return �Ƿ��ȡ�ɹ�
	 */
	public boolean isSucceed(){
		return suc;
	}
}
