package com.javaranch.db;

import junit.framework.*;
import com.javaranch.common.*;

public class TestDBQuery extends TestCase
{

    public static DBFacade facade = new DBFacade( );

    public void test_constructors() throws Exception
    {
        String tableName = "Tina";
        DBQuery q = new DBQuery( facade , tableName );
        assertEquals( q.getTable() , tableName );
        assertEquals( q.getWhereClause() , "" );
        assertNull( q.getDataColumns() );
        assertEquals( q.getOrderByClause() , "" );

        String columnName = "Colin";
        String searchText = "Serge";
        q = new DBQuery( facade , tableName , columnName , searchText );
        assertEquals( q.getTable() , tableName );
        assertEquals( q.getWhereClause() , " WHERE Colin='Serge'" );
        assertNull( q.getDataColumns() );
        assertEquals( q.getOrderByClause() , "" );

        String[] columns = { "pillar" , "pole" , "pedastal" };
        q = new DBQuery( facade , tableName , columnName , searchText , columns );
        assertEquals( q.getTable() , tableName );
        assertEquals( q.getWhereClause() , " WHERE Colin='Serge'" );
        assertTrue( Str.equal( q.getDataColumns() , columns ) );
        assertEquals( q.getOrderByClause() , "" );

        String[] searches = { searchText , "Sergio" , "Serbia" };
        q = new DBQuery( facade , tableName , columnName , searches , columns );
        assertEquals( q.getTable() , tableName );
        assertEquals( q.getWhereClause() , " WHERE Colin IN ('Serge' , 'Sergio' , 'Serbia')" );
        assertTrue( Str.equal( q.getDataColumns() , columns ) );
        assertEquals( q.getOrderByClause() , "" );
    }

    public void test_orderBy() throws Exception
    {
        DBQuery q = new DBQuery( facade ,  "FrootLoopCollection" );
        q.setOrderByClause( "Color" );
        assertEquals( q.getOrderByClause() , " ORDER BY Color" );
        assertEquals( q.getTable() , "FrootLoopCollection" );
        assertEquals( q.getWhereClause() , "" );
        assertNull( q.getDataColumns() );
    }

    public void test_setSearch() throws Exception
    {
        DBQuery q = new DBQuery( facade , "FrootLoopCollection" );
        assertEquals( q.getTable() , "FrootLoopCollection" );
        assertEquals( q.getWhereClause() , "" );
        assertNull( q.getDataColumns() );
        assertEquals( q.getOrderByClause() , "" );
        q.setSearch( "Color" , "Green" );
        assertEquals( q.getWhereClause() , " WHERE Color='Green'" );
        q.setSearch( "Color" , "Orange" );
        assertEquals( q.getWhereClause() , " WHERE Color='Orange'" );
        q.setSearch( "Texture" , "Soggy" );
        assertEquals( q.getWhereClause() , " WHERE Texture='Soggy'" );
        q.setSearch( "Color" , new String[]{ "Green" , "Orange" } );
        assertEquals( q.getWhereClause() , " WHERE Color IN ('Green' , 'Orange')" );
        q.setSearch( "Color" , (String)null );
        assertEquals( q.getWhereClause() , " WHERE Color IS NULL" );
    }

    public void test_addSearch() throws Exception
    {
        DBQuery q = new DBQuery( facade , "FrootLoopCollection" );
        assertEquals( q.getTable() , "FrootLoopCollection" );
        q.addSearch( "Color" , "Green" );
        assertEquals( q.getWhereClause() , " WHERE Color='Green'" );
        q.addSearch( "Texture" , "Soggy" );
        assertEquals( q.getWhereClause() , " WHERE Color='Green' AND Texture='Soggy'" );
        q.addSearch( "Smell" , new String[]{ "Tangy" , "Funky" } );
        assertEquals( q.getWhereClause() , " WHERE Color='Green' AND Texture='Soggy' AND Smell IN ('Tangy' , 'Funky')" );
    }

    public void test_setWhereClause() throws Exception
    {
        DBQuery q = new DBQuery( facade , "FrootLoopCollection" );
        q.setWhereClause( "Flavor sounds like sweet, crunchy goodness" );
        assertEquals( q.getWhereClause() , " WHERE Flavor sounds like sweet, crunchy goodness" );
        q.setSearch( "Color" , "Green" );
        assertEquals( q.getWhereClause() , " WHERE Color='Green'" );
        q.setWhereClause( "Flavor sounds like sweet, crunchy goodness" );
        assertEquals( q.getWhereClause() , " WHERE Flavor sounds like sweet, crunchy goodness" );
    }




}
