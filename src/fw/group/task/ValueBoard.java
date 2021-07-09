package fw.group.task;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ValueBoard {
	Map<String,Double> value = new HashMap<>();

	
	/**
	 * �ڼƷְ��ϼ�¼�µı�����
	 * ����������Ѵ��ڣ��򸲸�����
	 * @param Name ������
	 * @param Value ֵ
	 */
	public void Value(String Name,double Value){
		if(value.containsKey(Name)){
			value.remove(Name);
		}
		value.put(Name, Value);
	}
	
	public void ValueAdd(String Name,double Value){
		if(value.containsKey(Name)){
			
			value.replace(Name, value.get(Name)+Value);
		}else{
			value.put(Name, Value);
		}
	}
	
	
	/**
	 * �ڼƷְ��ϻ�ȡһ��������
	 * ��������ڸñ��������ȡ0
	 * @param Name ������
	 * @return ��ȡ����ֵ
	 */
	public double getValue(String Name){
		if(value.containsKey(Name)){
			return value.get(Name);
		}
		return 0;
	}
	
	/**
	 * �Ƴ��Ʒְ���ָ��������
	 * @param Name ������
	 */
	public void removeValue(String Name){
		if(value.containsKey(Name)){
			value.remove(Name);
		}
	}
	
	/**
	 * ��üƷְ��ϵ����б�����
	 * @return �����б�
	 */
	public Set<String> ValueList(){
		return value.keySet();
	}
}
