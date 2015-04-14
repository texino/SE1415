package it.dei.unipd.esp1415.objects;

/**
 * A class for the (x,y,z) values of the accelerometer
 */
public class AccelPoint {
	
	private float x,y,z;
	
	public AccelPoint(float x,float y, float z)
	{
		this.x=x;
		this.y=y;
		this.z=z;
	}
	
	public float getX()
	{
		return x;
	}
	
	public float getY()
	{
		return y;
	}
	
	public float getZ()
	{
		return z;
	}
}
