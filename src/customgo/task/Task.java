package customgo.task;

import org.bukkit.entity.Player;

import fw.group.Group;

public interface Task{
	/**
	 * ע������Զ���Task�ĸ�ָ�
	 * �����õ�Task���ָø�ָ��ɵ���TaskListen��
	 * ע�ⲻ����ԭ��ָ���ظ���Ҳ���ܺ��������ע��ĸ�ָ���ظ���
	 * @return ������ע��ĸ�ָ��
	 */
	String identity();
	
	/*
	 * �ýӿ����ڽ������Զ��з��͵�Task���ݣ�
	 * ������������д���
	 */
	void TaskListen(Group byGroup,String value,Player Target);
}
