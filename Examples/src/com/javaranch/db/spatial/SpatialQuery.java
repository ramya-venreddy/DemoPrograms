package com.javaranch.db.spatial;

import com.javaranch.db.DBQuery;
import com.javaranch.db.DBFacade;

public class SpatialQuery extends DBQuery
{

//    private List points = new ArrayList();
//    private List area = new ArrayList();

    public SpatialQuery( DBFacade facade , String table )
    {
        super( facade , table );
    }

    public SpatialQuery( DBFacade facade , String table , String column , SpatialPolygon searchArea )
    {
        this( facade , table );
        setSearch( column , searchArea );
    }

    public SpatialQuery( DBFacade facade , String table , String column , SpatialPolygon[] searchArea )
    {
        this( facade , table );
        setSearch( column , searchArea );
    }

    public SpatialQuery( DBFacade facade , String table , String column , SpatialPolygon searchArea , String[] dataColumns )
    {
        this( facade , table );
        setSearch( column , searchArea );
        setDataColumns( dataColumns );
    }

    public SpatialQuery( DBFacade facade , String table , String column , SpatialPolygon[] searchArea , String[] dataColumns )
    {
        this( facade , table );
        setSearch( column , searchArea );
        setDataColumns( dataColumns );
    }


    public SpatialQuery( DBFacade facade , String table , String column , String searchText )
    {
        super( facade , table , column , searchText );
    }

    public SpatialQuery( DBFacade facade , String table , String column , int searchValue )
    {
        super( facade , table , column , searchValue );
    }

    public SpatialQuery( DBFacade facade , String table , String column , int[] searchValues )
    {
        super( facade , table , column , searchValues );
    }

    public SpatialQuery( DBFacade facade , String table , String column , String[] searchText )
    {
        super( facade , table , column , searchText );
    }

    public SpatialQuery( DBFacade facade , String table , String column , String searchText , String[] dataColumns )
    {
        super( facade , table , column , searchText , dataColumns );
    }

    public SpatialQuery( DBFacade facade , String table , String column , String[] searchText , String[] dataColumns )
    {
        super( facade , table , column , searchText , dataColumns );
    }

    public SpatialQuery( DBFacade facade , String table , String column , int searchValue , String[] dataColumns )
    {
        super( facade , table , column , searchValue , dataColumns );
    }

    public SpatialQuery( DBFacade facade , String table , String column , int[] searchValues , String[] dataColumns )
    {
        super( facade , table , column , searchValues , dataColumns );
    }

    private OracleSpatialDBFacade getFacade()
    {
        return (OracleSpatialDBFacade)getDBFacade();
    }

    private String getSearchText( String column , SpatialPolygon searchArea )
    {
        StringBuffer buffy = new StringBuffer();
        buffy.append( "SDO_FILTER(" + column + ", " + getFacade().toOracleSpeak( searchArea ) );
        buffy.append( ",'querytype=WINDOW') = 'TRUE'" );
        return buffy.toString();
    }

    private String getSearchText( String column , SpatialPolygon[] searchArea )
    {
        StringBuffer buffy = new StringBuffer();
        buffy.append( "SDO_FILTER(" + column + ", " + getFacade().toOracleSpeak( searchArea ) );
        buffy.append( ",'querytype=WINDOW') = 'TRUE'" );
        return buffy.toString();
    }

    public void setSearch( String column , SpatialPolygon searchArea )
    {
        testColumn( column );
        super.setWhereClause( getSearchText( column , searchArea ) );
    }

    public void setSearch( String column , SpatialPolygon[] searchArea )
    {
        testColumn( column );
        super.setWhereClause( getSearchText( column , searchArea ) );
    }

}
