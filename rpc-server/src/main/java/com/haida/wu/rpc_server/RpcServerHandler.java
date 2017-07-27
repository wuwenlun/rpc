package com.haida.wu.rpc_server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.haida.wu.rpc.model.request.ServerRequest;
import com.haida.wu.rpc.model.response.Response;
import com.haida.wu.rpc_server.mediator.Mediator;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class RpcServerHandler extends ChannelInboundHandlerAdapter{
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ServerRequest request = JSONObject.parseObject((String)msg, ServerRequest.class);
		Mediator mediator = new Mediator();
		Response response = mediator.communicate(request);
		ctx.channel().writeAndFlush(JSON.toJSONString(response)+"\r\n");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if(evt instanceof IdleStateEvent) {
			IdleStateEvent state = (IdleStateEvent)evt;
			if(state.state().equals(IdleState.READER_IDLE)) {
				ctx.channel().close();
			} else if(state.state().equals(IdleState.ALL_IDLE)) {
				ctx.channel().writeAndFlush("ping\r\n");
			}
		}
	}

}
