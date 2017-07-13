package com.javaranch.db;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.Types;

public class OracleFacade extends DBFacade
{

    public OracleFacade( String jndiLookup )
    {
        super( jndiLookup );
    }

    public OracleFacade( String databaseDriver , String databaseURL )
    {
        super( databaseDriver , databaseURL );
    }

    public OracleFacade( String driver , String databaseURL , String databaseName , String databasePassword )
    {
        super( driver , databaseURL , databaseName , databasePassword );
    }

    protected PreparedStatement getPreparedStatementForInsert( Connection con , String sql , String idColumnName ) throws Exception
    {
        return con.prepareCall( "BEGIN " + sql + " returning " + idColumnName + " INTO ?; END;" );
    }

    protected String processPreparedStatementForInsert( PreparedStatement statement , int streamDataCount ) throws Exception
    {
        CallableStatement callableStatement = (CallableStatement)statement ;
        int outParameterNumber = streamDataCount + 1 ;
        callableStatement.registerOutParameter( outParameterNumber , Types.INTEGER );
        statement.executeUpdate();
        return callableStatement.getString( outParameterNumber );
    }

}
