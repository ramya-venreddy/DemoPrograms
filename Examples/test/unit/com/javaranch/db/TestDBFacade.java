package com.javaranch.db;

import junit.framework.TestCase;

public class TestDBFacade extends TestCase
{

    public void test_normalizeSearchText() throws Exception
    {
        assertEquals( "NULL" , DBFacade.normalizeSearchText( null ) );
        assertEquals( "'can''t'" , DBFacade.normalizeSearchText( "can't" ) );
        assertEquals( "'c''a''n''t'" , DBFacade.normalizeSearchText( "c'a'n't" ) );
        assertEquals( "'''cant'''" , DBFacade.normalizeSearchText( "'cant'" ) );
    }

}
