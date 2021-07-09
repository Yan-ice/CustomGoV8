package fw.timer;

import java.util.List;

import fw.group.Group;
import fw.group.task.GroupTaskRunner;

public class GroupWaitTimer extends BaseTimer {
	int FastTime;
	Group byGroup;

	public GroupWaitTimer(Group group, int time, int fasttime) {
		this.byGroup = group;
		this.time = time;
		this.Maxtime = time;
		this.FastTime = fasttime;
	}

	public void setGroup(Group group) {
		byGroup = group;
	}

	public boolean isComplete() {
		return byGroup != null;
	}

	public int getFastTime() {
		return FastTime;
	}

	public void setFastTime(int FastTime) {
		this.FastTime = FastTime;
	}

	@Override
	public void TimeUp() {
		byGroup.Start();
		time = 0;
	}

	/**
	 * �÷���ִ��ʱ�������ʱ����ʣ��ʱ����ڵ���ʱ�䣬��ʱ���������������ʱ�䡣���ڹ��캯��ʱ�趨��
	 */
	public void FastStart() {
		if (isComplete()) {
			if (this.time > FastTime) {
				this.time = FastTime;
			}
		}
	}
	

	public boolean ShutDown() {
		return byGroup.GetPlayerAmount() < byGroup.getMinPlayer();
	}

	@Override
	public void whenShutDown() {
		time = 0;
	}

	@Override
	public void EveryTick() {
		if (byGroup.getFileConf().contains("ControlTask.onLobbyTimePast(" + time + ")")) {
			List<String> task = byGroup.getFileConf().getStringList("ControlTask.onLobbyTimePast(" + time + ")");
			new GroupTaskRunner(task, byGroup, null,byGroup.byLobby().getPlayerList());

		}
		if (byGroup.getFileConf().contains("ControlTask.onLobbyTimeRest(" + time + ")")) {
			List<String> task = byGroup.getFileConf().getStringList("ControlTask.onLobbyTimeRest(" + time + ")");
			new GroupTaskRunner(task, byGroup, null,byGroup.byLobby().getPlayerList());

		}
	}
}
