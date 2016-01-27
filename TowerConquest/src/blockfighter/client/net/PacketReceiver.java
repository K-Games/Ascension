/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blockfighter.client.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import blockfighter.client.screen.ScreenIngame;
import blockfighter.client.screen.ScreenLoading;
import blockfighter.client.screen.ScreenServerList;

/**
 *
 * @author Ken Kwan
 */
public class PacketReceiver extends Thread {

	private static LogicModule logic;
	private DatagramSocket socket = null;
	private static ExecutorService threadPool = Executors.newFixedThreadPool(5);
	private boolean isConnected = true;

	public static void setLogic(final LogicModule l) {
		logic = l;
	}

	public PacketReceiver(final DatagramSocket s) {
		this.socket = s;
	}

	@Override
	public void run() {
		try {
			while (true) {
				final byte[] request = new byte[Globals.PACKET_MAX_SIZE];
				final DatagramPacket p = new DatagramPacket(request, request.length);
				this.socket.receive(p);
				threadPool.execute(new PacketHandler(p));
			}
		} catch (final SocketTimeoutException e) {
			if (logic.getScreen() instanceof ScreenServerList) {
				((ScreenServerList) logic.getScreen()).setStatus(ScreenServerList.STATUS_FAILEDCONNECT);
			}
		} catch (final SocketException e) {
			if (logic.getScreen() instanceof ScreenServerList) {
				((ScreenServerList) logic.getScreen()).setStatus(ScreenServerList.STATUS_SOCKETCLOSED);
			}
		} catch (final IOException ex) {
		}
		System.out.println("Receiver End");
		if (logic.getScreen() instanceof ScreenIngame || logic.getScreen() instanceof ScreenLoading) {
			logic.returnMenu();
		}
		this.isConnected = false;
	}

	public void shutdown() {
		this.socket.close();
	}

	public boolean isConnected() {
		return this.isConnected;
	}
}
