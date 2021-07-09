package fw.group.trigger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class Sneak extends TriggerBase {

	public Sneak(String Name) {
		super(Name);
		// TODO Auto-generated constructor stub
	}

	@EventHandler
	void Listen(PlayerToggleSneakEvent evt2) {
		if(evt2.isCancelled() && !IgnoreCancel){
			return;
		}
		Player pl = evt2.getPlayer();
		if (byGroup.hasPlayer(pl)) {
			Strike(pl);

		}
	}
}
