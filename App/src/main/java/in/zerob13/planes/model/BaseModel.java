package in.zerob13.planes.model;

/**
 * Created by zerob13 on 12/7/13.
 */
public class BaseModel {
	/** 步进 */
	public static final int SPEED_STEP = 10;
	/** 横向坐标 */
	protected int x;
	/** 纵向坐标 */
	protected int y;

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

}