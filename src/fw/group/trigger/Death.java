package fw.group.trigger;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class Death extends TriggerBase {
	public Death(String ID) {
		super(ID);
	}

	@EventHandler(priority=EventPriority.LOW)
	protected void Listen(PlayerRespawnEvent evt2) {
		Player pl = evt2.getPlayer();
		int isPro = 0;
		if(pl.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent)pl.getLastDamageCause();
			if(e.getDamager() instanceof Projectile) {
				isPro = 1;
			}
		}
		if (byGroup.hasPlayer(pl)) {
			byGroup.Tags().addTag("isProjectile", isPro+"");
			Strike(pl);
		}
	}
}
