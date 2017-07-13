package com.javaranch.common ;

/** Used when unit testing code. <p>

    This class is to be used only in conjunction with the UnitTest framework. <p>

    See UnitTest.java for details. <p>

    - - - - - - - - - - - - - - - - - <p>

    Copyright (c) 1999-2004 Paul Wheaton <p>

    You are welcome to do whatever you want to with this source file provided
    that you maintain this comment fragment (between the dashed lines).
    Modify it, change the package name, change the class name ... personal or business
    use ...  sell it, share it ... add a copyright for the portions you add ... <p>

    My goal in giving this away and maintaining the copyright is to hopefully direct
    developers back to JavaRanch. <p>

    The original source can be found at <a href=http://www.javaranch.com>JavaRanch</a> <p>

    - - - - - - - - - - - - - - - - - <p>

    @author Paul Wheaton

*/

public class TestLog extends ErrorLog
{

    /** The message "test fail" plus the source file name and line number are added to the log. <p>
    */
    public void fail()
    {
        add( "test fail" );
    }

    /** The message "test fail:" plus the message, source file name and line number are added to the log. <p>
    */
    public void fail( String message )
    {
        add( message );
    }

    /** If false is passed in, the message "test fail" plus the source file name and line number are added to the log. <p>
    */
    public void test( boolean b )
    {
        if ( ! b )
        {
            fail();
        }
    }

    /** If false is passed in, the text "test fail: " plus the message, source file name and line number are added to the log. <p>
    */
    public void test( boolean b , String message )
    {
        if ( ! b )
        {
            fail( message );
        }
    }

    private void fail( String testResult , String expected , String message )
    {
        fail( "test result \"" + testResult + "\" does not match expected value \"" + expected + "\".  " + message  );
    }

    private void fail( String testResult , String expected )
    {
        fail( testResult , expected , "" );
    }

    /** If the two values do not match, the discrepency, the file name and the line number will be added to the log. <p>

        The message will look similar to this: test result "5" does not match expected value "2". <p>

        This method will be used for testing byte and short values too. <p>
    */
    public void test( int testResult , int expected )
    {
        test( testResult , expected , "" );
    }

    /** If the two values do not match, the discrepency, the message, the file name and the line number will be added to the log. <p>

        The message will look similar to this: test result "5" does not match expected value "2".  Too many pancakes.<p>

        This method will be used for testing byte and short values too. <p>
    */
    public void test( int testResult , int expected , String message )
    {
        if ( testResult != expected )
        {
            fail( String.valueOf( testResult ) , String.valueOf( expected ) , message );
        }
    }

    /** If the two values do not match, the discrepency, the file name and the line number will be added to the log. <p>

        The message will look similar to this: test result "5" does not match expected value "2". <p>
    */
    public void test( char testResult , char expected )
    {
        test( testResult , expected , "" );
    }

    /** If the two values do not match, the discrepency, the message, the file name and the line number will be added to the log. <p>

        The message will look similar to this: test result "5" does not match expected value "2".  Too many pancakes.<p>
    */
    public void test( char testResult , char expected , String message )
    {
        if ( testResult != expected )
        {
            fail( String.valueOf( testResult ) , String.valueOf( expected ) , message );
        }
    }

    /** If the two values do not match, the discrepency, the file name and the line number will be added to the log. <p>

        The message will look similar to this: test result "5" does not match expected value "2". <p>
    */
    public void test( long testResult , long expected )
    {
        test( testResult , expected , "" );
    }

    /** If the two values do not match, the discrepency, the message, the file name and the line number will be added to the log. <p>

        The message will look similar to this: test result "5" does not match expected value "2".  Too many pancakes.<p>
    */
    public void test( long testResult , long expected , String message )
    {
        if ( testResult != expected )
        {
            fail( String.valueOf( testResult ) , String.valueOf( expected ) , message );
        }
    }

    /** If the two values do not match, the discrepency, the file name and the line number will be added to the log. <p>

        The message will look similar to this: test result "5" does not match expected value "2". <p>

        This method will be used for testing float values too. <p>

        Because of the imprecision of floating point, floats and doubles must be tested with a range. <p>
    */
    public void testRange( double testResult , double low , double high )
    {
        testRange( testResult , low , high , "" );
    }

    /** If the two values do not match, the discrepency, the message, the file name and the line number will be added to the log. <p>

        The message will look similar to this: test result "5" does not match expected value "2".  Too many pancakes.<p>

        This method will be used for testing float values too. <p>

        Because of the imprecision of floating point, floats and doubles must be tested with a range. <p>
    */
    public void testRange( double testResult , double low , double high , String message )
    {
        if ( ( testResult < low ) || ( testResult > high ) )
        {
            fail( "test result " + testResult + " does not fall within the expected range of " + low + " and " + high + ".  " + message  );
        }
    }

    /** If the two values do not match, the discrepency, the file name and the line number will be added to the log. <p>

        The message will look similar to this: test result "5" does not match expected value "2". <p>

        Note that the objects must properly implement the equals() method and the toString() method. <p>
    */
    public void test( Object testResult , Object expected )
    {
        test( testResult , expected , "" );
    }

    /** If the two values do not match, the discrepency, the message, the file name and the line number will be added to the log. <p>

        The message will look similar to this: test result "5" does not match expected value "2".  Too many pancakes.<p>

        Note that the objects must properly implement the equals() method and the toString() method. <p>
    */
    public void test( Object testResult , Object expected , String message )
    {
        if ( ( testResult == null ) && ( expected != null ) )
        {
            fail( "null" , expected.toString() , message );
        }
        else if ( testResult != null )
        {
            if ( expected == null )
            {
                fail( testResult.toString() , "null" , message );
            }
            else if ( ! testResult.equals( expected ) )
            {
                fail( testResult.toString() , expected.toString() , message );
            }
        }
    }

}





