package customgo.event.lobby;

import org.bukkit.entity.Player;

import fw.group.Lobby;

public final class EventOnPlayerJoinLobby extends EventLobby{
	private boolean cancel = false;
	private Player p;
	public EventOnPlayerJoinLobby(Lobby gro,Player p){
		super(gro);
		this.p = p;
	}
	
	/**
	 * ����ҵļ����Ƿ���ֹ��
	 * @return ����Ƿ���ֹ����
	 */
	public boolean isPrevented(){
		return cancel;
	}
	
	/**
	 * ������ֹ��Ҽ��롣
	 * @param c ����Ƿ�Ӧ����ֹ����
	 */
	public void setPrevent(boolean c){
		cancel = c;
	}
	/**
	 * ��ȡ�������ҡ�
	 * @return ���
	 */
	public Player getPlayer(){
		return p;
	}
}
