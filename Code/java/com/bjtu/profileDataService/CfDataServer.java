package com.bjtu.profileDataService;

import com.BJTU.grpc.DataProto.DataProto;
import com.BJTU.grpc.DataProto.dataProviderGrpc;
import com.bjtu.simpleDataService.simpleDataClient;
import com.bjtu.utils.RedisUtil;
import com.bjtu.utils.TypeConvertUtil;
import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class CfDataServer {

    private int port = 5004;
    private Server server;

    private void start() throws IOException {
        //启动服务
        System.out.println("*** server start");
        server = ServerBuilder.forPort(port)
                .addService(new cfData())
                .build()
                .start();
        //防止主线程结束
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run(){
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                CfDataServer.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop(){
        if (server != null){
            server.shutdown();
        }
    }

    // block 一直到退出程序
    private void blockUntilShutdown() throws InterruptedException {
        System.out.println("*** sever block until shutdown");
        if (server != null){
            server.awaitTermination();
        }
    }

    //程序入口
    public  static  void main(String[] args) throws IOException, InterruptedException {

        final CfDataServer server = new CfDataServer();
        server.start();
        server.blockUntilShutdown();
    }

    //定义一个实现服务接口的类
    private static class cfData extends dataProviderGrpc.dataProviderImplBase {

        @Override
        public void getUserData(DataProto.UserDataRequest request, StreamObserver<DataProto.UserDataReply> responseObserver) throws InterruptedException, InvalidProtocolBufferException {
            String ID=request.getUserId();
            //@@@@通过用户ID生成用户画像
            // 通过grpc请求用户打分数据并进行反序列化
            String strTemp = new simpleDataClient().getRating(ID);
            DataProto.UserDataReply reply;
            if (strTemp != null){
                // 利用protoBuf进行反序列化
                DataProto.UserDataReply userReplyTemp = DataProto.UserDataReply.parseFrom(TypeConvertUtil.StringToBytearray(strTemp));
                // 将string转化为map
                Map<String, ArrayList<String>> userRatingsMap = TypeConvertUtil.StringToMap(userReplyTemp.getData());
                // 生成用户画像
                Map<String, ArrayList<String>> userProfileMap= UserPro.getUserPro(ID, userRatingsMap);
                //序列化
                DataProto.UserDataReply ufReply = DataProto.UserDataReply.newBuilder().setUserId(ID).setData(TypeConvertUtil.MapToString(userProfileMap)).build();
                byte[] ratingReplyBytes = ufReply.toByteArray();
                String replyStr = TypeConvertUtil.BytearrayToString(ratingReplyBytes);
                // 传输
                reply = DataProto.UserDataReply.newBuilder().setUserId(ID).setData(replyStr).build();
            }else {
                reply = DataProto.UserDataReply.newBuilder().setUserId(ID).setData("").build();
            }

            responseObserver.onNext(reply);
            responseObserver.onCompleted();
            System.out.println("*** Message from Client: Request(UserID):" + ID);
        }

        @Override
        public void getMovieData(DataProto.MovieDataRequest request, StreamObserver<DataProto.MovieDataReply> responseObserver) throws InterruptedException, InvalidProtocolBufferException {
            String id=request.getMovieId();
            //@@@@通过simpleDataClient电影ID获取电影记录
            DataProto.MovieDataReply reply = DataProto.MovieDataReply.newBuilder().setMovieId(id).setData(RedisUtil.getString(RedisUtil.Index.MOVIE_INFO + id)).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
            System.out.println("*** Message from Client: Request(MovieID):" + id);
        }

    }

}
