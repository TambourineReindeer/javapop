import java.awt.Point;
import java.util.Vector;

import javax.media.opengl.GL;


public class House {
	
	public static HeightMap heightMap;
	private static int[][] farm;
	private static Vector<House> houses;
	
	private Point pos;
	
	public static void init(HeightMap heightMap)
	{
		House.heightMap = heightMap;
		farm = new int[heightMap.getWidth()][heightMap.getBreadth()];
		houses = new Vector<House>();
	}
	
	private House(int x, int y, int team){
		farm[x][y] = team;
		pos = new Point(x,y);
		houses.add(this);
	}
	
	public static House newHouse(int x, int y, int team)
	{
		if(heightMap.isFlat(x, y) && canBuild(x, y))
		{
			return new House(x,y, team);
		}
	return null;	
	}
	
	public static boolean canBuild(int x, int y)
	{
		return farm[x][y] ==0;
	}
	
	public static void display(GL gl)
	{
		if (houses != null)
		{
			for(House h:houses)
			{
				
			}
		}
	}
}
