package customgo.event.group;

import fw.group.Group;

public final class EventOnGroupLoaded extends EventGroup{
	boolean suc;
	public EventOnGroupLoaded(Group gro,boolean success){
		super(gro);
		suc = success;
	}
	/**
	 * ���ض����Ƿ��ȡ�ɹ���
	 * @return �Ƿ��ȡ�ɹ�
	 */
	public boolean isSucceed(){
		return suc;
	}
}
