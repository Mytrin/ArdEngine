package net.sf.ardengine.collisions;

import java.awt.geom.Line2D;
import java.util.ArrayList;

import net.sf.ardengine.Node;

/**
 * Collision shape - Polygon
 */
public class CollisionPolygon extends CollisionShape {
	/** Coords of polygon in node */
	private float[] coords;
	/** Coords of polygon in world */
	private float[] actCoords;
	/** Actual X coord of node's center */
	private float centerX;
	/** Actual Y coord of node's center */
	private float centerY;

	/**
	 * @param coords
	 *            Coords of polygon([x1,y1,x2,y2...]) (like node's [X0;Y0] would
	 *            be [0;0])
	 * @param targetNode
	 *            Node, for which is shape created
	 */
	public CollisionPolygon(float[] coords, Node targetNode) {
		this.coords = coords;
		centerX = targetNode.getX() + targetNode.getWidth() / 2;
		centerY = targetNode.getY() + targetNode.getHeight() / 2;
		actCoords = new float[coords.length];
		for (int i = 0; i < coords.length - 1; i += 2) {
			actCoords[i] = (coords[i] + targetNode.getX());
			actCoords[i + 1] = coords[i + 1] + targetNode.getY();
		}
		updateProperties(targetNode);
	}

	/**
     * Creates collision polygon with size of target node
	 * @param targetNode
	 *            Node, for which is shape created
	 */
	public CollisionPolygon(Node targetNode) {
		this(getBoundingBox(targetNode), targetNode);
	}

	private static float[] getBoundingBox(Node targetNode){
	    float nodeWidth = targetNode.getWidth();
        float nodeHeight = targetNode.getHeight();
	    return new float[]{0, 0, nodeWidth, 0, nodeWidth,
                nodeHeight, 0, nodeHeight};
    }

	@Override
	public boolean isColliding(CollisionShape cs) {

		if (cs instanceof CollisionPolygon) {

			CollisionPolygon cp = (CollisionPolygon) cs;

			float lowestX = actCoords[0]; // Searching for source of ray
			float lowestY = actCoords[1];

			float myHighestX = actCoords[0]; // need to know highest X and Y of
												// both bolygons
			float myHighestY = actCoords[1];

			float hisHighestX = cp.getActCoords()[0];
			float hisHighestY = cp.getActCoords()[1];

			boolean thisHasNearestX = true;
			boolean thisHasNearestY = true;

			for (int i = 2; i < actCoords.length - 1; i += 2) {
				if (lowestX > actCoords[i]) {
					lowestX = actCoords[i];
				}
				if (myHighestX < actCoords[i]) {
					myHighestX = actCoords[i];
				}
			}

			for (int i = 0; i < cp.getActCoords().length - 1; i += 2) {
				if (lowestX > cp.getActCoords()[i]) {
					lowestX = cp.getActCoords()[i];
					thisHasNearestX = false;
				}
				if (hisHighestX < cp.getActCoords()[i]) {
					hisHighestX = cp.getActCoords()[i];
				}
			}

			for (int i = 3; i < actCoords.length - 1; i += 2) {
				if (lowestY > actCoords[i]) {
					lowestY = actCoords[i];
				}
				if (myHighestY < actCoords[i]) {
					myHighestY = actCoords[i];
				}
			}

			for (int i = 1; i < cp.getActCoords().length - 1; i += 2) {
				if (lowestY > cp.getActCoords()[i]) {
					lowestY = cp.getActCoords()[i];
					thisHasNearestY = false;
				}
				if (hisHighestY < cp.getActCoords()[i]) {
					hisHighestY = cp.getActCoords()[i];
				}
			}

			lowestX -= 10; // Making sure that source of ray is not within the
							// polygons
			lowestY -= 10;
			float highestX; // Creating border X and Y values
			if (thisHasNearestX) {
				highestX = myHighestX;
			} else {
				highestX = hisHighestX;
			}
			float highestY;
			if (thisHasNearestY) {
				highestY = myHighestY;
			} else {
				highestY = hisHighestY;
			}

			// Testing which polygon is more advantegous for the test(hass less
			// points to ray)
			ArrayList<Float> myTestablePoints = new ArrayList<>();
			for (int i = 0; i < actCoords.length - 1; i += 2) {
				if (actCoords[i] <= highestX && actCoords[i + 1] <= highestY) {
					myTestablePoints.add(actCoords[i]);
					myTestablePoints.add(actCoords[i + 1]);
				}
			}

			ArrayList<Float> hisTestablePoints = new ArrayList<>();
			for (int i = 0; i < cp.getActCoords().length - 1; i += 2) {
				if (cp.getActCoords()[i] <= highestX
						&& cp.getActCoords()[i + 1] <= highestY) {
					hisTestablePoints.add(cp.getActCoords()[i]);
					hisTestablePoints.add(cp.getActCoords()[i + 1]);
				}
			}

			for (int i = 0; i < myTestablePoints.size() - 1; i += 2) {
				if (isPointInPolygon(myTestablePoints.get(i),
						myTestablePoints.get(i + 1), cp.getActCoords(),
						lowestX, lowestY)) {
					return true;
				}
			}

			for (int i = 0; i < hisTestablePoints.size() - 1; i += 2) {
				if (isPointInPolygon(hisTestablePoints.get(i),
						hisTestablePoints.get(i + 1), actCoords, lowestX,
						lowestY)) {
					return true;
				}
			}

			return false;
		} else if (cs instanceof CollisionCircle) {
			return cs.isColliding(this);
		}

		return false;
	}

