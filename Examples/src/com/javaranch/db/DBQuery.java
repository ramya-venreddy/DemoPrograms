package com.javaranch.db;

import com.javaranch.common.*;

import java.security.InvalidParameterException;

// todo - write javadoc
public class DBQuery
{

    //private static boolean useNolock = false ;

    private DBFacade facade ;
    private String[] dataColumns = null;
    private String table;
    private String where = null;
    private String orderBy = null;

    public DBQuery( DBFacade facade , String table )
    {
        this.facade = facade ;
        this.table = table;
    }

    public DBQuery( DBFacade facade , String table , String column , String searchText )
    {
        this.facade = facade ;
        this.table = table;
        setSearch( column , searchText );
    }

    public DBQuery( DBFacade facade , String table , String column , int searchValue )
    {
        this.facade = facade ;
        this.table = table;
        setSearch( column , searchValue );
    }

    public DBQuery( DBFacade facade , String table , String column , int[] searchValues )
    {
        this.facade = facade ;
        this.table = table;
        setSearch( column , searchValues );
    }

    public DBQuery( DBFacade facade , String table , String column , String[] searchText )
    {
        this.facade = facade ;
        this.table = table;
        setSearch( column , searchText );
    }

    public DBQuery( DBFacade facade , String table , String column , String searchText , String[] dataColumns )
    {
        this.facade = facade ;
        this.table = table;
        setSearch( column , searchText );
        this.dataColumns = dataColumns;
    }

    /**
     * @param searchText An "OR" will be used between each searchText item.
     */
    public DBQuery( DBFacade facade , String table , String column , String[] searchText , String[] dataColumns )
    {
        this.facade = facade ;
        this.table = table;
        setSearch( column , searchText );
        this.dataColumns = dataColumns;
    }

    public DBQuery( DBFacade facade , String table , String column , int searchValue , String[] dataColumns )
    {
        this.facade = facade ;
        this.table = table;
        setSearch( column , searchValue );
        this.dataColumns = dataColumns;
    }

    /**
     * @param searchValues An "OR" will be used between each searchValues item.
     */
    public DBQuery( DBFacade facade , String table , String column , int[] searchValues , String[] dataColumns )
    {
        this.facade = facade ;
        this.table = table;
        setSearch( column , searchValues );
        this.dataColumns = dataColumns;
    }

//    public void useNolockHint()
//    {
//        useNolock = true ;
//    }

    /** Specify the order to receive the data. <p>

     @param orderBy Pass in null if order is not important, otherwise pass in the SQL that would normally follow the "ORDER BY" SQL phrase - usually, this is simply the name of a column. <p>
     */
    public void setOrderByClause( String orderBy )
    {
        this.orderBy = orderBy;
    }

    private String buildSearch( String column , String searchText )
    {
        String returnVal = null;
        if ( searchText == null )
        {
            returnVal = column + " IS NULL" ;
        }
        else
        {
            returnVal = column + '=' + DBFacade.normalizeSearchText( searchText ) ;
        }
        return returnVal;
    }

    protected void testColumn( String column )
    {
        if ( ! Str.usable( column ) )
        {
            throw new InvalidParameterException( "column must be specified" );
        }
    }

    /** The heart of this query object.
     */
    public void setSearch( String column , String searchText )
    {
        testColumn( column );
        where = buildSearch( column , searchText );
    }

    private void prepAdd()
    {
        if ( where == null )
        {
            where = "";
        }
        else
        {
            if ( where.length() > 0 )
            {
                where += " AND ";
            }
        }
    }

    /** Add search parameters to the existing search using 'AND'.
     */
    public void addSearch( String column , String searchText )
    {
        testColumn( column );
        prepAdd();
        where += buildSearch( column , searchText );
    }

    public void setSearch( String column , int searchValue )
    {
        setSearch( column , String.valueOf( searchValue ) );
    }

    public void addSearch( String column , int searchValue )
    {
        addSearch( column , String.valueOf( searchValue ) );
    }


    // if startsWith is true, then this is a startsWith search
    private String buildSubstringSearch( String column , String substring , boolean startsWith )
    {
        String returnVal = null;
        if ( substring != null )
        {
            Str s = new Str( DBFacade.normalizeSearchText( substring ) );
            if ( ! startsWith )
            {
                s.insert( '%' , 1 );
            }
            s.insert( '%' , s.length() - 1 );
            returnVal = column + " LIKE " + s ;
        }
        return returnVal;
    }

    /** Set the query search to look for a substring within a column.
     *
     * Creates a SQL "LIKE" query statement.
     *
     * @param substring the string fragment to look for in a column.
     */
    public void setSubstringSearch( String column , String substring )
    {
        testColumn( column );
        where = buildSubstringSearch( column , substring , false );
    }

    /** Add search parameters to the existing search using 'AND', looking for a substring within a column.
     *
     * Creates a SQL "LIKE" query statement.
     *
     * @param substring the string fragment to look for in a column.
     */
    public void addSubstringSearch( String column , String substring )
    {
        testColumn( column );
        if ( substring != null )
        {
            prepAdd();
            where += buildSubstringSearch( column , substring , false );
        }
    }

    /** Set the query search to look for a substring at the beginning of a column.
     *
     * Creates a SQL "LIKE" query statement.
     *
     * @param substring the string fragment to look for in a column.
     */
    public void setStartsWithSearch( String column , String substring )
    {
        testColumn( column );
        where = buildSubstringSearch( column , substring , true );
    }

