package in.zerob13.planes.model;

import in.zerob13.planes.R;

/**
 * Created by zerob13 on 12/7/13.
 */
public class Enemy extends BaseModel {

	public static final int ENEMY_RES = R.drawable.enemy;
	private int life;

	public Enemy(int x, int y) {
		this.x = x;
		this.y = y;
		life = 1;
	}

	public void onUpdate() {
		y += SPEED_STEP;
	}

	public void onHit() {
		life--;
	}

	public void destory() {
		life = -1;
	}

	public boolean isAlive() {
		return life > 0;
	}

}
