package customgo.event.group;

import fw.group.Group;

public final class EventOnPlayerAmountChange extends EventGroup{
	public EventOnPlayerAmountChange(Group gro){
		super(gro);
	}
	/**
	 * ���ض����������ı��ʣ�����������
	 * @return ʣ�����
	 */
	public int RestPlayerAmount(){
		return gro.GetPlayerAmount();
	}
}
