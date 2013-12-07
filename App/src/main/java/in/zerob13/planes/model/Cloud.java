package in.zerob13.planes.model;

import in.zerob13.planes.R;

/**
 * Created by zerob13 on 12/7/13.
 */
public class Cloud extends BaseModel {

	public static final int[] DRAWABLE = { R.drawable.cloud1, R.drawable.cloud2, R.drawable.cloud3 };
	private int res;

	public Cloud(int x, int y, int type) {
		this.x = x;
		this.y = y;
		res = type;
	}

	public void onUpdate() {
		y += SPEED_STEP;
	}

	public int getRes() {
		return res;
	}
}
