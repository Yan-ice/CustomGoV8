package fw.group.trigger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;

public class KillPlayer extends TriggerBase {

	public KillPlayer(String Name) {
		super(Name);
		// TODO Auto-generated constructor stub
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	void KillPlayerListen(PlayerDeathEvent evt) {
			Player damaged = evt.getEntity();
			damaged.getKiller();
			Player damager = damaged.getKiller();
			if(damager!=null){
				if (byGroup.hasPlayer(damager)) {
					byGroup.Tags().addTag("Name", damaged.getName());
					Strike(damager);
				}
			}
	}

}
