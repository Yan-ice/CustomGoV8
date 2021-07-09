package fw.timer;

import java.util.List;

import fw.group.Group;
import fw.group.trigger.TriggerBase;

public class CustomTimer extends BaseTimer {
	Group byGroup;

	String Name;
	
	boolean Cancelled = false;
	public CustomTimer(Group group, String Name,int time) {
		byGroup = group;
		this.Name = Name;
		setMaxTime(time);
	}

	public void setGroup(Group group) {
		byGroup = group;
	}
	public String getName(){
		return Name;
	}

	public boolean isComplete() {
		return byGroup != null;
	}

	public void addTime(int atime){
		time = time + atime;
	}
	public void cutTime(int atime){
		time = time - atime;
	}
	public void setTime(int atime){
		time = atime;
	}
	boolean shut = false;
	public void shutdown(){
		shut = true;
	}
	@Override
	public void TimeUp() {
		List<TriggerBase> list = byGroup.getTrigger().getTriggerList();
		for (int a = 0; a < list.size(); a++) {
			if (list.get(a) instanceof fw.group.trigger.TimeUp) {
				if(list.get(a).getId()!=null && list.get(a).getId().size()>0){
					for(String s : list.get(a).getId()){
						if(s.equals(Name)){
							list.get(a).Strike(null);
							return;
						}
					}
				}
				
			}
		}
		time = Maxtime;
	}

	public boolean ShutDown() {
		return byGroup.GetPlayerAmount()==0 || shut;
	}

	public void EveryTick() {
		
	}

	@Override
	public void whenShutDown() {
		time = 0;
	}
}
