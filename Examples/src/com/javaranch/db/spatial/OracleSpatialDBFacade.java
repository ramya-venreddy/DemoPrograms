package com.javaranch.db.spatial;

import com.javaranch.db.DBResults;
import com.javaranch.db.OracleFacade;
import com.javaranch.common.Str;

import java.sql.*;
import java.security.InvalidParameterException;
import java.util.Map;
import java.util.Iterator;
import java.util.HashMap;

public abstract class OracleSpatialDBFacade extends OracleFacade
{

    public static final String spatialPointName = "spatialPoint";
    public static final String spatialPolygonName = "spatialPolygon";
    public static final String spatialMultiPolygonName = "spatialMultiPolygon";

    public OracleSpatialDBFacade( String jndiLookup )
    {
        super( jndiLookup );
    }

    public OracleSpatialDBFacade( String databaseDriver , String databaseURL )
    {
        super( databaseDriver , databaseURL );
    }

    public OracleSpatialDBFacade( String driver , String databaseURL , String databaseName , String databasePassword )
    {
        super( driver , databaseURL , databaseName , databasePassword );
    }

    // given an oracle spatial object from the result set, use oracle specific tools to get the data and
    //  return an XML reprentation
    protected abstract String getSpatialXML( Object geometryObject ) throws SQLException ;

    /** Sometimes you just gotta do it in SQL.
     *
     * This method will at least handle the statements and result sets for you.
     *
     * @param con A database connection.
     * @param sql A complete SQL statement
     * @return contains zero objects if none are found.
     */
    public DBResults executeQuery( Connection con , String sql ) throws SQLException
    {
        if ( con == null )
        {
            throw new InvalidParameterException( "parameter con is not allowed to be null" );
        }
        if ( ! Str.usable( sql ) )
        {
            throw new InvalidParameterException( "parameter sql must contain usable SQL!" );
        }
        DBResults data = new DBResults();
        Statement statement = con.createStatement();
        try
        {
            ResultSet rs = statement.executeQuery( sql );
            ResultSetMetaData meta = rs.getMetaData();
            int numberOfColumns = meta.getColumnCount();

            boolean useSpatial = false ;
            boolean[] isSpatial = new boolean[ numberOfColumns ];
            for( int i = 0 ; i < numberOfColumns ; i++ )
            {
                isSpatial[ i ] = false ;
                int dbIndex = i + 1 ;
                if ( meta.getColumnType( dbIndex ) == java.sql.Types.STRUCT )
                {
                    useSpatial = true ;
                    isSpatial[ i ] = true ;
                }
            }
            while ( rs.next() )
            {
                String[] s = new String[ numberOfColumns ];
                for ( int i = 0 ; i < s.length ; i++ )
                {
                    String value = null ;
                    int dbIndex = i + 1 ;
                    if ( useSpatial && isSpatial[ i ] )
                    {
                        value = getSpatialXML( rs.getObject( dbIndex ) );
                    }
                    if ( value == null )
                    {
                        value = rs.getString( dbIndex );
                    }
                    s[ i ] = value ;
                }
                data.add( s );
            }
        }
        catch ( Exception e )
        {
            SQLException e2 = new SQLException( "executeQuery() execute fail [" + sql + "]: " + e );
            e2.setStackTrace( e.getStackTrace() );
            throw e2 ;
        }
        finally
        {
            statement.close();
        }
        return data ;
    }

    String toOracleSpeak( SpatialPoint point )
    {
        StringBuffer buffy = new StringBuffer();
        buffy.append( "mdsys.sdo_geometry(3001,8307,mdsys.sdo_point_type(" );
        buffy.append( point.getLongitude() + "," + point.getLatitude() + ",3000),null,null)" );
        return buffy.toString();
    }

    String toOracleSpeak( SpatialPolygon searchArea )
    {
        StringBuffer buffy = new StringBuffer();
        buffy.append( " mdsys.sdo_geometry(3003,8307,NULL," );
        buffy.append( "mdsys.sdo_elem_info_array(1,1003,1),mdsys.sdo_ordinate_array(" );
        SpatialPoint[] points = searchArea.getPoints();
        for ( int i = 0 ; i < points.length ; i++ )
        {
            SpatialPoint point = points[ i ];
            buffy.append( point.getLongitude() + "," + point.getLatitude() + ",3000, " );
        }
        if ( points.length > 1 )
        {
            // trim last two chars
            buffy.setLength( buffy.length() - 2 );
        }
        buffy.append( "))" );
        return buffy.toString();
    }

