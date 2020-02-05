package com.project.utils;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

public class MemCacheServer 
{
    public static void getInstance()
    {
    	MemCachedUtil.cachedClient = new MemCachedClient();  
    	MemCachedUtil.cachedClient.setPrimitiveAsString(true);
        SockIOPool pool = SockIOPool.getInstance();  
        pool.setServers(new String[]{"127.0.0.1:11211"});  
        Integer[] weights = {3};
        pool.setWeights( weights );
        pool.setInitConn(20);  
        pool.setMinConn(10);  
        pool.setMaxConn(7000);  
        pool.setMaxIdle(1000 * 60 * 60 * 3);  
        pool.setMaintSleep(60);  
        pool.setNagle(false);  
        pool.setSocketTO(3000);  
        pool.setSocketConnectTO(0);  
        pool.initialize(); 
    }
}
