package in.zerob13.planes.UI;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import in.zerob13.planes.R;
import in.zerob13.planes.model.BaseModel;
import in.zerob13.planes.model.Bullet;
import in.zerob13.planes.model.Cloud;
import in.zerob13.planes.model.Enemy;
import in.zerob13.planes.model.Plane;

/**
 * Created by zerob13 on 12/7/13.
 */
public class AirSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

	private float largestMoveStepY = 70;
	private float largestMoveStepX = 20;
	private Thread mainThread;
	private SurfaceHolder surfaceHolder;
	private Paint paint;
	private Paint textPaint;
	private Paint overPaint;

	private int screen_width;
	private int screen_height;

	private ArrayList<Enemy> enemies;
	private ArrayList<Bullet> bullets;
	private ArrayList<Cloud> clouds;
	private ArrayList<BaseModel> grave;
	private Plane hero;

	private float lastX;
	private float lastY;
	/** 用来刷新子弹产生频率 */
	private int count;

	private Bitmap heroBit;
	private Bitmap enemyBit;
	private Bitmap heroRear;
	private Bitmap bulletBit;
	private Bitmap cloudBit[];

	private IGameEventListener mListener;
	private Random radom;

	public AirSurfaceView(Context context) {
		this(context, null);
	}

	public AirSurfaceView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AirSurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void setListener(IGameEventListener listener) {
		mListener = listener;
	}

	/**
	 * 初始化
	 */
	private void init() {
		setFocusable(true);
		setKeepScreenOn(true);

		mainThread = new Thread(this);
		radom = new Random(System.currentTimeMillis());

		DisplayMetrics dm = new DisplayMetrics();
		((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(dm);
		screen_height = dm.heightPixels;
		screen_width = dm.widthPixels;

		this.setKeepScreenOn(true);
		surfaceHolder = this.getHolder();
		surfaceHolder.addCallback(this);

		paint = new Paint();
		paint.setColor(Color.rgb(194, 200, 200));

		overPaint = new Paint();
		overPaint.setColor(Color.BLACK);

		textPaint = new Paint();
		textPaint.setColor(Color.RED);
		enemies = new ArrayList<Enemy>();

		bullets = new ArrayList<Bullet>();

		grave = new ArrayList<BaseModel>();

		clouds = new ArrayList<Cloud>();
		clouds.add(genCloud(0));
		clouds.add(genCloud(1));
		clouds.add(genCloud(0));
		clouds.add(genCloud(2));
		clouds.add(genCloud(1));
		clouds.add(genCloud(2));
		clouds.add(genCloud(2));
		clouds.add(genCloud(1));
		clouds.add(genCloud(2));

		largestMoveStepY = screen_height / 4;
		largestMoveStepX = screen_width / 3;
		lastX = screen_width / 2;
		lastY = screen_height / 2;
		hero = new Plane(screen_width / 2, screen_height / 2);
		hero.setAlive(true);
		count = 0;

		heroBit = BitmapFactory.decodeResource(getResources(), Plane.HERO_RES);
		heroRear = BitmapFactory.decodeResource(getResources(), R.drawable.plane_rear_fire);
		enemyBit = BitmapFactory.decodeResource(getResources(), Enemy.ENEMY_RES);
		bulletBit = BitmapFactory.decodeResource(getResources(), Bullet.BULLET_RES);
		cloudBit = new Bitmap[3];
		for (int i = 0; i < 3; i++) {
			cloudBit[i] = BitmapFactory.decodeResource(getResources(), Cloud.DRAWABLE[i]);
		}

	}

	/**
	 * 重置状态
	 */
	public void reset() {
		surfaceHolder.removeCallback(null);
		init();
		mainThread.start();
		mListener.onGameStart();
	}

	/**
	 * 判断是否打中了敌人 (两张矩形图是否相交)
	 * 
	 * @param obj1
	 * @param obj2
	 * @return
	 */
	private boolean isHit(BaseModel obj1, BaseModel obj2) {
		//TODO: zerob13:(貌似这个矩形判断对撞机不是很准，估计是 bitmap 长度的问题)
		int obj1_height = 0;
		int obj1_wight = 0;
		int obj2_height = 0;
		int obj2_wight = 0;
		int maxLeft = 0;
		int maxTop = 0;
		int minRight = 0;
		int minBottom = 0;

		if (obj1 instanceof Bullet) {
			obj1_height = bulletBit.getHeight();
			obj1_wight = bulletBit.getWidth();
		}
		if (obj1 instanceof Enemy) {
			obj1_height = enemyBit.getHeight();
			obj1_wight = enemyBit.getWidth();
		}
		if (obj1 instanceof Plane) {
			obj1_height = heroBit.getHeight();
			obj1_wight = heroBit.getWidth();
		}

		if (obj2 instanceof Bullet) {
			obj2_height = bulletBit.getHeight();
			obj2_wight = bulletBit.getWidth();
		}
		if (obj2 instanceof Enemy) {
			obj2_height = enemyBit.getHeight();
			obj2_wight = enemyBit.getWidth();
		}
		if (obj2 instanceof Plane) {
			obj2_height = heroBit.getHeight();
			obj2_wight = heroBit.getWidth();
		}
		if (obj1.getX() > obj2.getX()) {
			maxLeft = obj1.getX();
		} else {
			maxLeft = obj2.getX();
		}

		if (obj1.getY() > obj2.getY()) {
			maxTop = obj1.getY();
		} else {
			maxTop = obj2.getY();
		}

		if (obj1_wight + obj1.getX() <= obj2_wight + obj2.getX()) {
			minRight = obj1_wight + obj1.getX();
		}
		if (obj1_height + obj1.getY() <= obj2_height + obj2.getY()) {
			minBottom = obj1_height + obj1.getY();
		}
		if (maxLeft > minRight || maxTop > minBottom) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 产生敌人
	 * 
	 * @return
	 */
	private Enemy genEnemy() {
		int rx = genRadomX(screen_width - enemyBit.getWidth() - 1);
		Enemy enemy = new Enemy(rx, 0);
		return enemy;
	}

	/**
	 * 产生背景
	 * 
	 * @param type
	 * @return
	 */
	private Cloud genCloud(int type) {
		int rx = genRadomX(screen_width - 20);
		int ry = genRadomX(screen_height - 20);
		Cloud cloud = new Cloud(rx, ry, type);
		return cloud;
	}

	/**
	 * 随机值生成
	 * 
	 * @param range
	 * @return
	 */
	private int genRadomX(int range) {
		return radom.nextInt(range);
	}

	/**
	 * 更新数据
	 */
	private void update() {
		count++;

		int count1 = radom.nextInt(15 - 6 - 1);
		if (enemies.size() <= count1) {
			enemies.add(genEnemy());
		}

		grave.clear();
		for (Enemy ai : enemies) {
			ai.onUpdate();
			if (ai.getY() >= screen_height) {
				ai.destory();
			}
			if (Math.abs(hero.getY() - ai.getY()) <= enemyBit.getHeight()
					&& Math.abs(hero.getX() - ai.getX()) <= enemyBit.getWidth()) {
				hero.setAlive(false);
				mListener.onGameFinish(100);
				render();
				return;
			}
			if (!ai.isAlive()) {
				grave.add(ai);
			}
		}

		for (Bullet bul : bullets) {
			bul.onUpdate(bulletBit.getHeight());
			if (bul.getY() <= 0) {
				grave.add(bul);
			}

			for (Enemy ai : enemies) {
				//				if (Math.abs(bul.getY() - ai.getY()) <= enemyBit.getHeight()
				//						&& Math.abs(bul.getX() - ai.getX()) <= enemyBit.getWidth()) {
				if (isHit(bul, ai)) {
					ai.onHit();
					if (!ai.isAlive()) {
						grave.add(ai);
					}
					grave.add(bul);
				}
			}
		}

		if (count % 4 == 0) {
			for (Cloud clu : clouds) {
				clu.onUpdate();
				if (clu.getY() >= screen_height) {
					clu.setY(0);
					clu.setX(genRadomX(screen_width - 1));
				}
			}
		}

		for (BaseModel ai : grave) {
			if (ai instanceof Enemy) {
				enemies.remove(ai);
			} else if (ai instanceof Bullet) {
				bullets.remove(ai);
			}
		}

		if ((count % 3) == 0) {
			Bullet bul = new Bullet(hero.getX() + heroBit.getWidth() / 2 - bulletBit.getWidth() / 2,
					hero.getY() - bulletBit.getHeight() + 5);
			bullets.add(bul);
		}
		count = count % 12;
	}

	/**
	 * 重置hero位置，防止飞出去
	 */
	private void resetHeroPosition() {
		if (hero.getX() > screen_width - heroBit.getWidth()) {
			hero.setX(screen_width - heroBit.getWidth());
		}
		if (hero.getY() > screen_height - heroBit.getHeight()) {
			hero.setY(screen_height - heroBit.getHeight());
		}
		if (hero.getY() < 0) {
			hero.setY(0);
		}
		if (hero.getX() < 0) {
			hero.setX(0);
		}
	}

	/**
	 * 绘图
	 */
	public void render() {

		resetHeroPosition();

		Canvas canvas = surfaceHolder.lockCanvas();

		try {
			if (canvas != null) {
				canvas.save();
				if (hero.isAlive()) {
					//draw background
					canvas.drawRect(0, 0, screen_width, screen_height, paint);
					for (Cloud clo : clouds) {
						canvas.drawBitmap(cloudBit[clo.getRes()], clo.getX(), clo.getY(), paint);
					}
					//draw hero plane
					canvas.drawBitmap(heroBit, hero.getX(), hero.getY(), paint);
					//draw rear
					canvas.drawBitmap(heroRear, hero.getX(), hero.getY() + heroBit.getHeight(), paint);
					//draw enemies
					for (Enemy ei : enemies) {
						canvas.drawBitmap(enemyBit, ei.getX(), ei.getY(), paint);
					}
					//draw Bullet
					for (Bullet bul : bullets) {
						canvas.drawBitmap(bulletBit, bul.getX(), bul.getY(), paint);
					}
				} else {
					canvas.drawRect(0, 0, screen_width, screen_height, overPaint);
					textPaint.setTextSize(getResources().getDimension(R.dimen.game_over_text_size));
					canvas.drawText(getResources().getString(R.string.game_over), screen_width / 5,
							screen_height / 3, textPaint);
				}

				canvas.restore();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (canvas != null) {
				surfaceHolder.unlockCanvasAndPost(canvas);
			}
		}

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!mainThread.isAlive() && hero.isAlive()) {
			mainThread.start();
			mListener.onGameStart();
		}

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}

	@Override
	public void run() {

		while (true) {
			try {
				if (hero.isAlive()) {
					update();
					render();
				} else {
					break;
				}
				Thread.sleep(40);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				float xd = ev.getX();
				float yd = ev.getY();
				lastX = xd;
				lastY = yd;
				//				Log.e("zerob13", "down " + lastX + " " + lastY + " " + xd + " " + yd);
				break;
			case MotionEvent.ACTION_MOVE:
				float x = ev.getX();
				float y = ev.getY();
				//				Log.e("zerob13", "move " + x + " " + y);
				//				Log.e("zerob13", "move " + Math.abs(x - lastX) + " " + Math.abs(y - lastY));
				if (Math.abs(x - lastX) > largestMoveStepX || Math.abs(y - lastY) > largestMoveStepY) {
					lastX = x;
					lastY = y;
					break;

				}
				float xx = hero.getX() + (x - lastX);
				float yy = hero.getY() + (y - lastY);
				lastX = x;
				lastY = y;
				hero.setX((int) xx);
				hero.setY((int) yy);
				//				Log.e("zerob13", "move " + lastX + " " + lastY + " " + x + " " + y);
				render();
				break;
			default:
				break;
		}
		return true;
	}

}
