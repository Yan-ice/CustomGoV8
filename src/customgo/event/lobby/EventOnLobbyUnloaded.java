package customgo.event.lobby;

import fw.group.Lobby;

public final class EventOnLobbyUnloaded extends EventLobby{
	boolean suc;
	public EventOnLobbyUnloaded(Lobby g,boolean success){
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
