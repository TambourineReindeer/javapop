package com.novusradix.JavaPop.Messaging;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author gef
 */
public class RockUpdate extends Message{

    int[] newPoints,deletedPoints;
    
    public RockUpdate(Set<Point> newRocks, Set<Point> deletedRocks)
    {
        newPoints = new int[newRocks.size()*2];
        deletedPoints = new int[deletedRocks.size()*2];
        int i=0;
        for(Point p:newRocks)
        {
            newPoints[i++]=p.x;
            newPoints[i++]=p.y;
        }
        i=0;
        for(Point p:deletedRocks)
        {
            deletedPoints[i++] = p.x;
            deletedPoints[i++] = p.y;
        }
    }
    @Override
    public void execute() {
        Set<Point> newRocks = new HashSet<Point>();
        Set<Point> deletedRocks = new HashSet<Point>();
        int i=0;
        Point p;
        for(int n=0;n<newPoints.length/2;n++)
        {
            p=new Point();
            p.x = newPoints[i++];
            p.y = newPoints[i++];
            newRocks.add(p);
        }
        i=0;
        for(int n=0;n<deletedPoints.length/2;n++)
        {
            p=new Point();
            p.x = deletedPoints[i++];
            p.y = deletedPoints[i++];
            deletedRocks.add(p);
        }
        clientMap.addRocks(newRocks);
        clientMap.removeRocks(deletedRocks);
    }

}
