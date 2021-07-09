package fw.group.task;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;

public class PlayerValueBoard {
	Map<Player,ValueBoard> pvalue = new HashMap<>();
	
	public PlayerValueBoard(Set<Player> pl){
		for(Player p : pl){
			pvalue.put(p, new ValueBoard());
		}
	}
	public PlayerValueBoard(){
		
	}
	
	/**
	 * �ڼƷְ��ϸ�һ����Ҽ�¼�µı�����
	 * ����������Ѵ��ڣ��򸲸�����
	 * @param Name ������
	 * @param Value ֵ
	 * @param player ����¼�����
	 */
	public void Value(String Name,Double Value,Player player){
		if(player == null){
			return;
		}else{
			if(pvalue.containsKey(player)){
				ValueBoard value = pvalue.get(player);
				value.Value(Name, Value);
			}else{
				pvalue.put(player, new ValueBoard());
				Value(Name, Value,player);
			}
		}
	}
	
	/**
	 * �ڼƷְ��ϸ�һ����ұ�������ֵ��
	 * ����������Ѵ��ڣ��򸲸�����
	 * @param Name ������
	 * @param Value ֵ
	 * @param player ����¼�����
	 */
	public void ValueAdd(String Name,Double Value,Player player){
		if(player == null){
			return;
		}else{
			if(pvalue.containsKey(player)){
				ValueBoard value = pvalue.get(player);
				value.ValueAdd(Name, Value);
			}else{
				pvalue.put(player, new ValueBoard());
				ValueAdd(Name, Value,player);
			}
		}
	}
	
	/**
	 * �ڼƷְ��ϻ�ȡһ����ҵı�����
	 * ��������ڸ���ң����ȡ"[����ұ���]"
	 * ������ڸ���Ҷ������ڸñ��������ȡ"[δ֪����]"
	 * @param Name ������
	 * @param player ����ȡ�����
	 * @return ��ȡ����ֵ
	 */
	public double getValue(String Name,Player player){
		if(player == null){
			return 0;
		}else{
			if(pvalue.containsKey(player)){
				ValueBoard value = pvalue.get(player);
				return value.getValue(Name);
			}else{
				return 0;
			}
		}
	}
	
	/**
	 * �Ƴ��Ʒְ���ָ����ҵ�ָ��������
	 * @param Name ������
	 * @param player ���Ƴ����������
	 */
	public void removeValue(String Name,Player player){
		if(player == null){
			return;
		}else{
			if(pvalue.containsKey(player)){
				ValueBoard value = pvalue.get(player);
				value.removeValue(Name);
			}else{
				removeValue(Name,null);
			}
		}
	}
	
	/**
	 * ��üƷְ���һ����ҵ����б�����
	 * @param player ���
	 * @return �����б�
	 */
	public Set<String> ValueList(Player player){
		if(player != null){
			if(pvalue.containsKey(player)){
				return pvalue.get(player).ValueList();
			}
		}
		return new HashSet<>();
	}
}
