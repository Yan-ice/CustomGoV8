package customgo.event.group;

import fw.group.Group;

public final class EventOnPlayerAmountChange extends EventGroup{
	public EventOnPlayerAmountChange(Group gro){
		super(gro);
	}
	/**
	 * 返回队列在人数改变后剩余玩家数量。
	 * @return 剩余玩家
	 */
	public int RestPlayerAmount(){
		return gro.GetPlayerAmount();
	}
}
