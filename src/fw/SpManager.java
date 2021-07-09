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
	 * 注册一个Task。
	 * @param task 需要注册的Task
	 */
	public static void Register(Task task){
		GroupTaskRunner.RegisterListener(task);
	}
	
	/**
	 * 注册一个队列监听器。
	 * @param listener 需要注册的监听器
	 */
	public static void Register(GroupListener listener){
		Group.RegisterListener(listener);
		
	}
	
	/**
	 * 注册一个游戏监听器。
	 * @param listener 需要注册的监听器
	 */
	public static void Register(LobbyListener listener){
		Lobby.RegisterListener(listener);
	}
	
	/**
	 * 注册一个Task中的value。
	 * @param report 需要注册的value
	 */
	public static void Register(ReportListener report){
		Data.RegisterReport(report);
	}
	
	/**
	 * 注册一个Task中的value。
	 * @param value 需要注册的value
	 */
	public static void Register(Value value){
		Calculater.RegValue(value);
	}

	
	
	/**
	 * 注销一个队列监听器。
	 * @param listener 监听器
	 */
	public static void unRegister(GroupListener listener){
		Group.UnRegisterListener(listener);
		
	}
	
	/**
	 * 注销一个游戏监听器。
	 * @param listener 监听器
	 */
	public static void unRegister(LobbyListener listener){
		Lobby.UnRegisterListener(listener);
	}
}
