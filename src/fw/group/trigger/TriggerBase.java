package fw.group.trigger;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import fw.group.Group;
import fw.group.task.GroupTaskRunner;

public abstract class TriggerBase implements Listener {
	String Name;
	Group byGroup;
	List<String> Task;
	List<String> Id = new ArrayList<>();
	
	int maxrunTime = -1;
	int runTime = 0;
	boolean IgnoreCancel = false;
	
	public TriggerBase(String Name) {
		this.Name = Name;
	}

	public List<String> getTask() {
		return Task;
	}

	public Group fromGroup() {
		return byGroup;
	}

	void setId(List<String> a) {
		Id = a;
	}
	
	public void unLoad() {
		HandlerList.unregisterAll(this);
	}

	public List<String> getId() {
		return Id;
	}
	
	public String getName() {
		return Name;
	}
	
	public boolean Load(Group byGroup) {
		this.byGroup = byGroup;
		try {
			if (byGroup.getFileConf().contains("Trigger." + Name + ".Id")) {
				Id = byGroup.getFileConf().getStringList("Trigger." + Name + ".Id");
			}
			if (byGroup.getFileConf().contains("Trigger." + Name + ".IgnoreCancel")) {
				IgnoreCancel = byGroup.getFileConf().getBoolean("Trigger." + Name + ".IgnoreCancel");
			}
			Task = byGroup.getFileConf().getStringList("Trigger." + Name + ".Task");
			
			if (byGroup.getFileConf().contains("Trigger." + Name + ".RunTime")) {
				maxrunTime = byGroup.getFileConf().getInt("Trigger." + Name + ".RunTime");
			}
		} catch (NullPointerException a) {
			return false;
		}
		return true;
	}

	public void Strike(Player player) {
		if(player != null){
			new GroupTaskRunner(Task, byGroup, player,byGroup.byLobby().getPlayerList());
		}else{
			new GroupTaskRunner(Task, byGroup, null,byGroup.byLobby().getPlayerList());
		}
		runTime++;
		if(runTime==maxrunTime){
			runTime=0;
			HandlerList.unregisterAll(this);
		}
	}
}
