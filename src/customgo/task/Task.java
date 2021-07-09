package customgo.task;

import org.bukkit.entity.Player;

import fw.group.Group;

public interface Task{
	/**
	 * 注册你的自定义Task的根指令，
	 * 当配置的Task出现该根指令即可调用TaskListen。
	 * 注意不能与原根指令重复，也不能和其他玩家注册的根指令重复。
	 * @return 返回你注册的根指令
	 */
	String identity();
	
	/*
	 * 该接口用于接收来自队列发送的Task数据，
	 * 由你在这里进行处理。
	 */
	void TaskListen(Group byGroup,String value,Player Target);
}
