package com.mybiz.myproject.db;

import junit.framework.TestCase;

public class TestOracle extends TestCase
{

    public void testRoundTrip() throws Exception
    {
        SoupFacade.executeUpdate( "DELETE FROM " + AnimalTable.tableName );
        AnimalTable.Row row = AnimalTable.getRow();
        row.setSpecies( "dudes" );
        row.setQuantity( 22 );
        row.setBuildingID( 55 );
        long id = row.insert();
        row = AnimalTable.getRow();
        assertEquals( 0 , row.getID() );
        row = AnimalTable.getRow( id );
        assertEquals( id , row.getID() );
        assertEquals( "dudes" , row.getSpecies() );
        assertEquals( 22 , row.getQuantity() );
        assertEquals( 55 , row.getBuildingID() );

        row.setQuantity( 444 );
        row.update();
        row = null ;
        row = AnimalTable.getRow( id );
        assertEquals( 444 , row.getQuantity() );

        row.delete();
        AnimalTable.Row[] rows = AnimalTable.getAllRows();
        assertEquals( 0 , rows.length );
    }

}
