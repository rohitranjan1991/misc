package com.example.video;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

import android.media.AudioManager;
import android.media.AudioTrack;
import android.widget.Toast;

public class video_receiving extends Thread {

	@Override
	public void run() {
	
		
		receiveAndPlayVoice();
		super.run();
	}
	
	
	public void receiveAndPlayVoice() {
		//am.setMode(AudioManager.MODE_IN_CALL);

		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(5456);
			byte[] buffer = new byte[8192];
			
			boolean isReceiving = true;
			//socket.setSoTimeout(2000);

			while (isReceiving) {

				DatagramPacket packet = new DatagramPacket(buffer,
						buffer.length);
				try {
					socket.receive(packet);
					buffer = packet.getData();
					
				} catch (Exception e) {
					isReceiving = false;
					
					
				}
				
			} 
			
			//am.setMode(AudioManager.MODE_NORMAL);
			
			if (!socket.isClosed()) {
				socket.close();
				socket = null;
			}

			
		} catch (Exception e) {
			if (!socket.isClosed()) {
				socket.close();
				socket = null;
			}
			e.printStackTrace();
		}

	}

}
