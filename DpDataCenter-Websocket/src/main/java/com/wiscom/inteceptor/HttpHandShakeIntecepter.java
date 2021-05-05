package com.wiscom.inteceptor;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 功能描述：http握手拦截器，可以通过这个类的方法获取resuest和response
 */
public class HttpHandShakeIntecepter implements HandshakeInterceptor{

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
		//判断是不是子类
		if(request instanceof ServletServerHttpRequest) {
			ServletServerHttpRequest servletRequest = (ServletServerHttpRequest)request;
			//获取session对象
			HttpSession session = servletRequest.getServletRequest().getSession();
			//获得sessionId
			String sessionId = session.getId();
			//获取客户端的ip地址,并进行截取
			String ip = request.getRemoteAddress().toString();
			ip = ip.substring(ip.indexOf("/")+1, ip.indexOf(":"));
			//获取客户端的端口号
			int port = request.getRemoteAddress().getPort();
			//存放sessionId、ip地址、端口等信息
			Map<String, Object> clientInfoMap = new HashMap<>();
			clientInfoMap.put("sessionId", sessionId);
			clientInfoMap.put("ip", ip);
			clientInfoMap.put("port", port);
			attributes.put("attributes", clientInfoMap);
		}
		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
	}

}