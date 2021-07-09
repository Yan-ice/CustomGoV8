package fw.group.trigger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

public class Offline extends TriggerBase {

	public Offline(String Name) {
		super(Name);
		// TODO Auto-generated constructor stub
	}

	@EventHandler
	void Listen(PlayerQuitEvent evt2) {
		Player pl = evt2.getPlayer();
		if (byGroup.hasPlayer(pl)) {
			Strike(pl);
		}
	}
}
