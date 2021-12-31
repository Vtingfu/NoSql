package com.bjtu.simpleDataService;

import com.BJTU.grpc.DataProto.DataProto;
import com.BJTU.grpc.DataProto.dataProviderGrpc;
import com.bjtu.utils.RedisUtil;
import com.bjtu.utils.TypeConvertUtil;
import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class simpleDataClient {
    private final String host = "127.0.0.1";
    private final int port = 5005;
    private final ManagedChannel channel;
    private final dataProviderGrpc.dataProviderBlockingStub blockingStub;

    public simpleDataClient() {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext(true)
                .build();

        blockingStub = dataProviderGrpc.newBlockingStub(channel);
    }


    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    // user相关数据请求
    private String userRatingsRequest(String ID) {

        DataProto.UserDataRequest request = DataProto.UserDataRequest.newBuilder().setUserId(ID).build();
        DataProto.UserDataReply response;
        try {
            response = blockingStub.getUserData(request);
        } catch (StatusRuntimeException e) {
            //输出错误
            return null;
        }
        return response.getData();
    }
   //提供给外界的接口，获取从服务器获得的打分数据
    public String getRating(String userID) throws InterruptedException {
   // simpleDataClient client = new simpleDataClient();
        try {
            return userRatingsRequest(userID);
        } finally {
            shutdown();
        }
    }

    // movie相关数据请求
    private String movieInfoRequest(String ID) {
        DataProto.MovieDataRequest request = DataProto.MovieDataRequest.newBuilder().setMovieId(ID).build();
        DataProto.MovieDataReply response;
        try {
            response = blockingStub.getMovieData(request);
        } catch (StatusRuntimeException e) {
            //输出错误
            return null;
        }
        return response.getData();
    }
    //提供给外界的接口，获取从服务器获得的打分数据
    public String getMovieInfo(String movieID) throws InterruptedException {
        // simpleDataClient client = new simpleDataClient();
        try {
            return movieInfoRequest(movieID);
        } finally {
            shutdown();
        }
    }

    public static void main(String[] args) throws InterruptedException, InvalidProtocolBufferException {
        String strTemp = new simpleDataClient().getRating("1");
//        String strTemp = RedisUtil.getString(RedisUtil.Index.MOVIE_INFO + "1");
//        System.out.println("strTemp = " + strTemp);
        // 利用protoBuf进行反序列化
//        DataProto.UserDataReply userReplyTemp = DataProto.UserDataReply.parseFrom(TypeConvertUtil.StringToBytearray(strTemp));
//        // 将string转化为map
//        Map<String, ArrayList<String>> userMap = TypeConvertUtil.StringToMap(userReplyTemp.getData());
//        // 测试输出
//        System.out.println("userMap = " + userMap);

    }
}


