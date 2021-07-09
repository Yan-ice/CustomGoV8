package customgo.event.group;

import fw.group.Group;

public final class EventOnGroupLoaded extends EventGroup{
	boolean suc;
	public EventOnGroupLoaded(Group gro,boolean success){
		super(gro);
		suc = success;
	}
	/**
	 * 返回队列是否读取成功。
	 * @return 是否读取成功
	 */
	public boolean isSucceed(){
		return suc;
	}
}
