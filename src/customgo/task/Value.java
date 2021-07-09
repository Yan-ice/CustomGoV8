package customgo.task;

public interface Value {
	/**
	 * 发送你的value的识别id给插件。
	 * @return ID
	 */
	String setId();
	/**
	 * value识别到含您的ID的value占位符后，会调用该函数。
	 * @param args 返回的value占位符中含有的参数。
	 * @return 该value占位符将被替换为的字符串。返回null则不做处理。
	 */
	String ValueFeedBack(String[] args);
	
	
	
}
