package com.javaranch.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class TableFacade
{

    private DBFacade db ;
    private String tableName ;

    public TableFacade( DBFacade db , String tableName )
    {
        this.db = db ;
        this.tableName = tableName ;
    }

    public String getTableName()
    {
        return tableName ;
    }

    public void setTableName( String tableName )
    {
        this.tableName = tableName;
    }

    public DBFacade getDBFacade()
    {
        return db;
    }

    public void setDBFacade( DBFacade db )
    {
        this.db = db;
    }

    public DBResults search( Connection con , String whereClause , String[] dataColumns ) throws SQLException
    {
        return db.search( con , getTableName() , whereClause , dataColumns );
    }

    public DBResults search( String whereClause , String[] dataColumns ) throws SQLException
    {
        return db.search( getTableName() , whereClause , dataColumns );
    }

    public DBResults search( Connection con , String searchColumn , String searchText , String[] dataColumns ) throws SQLException
    {
        return db.search( con , getTableName() , searchColumn , searchText , dataColumns );
    }

    public DBResults search( Connection con , String searchColumn , String[] searchText , String[] dataColumns ) throws SQLException
    {
        return db.search( con , getTableName() , searchColumn , searchText , dataColumns );
    }

    public DBResults search( String searchColumn , String searchText , String[] dataColumns ) throws SQLException
    {
        return db.search( getTableName() , searchColumn , searchText , dataColumns );
    }

    public DBResults search( String searchColumn , String[] searchText , String[] dataColumns ) throws SQLException
    {
        return db.search( getTableName() , searchColumn , searchText , dataColumns );
    }

    public DBResults search( Connection con , String searchColumn , int searchValue , String[] dataColumns ) throws SQLException
    {
        return db.search( con , getTableName() , searchColumn , searchValue , dataColumns );
    }

    public DBResults search( Connection con , String searchColumn , int[] searchValues , String[] dataColumns ) throws SQLException
    {
        return db.search( con , getTableName() , searchColumn , searchValues , dataColumns );
    }

    public DBResults search( String searchColumn , int searchValue , String[] dataColumns ) throws SQLException
    {
        return db.search( getTableName() , searchColumn , searchValue , dataColumns );
    }

    public DBResults search( String searchColumn , int[] searchValues , String[] dataColumns ) throws SQLException
    {
        return db.search( getTableName() , searchColumn , searchValues , dataColumns );
    }

    /** Get all rows.
     */
    public DBResults search( Connection con , String[] dataColumns ) throws SQLException
    {
        return db.search( con , getTableName() , null , dataColumns );
    }

    /** Get all rows.
     */
    public DBResults search( String[] dataColumns ) throws SQLException
    {
        return db.search( getTableName() , null , dataColumns );
    }

    public void update( Connection con , String whereClause , Map data ) throws SQLException
    {
        db.update( con , getTableName() , whereClause , data );
    }

    public void update( Connection con , String column , String searchText , Map data ) throws SQLException
    {
        db.update( con , getTableName() , column , searchText , data );
    }

    public void update( String searchColumn , String searchText , Map data ) throws SQLException
    {
        db.update( getTableName() , searchColumn , searchText , data );
    }

    public void delete( Connection con , String searchColumn , String searchText ) throws SQLException
    {
        db.delete( con , getTableName() , searchColumn , searchText );
    }

    public void delete( String searchColumn , String searchText ) throws SQLException
    {
        db.delete( getTableName() , searchColumn , searchText );
    }

    public void insert( Connection con , Map data ) throws SQLException
    {
        db.insert( con , getTableName() , data );
    }

    public void insert( Map data ) throws SQLException
    {
        db.insert( getTableName() , data );
    }

    public long insertAndGetID( Map data ) throws SQLException
    {
        return db.insertAndGetID( getTableName() , data );
    }

    public long insertAndGetID( Connection con , Map data ) throws SQLException
    {
        return db.insertAndGetID( con , getTableName() , data );
    }

    public long insertAndGetID( Map data , String idColumnName ) throws SQLException
    {
        return db.insertAndGetID( getTableName() , data , idColumnName );
    }

    public long insertAndGetID( Connection con , Map data , String idColumnName ) throws SQLException
    {
        return db.insertAndGetID( con , getTableName() , data , idColumnName );
    }



}