	@Override
	public final void updateProperties(Node e) {
		aktScale = e.getScale();
		centerX = e.getX() + e.getWidth() / 2;
		centerY = e.getY() + e.getHeight() / 2;
		float angleRadians;
		if (!(e.getAngle() < 0)) {
			angleRadians = (float) ((e.getAngle()) * Math.PI / 180);
		} else {
			angleRadians = (float) ((360 + e.getAngle()) * Math.PI / 180);
		}
		for (int i = 0; i < coords.length - 1; i += 2) {
			actCoords[i] = (float) (centerX + (e.getX() + coords[i] - centerX)
					* Math.cos(angleRadians) + (e.getY() + coords[i + 1] - centerY)
					* Math.sin(angleRadians));
			actCoords[i + 1] = (float) (centerY
					- (e.getX() + coords[i] - centerX) * Math.sin(angleRadians) + (e
					.getY() + coords[i + 1] - centerY)
					* Math.cos(angleRadians));
		}
		if (withScale) {
			float polygonCenterX = 0;
			float polygonCenterY = 0;

			for (int i = 0; i < actCoords.length - 1; i += 2) {
				polygonCenterX += actCoords[i];
				polygonCenterY += actCoords[i + 1];
			}

			polygonCenterX = polygonCenterX * 2 / actCoords.length;
			polygonCenterY = polygonCenterY * 2 / actCoords.length;

			for (int i = 0; i < actCoords.length - 1; i += 2) {
				float pCXA = actCoords[i] - polygonCenterX;
				float pCYA = actCoords[i + 1] - polygonCenterY;

				actCoords[i] = polygonCenterX + pCXA * aktScale;
				actCoords[i + 1] = polygonCenterY + pCYA * aktScale;
			}

		}
		// coords={x1,y1,x2,y2,...,xN} -this will be usual bug I guess
	}

	@Override
	public float[] getActCoords() {
		return actCoords;
	}

	/**
	 * 
	 * @param pointX X coord of point
	 * @param pointY Y coord of point
	 * @param polygonCoords Coords of polygon
	 * @param lowestX X coord before the polygon
	 * @param lowestY Y coord above the polygon
	 * @return true if point is inside polygon
	 */
	private boolean isPointInPolygon(float pointX, float pointY,
			float[] polygonCoords, float lowestX, float lowestY) {
		int crossedSides = 0;
		Line2D.Float ray = new Line2D.Float(lowestX, lowestY, pointX, pointY);

		for (int i = 0; i < polygonCoords.length - 1; i += 2) {

			float ax = polygonCoords[i];
			float ay = polygonCoords[i + 1];
			float bx;
			float by;
			if (i + 2 < polygonCoords.length - 1) {
				bx = polygonCoords[i + 2];
				by = polygonCoords[i + 3];
			} else {
				bx = polygonCoords[0];
				by = polygonCoords[1];
			}

			if (Line2D.linesIntersect(ray.x1, ray.y1, ray.x2, ray.y2, ax, ay,
					bx, by)) {
				crossedSides++;
			}
		}
		if (crossedSides % 2 == 0) {
			return false;
		} else {
			return true;
		}
	}

    //DEBUG
    @Override
    public float[] getLineCoordinates() {
        return actCoords;
    }

}
