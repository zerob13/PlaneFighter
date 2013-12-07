package in.zerob13.planes.model;

import in.zerob13.planes.R;

/**
 * Created by zerob13 on 12/7/13.
 */
public class Plane extends BaseModel {
	public static final int HERO_RES = R.drawable.plane;

	private boolean isAlive;

	public Plane(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}
}
