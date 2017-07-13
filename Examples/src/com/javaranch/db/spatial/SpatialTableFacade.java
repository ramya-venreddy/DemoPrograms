package com.javaranch.db.spatial;

import com.javaranch.db.TableFacade;
import com.javaranch.db.DBFacade;
import com.javaranch.db.DBResults;

import java.sql.Connection;
import java.sql.SQLException;

public class SpatialTableFacade extends TableFacade
{

    private OracleSpatialDBFacade db ;

    public SpatialTableFacade( OracleSpatialDBFacade db , String tableName )
    {
        super( db , tableName );
        this.db = db ;
    }

    public DBResults search( Connection con , String searchColumn , SpatialPolygon searchArea , String[] dataColumns ) throws SQLException
    {
        return db.search( con , new SpatialQuery( db , getTableName() , searchColumn , searchArea , dataColumns ) );
    }

    public DBResults search( String searchColumn , SpatialPolygon searchArea , String[] dataColumns ) throws SQLException
    {
        return db.search( new SpatialQuery( db , getTableName() , searchColumn , searchArea , dataColumns ) );
    }

    public DBResults search( Connection con , String searchColumn , SpatialPolygon[] searchArea , String[] dataColumns ) throws SQLException
    {
        return db.search( con , new SpatialQuery( db , getTableName() , searchColumn , searchArea , dataColumns ) );
    }

    public DBResults search( String searchColumn , SpatialPolygon[] searchArea , String[] dataColumns ) throws SQLException
    {
        return db.search( new SpatialQuery( db , getTableName() , searchColumn , searchArea , dataColumns ) );
    }

}
