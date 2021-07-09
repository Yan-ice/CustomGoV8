package fw.group.trigger;

import org.bukkit.entity.Player;

public class Respond extends TriggerBase {

	public Respond(String Name) {
		super(Name);
		// TODO Auto-generated constructor stub
	}
	
	public void Responde(String s,Player pl){
		for(String id : Id){
			if(s.contains(id)){
				Strike(pl);
			}
		}
	}

}
