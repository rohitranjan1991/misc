package com.example.video;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.Toast;

public class sv extends SurfaceView implements Callback{

	public sv(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public sv(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public sv(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void dispatchDraw(Canvas canvas) {
		Log.d("dispatch draw", "From inside it");
		super.dispatchDraw(canvas);
	}
	@Override
	public void draw(Canvas canvas) {
		Log.d("dispatch draw", "From inside it");
		super.draw(canvas);
	}
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		
		
	}
	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		setWillNotDraw(false);
		
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		
	}

	
	
	
	

	
	
}
