package core.utilities;

import processing.core.PVector;

public class RayPicking {
	
	private PVector clickPosInWorld = new PVector();
	private PVector direction = new PVector();
	
	public void intersectionWithXYPlane(float[] worldPos) {
		float s = clickPosInWorld.z / direction.z;
		worldPos[0] = clickPosInWorld.x + direction.x * s;
		worldPos[1] = clickPosInWorld.y + direction.y * s;
		worldPos[2] = 0;
	}
	
	public PVector getClickPosInWorld() {
		return clickPosInWorld;
	}
	
	public PVector getDirection() {
		return direction;
	}
}
