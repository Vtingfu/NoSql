package com.bjtu.simpleDataService;

import com.BJTU.grpc.DataProto.DataProto;
import com.BJTU.grpc.DataProto.dataProviderGrpc;
import com.bjtu.utils.RedisUtil;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;

public class simpleDataServer {

    private int port = 5005;
    private Server server;

    private void start() throws IOException {
        //启动服务
        System.out.println("*** server start");
        server = ServerBuilder.forPort(port)
                .addService(new SimpleData())
                .build()
                .start();
        //防止主线程结束
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run(){
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                simpleDataServer.this.stop();
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

        final simpleDataServer server = new simpleDataServer();
        server.start();
        server.blockUntilShutdown();
    }

    //定义一个实现服务接口的类
    private static class SimpleData extends dataProviderGrpc.dataProviderImplBase {

        @Override
        public void getUserData(DataProto.UserDataRequest request, StreamObserver<DataProto.UserDataReply> responseObserver) {
            String ID=request.getUserId();
            //@@@@通过用户ID获取用户打分记录
            com.BJTU.grpc.DataProto.DataProto.UserDataReply reply = com.BJTU.grpc.DataProto.DataProto.UserDataReply.newBuilder().setUserId(ID).setData(RedisUtil.getString(RedisUtil.Index.USER_RATES + ID)).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
            System.out.println("*** Message from Client: Request(UserID):" + ID);
        }

        @Override
        public void getMovieData(DataProto.MovieDataRequest request, StreamObserver<DataProto.MovieDataReply> responseObserver) {
            String id=request.getMovieId();
            //@@@@通过电影ID获取电影记录
            DataProto.MovieDataReply reply = DataProto.MovieDataReply.newBuilder().setMovieId(id).setData(RedisUtil.getString(RedisUtil.Index.MOVIE_INFO + id)).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
            System.out.println("*** Message from Client: Request(MovieID):" + id);
        }

    }

}
