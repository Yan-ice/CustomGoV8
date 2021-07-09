package customgo.event.group;

import org.bukkit.entity.Player;

import fw.group.Group;

public final class EventOnPlayerLeaveGroup extends EventGroup{
	private boolean waiting = false;
	private Player p;
	public EventOnPlayerLeaveGroup(Group gro,Player p,boolean inWaiting){
		super(gro);
		this.p = p;
		this.waiting = inWaiting;
	}
	
	/**
	 * �ж�����뿪ʱ����Ϸ�Ƿ��ڵȴ���
	 * @return ��Ϸ�Ƿ��ڵȴ�
	 */
	public boolean isInWaiting(){
		return waiting;
	}

	/**
	 * ��ȡ�뿪����ҡ�
	 * @return ���
	 */
	public Player getPlayer(){
		return p;
	}
}
