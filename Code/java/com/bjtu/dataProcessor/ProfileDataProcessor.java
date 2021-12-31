package com.bjtu.dataProcessor;

import com.BJTU.grpc.DataProto.DataProto;
import com.BJTU.grpc.simpleData.SimpleData;
import com.bjtu.profileDataService.CfDataClient;
import com.bjtu.profileDataService.UserPro;
import com.bjtu.utils.MongoDbUtil;
import com.bjtu.utils.RedisUtil;
import com.bjtu.utils.TypeConvertUtil;
import com.google.protobuf.InvalidProtocolBufferException;

import java.util.ArrayList;
import java.util.Map;

/**
 * Data Processor class for profiles.
 * 针对画像的数据处理类.
 *
 * @author HelloTeam
 * @version 2021/12/26
 */
public class ProfileDataProcessor {
    public static void main(String[] args) throws InterruptedException, InvalidProtocolBufferException {
        try {
            // Initialize MongoDB
            MongoDbUtil.Init();
            int index = 0;
            while (++index <= 5){
                // Get an user's rating record from Redis
                String strTemp = RedisUtil.getString(RedisUtil.Index.USER_RATES + index);
                // Resolve simple data
                SimpleData.RatingReply userReplyTemp = SimpleData.RatingReply.parseFrom(TypeConvertUtil.StringToBytearray(strTemp));
                // Get the user's information map
                Map<String, ArrayList<String>> userMap = TypeConvertUtil.StringToMap(userReplyTemp.getRates());
                // Get the user's profile information
                Map<String, ArrayList<String>> userProfileMap= UserPro.getUserPro(String.valueOf(index), userMap);
                System.out.println("user = " + userProfileMap.get("basicInfo").get(0) + userProfileMap.get("type"));
                // Store the searched user profile map into MongoDB
                MongoDbUtil.PutDoc("testCollection", userProfileMap);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
