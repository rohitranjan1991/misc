package com.example.video;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Date;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.media.MediaRecorder;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.Toast;

public class MainActivity extends Activity implements Callback, PreviewCallback {

	@Override
	protected void onDestroy() {
		stopRecording();
		super.onDestroy();
	}

	private SurfaceHolder surfaceHolder;
	private SurfaceView surfaceView;
	public MediaRecorder mrec = new MediaRecorder();
	private Camera mCamera;
	private DatagramSocket dsocket;
	private LocalSocket receiver;
	LocalServerSocket server;
	LocalSocket sender = null;
	private SurfaceView surfaceView1;
	private SurfaceHolder surfaceHolder1;
	File tempFile=null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		surfaceView = (SurfaceView) findViewById(R.id.surface_camera);
		mCamera = Camera.open();
		mCamera.setPreviewCallbackWithBuffer(this);

		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceView1 = (SurfaceView) findViewById(R.id.surface_view1);
		surfaceHolder1 = surfaceView1.getHolder();
		surfaceHolder1.addCallback(this);
		surfaceHolder1.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		// new video_receiving().start();
		new Thread() {

			@Override
			public void run() {
				
					byte[] buff=new byte[8192];
					try {
						server = new LocalServerSocket("abc");

						receiver = new LocalSocket();
						receiver.connect(server.getLocalSocketAddress());
						receiver.setReceiveBufferSize(8192);
						//receiver.bind(server.getLocalSocketAddress());
						
							
						sender = server.accept();
						sender.setSendBufferSize(8192);
						
						byte[] mdat = { 'm', 'd', 'a', 't' };
						byte[] buffer = new byte[mdat.length];
						int pos=-1;
						Boolean mainVideo=false;
						while(true)
						{
							
							if(mainVideo)
							{ 
								sender.getInputStream().read(buff);
								pos= searchFor(buff, mdat);
							if(pos!=-1)
							{
								mainVideo=true;
							}
							}
							else{
								InputStream _remoteStream = sender.getInputStream();
								
								 File tempFile = File.createTempFile("abc","mp4");
							        tempFile.deleteOnExit();
							        FileInputStream _localInStream = new FileInputStream(tempFile);
							        FileOutputStream _localOutStream = new FileOutputStream(tempFile);
							        int buffered = bufferMedia(
							                _remoteStream, _localOutStream, buff.length      // = 128KB for instance
							            );
							        
							        
							        
							        
							}
							
							
						}
						

					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			private
			int bufferMedia(InputStream inStream, OutputStream outStream, int nBytes)
			throws IOException
			{
			    final int BUFFER_SIZE = 8 * (1 << 10);
			    byte[] buffer = new byte[BUFFER_SIZE];          // TODO: Do static allocation instead

			    int buffered = 0, read = -1;
			    while (buffered < nBytes) {
			        read = inStream.read(buffer);
			        if (read == -1) {
			            break;
			        }           
			        outStream.write(buffer, 0, read);
			        outStream.flush();
			        buffered += read;
			    }

			    if (read == -1 && buffered == 0) {
			        return -1;
			    }

			    return buffered;
			}

			 public int searchFor(byte[] array, byte[] subArray) {
			        if (subArray.length > array.length)
			            return -2;
			        int p = (new String(array)).indexOf(new String(subArray));
			        for (int i = 1; i < subArray.length; i++) {
			            if (array[p + i] != subArray[i])
			                return -1;
			        }
			        return p;
			    }
			
		}.start();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(0, 0, 0, "Start");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().equals("Start")) {
			try {

				new Thread() {

					@Override
					public void run() {

						try {
							startRecording();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						super.run();
					}
				}.start();

				// startRecording();
				 item.setTitle("Stop");

			} catch (Exception e) {

				String message = e.getMessage();
				Log.i(null, "Problem " + message);
				mrec.release();
			}

		} else if (item.getTitle().equals("Stop")) {
			mrec.stop();
			mrec.release();
			mrec = null;
			item.setTitle("Start");
		}

		return super.onOptionsItemSelected(item);
	}

	protected void startRecording() throws IOException {
		if (mCamera == null)
			mCamera = Camera.open();

		String filename;
		String path;

		path = Environment.getExternalStorageDirectory().getAbsolutePath()
				.toString();

		Date date = new Date();
		// filename="/rec"+date.toString().replace(" ", "_").replace(":",
		// "_")+".mp4";
		filename = "/rec.mp4";
		// create empty file it must use
		File file = new File(path, filename);

		// ///////////////////////////////////////////////////////////////////////////

		/*
		 * String ip = "10.0.2.59";
		 * 
		 * final InetAddress destination = InetAddress.getByName(ip);
		 * DatagramSocket socket = new
		 * DatagramSocket(5456,InetAddress.getByName(ip)); ParcelFileDescriptor
		 * pfd = ParcelFileDescriptor.fromDatagramSocket(socket);
		 */

		// ///////////////////////////////////////////////////////////////////////////

		mrec = new MediaRecorder();

		mCamera.lock();
		mCamera.unlock();

		// Please maintain sequence of following code.

		// If you change sequence it will not work.
		mrec.setCamera(mCamera);
		mrec.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		mrec.setAudioSource(MediaRecorder.AudioSource.MIC);
		mrec.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		mrec.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
		mrec.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		// mrec.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		mrec.setPreviewDisplay(surfaceHolder.getSurface());
		//mrec.setOutputFile("/mnt/sdcard/rec.mp4");
		
		mrec.setOutputFile(receiver.getFileDescriptor());
		
		mrec.prepare();
		mrec.start();

	}

	protected void stopRecording() {

		if (mrec != null) {
			mrec.stop();
			mrec.release();
			mCamera.release();
			mCamera.lock();
		}
	}

	private void releaseMediaRecorder() {

		if (mrec != null) {
			mrec.reset(); // clear recorder configuration
			mrec.release(); // release the recorder object
		}
	}

	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.release(); // release the camera for other applications
			mCamera = null;
		}

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		if (mCamera != null) {
			Parameters params = mCamera.getParameters();
			mCamera.setParameters(params);
			Log.i("Surface", "Created");
		} else {
			Toast.makeText(getApplicationContext(), "Camera not available!",
					Toast.LENGTH_LONG).show();

			finish();
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mCamera.stopPreview();
		mCamera.release();

	}

