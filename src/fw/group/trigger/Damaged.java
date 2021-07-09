package fw.group.trigger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;

public class Damaged extends TriggerBase {

	public Damaged(String Name) {
		super(Name);
		// TODO Auto-generated constructor stub
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	protected void KillPlayerListen(EntityDamageEvent evt) {
		if(evt.isCancelled() && !IgnoreCancel){
			return;
		}
		if (evt.getEntity() instanceof Player) {
			Player damaged = (Player) evt.getEntity();
				if (byGroup.hasPlayer(damaged)) {
					byGroup.ValueBoard().Value("damage", ((int)(evt.getDamage()*10))/10);
					Strike(damaged);
				}
		}

	}
}
