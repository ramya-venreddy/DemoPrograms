package com.javaranch.db;

import com.javaranch.common.*;

import java.util.*;
import java.security.InvalidParameterException;

/** Returned from DBFacade and Jenny generated classes.
 *
 * A DBResults object contains all of the results returned from the database completely in memory - there are no connections
 * that would need to be closed.
 *
 * All of the data in a DBResults object is a string.  The original DBResults object contained all objects.  It was
 * determined that almost all of the casting was to String.  By making this change, it does seem that a great deal of
 * code became much easier to read.
 *
 * Example:
 * <ul>
 *
 *   Suppose you have a table called Employee ...
 *
 *   <pre>
 *
 *      private NameIDPair[] getStoreEmployees( int storeID )
 *      {
 *          String[] columns = { EmployeeTable.nameColumnName , EmployeeTable.idColumnName };
 *          DBResults r = EmployeeTable.search( EmployeeTable.storeIDColumnName , String.valueOf( storeID ) , columns );
 *          return r.toNameIDPairArray();
 *      }
 *
 *   </pre>
 * </ul>
 *
 */
public class DBResults extends ArrayList
{

    /** Will return null if there is no such row. <p>
     */
    public String[] getRow( int row )
    {
        String[] returnVal = null;
        if ( row < size() )
        {
            returnVal = (String[]) get( row );
        }
        return returnVal;
    }

    /**
     * @return the number of columns found in the first row (and probably all rows).  If there are zero rows, zero will be returned.
     */
    public int getColumnCount()
    {
        int returnVal = 0;
        if ( size() > 0 )
        {
            returnVal = getRow( 0 ).length;
        }
        return returnVal;
    }

    /**
     * @return the data found in the cell as a string.
     */
    public String get( int row , int column )
    {
        if ( row < 0 )
        {
            throw new InvalidParameterException( "row must be zero or greater" );
        }
        if ( row >= size() )
        {
            throw new InvalidParameterException( "invalid value for row:  there aren't that many rows!" );
        }
        String returnVal = null;
        String[] data = getRow( row );
        if ( data != null )
        {
            if ( column < 0 )
            {
                throw new InvalidParameterException( "column must be zero or greater" );
            }
            if ( column >= data.length )
            {
                throw new InvalidParameterException( "invalid value for column: there aren't that many columns!" );
            }
            returnVal = data[ column ];
        }
        return returnVal;
    }

    /** Extract the first column into a String array.
     * @return an array of length zero if the results have a size of zero.
     */
    public String[] toStringArray()
    {
        String[] s = new String[ size() ];
        for ( int i = 0 ; i < s.length ; i++ )
        {
            s[ i ] = get( i , 0 );
        }
        return s ;
    }

    /**
     * The columns must be in the order of "name" and then "id".  "firstname", "lastname" and then "id" would work too.
     *
     * There are many times that you want to get a list of names with ID's for use in a short menu.  The name is displayed
     * to the user and the ID is used as the key in case the user selects that item.
     *
     * Example:
     * <ul>
     *
     *   Suppose you have a table called Employee ...
     *
     *   <pre>
     *
     *      private NameIDPair[] getStoreEmployees( int storeID )
     *      {
     *          String[] columns = { EmployeeTable.nameColumnName , EmployeeTable.idColumnName };
     *          DBResults r = EmployeeTable.search( EmployeeTable.storeIDColumnName , String.valueOf( storeID ) , columns );
     *          return r.toNameIDPairArray();
     *      }
     *
     *   </pre>
     * </ul>
     *
     * @return an array of length zero if the results have a size of zero.
     */
    public NameIDPair[] toNameIDPairArray()
    {
        NameIDPair[] pairs = new NameIDPair[ size() ];
        int nameCount = getColumnCount() - 1 ;
        for ( int i = 0 ; i < pairs.length ; i++ )
        {
            StringBuffer buffy = new StringBuffer();
            for( int j = 0 ; j < nameCount ; j++ )
            {
                buffy.append( get( i , j ) );
                buffy.append( ' ' );
            }
            String name = buffy.toString().trim();
            int id =  Integer.parseInt( get( i , nameCount ) );
            pairs[ i ] = new NameIDPair( name , id );
        }
        return pairs ;
    }

    public KeyValuePair[] toKeyValuePairArray()
    {
        KeyValuePair[] pairs = new KeyValuePair[ size() ];
        for ( int i = 0 ; i < pairs.length ; i++ )
        {
            pairs[ i ] = new KeyValuePair( get( i , 0 ) , get( i , 1 ) );
        }
        return pairs ;
    }

    /** Extract the first column into an int array.
     *
     * This is used for cases where you need to keep track of all of the records in a table that meet a certain
     * criteria, but keeping the whole record would consume too much memory.
     *
     * @return an array of length zero if the results have a size of zero.
     */
    public int[] toIntArray()
    {
        int[] a = new int[ size() ];
        for ( int i = 0 ; i < a.length ; i++ )
        {
            a[ i ] = Str.toInt( get( i , 0 ) );
        }
        return a ;
    }



}


