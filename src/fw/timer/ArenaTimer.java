package fw.timer;

import java.util.List;

import fw.group.Group;
import fw.group.task.GroupTaskRunner;
import fw.group.trigger.TriggerBase;

public class ArenaTimer extends BaseTimer {
	Group byGroup;

	public ArenaTimer(Group group, int time) {
		byGroup = group;
		setMaxTime(time);
	}

	public void setGroup(Group group) {
		byGroup = group;
	}

	public boolean isComplete() {
		return byGroup != null;
	}

	@Override
	public void TimeUp() {
		List<TriggerBase> list = byGroup.getTrigger().getTriggerList();
		for (int a = 0; a < list.size(); a++) {
			if (list.get(a) instanceof fw.group.trigger.TimeUp) {
				if(list.get(a).getId()==null || list.get(a).getId().size()==0){
					list.get(a).Strike(null);
				}
			}
		}
		while(byGroup.getPlayerList().size()>0){
			byGroup.LeaveGroup(byGroup.getPlayerList().get(0), false);
		}
		time = 0;
	}

	public boolean ShutDown() {
		return byGroup.canJoin();
	}

	public void EveryTick() {
		if (byGroup.getFileConf().contains("ControlTask.onTimePast(" + time + ")")) {
			List<String> task = byGroup.getFileConf().getStringList("ControlTask.onTimePast(" + time + ")");
			new GroupTaskRunner(task, byGroup, null,byGroup.byLobby().getPlayerList());

		}
		if (byGroup.getFileConf().contains("ControlTask.onTimeRest(" + time + ")")) {
			List<String> task = byGroup.getFileConf().getStringList("ControlTask.onTimeRest(" + time + ")");
			new GroupTaskRunner(task, byGroup, null,byGroup.byLobby().getPlayerList());

		}
		if (byGroup.getFileConf().contains("ControlTask.onEverySecond")) {
			List<String> task = byGroup.getFileConf().getStringList("ControlTask.onEverySecond");
			new GroupTaskRunner(task, byGroup, null,byGroup.byLobby().getPlayerList());

		}
	}

	@Override
	public void whenShutDown() {
		time = 0;
	}
}
