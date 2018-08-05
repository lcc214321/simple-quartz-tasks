package com.binpo.tasks.jobs.publish;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;

import redis.clients.jedis.Jedis;

import com.alibaba.fastjson.JSON;

/**
 * 任务调度处理，添加，删除，暂停等 id:数据库中配置的工作计划的id，通过id获取工作计划数据对现有的工作计划进行调整
 * 
 * @author zhang
 *
 */
public class RedisPublish {
    Jedis jedis = new Jedis("127.0.0.1", 6379);

    @Test
    public void testLua() {
        String string = UUID.randomUUID().toString();
        String set = jedis.set("taskId", string, "NX", "PX", 30000);
        System.out.println(set);

        // jedis.eval(script, keys, args)
    }
    
    @Test
    public void testLock() {
        String key ="taskId";
        String string = UUID.randomUUID().toString();
        String set = jedis.set(key, string, "NX", "PX", 30000);
        System.out.println(set);
        

        String script1 = "if redis.call('get',KEYS[1]) == ARGV[1] then "
                + " \n return redis.call('del',KEYS[1]) else return 0 end ";
        Object eval = jedis.eval(script1,Arrays.asList(key),Arrays.asList(string));
        System.out.println(eval);
    }
    
    

    /**
     * 
     * Redis执行lua脚本get set
     *
     */
    @Test
    public void luaTestSetGet() {

        // set
        String setScript = " return redis.call('set','foo','hello redis') ";
        Object eval = jedis.eval(setScript);
        System.out.println(eval);

        // get
        String getScript = " return redis.call('get','foo') ";
        eval = jedis.eval(getScript);
        System.out.println(eval);
        
        
        String script1 = "if redis.call('get',KEYS[1]) == ARGV[1] then "
                + " \n return redis.call('del',KEYS[1]) else return 0 end ";
//        jedis.eval(script1, Arrays.asList(a), args)
        
        
        
    }

    @Test
    public void getallJobs() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", "1");
        params.put("method", "getAllJob");
        jedis.publish("jobManageAction", JSON.toJSONString(params));
    }

    @Test
    public void addJob() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", "1");
        params.put("method", "addJob");
        params.put("oldJobName", "test1");
        params.put("oldJobGroup", "testGroup");
        jedis.publish("jobManageAction", JSON.toJSONString(params));
    }

    @Test
    public void runAJobNow() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", "1");
        params.put("method", "runAJobNow");
        params.put("executeParams", "{}");

        jedis.publish("jobManageAction", JSON.toJSONString(params));
    }

    @Test
    public void pauseJob() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", "1");
        params.put("method", "pauseJob");
        jedis.publish("jobManageAction", JSON.toJSONString(params));
    }

    @Test
    public void resumeJob() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", "1");
        params.put("method", "resumeJob");
        jedis.publish("jobManageAction", JSON.toJSONString(params));
    }

    @Test
    public void deleteJob() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", "1");
        params.put("method", "deleteJob");
        jedis.publish("jobManageAction", JSON.toJSONString(params));
    }

}
