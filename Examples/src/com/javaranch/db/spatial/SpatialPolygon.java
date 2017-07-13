package com.javaranch.db.spatial;


public class SpatialPolygon implements java.io.Serializable
{

    private SpatialPoint[] points ;

    public SpatialPolygon( SpatialPoint[] points )
    {
        this.points = points;
    }

    public SpatialPoint[] getPoints()
    {
        return points;
    }

}
