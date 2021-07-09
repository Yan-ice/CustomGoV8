package fw.location;

import java.util.List;
import org.bukkit.entity.Player;

public class Teleporter {
	Player player;

	/**
	 * Ϊһ����Ҵ�����������
	 * ���������Ա�ݵش��͸���ҡ�
	 * @param player ��������Ӧ�����
	 */
	public Teleporter(Player player) {
		this.player = player;
	}

	/**
	 * �Զ��������Ĵ����ԡ�������ڽ�������ҡ�
	 * 
	 * @param Loc
	 *            ��Ҫ���͵ĵص�
	 * @param SafeTp
	 *            ����ص㲻��ȫ���Ƿ��͵�������ȫ��
	 */
	public void Teleport(FLocation Loc, boolean SafeTp) {
		if (Loc == null || !Loc.isComplete()) {
			return;
		}
		player.teleport(Loc.ToMC(player));
	}

	/**
	 * �Զ��������Ĵ�����,�����͵����һ������㡣
	 * @param Loc ��Ҫ��鲢���ѡ���͵������б�
	 */
	public void TeleportRandom(List<FLocation> Loc) {
		if(Loc != null && Loc.size()>0){
			FLocation loc = FLocation.RandomLoc(Loc);
			if(player!=null){
				if (loc.isComplete()) {
					player.teleport(loc.ToMC(player));
				}
			}
			
		}
		
		return;
	}
}
