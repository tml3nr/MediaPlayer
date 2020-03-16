package org.rpi.songcast.ohu.receiver;

import java.util.concurrent.ConcurrentHashMap;

import org.rpi.songcast.ohu.receiver.handlers.OHUMessageAudioHandler;
import org.rpi.songcast.ohu.receiver.handlers.OHUMessageBuffefHandler;
import org.rpi.songcast.ohu.receiver.handlers.OHUMessageDecoder;
import org.rpi.songcast.ohu.receiver.handlers.OHUMessageMetaTextHandler;
import org.rpi.songcast.ohu.receiver.handlers.OHUMessageSlaveHandler;
import org.rpi.songcast.ohu.receiver.handlers.OHUMessageTrackHandler;
import org.rpi.songcast.ohu.receiver.handlers.OHUSlaveForwarder;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class OHUChannelInitializer extends ChannelInitializer<NioDatagramChannel> {
	
	private ConcurrentHashMap<String, SlaveInfo> endpoints = new ConcurrentHashMap<String, SlaveInfo>();

	@Override
	protected void initChannel(NioDatagramChannel ch) throws Exception {
		ChannelPipeline p = ch.pipeline();
		//p.addLast(new LoggingHandler(LogLevel.DEBUG));
		p.addLast("OHUDecoder",new OHUMessageDecoder(this));
		p.addLast("OHUSlaveForwarder",new OHUSlaveForwarder(this));
		p.addLast("OHUMessageBuffer", new OHUMessageBuffefHandler());		
		p.addLast("OHUAudioHandler", new OHUMessageAudioHandler());
		p.addLast("OHMTrackHandler", new OHUMessageTrackHandler());
		p.addLast("OHMMessageMetaTextHandler", new OHUMessageMetaTextHandler());
		p.addLast("OHUSlaveHandler", new OHUMessageSlaveHandler());
		p.addLast("OHULeakCatcher", new OHULeakCatcher());
		
	}

	/**
	 * Set the Slave Endpoints
	 * @param endpoints
	 */
	public void setEndpoints(ConcurrentHashMap<String, SlaveInfo> endpoints) {
		this.endpoints = endpoints;
		
	}

	/**
	 * Do we have any Slaves
	 * @return
	 */
	public boolean hasSlaves() {
		return endpoints.size()>0;
	}
}
