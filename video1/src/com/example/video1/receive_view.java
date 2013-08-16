package com.example.video1;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;

public class receive_view extends SurfaceView implements Callback{

	private boolean isPreviewRunning;

	public receive_view(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		synchronized (this) {
			if (isPreviewRunning)
				return;

			this.setWillNotDraw(false); // This allows us to make our own draw
										// calls to this canvas

			//mCamera = Camera.open();
			isPreviewRunning = true;
			//Camera.Parameters p = mCamera.getParameters();
			//Size size = p.getPreviewSize();
			//width = size.width;
			//height = size.height;
			//p.setPreviewFormat(ImageFormat.NV21);
			//showSupportedCameraFormats(p);
			//mCamera.setParameters(p);

	//		rgbints = new int[width * height];

			// try { mCamera.setPreviewDisplay(holder); } catch (IOException e)
			// { Log.e("Camera", "mCamera.setPreviewDisplay(holder);"); }

			//setDisplayOrientation(mCamera, 90);
			/*mCamera.startPreview();
			mCamera.setPreviewCallback(this);*/

		}
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
	
	
	private  class getVideo extends Thread{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
		}
		
	}
	

}
