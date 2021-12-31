package com.bjtu.profileDataService;

import com.BJTU.grpc.DataProto.DataProto;
import com.bjtu.simpleDataService.simpleDataClient;
import com.bjtu.utils.RedisUtil;
import com.bjtu.utils.TypeConvertUtil;
import com.google.protobuf.InvalidProtocolBufferException;

import java.sql.Timestamp;
import java.util.*;

public class UserPro {

    private static ArrayList<String> Proportion(Map<String, ArrayList<String>> map) throws InvalidProtocolBufferException, InterruptedException {
        int[] types = new int[20];
        float[] P = new float[20];
        String[] category = {"Adventure", "Animation", "Action", "Crime", "Children", "Comedy", "Drama", "Documentary", "Fantasy", "Horror",
                "IMAX", "Romance", "Mystery", "Thriller", "Sci-Fi", "War", "Musical", "Western", "Film-Noir"};

//        int y=0;
        for (String key : map.keySet()) {
            //通过key得到movie信息
            String strTemp = new simpleDataClient().getMovieInfo(key);
//            String strTemp = RedisUtil.getString(RedisUtil.Index.MOVIE_INFO + key);

//            if (strTemp == null) break;
            // 利用protoBuf进行反序列化
            DataProto.MovieDataReply movieReply = DataProto.MovieDataReply.parseFrom(TypeConvertUtil.StringToBytearray(strTemp));
            // 将string转化为map
            Map<String, ArrayList<String>> movieMap = TypeConvertUtil.StringToMap(movieReply.getData());
//            info.add("name");
//            info.add("Adventure|Animation|hhh");
            ArrayList<String> info = movieMap.get("BasicInfo");
            String Type = info.get(1);
            String Sub = info.get(1);

//            String[] str={"Adventure|Animation|hhh","Adventure|hhh"};
//            String Type = str[y];
//            String Sub = str[y];
//            y++;

            int my_count=0;
            for (int x = 0; x < category.length; x++){
                if (Type.contains(category[x])) {
                    types[x]++;
                    Sub=getSubString(Sub,category[x]);
                    my_count++;
                }
            }
//          Action|Adventure|Comedy|Fantasy|Romance
            if (Sub.length()>my_count) //或者大于my_count-1也行
            {
                types[category.length]++;
            }
            // if(Type.contains("Film-Noir")) types[18]++;
//            if(!(Type.contains("Adventure")||Type.contains("Animation")||Type.contains("Action")||Type.contains("Crime")||Type.contains("Children")||Type.contains("Comedy")||
//                 Type.contains("Drama")||Type.contains("Documentary")||Type.contains("Fantasy")||Type.contains("Horror")||Type.contains("IMAX")||Type.contains("Romance")||
//                 Type.contains("Mystery")||Type.contains("Thriller")||Type.contains("Sci-Fi")||Type.contains("War")||Type.contains("Musical")||Type.contains("Western")||Type.contains("Film-Noir")))
//                types[19]++;
            }

        float num = map.size();
        for (int i = 0; i < types.length; i++) {
            P[i] = types[i] / num;
        }

        ArrayList<String> list=new ArrayList<String>();
        for (int n = 0; n < category.length; n++){
            list.add( Float.toString(P[n]));
        }
        list.add(Float.toString(P[category.length]));
//        Map<String, Float> ProportionMap = new HashMap<String, Float>();
//        for (int n = 0; n < category.length; n++){
//            ProportionMap.put(category[n], P[n]);
//        }
//        ProportionMap.put("other", P[category.length]);
        return list;
    }

    /**
     * 去除字符串str1中的str2
     *
     * @param str1 原字符串
     * @param str2 去掉的字符串
     * @return
     */
    private static String getSubString(String str1, String str2) {
        StringBuffer sb = new StringBuffer(str1);
        while (true) {
            int index = sb.indexOf(str2);
            if (index == -1) {
                break;
            }
            sb.delete(index, index + str2.length());
        }
        return sb.toString();
    }


    private static int MovieNum(Map<String, ArrayList<String>> map)
    {
        return map.size();
    }

