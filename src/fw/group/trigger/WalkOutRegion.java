package fw.group.trigger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import fw.Data;
import fw.location.FArena;
import fw.location.FLocation;

public class WalkOutRegion extends TriggerBase {

	public WalkOutRegion(String Name) {
		super(Name);
		// TODO Auto-generated constructor stub
	}
	
	boolean Cooldown = false;
	@EventHandler
	void Listen(PlayerMoveEvent evt2) {
		Player pl = evt2.getPlayer();
		if (!Cooldown && byGroup.hasPlayer(pl)) {
			if (Id != null & Id.size()>0) {
				for (String id : Id) {
					FArena ar = new FArena(id,pl.getWorld());
					if(!ar.inArea(new FLocation(pl.getLocation()))){
							Strike(pl);
							Cooldown = true;
							new BukkitRunnable(){

								@Override
								public void run() {
									Cooldown = false;
								}
								
							}.runTaskLater(Data.fmain, 20);
							return;
					}
					
				}
			}
		}
	}
}
