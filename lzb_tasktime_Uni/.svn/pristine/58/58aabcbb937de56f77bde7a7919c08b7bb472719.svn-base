package com.task;

import java.util.Date;

import org.apache.log4j.Logger;

import com.service.DealmakingService;
import com.thinkive.timerengine.Task;

public class DealmakingTask implements Task 
{

	private static Logger logger = Logger.getLogger(DealmakingTask.class);
	private static DealmakingService service = new DealmakingService();
	@Override
	public void execute() 
	{
		logger.info("开始执行算息："+new Date().toLocaleString());
		//执行定存算息
		service.chdc();
		//执行获取算息
		service.chhq();
		logger.info("结束执行算息："+new Date().toLocaleString());
	}

	public static void main(String[] args) {
		service.chhq();
	}
}
