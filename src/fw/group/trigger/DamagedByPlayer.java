package fw.group.trigger;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamagedByPlayer extends TriggerBase {

	public DamagedByPlayer(String Name) {
		super(Name);
		// TODO Auto-generated constructor stub
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	protected void dmg(EntityDamageByEntityEvent evt) {
		if(evt.isCancelled() && !IgnoreCancel){
			return;
		}
		if (evt.getEntity() instanceof Player && evt.getDamager() instanceof Player) {
			Player damaged = (Player) evt.getEntity();
				if (byGroup.hasPlayer(damaged)) {
					byGroup.ValueBoard().Value("damage", ((int)(evt.getDamage()*10))/10);
					byGroup.Tags().addTag("damager", evt.getDamager().getName());
					Strike(damaged);
				}
		}else if(evt.getEntity() instanceof Player && evt.getDamager() instanceof Projectile && ((Projectile)evt.getDamager()).getShooter() instanceof Player){
			Player damaged = (Player) evt.getEntity();
			if (byGroup.hasPlayer(damaged)) {
				byGroup.ValueBoard().Value("damage", ((int)(evt.getDamage()*10))/10);
				byGroup.Tags().addTag("damager", evt.getDamager().getName());
				Strike(damaged);
			}
		}

	}
}
