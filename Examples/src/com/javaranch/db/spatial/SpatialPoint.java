package com.javaranch.db.spatial;

public class SpatialPoint implements java.io.Serializable
{
    private double latitude ;
    private double longitude ;

    public SpatialPoint( double latitude , double longitude )
    {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public double getLongitude()
    {
        return longitude;
    }

}