    String toOracleSpeak( SpatialPolygon[] searchArea )
    {
        StringBuffer buffy = new StringBuffer();
        buffy.append( "mdsys.sdo_geometry(3007,8307,NULL," );
        buffy.append( "mdsys.sdo_elem_info_array(" );
        int index = 1 ;
        StringBuffer oracleData = new StringBuffer();
        for ( int polygonIndex = 0 ; polygonIndex < searchArea.length ; polygonIndex++ )
        {
            SpatialPolygon polygon = searchArea[ polygonIndex ];
            SpatialPoint[] points = polygon.getPoints();
            buffy.append( index + ",1003,1, " );
            index += points.length * 3 ;
            for ( int i = 0 ; i < points.length ; i++ )
            {
                SpatialPoint point = points[ i ];
                oracleData.append( point.getLongitude() + "," + point.getLatitude() + ",3000, " );
            }
        }
        buffy.setLength( buffy.length() - 2 ); // trim last two chars
        buffy.append( "),mdsys.sdo_ordinate_array(" );
        buffy.append( oracleData );
        buffy.setLength( buffy.length() - 2 ); // trim last two chars
        buffy.append( "))" );
        return buffy.toString();
    }

    // return null if cannot be done
    private String objectToOracleSpeak( Object obj )
    {
        String returnVal = null ;
        // I was going to use a static hashmap of class names here, but this just turned out much simpler/shorter.
        if ( obj instanceof SpatialPolygon )
        {
            returnVal = toOracleSpeak( (SpatialPolygon)obj );
        }
        else if ( obj instanceof SpatialPoint )
        {
            returnVal = toOracleSpeak( (SpatialPoint)obj );
        }
        else if ( obj instanceof SpatialPolygon[] )
        {
            returnVal = toOracleSpeak( (SpatialPolygon[])obj );
        }
        return returnVal;
    }

    public abstract Object fromXML( String xml );
    public abstract String toXML( Object obj );

    private void toOracleSpeak( Map data )
    {
        Map modifiedData = new HashMap();
        Iterator iterator = data.entrySet().iterator();
        while ( iterator.hasNext() )
        {
            Map.Entry entry = (Map.Entry)iterator.next();
            String key = (String)entry.getKey();
            Object obj = entry.getValue();

            // we need to find out if the object is a spatial object.
            // A spatial object would be an XML string, a SpatialPoint, a SpatialPolygon or a SpatialPolygon array.

            if ( obj != null )
            {
                if ( obj instanceof String )
                {
                    String s = (String)obj ;
                    if ( s.startsWith( "<com.javaranch.db.spatial.Spatial" ) )
                    {
                        // we have a winner!
                        s = objectToOracleSpeak( fromXML( s ) );
                        if ( s != null )
                        {
                            modifiedData.put( key , s );
                        }
                    }
                }
                else
                {
                    // if it isn't a string, it is probably a spatial object
                    String s = objectToOracleSpeak( obj );
                    if ( s != null )
                    {
                        modifiedData.put( key , s );
                    }
                }
            }
        }
        iterator = modifiedData.entrySet().iterator();
        while( iterator.hasNext() )
        {
            Map.Entry entry = (Map.Entry)iterator.next();
            String key = (String)entry.getKey();
            Object obj = entry.getValue();
            data.remove( key );
            data.put( "+" + key , obj );
        }
    }

    public void update( Connection con , String table , String whereClause , Map data ) throws SQLException
    {
        toOracleSpeak( data );
        super.update( con , table , whereClause , data );
    }

    protected String insertAndPossiblyGetNewID( Connection con , String table , Map data , String idColumnName ) throws SQLException
    {
        toOracleSpeak( data );
        return super.insertAndPossiblyGetNewID( con , table , data , idColumnName );
    }

}
