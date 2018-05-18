package com.cnblogs.hellxz.controller;

import com.cnblogs.hellxz.entity.User;
import com.cnblogs.hellxz.utils.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @Author : Hellxz
 * @Description: 服务提供者
 * @Date : 2018/4/18 11:36
 */
@RestController
public class GetRequestController {

    @Autowired
    private DiscoveryClient client; //注入发现客户端

    private final Logger logger = Logger.getLogger(GetRequestController.class);

    /**
     * go straight test
     */
    @GetMapping(value = "/hello")
    public String hello(){
        //获取服务实例，作用为之后console显示效果
        ServiceInstance serviceInstance = client.getLocalServiceInstance();
        logger.info("/hello host:"+serviceInstance.getHost()+" service_id:" +serviceInstance.getServiceId());
        return "hello";
    }

    /**
     * parameter test
     */
    @GetMapping(value = "/greet/{dd}")
    public String greet(@PathVariable String dd){
        ServiceInstance serviceInstance = client.getLocalServiceInstance();
        logger.info("/hello host:"+serviceInstance.getHost()+" service_id:" +serviceInstance.getServiceId());
        return "hello "+dd;
    }

    /**
     * 返回测试对象
     */
    @GetMapping("/user")
    public User getUser(){
        ServiceInstance serviceInstance = client.getLocalServiceInstance();
        logger.info("/user "+serviceInstance.getHost()+" port:"+serviceInstance.getPort()+" serviceInstanceid:"+serviceInstance.getServiceId());
        return new User("hellxz","male", "123456789");
    }

    /**
     * 根据名称返回对象，这里模拟查数据库操作
     */
    @GetMapping("/user/{name}")
    public User getUserSelect(@PathVariable String name){
        ServiceInstance serviceInstance = client.getLocalServiceInstance();
        logger.info("/user "+serviceInstance.getHost()+" port:"+serviceInstance.getPort()+" serviceInstanceid:"+serviceInstance.getServiceId());
        if(name.isEmpty()){
            return new User();
        }else if(name.equals("hellxz")){
            return new User("hellxz","male", "123456789");
        }else{
            return new User("随机用户","male", "987654321");
        }
    }
    //==================================================================================

    /**
     * 为消费端提供两个接口，一个是返回一个对象的，另一个是返回两个对象
     */
    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable Long id){
        //因为没有使用数据库，只返回一个User对象
        return new User("first", "女", "110-"+id);
    }

    @GetMapping("/users")
    public List<User> getUsersByIds(@RequestParam("ids") String ids){
        List<User> userList = new ArrayList<>();
        User user = null;
        logger.info("-=============ids="+ids);
        List<String> stringList = StringUtils.commaSplitrStringToList(ids);
        for(String id : stringList){
            user = new User(id ,"男","123-"+id);
            userList.add(user);
        }
        return userList;
    }

    /**
     * 为了请求测试Hystrix请求缓存提供的返回随机数的接口
     */
    @GetMapping("/hystrix/cache")
    public Integer getRandomInteger(){
        Random random = new Random();
        int randomInt = random.nextInt(99999);
        return randomInt;
    }

}