    private static float Frequency(Map<String, ArrayList<String>> map)
    {
        Timestamp minT = null;
        Timestamp maxT = null;
        long   min = 0;
        long   max = 0;
        Set<Map.Entry<String, ArrayList<String>>> entries = map.entrySet();
        int count=0;
        for (Map.Entry<String,  ArrayList<String>> entry : entries) {
            long time = Long.parseLong(entry.getValue().get(1));
            minT = new Timestamp(time);
            maxT = new Timestamp(time);
            count++;
            if(count==1)
                break;
        }
        for (Map.Entry<String,  ArrayList<String>> entry : entries) {
            long time = Long.parseLong(entry.getValue().get(1));
            Timestamp temp = new Timestamp(time);
            if(temp.getTime()<minT.getTime()){
                minT=temp;
                min=time;
            }
            if(temp.getTime()>maxT.getTime()){
                maxT=temp;
                max=time;
            }
        }

        Date date1 = new Date(min);
        Date date2 = new Date(max);
        Calendar aCalendar = Calendar.getInstance();

        aCalendar.setTime(date1);
        int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);
        aCalendar.setTime(date2);
        int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);
        float dayNUM=day1 - day2;
        return map.size()*7/dayNUM;//7代表每周

    }
    private static Map<String, ArrayList<String>>  rate_tag(Map<String, ArrayList<String>> rate_map,Map<String, ArrayList<String>> tags_map)
    {

        Map<String, ArrayList<String>> rate_tag_Map = new HashMap<String, ArrayList<String>>();

        for (String key : rate_map.keySet()) {
            ArrayList<String> rate_tag_list = new ArrayList<String>();
            rate_tag_list.add(rate_map.get(key).get(0));
            rate_tag_list.add(tags_map.get(key).get(0));
            rate_tag_Map.put( key,rate_tag_list);
        }

        return rate_tag_Map;
    }


    private static float avg(Map<String, ArrayList<String>> map)
    {
        float sum=0;
        Set<Map.Entry<String, ArrayList<String>>> entries = map.entrySet();
        for (Map.Entry<String,  ArrayList<String>> entry : entries) {
            sum+=Float.parseFloat(entry.getValue().get(0));
        }
        return sum/(map.size());
    }

    private static float var(Map<String, ArrayList<String>> map) {
        float sum = 0;
        float avg = avg(map);
        for (String key:map.keySet()) {
            sum += Math.pow((Float.parseFloat(map.get(key).get(0))-avg),2);
        }
        return sum/(map.size());
    }

    public static Map<String, ArrayList<String>> getUserPro(String id ,Map<String, ArrayList<String>> map) throws InvalidProtocolBufferException, InterruptedException {
       Map<String, ArrayList<String>> userPro_map=new HashMap<String, ArrayList<String>>();
       int num=MovieNum(map);
       float avg=avg(map);
       float var=var(map);

        ArrayList<String> basicInfo = new ArrayList<String>();
        basicInfo.add(id);
        basicInfo.add(Integer.toString(num));
        basicInfo.add(Float.toString(avg));
        basicInfo.add(Float.toString(var));
        userPro_map.put("basicInfo",basicInfo);

        ArrayList<String> proportion=Proportion(map);
        userPro_map.put("type",proportion);
        int i=1;
        for (String key : map.keySet()) {
            ArrayList<String> rating = new ArrayList<String>();
            rating.add(key);
            rating.add(map.get(key).get(0));
            rating.add(map.get(key).get(1));
            userPro_map.put("rating"+i,rating);
            i++;
            if (i > 20) break;
        }
        return userPro_map;
    }

    //测试用
    public static void main(String[] args) throws InterruptedException, InvalidProtocolBufferException {
//        Map<String, ArrayList<String>> movieMap = new HashMap<String, ArrayList<String>>();
//        Map<String, ArrayList<String>> movieMap2 = new HashMap<String, ArrayList<String>>();
//
//        ArrayList<String> movie1 = new ArrayList<String>();
//        movie1.add("5.0");
//        movie1.add("1147880044");
//        movieMap.put("230",movie1);
//
//        ArrayList<String> movie2 = new ArrayList<String>();
//        movie2.add("classic!");
//        movie2.add("1148880044");
//        movieMap2.put("230",movie2);
//
////        ArrayList<String> movie3 = new ArrayList<String>();
////        movie3.add("3.0");
////        movie3.add("1147880046");
////
////        movieMap.put("222",movie3);
//        System.out.println(avg(movieMap));
//        System.out.println(var(movieMap));
//        System.out.println(Frequency(movieMap));
////        System.out.println(Proportion(movieMap));
//        System.out.println(rate_tag(movieMap,movieMap2));


        //Temp
        String strTemp = new simpleDataClient().getRating("1");
        // 利用protoBuf进行反序列化
        DataProto.MovieDataReply userReplyTemp = DataProto.MovieDataReply.parseFrom(TypeConvertUtil.StringToBytearray(strTemp));
        // 将string转化为map
        Map<String, ArrayList<String>> userMap = TypeConvertUtil.StringToMap(userReplyTemp.getData());
        System.out.println("userMap = " + userMap);
        Map<String, ArrayList<String>> userProfileMap= UserPro.getUserPro("1", userMap);
        System.out.println("userProfileMap = " + userProfileMap);

    }
}
