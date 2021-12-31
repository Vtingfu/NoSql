package com.bjtu.dataProcessor;

import com.BJTU.grpc.DataProto.DataProto;
import com.bjtu.utils.RedisUtil;
import com.bjtu.utils.TypeConvertUtil;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SimpleDataProcessor {
    // 文件路径
    public static final String ratings16Path = "src/main/resources/data/shuffered/ratings16.csv";
    public static final String ratings20Path = "src/main/resources/data/shuffered/ratings20.csv";
    public static final String ratings64Path = "src/main/resources/data/shuffered/ratings64.csv";
    public static final String ratings80Path = "src/main/resources/data/shuffered/ratings80.csv";
    public static final String moviesPath = "src/main/resources/data/raw/movies.csv";

    // 存入用户打分数据
    public static void putUserRates() {
        Map<String, ArrayList<String>> rates = new HashMap<String, ArrayList<String>>();
        ArrayList<String> strings = new ArrayList<String>();
        ArrayList<String> infoTemp = new ArrayList<String>();
        String key;
        //ratings存入过程
        int userTemp = 0;
        // 进度计数器
        int count = 0;
        try (Reader reader = Files.newBufferedReader(Paths.get(ratings80Path))) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(reader);
            // 循环处理数据
            for (CSVRecord record : records) {
                infoTemp.add(record.get(0));
                infoTemp.add(record.get(1));
                infoTemp.add(record.get(2));
                infoTemp.add(record.get(3));

                // 满足存入条件
                if ((Integer.parseInt(infoTemp.get(0)) != userTemp || !records.iterator().hasNext()) && !rates.isEmpty()){
                    String ratesStr = TypeConvertUtil.MapToString(rates);
                    rates.clear();
                    DataProto.UserDataReply ratingReply = DataProto.UserDataReply.newBuilder().setUserId(String.valueOf(userTemp)).setData(ratesStr).build();
                    byte[] ratingReplyBytes = ratingReply.toByteArray();
                    String ratingReplyStr = TypeConvertUtil.BytearrayToString(ratingReplyBytes);
                    //开始存入数据库
                    key = RedisUtil.Index.USER_RATES + userTemp;
                    //System.out.println("ratingReplyStr = " + ratingReplyStr);
                    RedisUtil.putSting(RedisUtil.Index.USER_RATES + userTemp, ratingReplyStr);
                    System.out.println("putting user ratings: " + userTemp);
                } else {
                    userTemp = Integer.parseInt(infoTemp.get(0));
                    // 向当前容器插入数据
                    strings.add(infoTemp.get(2));
                    strings.add(infoTemp.get(3));
                    rates.put(record.get(1), new ArrayList<String>(strings));
                    strings.clear();
                    infoTemp.clear();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // 存入电影信息数据
    public static void putMovies() {
        Map<String, ArrayList<String>> moviesMap = new HashMap<String, ArrayList<String>>();
        ArrayList<String> strings = new ArrayList<String>();
        String key;
        int count = 0;
        //movies存入过程
        try (Reader reader = Files.newBufferedReader(Paths.get(moviesPath))) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(reader);
            // 循环处理数据
            for (CSVRecord record : records) {
                // 插入数据
                strings.clear();
                strings.add(record.get(1));
                strings.add(record.get(2));
                moviesMap.put("BasicInfo", strings);

                // 存入数据库
                String movieInfoMapStr = TypeConvertUtil.MapToString(moviesMap);
                moviesMap.clear();
                DataProto.MovieDataReply ratingReply = DataProto.MovieDataReply.newBuilder().setMovieId(record.get(0)).setData(movieInfoMapStr).build();
                byte[] movieReplyBytes = ratingReply.toByteArray();
                String movieReplyStr = TypeConvertUtil.BytearrayToString(movieReplyBytes);
                //开始存入数据库
                key = RedisUtil.Index.MOVIE_INFO + record.get(0);
                //System.out.println("movieReplyStr = " + movieReplyStr);
                RedisUtil.putSting(key, movieReplyStr);
                System.out.println("putting movies info: " + record.get(0));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) throws InvalidProtocolBufferException{
        // User相关
        putUserRates();
        // user-rates反序列化过程
//        String strTemp = RedisUtil.getString(RedisUtil.Index.USER_RATES + "1");
//        SimpleData.RatingReply userReplyTemp = SimpleData.RatingReply.parseFrom(TypeConvertUtil.StringToBytearray(strTemp));
//        Map<String, ArrayList<String>> userMap = TypeConvertUtil.StringToMap(userReplyTemp.getRates());
//        System.out.println("userMap = " + userMap);

        // Movie相关
        putMovies();

    }
}
