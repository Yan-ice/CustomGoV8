package customgo.event.group;

import fw.group.Group;

public abstract class EventGroup {
	protected Group gro;
	public EventGroup(Group gro){
		this.gro = gro;
	}
	
	/**
	 * 返回触发事件的队列。
	 * @return 队列
	 */
	public Group getGroup(){
		return gro;
	}

}
