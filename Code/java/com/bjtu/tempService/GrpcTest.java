package com.bjtu.tempService;

import com.BJTU.grpc.helloworld.GreeterGrpc;
import com.BJTU.grpc.helloworld.HelloWorldProto.HelloReply;
import com.BJTU.grpc.helloworld.HelloWorldProto.HelloRequest;
import com.google.protobuf.TextFormat;
import com.googlecode.protobuf.format.JsonFormat;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.TimeUnit;

//grpc客户端类
public class GrpcTest {
    private final ManagedChannel channel;//客户端与服务器的通信channel
    private final GreeterGrpc.GreeterBlockingStub blockStub;//阻塞式客户端存根节点
    public GrpcTest(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext(true).build();//指定grpc服务器地址和端口初始化通信channel
        blockStub = GreeterGrpc.newBlockingStub(channel);//根据通信channel初始化客户端存根节点
    }
    public void shutdown() throws InterruptedException{
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
    //客户端方法
    public void sayHello(String str){
        HelloRequest request = HelloRequest.newBuilder().setName("sample request").build();
        System.out.println("request is : " + TextFormat.shortDebugString(request));
        //客户端存根节点调用grpc服务接口，传递请求参数
        HelloReply response = blockStub.sayHello(request);
        //System.out.println("client, serviceName:" + response.getServiceName() + "; methodName:" + response.getMethodName());

        // protobuf 转 json
        String strJsonResponse = JsonFormat.printToString(response);
        System.out.println("strJsonResponse: " + strJsonResponse);

    }
    public static void main(String[] args) throws InterruptedException{
        //初始化grpc客户端对象
        GrpcTest client = new GrpcTest("127.0.0.1",50051);
        for(int i=0; i<1; i++){
            client.sayHello("client word:"+ i);
            Thread.sleep(1000);
        }
        client.shutdown();
    }
}


