package fw.group;

import java.util.HashSet;
import java.util.Set;

import fw.timer.ArenaTimer;
import fw.timer.CustomTimer;
import fw.timer.FreeTimer;
import fw.timer.GroupWaitTimer;

public class GroupTimerControl {
	public int LobbyTime;
	public int Full_LobbyTime;
	public int ArenaTime;
	
	Group group;

	public GroupTimerControl(Group group, int LobbyTime, int Full_LobbyTime, int ArenaTime) {
		this.group = group;
		this.LobbyTime = LobbyTime;
		this.Full_LobbyTime = Full_LobbyTime;
		this.ArenaTime = ArenaTime;

		Ltimer = new GroupWaitTimer(group, LobbyTime, Full_LobbyTime);
		Atimer = new ArenaTimer(group, ArenaTime);
		freeTimer = new FreeTimer(group);
	}

	public boolean isComplete() {
		return this.LobbyTime >= 0 & this.Full_LobbyTime >= 0 & this.ArenaTime >= 0;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	private GroupWaitTimer Ltimer;
	private ArenaTimer Atimer;
	private FreeTimer freeTimer;
	private Set<CustomTimer> timerlist = new HashSet<>();
	
	
	public GroupWaitTimer LobbyTimer() {
		return Ltimer;
	}

	public ArenaTimer ArenaTimer() {
		return Atimer;
	}
	
	public FreeTimer FreeTimer() {
		return freeTimer;
	}
	
	public CustomTimer getCustomTimer(String Name) {
		for(CustomTimer t : timerlist){
			if(t.getName().equals(Name)){
				return t;
			}
		}
		return null;
	}
	public Set<CustomTimer> getCustomTimerList() {
		return timerlist;
	}
	
	public void addCustomTimer(String Name,int MaxTime) {
		for(CustomTimer t : timerlist){
			if(t.getName().equals(Name)){
				if(!t.isRunning()){
					timerlist.remove(t);
					CustomTimer tt = new CustomTimer(group,Name,MaxTime);
					timerlist.add(tt);
					tt.Start();
					return;
				}else{
					t.setTime(MaxTime);
					return;
				}
			}
		}
		CustomTimer tt = new CustomTimer(group,Name,MaxTime);
		timerlist.add(tt);
		tt.Start();
	}
	
	
	public void start(int a) {
		if (isComplete()) {
			switch (a) {
			case 1:
				Ltimer = new GroupWaitTimer(group, LobbyTime, Full_LobbyTime);
				Ltimer.Start();
				
				break;
			case 2:
				Atimer = new ArenaTimer(group, ArenaTime);
				Atimer.Start();
				break;
			case 3:
				freeTimer = new FreeTimer(group);
				freeTimer.Start();
				break;
			}
		}
	}
}
