package customgo.event.lobby;

import fw.group.Lobby;

public abstract class EventLobby {
	protected Lobby gro;
	public EventLobby(Lobby gro){
		this.gro = gro;
	}
	
	/**
	 * ���ش����¼�����Ϸ��
	 * @return ��Ϸ
	 */
	public Lobby getLobby(){
		return gro;
	}

}
