package fw.group.trigger;

import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamagePlayer extends TriggerBase {

	public DamagePlayer(String Name) {
		super(Name);
		// TODO Auto-generated constructor stub
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	protected void KillPlayerListen(EntityDamageByEntityEvent evt) {
		if(evt.isCancelled() && !IgnoreCancel){
			return;
		}
		if (evt.getEntity() instanceof Player) {
			Player damaged = (Player) evt.getEntity();
			Player damager;
				if (((Damageable)damaged).getHealth()<=evt.getDamage()) {
					if (evt.getDamager() instanceof Player) {
						damager = (Player) evt.getDamager();
					} else if(evt.getDamager() instanceof Projectile){
						if(((Projectile)evt.getDamager()).getShooter() instanceof Player){
							damager = (Player)((Projectile)evt.getDamager()).getShooter();
						}else{
							return;
						}
					}else{
						return;
					}
					if (byGroup.hasPlayer(damager) || (damaged.getHealth()<=evt.getDamage() && !evt.isCancelled())) {
						byGroup.ValueBoard().Value("damage", ((int)(evt.getDamage()*10))/10);
						byGroup.Tags().addTag("Name", damaged.getName());
						Strike(damager);
					}
				}
		}

	}
}
