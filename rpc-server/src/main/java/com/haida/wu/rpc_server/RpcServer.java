package com.haida.wu.rpc_server;

import java.net.InetAddress;
import java.util.Properties;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.haida.wu.rpc.utils.CommonUtils;
import com.haida.wu.rpc.utils.PropertiesUtil;
import com.haida.wu.rpc_server.factory.ZookeeperFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

@Component
public class RpcServer implements ApplicationListener<ContextRefreshedEvent>{
	
	public void bind() {
		EventLoopGroup bossLoop = new NioEventLoopGroup();
		EventLoopGroup workerLoop = new NioEventLoopGroup();
		
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap
			.group(bossLoop, workerLoop)
			.channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_BACKLOG, 1024)
			.option(ChannelOption.SO_KEEPALIVE, false)
			.childHandler(new ChildChannelHandler());
		
		try {
			Properties properties = new PropertiesUtil("config.properties").getProperties();
			int port = Integer.parseInt(properties.getProperty(CommonUtils.PORT));
			
			ChannelFuture future = bootstrap.bind(port).sync();
			
			CuratorFramework client = ZookeeperFactory.createClient();
			String localAdress = InetAddress.getLocalHost().getHostAddress();
			String serverPath = localAdress+":"+port;
			String rootPath = CommonUtils.ZOOKEEPER_ROOT_DATA_PATH;
			
			String weight = properties.getProperty(CommonUtils.WEIGHT);
			client.create().withMode(CreateMode.EPHEMERAL).forPath(rootPath+serverPath+"#"+weight+"#");
			
			future.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			bossLoop.shutdownGracefully();
			workerLoop.shutdownGracefully();
		}
	}
	
	private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
			ch.pipeline().addLast(new StringDecoder());
			ch.pipeline().addLast(new IdleStateHandler(30, 30, 90));
			ch.pipeline().addLast(new RpcServerHandler());
			ch.pipeline().addLast(new StringEncoder());
		}
		
	}
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		bind();
	}

}