	@Override
	public void onPreviewFrame(byte[] data, Camera arg1) {
		YuvImage image = new YuvImage(data, ImageFormat.JPEG, 150, 150, null);
		// image.compressToJpeg(new Rect(150,150),);

	}
	
	
	//self streaming server
	

public class StreamProxy implements Runnable {

    private static final int SERVER_PORT=8888;

    private Thread thread;
    private boolean isRunning;
    private ServerSocket socket;
    private int port;

    public StreamProxy() {

        // Create listening socket
        try {
          socket = new ServerSocket(SERVER_PORT, 0, InetAddress.getByAddress(new byte[] {127,0,0,1}));
          socket.setSoTimeout(5000);
          port = socket.getLocalPort();
        } catch (UnknownHostException e) { // impossible
        } catch (IOException e) {
         // Log.e(TAG, "IOException initializing server", e);
        }

    }

    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        isRunning = false;
        thread.interrupt();
        try {
            thread.join(5000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
    }

    @Override
      public void run() {
        Looper.prepare();
        isRunning = true;
        while (isRunning) {
          try {
            Socket client = socket.accept();
            if (client == null) {
              continue;
            }
           // Log.d(TAG, "client connected");

            StreamToMediaPlayerTask task = new StreamToMediaPlayerTask(client);
            if (task.processRequest()) {
                task.execute();
            }

          } catch (SocketTimeoutException e) {
            // Do nothing
          } catch (IOException e) {
          //  Log.e(TAG, "Error connecting to client", e);
          }
        }
       // Log.d(TAG, "Proxy interrupted. Shutting down.");
      }




    private class StreamToMediaPlayerTask extends AsyncTask<String, Void, Integer> {

        String localPath;
        Socket client;
        int cbSkip;

        public StreamToMediaPlayerTask(Socket client) {
            this.client = client;
        }

        public boolean processRequest() {
            // Read HTTP headers
            String headers = "";
            try {
              headers = Utils.readTextStreamAvailable(client.getInputStream());
            } catch (IOException e) {
             // Log.e(TAG, "Error reading HTTP request header from stream:", e);
              return false;
            }

            // Get the important bits from the headers
            String[] headerLines = headers.split("\n");
            String urlLine = headerLines[0];
            if (!urlLine.startsWith("GET ")) {
              //  Log.e(TAG, "Only GET is supported");
                return false;               
            }
            urlLine = urlLine.substring(4);
            int charPos = urlLine.indexOf(' ');
            if (charPos != -1) {
                urlLine = urlLine.substring(1, charPos);
            }
            localPath = urlLine;

            // See if there's a "Range:" header
            for (int i=0 ; i<headerLines.length ; i++) {
                String headerLine = headerLines[i];
                if (headerLine.startsWith("Range: bytes=")) {
                    headerLine = headerLine.substring(13);
                    charPos = headerLine.indexOf('-');
                    if (charPos>0) {
                        headerLine = headerLine.substring(0,charPos);
                    }
                    cbSkip = Integer.parseInt(headerLine);
                }
            }
            return true;
        }

        @Override
        protected Integer doInBackground(String... params) {

                        long fileSize = tempFile.length();

            // Create HTTP header
            String headers = "HTTP/1.0 200 OK\r\n";
            headers += "Content-Type: " + "video/mp4v-es" + "\r\n";
            headers += "Content-Length: " + fileSize  + "\r\n";
            headers += "Connection: close\r\n";
            headers += "\r\n";

            // Begin with HTTP header
            int fc = 0;
            long cbToSend = fileSize - cbSkip;
            OutputStream output = null;
            byte[] buff = new byte[64 * 1024];
            try {
                output = new BufferedOutputStream(client.getOutputStream(), 32*1024);                           
                output.write(headers.getBytes());

                // Loop as long as there's stuff to send
                while (isRunning && cbToSend>0 && !client.isClosed()) {

                    // See if there's more to send
                    File file = new File(localPath);
                    fc++;
                    int cbSentThisBatch = 0;
                    if (file.exists()) {
                        FileInputStream input = new FileInputStream(file);
                        input.skip(cbSkip);
                        int cbToSendThisBatch = input.available();
                        while (cbToSendThisBatch > 0) {
                            int cbToRead = Math.min(cbToSendThisBatch, buff.length);
                            int cbRead = input.read(buff, 0, cbToRead);
                            if (cbRead == -1) {
                                break;
                            }
                            cbToSendThisBatch -= cbRead;
                            cbToSend -= cbRead;
                            output.write(buff, 0, cbRead);
                            output.flush();
                            cbSkip += cbRead;
                            cbSentThisBatch += cbRead;
                        }
                        input.close();
                    }

                    // If we did nothing this batch, block for a second
                    if (cbSentThisBatch == 0) {
                      //  Log.d(TAG, "Blocking until more data appears");
                        Thread.sleep(1000);
                    }
                }
            }
            catch (SocketException socketException) {
           //    Log.e(TAG, "SocketException() thrown, proxy client has probably closed. This can exit harmlessly");
            }
            catch (Exception e) {
               // Log.e(TAG, "Exception thrown from streaming task:");
              //  Log.e(TAG, e.getClass().getName() + " : " + e.getLocalizedMessage());
                e.printStackTrace();                
            }

            // Cleanup
            try {
                if (output != null) {
                    output.close();
                }
                client.close();
            }
            catch (IOException e) {
             //   Log.e(TAG, "IOException while cleaning up streaming task:");                
              //  Log.e(TAG, e.getClass().getName() + " : " + e.getLocalizedMessage());
                e.printStackTrace();                
            }

            return 1;
        }

    }
}
	
	
	
	
	
}