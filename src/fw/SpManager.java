package fw;

import customgo.listener.GroupListener;
import customgo.listener.LobbyListener;
import customgo.listener.ReportListener;
import customgo.task.Task;
import customgo.task.Value;
import fw.group.Group;
import fw.group.Lobby;
import fw.group.task.Calculater;
import fw.group.task.GroupTaskRunner;

public class SpManager {

	/**
	 * ע��һ��Task��
	 * @param task ��Ҫע���Task
	 */
	public static void Register(Task task){
		GroupTaskRunner.RegisterListener(task);
	}
	
	/**
	 * ע��һ�����м�������
	 * @param listener ��Ҫע��ļ�����
	 */
	public static void Register(GroupListener listener){
		Group.RegisterListener(listener);
		
	}
	
	/**
	 * ע��һ����Ϸ��������
	 * @param listener ��Ҫע��ļ�����
	 */
	public static void Register(LobbyListener listener){
		Lobby.RegisterListener(listener);
	}
	
	/**
	 * ע��һ��Task�е�value��
	 * @param report ��Ҫע���value
	 */
	public static void Register(ReportListener report){
		Data.RegisterReport(report);
	}
	
	/**
	 * ע��һ��Task�е�value��
	 * @param value ��Ҫע���value
	 */
	public static void Register(Value value){
		Calculater.RegValue(value);
	}

	
	
	/**
	 * ע��һ�����м�������
	 * @param listener ������
	 */
	public static void unRegister(GroupListener listener){
		Group.UnRegisterListener(listener);
		
	}
	
	/**
	 * ע��һ����Ϸ��������
	 * @param listener ������
	 */
	public static void unRegister(LobbyListener listener){
		Lobby.UnRegisterListener(listener);
	}
}
