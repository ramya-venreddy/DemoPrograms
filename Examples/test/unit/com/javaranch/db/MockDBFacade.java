package com.javaranch.db;

import java.sql.* ;
import java.util.* ;
import java.lang.reflect.Proxy ;
import java.lang.reflect.InvocationHandler ;
import java.lang.reflect.Method ;

public class MockDBFacade extends DBFacade
{

    public int getConnection_calls = 0 ;
    public Connection getConnection() throws Exception
    {
        getConnection_calls++;
        return (Connection)new MockConnection( Connection.class );
    }

    public int testConnection_calls = 0 ;
    public void testConnection() throws Exception
    {
        testConnection_calls++;
    }

    public Connection executeQuery_con = null ;
    public String executeQuery_sql = null ;
    public int executeQuery_calls = 0 ;
    public DBResults executeQuery_return = null ;
    public DBResults executeQuery( Connection con , String sql ) throws SQLException
    {
        executeQuery_con = con ;
        executeQuery_sql = sql ;
        executeQuery_calls++ ;
        return executeQuery_return ;
    }

    public Connection search_con = null ;
    public DBQuery search_q = null ;
    public String search_table = null ;
    public String search_whereClause = null ;
    public String search_searchColumn = null ;
    public Object search_searchText = null ;  // could be a string or a string array
    public String[] search_dataColumns = null ;
    public int search_calls = 0 ;
    public DBResults search_return = null ;

    public DBResults search( Connection con , DBQuery q ) throws SQLException
    {
        search_con = con ;
        search_q = q ;
        search_calls++ ;
        return search_return ;
    }

    public DBResults search( Connection con , String table , String whereClause , String[] dataColumns ) throws SQLException
    {
        search_con = con ;
        search_table = table ;
        search_whereClause = whereClause ;
        search_dataColumns = dataColumns ;
        search_calls++ ;
        return search_return ;
    }

    public DBResults search( String table , String whereClause , String[] dataColumns ) throws SQLException
    {
        search_table = table ;
        search_whereClause = whereClause ;
        search_dataColumns = dataColumns ;
        search_calls++ ;
        return search_return ;
    }

    public DBResults search( Connection con , String table , String searchColumn , String searchText , String[] dataColumns ) throws SQLException
    {
        search_con = con ;
        search_table = table ;
        search_searchColumn = searchColumn ;
        search_searchText = searchText ;
        search_dataColumns = dataColumns ;
        search_calls++ ;
        return search_return ;
    }

    public DBResults search( Connection con , String table , String searchColumn , String[] searchText , String[] dataColumns ) throws SQLException
    {
        search_con = con ;
        search_table = table ;
        search_searchColumn = searchColumn ;
        search_searchText = searchText ;
        search_dataColumns = dataColumns ;
        search_calls++ ;
        return search_return ;
    }

    public DBResults search( DBQuery q ) throws SQLException
    {
        search_q = q ;
        search_calls++ ;
        return search_return ;
    }

    public DBResults search( String table , String searchColumn , String searchText , String[] dataColumns ) throws SQLException
    {
        search_table = table ;
        search_searchColumn = searchColumn ;
        search_searchText = searchText ;
        search_dataColumns = dataColumns ;
        search_calls++ ;
        return search_return ;
    }

    public DBResults search( String table , String searchColumn , String searchText[] , String[] dataColumns ) throws SQLException
    {
        search_table = table ;
        search_searchColumn = searchColumn ;
        search_searchText = searchText ;
        search_dataColumns = dataColumns ;
        search_calls++ ;
        return search_return ;
    }

    public Connection update_con = null ;
    public String update_table = null ;
    public String update_whereClause = null ;
    public String update_searchColumn = null ;
    public Object update_searchText = null ; // could be a string or string array
    public Map update_data = null ;
    public int update_calls = 0 ;

    public void update( String table , String searchColumn , String searchText , Map data ) throws SQLException
    {
        update_table = table ;
        update_searchColumn = searchColumn ;
        update_searchText = searchText ;
        update_data = data ;
        update_calls++;
    }

    public void update( Connection con , String table , String searchColumn , String searchText , Map data ) throws SQLException
    {
        update_con = con ;
        update_table = table ;
        update_searchColumn = searchColumn ;
        update_searchText = searchText ;
        update_data = data ;
        update_calls++;
    }

    public void update( String table , String searchColumn , String[] searchText , Map data ) throws SQLException
    {
        update_table = table ;
        update_searchColumn = searchColumn ;
        update_searchText = searchText ;
        update_data = data ;
        update_calls++;
    }

    public void update( Connection con , String table , String searchColumn , String[] searchText , Map data ) throws SQLException
    {
        update_con = con ;
        update_table = table ;
        update_searchColumn = searchColumn ;
        update_searchText = searchText ;
        update_data = data ;
        update_calls++;
    }

    public void update( String table , String whereClause , Map data ) throws SQLException
    {
        update_table = table ;
        update_whereClause = whereClause ;
        update_data = data ;
        update_calls++;
    }

    public void update( Connection con , String table , String whereClause , Map data ) throws SQLException
    {
        update_con = con ;
        update_table = table ;
        update_whereClause = whereClause ;
        update_data = data ;
        update_calls++;
    }

    public Connection delete_con = null ;
    public String delete_table = null ;
    public String delete_searchColumn = null ;
    public String delete_searchText = null ;
    public int delete_calls = 0 ;

    public void delete( Connection con , String table , String searchColumn , String searchText ) throws SQLException
    {
        delete_con = con ;
        delete_table = table ;
        delete_searchColumn = searchColumn ;
        delete_searchText = searchText ;
        delete_calls++ ;
    }

    public void delete( String table , String searchColumn , String searchText ) throws SQLException
    {
        delete_table = table ;
        delete_searchColumn = searchColumn ;
        delete_searchText = searchText ;
        delete_calls++ ;
    }

    public Connection insert_con = null ;
    public String insert_table = null ;
    public Map insert_data = null ;
    public int insert_calls = 0 ;

    public void insert( Connection con , String table , Map data ) throws SQLException
    {
        insert_con = con ;
        insert_table = table ;
        insert_data = data ;
        insert_calls++ ;
    }

    public void insert( String table , Map data ) throws SQLException
    {
        insert_table = table ;
        insert_data = data ;
        insert_calls++ ;
    }

    public class MockConnection
    {

        MockConnection( Class interfaceToMock )
        {
            ClassLoader loader = interfaceToMock.getClassLoader();

            InvocationHandler handler = new InvocationHandler()
            {
                public Object invoke( Object proxy , Method method , Object[] args ) throws Throwable
                {
                    return this.invoke( proxy , method , args );
                }
            };
            Proxy.newProxyInstance( loader , new Class[]{ interfaceToMock } , handler );
        }

    }

}
