import com.thinkive.timerengine.TaskManager;
import com.util.MemCacheServer;

import org.apache.log4j.Logger;


/**
 * 描述: 
 * 版权: Copyright (c) 2008 
 * 公司: 思迪科技 
 * 作者: 李建 
 * 版本: 1.0 
 * 创建日期: 2008-11-17 
 * 创建时间: 下午04:03:03
 */
public class Main
{
    /**
     * 
     */
    private static Logger logger = Logger.getLogger(Main.class);
    
    

    /**
     * 描述：
     * 作者：李建
     * 时间：2008-11-17 下午04:03:10
     * @param args args
     */
    public static void main(String[] args)
    {
        //启动任务管理器
    	TaskManager.start();	
    	MemCacheServer.getInstance();
    }
    
    public Integer start(String[] args)
	{
		return null;
	}
	
	
	public int stop(int exitCode)
	{
		return exitCode;
	}
	
	
	public void controlEvent(int event)
	{
		
	}  
}
