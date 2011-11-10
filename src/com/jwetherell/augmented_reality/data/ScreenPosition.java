package com.jwetherell.augmented_reality.data;

/**
 * This class is used mostly as a utility to calculate relative positions.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class ScreenPosition {
    private float x = 0f;
    private float y = 0f;

	public ScreenPosition() {
        set(0, 0);
    }

	/**
	 * Set method for X and Y. Should be used instead of creating new objects.
	 * @param x X position.
	 * @param y Y position.
	 */
    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Get the X position.
     * @return Float X position.
     */
    public float getX() {
		return x;
	}
    /**
     * Set the X position.
     * @param x Float X position.
     */
	public void setX(float x) {
		this.x = x;
	}

    /**
     * Get the Y position.
     * @return Float Y position.
     */
	public float getY() {
		return y;
	}
    /**
     * Set the Y position.
     * @param y Float Y position.
     */
	public void setY(float y) {
		this.y = y;
	}

	/**
	 * Rotate the positions around the angle t.
	 * @param t Angle to rotate around.
	 */
    public void rotate(double t) {
        float xp = (float) Math.cos(t) * x - (float) Math.sin(t) * y;
        float yp = (float) Math.sin(t) * x + (float) Math.cos(t) * y;

        x = xp;
        y = yp;
    }

    /**
     * Add the X and Y to the positions X and Y.
     * @param x Float X to add to X.
     * @param y Float Y to add to Y.
     */
    public void add(float x, float y) {
        this.x += x;
        this.y += y;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "< x="+x+" y="+y+" >";
    }
}
