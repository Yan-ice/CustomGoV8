package fw.group;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import fw.group.trigger.BreakBlock;
import fw.group.trigger.Chat;
import fw.group.trigger.ConsoleCommand;
import fw.group.trigger.DamageEntity;
import fw.group.trigger.DamagePlayer;
import fw.group.trigger.Damaged;
import fw.group.trigger.DamagedByPlayer;
import fw.group.trigger.Death;
import fw.group.trigger.Diss;
import fw.group.trigger.Interact;
import fw.group.trigger.InteractBlock;
import fw.group.trigger.Join;
import fw.group.trigger.KillAllEntity;
import fw.group.trigger.KillEntity;
import fw.group.trigger.KillPlayer;
import fw.group.trigger.Leave;
import fw.group.trigger.Offline;
import fw.group.trigger.PickupItem;
import fw.group.trigger.Respond;
import fw.group.trigger.SendCommand;
import fw.group.trigger.Sneak;
import fw.group.trigger.TimeUp;
import fw.group.trigger.TriggerBase;
import fw.group.trigger.WalkInRegion;
import fw.group.trigger.WalkOnBlock;
import fw.group.trigger.WalkOutRegion;

public class Trigger {
	Group byGroup;
	List<TriggerBase> TriggerList = new ArrayList<>();

	public Trigger(Group group) {
		byGroup = group;
	}

	public List<TriggerBase> getTriggerList() {
		return TriggerList;
	}
	public TriggerBase getTrigger(String Name) {
		for(TriggerBase t: TriggerList){
			if(t.getName().equals(Name)){
				return t;
			}
		}
		return null;
	}

	public void Respond(String message,Player pl){
		for(TriggerBase e : TriggerList){
			if(e instanceof Respond){
				((Respond)e).Responde(message,pl);
			}
		}
	}
	
	public void load(Group byGroup) throws NullPointerException{
		while (TriggerList.size() > 0) {
			HandlerList.unregisterAll(TriggerList.get(0));
			TriggerList.remove(0);
		}
		// unload
		Set<String> List = byGroup.getFileConf().getConfigurationSection("Trigger").getKeys(false);
		for (String TriggerName : List) {
			if (byGroup.getFileConf().contains("Trigger." + TriggerName + ".Type")) {
				String type = byGroup.getFileConf().getString("Trigger." + TriggerName + ".Type");
				TriggerBase trigger;
				switch (type) {
				case "Death":
					trigger = new Death(TriggerName);
					break;
				case "Sneak":
					trigger = new Sneak(TriggerName);
					break;
				case "PickupItem":
					trigger = new PickupItem(TriggerName);
					break;
				case "WalkOnBlock":
					trigger = new WalkOnBlock(TriggerName);
					break;
				case "KillEntity":
					trigger = new KillEntity(TriggerName);
					break;
				case "KillPlayer":
					trigger = new KillPlayer(TriggerName);
					break;
				case "DamageEntity":
					trigger = new DamageEntity(TriggerName);
					break;
				case "DamagePlayer":
					trigger = new DamagePlayer(TriggerName);
					break;
				case "DamagedByPlayer":
					trigger = new DamagedByPlayer(TriggerName);
					break;	
				case "Damaged":
					trigger = new Damaged(TriggerName);
					break;
				case "Diss":
					trigger = new Diss(TriggerName);
					break;
				case "TimeUp":
					trigger = new TimeUp(TriggerName);
					break;
				case "Offline":
					trigger = new Offline(TriggerName);
					break;
				case "KillAllEntity":
					trigger = new KillAllEntity(TriggerName);
					break;
				case "SendCommand":
					trigger = new SendCommand(TriggerName);
					break;
				case "Respond":
					trigger = new Respond(TriggerName);
					break;
				case "BreakBlock":
					trigger = new BreakBlock(TriggerName);
					break;
				case "Interact":
					trigger = new Interact(TriggerName);
					break;
				case "Chat":
					trigger = new Chat(TriggerName);
					break;	
				case "Join":
					trigger = new Join(TriggerName);
					break;
				case "Leave":
					trigger = new Leave(TriggerName);
					break;
				case "InteractBlock":
					trigger = new InteractBlock(TriggerName);
					break;
				case "ConsoleCommand":
					trigger = new ConsoleCommand(TriggerName);
					break;
				case "WalkInRegion":
					trigger = new WalkInRegion(TriggerName);
					break;
				case "WalkOutRegion":
					trigger = new WalkOutRegion(TriggerName);
					break;	
				default:
					return;
				}
				if (trigger.Load(byGroup)) {
					this.TriggerList.add(trigger);
				}
			}

		}
	}
}
