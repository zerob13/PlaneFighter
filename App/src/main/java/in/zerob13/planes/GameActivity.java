package in.zerob13.planes;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import in.zerob13.planes.UI.AirSurfaceView;
import in.zerob13.planes.UI.IGameEventListener;

public class GameActivity extends Activity implements IGameEventListener, View.OnClickListener {

	private AirSurfaceView mainView;
	private Button restart;
	private FrameLayout rootView;
	private Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHandler = new Handler();
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initView();
		mainView.setListener(this);

	}

	private void initView() {
		FrameLayout.LayoutParams full = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		FrameLayout.LayoutParams center = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		rootView = new FrameLayout(this);
		rootView.setBackgroundColor(Color.WHITE);
		mainView = new AirSurfaceView(this);
		rootView.addView(mainView, full);
		center.gravity = Gravity.CENTER;
		restart = new Button(this);
		restart.setText(R.string.restart_game);
		restart.setTextSize(getResources().getDimension(R.dimen.restart_btn_text_size));
		restart.setVisibility(View.GONE);
		restart.setOnClickListener(this);
		rootView.addView(restart, center);
		setContentView(rootView);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mainView.postInvalidate();
	}

	@Override
	public void onGameStart() {

	}

	@Override
	public void onGameFinish(int score) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				restart.setVisibility(View.VISIBLE);
			}
		});

	}

	@Override
	public void onClick(View v) {
		if (v.equals(restart)) {
			restart.setVisibility(View.GONE);
			mainView.reset();
		}
	}
}
