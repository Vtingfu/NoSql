package com.bjtu.utils;

import redis.clients.jedis.Jedis;

public class RedisUtil {
    // Redis数据库api
    public static final Jedis jedis = new Jedis("localhost",6379);
    // 不同类别数据存储时Key的前缀
    public static class Index{
        public static final String USER_RATES = "user_rates_";
        public static final String MOVIE_INFO = "movie_info_";
    }

    public static void putSting(String key, String value){
        jedis.set(key , value);
    }

    public static String getString(String key){
        return jedis.get(key);
    }

    // 以下三个方法 传入id号，获取目标信息
    public static String get_userRates (Integer user_id) {
        Jedis jedis = new Jedis("localhost", 6379);
        String search = "user_rates_" + user_id;
        String result = jedis.get(search);
        jedis.close();
        return result;
    }

    public static String get_movieInfo (Integer movie_id){
        Jedis jedis = new Jedis("localhost", 6379);
        String search = "movieInfo_" + movie_id;
        String result = jedis.get(search);
        jedis.close();
        return result;
    }

    public static String get_UserTags (Integer user_id){
        Jedis jedis = new Jedis("localhost", 6379);
        String search = "user_tags_" + user_id;
        String result = jedis.get(search);
        jedis.close();
        return result;
    }

    //清除redis库
    public static void clean_All(){
        Jedis jedis = new Jedis("localhost", 6379);
        jedis.flushDB();
        jedis.close();
    }

    //以下三个方法获取指定数量的相关数据
    public static String[] multi_get_userRates (Integer num){
        Jedis jedis = new Jedis("localhost", 6379);
        String[] happy = new String[num+10];
        for(int i = 0 ; i < num ; i++){
            String search = "user_rates_" + (i+1);
            String result = jedis.get(search);
            happy[i] = result;
        }
        jedis.close();
        return happy;
    }

    public static String[] multi_get_movieInfo (Integer num){
        Jedis jedis = new Jedis("localhost", 6379);
        String[] happy = new String[num+10];
        for(int i = 0 ; i < num ; i++){
            String search = "movieInfo_" + (i+1);
            String result = jedis.get(search);
            happy[i] = result;
        }
        jedis.close();
        return happy;
    }

    public static String[] multi_get_UserTags (Integer num){
        Jedis jedis = new Jedis("localhost", 6379);
        String[] happy = new String[num+10];
        for(int i = 0 ; i < num ; i++){
            String search = "user_tags_" + (i+1);
            String result = jedis.get(search);
            happy[i] = result;
        }
        jedis.close();
        return happy;
    }

    public static void main(String[] args) {
        Jedis jedis = new Jedis("localhost");
        jedis.setex("testKey",  10,"testValue222");
        System.out.println("OK");
        System.out.println("pong : " + jedis.ping());

    }
}


