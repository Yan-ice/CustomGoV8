package fw.group.task;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Tag {
	Map<String,String> value = new HashMap<>();
	
	/**
	 * �ڼ�¼���ϼ�¼�±�ǩ��
	 * ����������Ѵ��ڣ��򸲸�����
	 * @param Name ������
	 * @param Value ֵ
	 */
	public void addTag(String Name,String Value){
		if(value.containsKey(Name)){
			value.remove(Name);
		}
		value.put(Name, Value);
	}
	
	/**
	 * �ڼƷְ��ϻ�ȡһ��������
	 * ��������ڸñ��������ȡ0
	 * @param Name ������
	 * @return ��ȡ����ֵ
	 */
	public String getTag(String Name){
		if(value.containsKey(Name)){
			return value.get(Name);
		}
		return "null";
	}
	
	/**
	 * �Ƴ��Ʒְ���ָ��������
	 * @param Name ������
	 */
	public void removeTag(String Name){
		if(value.containsKey(Name)){
			value.remove(Name);
		}
	}
	
	/**
	 * ��üƷְ��ϵ����б�����
	 * @return �����б�
	 */
	public Set<String> TagList(){
		return value.keySet();
	}
}
