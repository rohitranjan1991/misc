package com.example.video_stream;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class receiveActivity extends Activity {

	final Handler mHandle = new Handler() {

		private Handler getVideoHandler;

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:

				// for storing the handler
				switch (msg.arg1) {
				case 0:
					getVideoHandler= (Handler) msg.obj;
					break;
				
				}
				break;
				}
			}
		};
	
		SurfaceView vid;

		private SurfaceHolder vid_holder;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.receive_xml);
		vid=(SurfaceView) findViewById(R.id.custom_receive_view);
		vid_holder=vid.getHolder();
		
		
		
		

	}

	private class getVideo extends Thread {

		Handler baseHandler;
		final Handler tHandle = new Handler() {
			@Override
			public void handleMessage(Message msg) {

				if (msg.obj.toString().contentEquals("Stop")) {
					// waitForReply=false;
				}

			}
		};
		
		
		Boolean isReceiving=true;

		public getVideo(Handler baseHandler) {
			this.baseHandler = baseHandler;
			baseHandler
					.sendMessage(baseHandler.obtainMessage(0, 0, 0, tHandle));

		}

		@Override
		public void run() {
			byte[] buffer = new byte[460800];
			DatagramSocket socket = null;
			try{
				
				socket = new DatagramSocket(7689);
			while(isReceiving)
			{
				
				DatagramPacket packet = new DatagramPacket(buffer,
						buffer.length);
				try {
					socket.receive(packet);
					buffer = packet.getData();
					
					  Canvas canvas = null;
					
					try {
				        synchronized (vid_holder) {
				            canvas = vid_holder.lockCanvas(null);
				            int canvasWidth = canvas.getWidth();
				            int canvasHeight = canvas.getHeight();
				            
				           // decodeYUV(rgbints, data, width, height);

				            // draw the decoded image, centered on canvas
				            canvas.drawBitmap(rgbints, 0, width, canvasWidth-((width+canvasWidth)>>1), canvasHeight-((height+canvasHeight)>>1), width, height, false, null);

				            // use some color filter
				           // canvas.drawColor(mMultiplyColor, Mode.MULTIPLY);

				        }
				    }  catch (Exception e){
				        e.printStackTrace();
				    } finally {
				        // do this in a finally so that if an exception is thrown
				        // during the above, we don't leave the Surface in an
				        // inconsistent state
				        if (canvas != null) {
				            mHolder.unlockCanvasAndPost(canvas);
				        }
				    }
					
					
					
					
					
					
					
				} catch (Exception e) {
					isReceiving = false;
										
				}
				
			}
			}
			catch(Exception e)
			{
				Log.d("video receive ", e.getLocalizedMessage());
			}
			
			super.run();
		}
		
		/**
		 * Decodes YUV frame to a buffer which can be use to create a bitmap. use
		 * this for OS < FROYO which has a native YUV decoder decode Y, U, and V
		 * values on the YUV 420 buffer described as YCbCr_422_SP by Android
		 * 
		 * @param rgb
		 *            the outgoing array of RGB bytes
		 * @param fg
		 *            the incoming frame bytes
		 * @param width
		 *            of source frame
		 * @param height
		 *            of source frame
		 * @throws NullPointerException
		 * @throws IllegalArgumentException
		 */
		public void decodeYUV(int[] out, byte[] fg, int width, int height)
				throws NullPointerException, IllegalArgumentException {
			int sz = width * height;
			if (out == null)
				throw new NullPointerException("buffer out is null");
			if (out.length < sz)
				throw new IllegalArgumentException("buffer out size " + out.length
						+ " < minimum " + sz);
			if (fg == null)
				throw new NullPointerException("buffer 'fg' is null");
			if (fg.length < sz)
				throw new IllegalArgumentException("buffer fg size " + fg.length
						+ " < minimum " + sz * 3 / 2);
			int i, j;
			int Y, Cr = 0, Cb = 0;
			for (j = 0; j < height; j++) {
				int pixPtr = j * width;
				final int jDiv2 = j >> 1;
				for (i = 0; i < width; i++) {
					Y = fg[pixPtr];
					if (Y < 0)
						Y += 255;
					if ((i & 0x1) != 1) {
						final int cOff = sz + jDiv2 * width + (i >> 1) * 2;
						Cb = fg[cOff];
						if (Cb < 0)
							Cb += 127;
						else
							Cb -= 128;
						Cr = fg[cOff + 1];
						if (Cr < 0)
							Cr += 127;
						else
							Cr -= 128;
					}
					int R = Y + Cr + (Cr >> 2) + (Cr >> 3) + (Cr >> 5);
					if (R < 0)
						R = 0;
					else if (R > 255)
						R = 255;
					int G = Y - (Cb >> 2) + (Cb >> 4) + (Cb >> 5) - (Cr >> 1)
							+ (Cr >> 3) + (Cr >> 4) + (Cr >> 5);
					if (G < 0)
						G = 0;
					else if (G > 255)
						G = 255;
					int B = Y + Cb + (Cb >> 1) + (Cb >> 2) + (Cb >> 6);
					if (B < 0)
						B = 0;
					else if (B > 255)
						B = 255;
					out[pixPtr++] = 0xff000000 + (B << 16) + (G << 8) + R;
				}
			}

		}

		private void showSupportedCameraFormats(Parameters p) {
			List<Integer> supportedPictureFormats = p.getSupportedPreviewFormats();
			/*Log.d(TAG,
					"preview format:"
							+ cameraFormatIntToString(p.getPreviewFormat()));
			for (Integer x : supportedPictureFormats) {
				Log.d(TAG,
						"suppoterd format: "
								+ cameraFormatIntToString(x.intValue()));*/
			}

		}

		private String cameraFormatIntToString(int format) {
			switch (format) {
			case PixelFormat.JPEG:
				return "JPEG";
			case PixelFormat.YCbCr_420_SP:
				return "NV21";
			case PixelFormat.YCbCr_422_I:
				return "YUY2";
			case PixelFormat.YCbCr_422_SP:
				return "NV16";
			case PixelFormat.RGB_565:
				return "RGB_565";
			default:
				return "Unknown:" + format;

			}
		}

	}


