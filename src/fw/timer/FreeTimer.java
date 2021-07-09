package fw.timer;

import java.util.List;

import fw.group.Group;
import fw.group.task.GroupTaskRunner;

public class FreeTimer extends BaseTimer {
	Group byGroup;

	public FreeTimer(Group group) {
		byGroup = group;
	}

	public void setGroup(Group group) {
		byGroup = group;
	}

	public boolean isComplete() {
		return byGroup != null;
	}

	@Override
	public void TimeUp() {
		
	}

	public boolean ShutDown() {
		return byGroup.GetPlayerAmount()==0;
	}

	public void EveryTick() {
		if (byGroup.getFileConf().contains("ControlTask.onEverySecond")) {
			List<String> task = byGroup.getFileConf().getStringList("ControlTask.onEverySecond");
			new GroupTaskRunner(task, byGroup, null,byGroup.byLobby().getPlayerList());
		}
	}

	@Override
	public void whenShutDown() {
	}
}
