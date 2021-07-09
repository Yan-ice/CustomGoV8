package fw.group.trigger;

import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamageEntity extends TriggerBase {

	public DamageEntity(String Name) {
		super(Name);
		// TODO Auto-generated constructor stub
	}

	public List<String> EntityName() {
		return Id;
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	protected void KillEntityListen(EntityDamageByEntityEvent evt) {
		if(evt.isCancelled() && !IgnoreCancel){
			return;
		}
		if (evt.getEntity() instanceof LivingEntity) {
			LivingEntity damaged = (LivingEntity) evt.getEntity();
			Player damager;
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

			if (byGroup.hasPlayer(damager)) {
				if (damaged.getCustomName() != null) {
					for (String ids : Id) {
						if (damaged.getCustomName().indexOf(ids) != -1) {
							byGroup.ValueBoard().Value("damage", ((int)(evt.getDamage()*10))/10);
							Strike(damager);
							return;
						}
					}
				}
				if(damaged.getName()!=null){
					for (String ids : Id) {
						if (damaged.getName().indexOf(ids) != -1) {
							byGroup.ValueBoard().Value("damage",((int)(evt.getDamage()*10))/10);
							Strike(damager);
							return;
						}
					}
				}
				if (damaged.getUniqueId().toString() != null) {
					for (String ids : Id) {
						if (damaged.getUniqueId().toString().indexOf(ids) != -1) {
							byGroup.ValueBoard().Value("damage", ((int)(evt.getDamage()*10))/10);
							Strike(damager);
							return;
						}
					}
				}
			}
		}
	}
}