    /** Add search parameters to the existing search using 'AND', looking for a substring at the beginning of a column.
     *
     * Creates a SQL "LIKE" query statement.
     *
     * @param substring the string fragment to look for in a column.
     */
    public void addStartsWithSearch( String column , String substring )
    {
        testColumn( column );
        if ( substring != null )
        {
            prepAdd();
            where += buildSubstringSearch( column , substring , true );
        }
    }

    static String buildWhereClause( String column , String[] searchText )
    {
        String returnVal = null;
        if ( Str.usable( column ) && ( searchText != null ) && ( searchText.length > 0 ) )
        {
            Str s = new Str();
            s.append( column + " IN (" );
            for ( int i = 0 ; i < searchText.length ; i++ )
            {
                if ( i > 0 )
                {
                    s.append( " , " );
                }
                String text = searchText[ i ];
                if ( text == null )
                {
                    s.append( "NULL" );
                }
                else
                {
                    s.append( DBFacade.normalizeSearchText( searchText[ i ]  ) );
                }
            }
            s.append( ")" );
            returnVal = s.toString();
        }
        return returnVal;
    }

    /**
     * @param searchText The array of strings will be used to build a SQL "IN" statement.
     */
    public void setSearch( String column , String[] searchText )
    {
        testColumn( column );
        where = buildWhereClause( column , searchText );
    }

    /** Add search parameters to the existing search using 'AND'.
     *
     * @param searchText The array of strings will be used to build a SQL "IN" statement.
     */
    public void addSearch( String column , String[] searchText )
    {
        testColumn( column );
        if ( ( searchText != null ) && ( searchText.length > 0 ) )
        {
            prepAdd();
            where += buildWhereClause( column , searchText );
        }
    }

    /**
     * @param searchValues The array of strings will be used to build a SQL "IN" statement.
     */
    public void setSearch( String column , int[] searchValues )
    {
        testColumn( column );
        where = buildWhereClause( column , Str.toStringArray( searchValues ) );
    }

    /** Add search parameters to the existing search using 'AND'.
     *
     * @param searchValues The array of strings will be used to build a SQL "IN" statement.
     */
    public void addSearch( String column , int[] searchValues )
    {
        testColumn( column );
        if ( ( searchValues != null ) && ( searchValues.length > 0 ) )
        {
            prepAdd();
            where += buildWhereClause( column , Str.toStringArray( searchValues ) );
        }
    }

    /** In case you want to use an 'interesting' where clause that DBQuery cannot create otherwise.
     */
    public void setWhereClause( String whereClause )
    {
        where = whereClause;
    }

    /** Specify the columns you want to get back from the query.
     */
    public void setDataColumns( String[] dataColumns )
    {
        this.dataColumns = dataColumns;
    }

    /** For use by DBFacade.
     *
     * Made public for use by unit tests.
     */
    public String[] getDataColumns()
    {
        return dataColumns;
    }

    /** For use by DBFacade.
     *
     * Made public for use by unit tests.
     */
    public String getTable()
    {
        return table;
    }

    /** For use by DBFacade.
     *
     * Made public for use by unit tests.
     */
    public String getWhereClause()
    {
        String returnVal = "";
        if ( where != null )
        {
            returnVal = " WHERE " + where;
        }
        return returnVal;
    }

    /** For use by DBFacade.
     *
     * Made public for use by unit tests.
     */
    public String getOrderByClause()
    {
        String returnVal = "";
        if ( orderBy != null )
        {
            returnVal = " ORDER BY " + orderBy ;
        }
        return returnVal;
    }

    protected DBFacade getDBFacade()
    {
        return facade ;
    }

    public int hashCode()
    {
        return where.hashCode();
    }

    public boolean equals( Object obj )
    {
        boolean returnVal = false;
        if ( ( obj != null ) && ( obj instanceof DBQuery ) )
        {
            DBQuery q = (DBQuery) obj;
            if ( Str.equal( table , q.table ) && Str.equal( where , q.where ) && Str.equal( orderBy , q.orderBy ) )
            {
                returnVal = Str.equal( dataColumns , q.dataColumns );
            }
        }
        return returnVal;
    }

    public String toString()
    {
        StringBuffer buffy = new StringBuffer();
        buffy.append( "table=" + table );
        buffy.append( "|where=" + where );
        buffy.append( "|orderBy=" + orderBy );
        buffy.append( "|dataColumns=" );
        for ( int i = 0 ; i < dataColumns.length ; i++ )
        {
            if ( i > 0 )
            {
                buffy.append( ',' );
            }
            buffy.append( dataColumns[ i ] );
        }
        return buffy.toString();
    }

    public String getSQL()
    {
        String returnVal = "";
        if ( dataColumns != null )
        {
            Str sql = new Str( "SELECT " );
            for( int i = 0 ; i < dataColumns.length ; i++ )
            {
                sql.append( dataColumns[ i ] );
                sql.append( ',' );
            }
            sql.deleteLast( 1 );
            sql.append( " FROM " );
            sql.append( getTable() );
            if ( facade.useNoLockHint() )
            {
                sql.append( " (NOLOCK) " ); // for SQL SERVER
            }
            sql.append( getWhereClause() );
            sql.append( getOrderByClause() );
            returnVal = sql.toString();
        }
        return returnVal ;
    }


}


