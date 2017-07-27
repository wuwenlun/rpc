package com.haida.wu.rpc.utils.netty;

import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;

import com.alibaba.fastjson.JSON;
import com.haida.wu.rpc.utils.CommonUtils;
import com.haida.wu.rpc.utils.zookeeper.ZookeeperFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class RpcClient {
	
	EventLoopGroup loopGroup = new NioEventLoopGroup(1);
	Bootstrap bootstrap = new Bootstrap();
	
	private void connect() {
		CuratorFramework zookeeper = ZookeeperFactory.createClient("rpc.properties");
		try {
			List<String> serverPaths = zookeeper.getChildren().forPath(CommonUtils.ZOOKEEPER_ROOT_DATA_PATH);
			for(String path : serverPaths) {
				String[] info = path.split("#");
				String addressInfo = info[0];
				String[] ipPort = addressInfo.split(":");
				String ip = ipPort[0];
				int port = Integer.parseInt(ipPort[1]);
				int weight = Integer.parseInt(info[1]);
				connect(ip,port,weight);
			}
			zookeeper.getChildren().usingWatcher(new CuratorWatcher() {
				
				@Override
				public void process(WatchedEvent event) throws Exception {
					ChannelManager.clear();
					connect();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void connect(final String ip,final int port,int weight) {
		bootstrap
			.group(loopGroup)
			.channel(NioSocketChannel.class)
			.option(ChannelOption.TCP_NODELAY, true)
			.option(ChannelOption.SO_KEEPALIVE, false)
			.handler(new ChannelInitializer<NioSocketChannel>() {
				@Override
				protected void initChannel(NioSocketChannel ch) throws Exception {
					ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
					ch.pipeline().addLast(new StringDecoder());
					ch.pipeline().addLast(new RpcClientHandler());
					ch.pipeline().addLast(new StringEncoder());
				}
			});
		try {
			final ChannelFuture f = bootstrap.connect(ip, port).sync();
			final ChannelFuture copy = f;
			ChannelManager.add(weight, f);
			f.channel().closeFuture().addListener(new GenericFutureListener(){
				@Override
				public void operationComplete(Future future) throws Exception {
					ChannelFuture channelFuture = bootstrap.connect(ip, port).sync();
					ChannelManager.replace(copy, channelFuture);
				}
			});
		} catch (InterruptedException e) {
			e.printStackTrace();
			loopGroup.shutdownGracefully();
		} finally {
		}
	}
	
	
	public static Response send(Request request) {
		ChannelFuture f = ChannelManager.get();
		synchronized (RpcClient.class) {
			if(f==null) {
				new RpcClient().connect();
			}
		}
		String msg = JSON.toJSONString(request);
		DefaultFuture future = new DefaultFuture(request);
		f.channel().writeAndFlush(msg+"\r\n");
		Response response = future.get(1000);
		return response;
	}
	
}
