package customgo.event.group;

import fw.group.Group;

public abstract class EventGroup {
	protected Group gro;
	public EventGroup(Group gro){
		this.gro = gro;
	}
	
	/**
	 * ���ش����¼��Ķ��С�
	 * @return ����
	 */
	public Group getGroup(){
		return gro;
	}

}
