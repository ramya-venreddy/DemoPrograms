package com.javaranch.common;

import junit.framework.*;

public class TestStr extends TestCase
{

    public void test_append_char() throws Exception
    {
        Str s = new Str( "123" );
        s.append( '4' );
        assertEquals( s.toString() , "1234" );
        s = new Str();
        s.append( 'x' );
        assertEquals( s.toString() , "x" );
    }

    public void test_append_string() throws Exception
    {
        Str s = new Str( "123" );
        s.append( "45" );
        assertEquals( s.toString() , "12345" );
        s.append( "" );
        assertEquals( s.toString() , "12345" );
        s.append( "6" );
        assertEquals( s.toString() , "123456" );
        s = new Str();
        s.append( "" );
        assertEquals( s.toString() , "" );
        s.append( "x" );
        assertEquals( s.toString() , "x" );
    }

    public void test_insert_char() throws Exception
    {
        Str s = new Str( "123" );
        s.insert( 'x' , 1 );
        assertEquals( s.toString() , "1x23" );
        s.insert( 'y' , 0 );
        assertEquals( s.toString() , "y1x23" );
        s.insert( 'z' , 5 );
        assertEquals( s.toString() , "y1x23z" );
        s.insert( 'a' , 10 );
        assertEquals( s.toString() , "y1x23za" );
        s = new Str();
        s.insert( 'x' , 0 );
        assertEquals( s.toString() , "x" );
    }

    public void test_insert_string() throws Exception
    {
        Str s = new Str( "123" );
        s.insert( "x" , 1 );
        assertEquals( s.toString() , "1x23" );
        s.insert( "y" , 0 );
        assertEquals( s.toString() , "y1x23" );
        s.insert( "z" , 5 );
        assertEquals( s.toString() , "y1x23z" );
        s.insert( "a" , 10 );
        assertEquals( s.toString() , "y1x23za" );
        s.insert( "" , 0 );
        assertEquals( s.toString() , "y1x23za" );
        s.insert( "" , 3 );
        assertEquals( s.toString() , "y1x23za" );
        s.insert( "" , 7 );
        assertEquals( s.toString() , "y1x23za" );
        s.insert( "" , 20 );
        assertEquals( s.toString() , "y1x23za" );
        s.insert( "bbb" , 0 );
        assertEquals( s.toString() , "bbby1x23za" );
        s.insert( "ccc" , 3 );
        assertEquals( s.toString() , "bbbcccy1x23za" );
        s.insert( "ddd" , 13 );
        assertEquals( s.toString() , "bbbcccy1x23zaddd" );
        s.insert( "eee" , 50 );
        assertEquals( s.toString() , "bbbcccy1x23zadddeee" );

        s = new Str();
        s.insert( "" , 0 );
        assertEquals( s.toString() , "" );
        s.insert( "xxx" , 0 );
        assertEquals( s.toString() , "xxx" );
    }

    public void test_replace_char_char() throws Exception
    {
        Str s = new Str( "123456789012345678901234567890" );
        s.replace( '5' , 'e' );
        assertEquals( s.toString() , "1234e678901234e678901234e67890" );
        s.replace( '1' , 'f' ); // beginning of string
        assertEquals( s.toString() , "f234e67890f234e67890f234e67890" );
        s.replace( '0' , 'g' ); // end of string
        assertEquals( s.toString() , "f234e6789gf234e6789gf234e6789g" );
        s.replace( 'X' , 'h' ); // there are no x's
        assertEquals( s.toString() , "f234e6789gf234e6789gf234e6789g" );

        s = new Str();
        s.replace( '5' , 'e' );
        assertEquals( s.toString() , "" );
    }

    public void test_replace_char_string() throws Exception
    {
        Str s = new Str( "123456789012345678901234567890" );
        s.replace( '5' , "e" );
        assertEquals( s.toString() , "1234e678901234e678901234e67890" );
        s.replace( '1' , "f" ); // beginning of string
        assertEquals( s.toString() , "f234e67890f234e67890f234e67890" );
        s.replace( '0' , "g" ); // end of string
        assertEquals( s.toString() , "f234e6789gf234e6789gf234e6789g" );
        s.replace( 'X' , "h" ); // there are no x's
        assertEquals( s.toString() , "f234e6789gf234e6789gf234e6789g" );

        s.replace( 'e' , "eee" );
        assertEquals( s.toString() , "f234eee6789gf234eee6789gf234eee6789g" );
        s.replace( '4' , "" );
        assertEquals( s.toString() , "f23eee6789gf23eee6789gf23eee6789g" );
        s.replace( 'e' , "" );
        assertEquals( s.toString() , "f236789gf236789gf236789g" );
        s.replace( '8' , "ii" );
        assertEquals( s.toString() , "f2367ii9gf2367ii9gf2367ii9g" );

        s = new Str();
        s.replace( '5' , "e" );
        assertEquals( s.toString() , "" );
    }

    public void test_removeDoubleSpaces() throws Exception
    {
        Str s = new Str( "1234567890" );
        s.removeDoubleSpaces();
        assertEquals( s.toString() , "1234567890" );
        s.set( "    1   2   3     45  6      " );
        s.removeDoubleSpaces();
        assertEquals( s.toString() , " 1 2 3 45 6 " );
    }

    public void test_extractWord() throws Exception
    {
        Str s = new Str( "The Tick sez \"Spooooooon!\"" );
        Str word = s.extractWord();
        assertEquals( word.toString() , "The" );
        assertEquals( s.toString() , "Tick sez \"Spooooooon!\"" );
        word = s.extractWord();
        assertEquals( word.toString() , "Tick" );
        assertEquals( s.toString() , "sez \"Spooooooon!\"" );
        word = s.extractWord();
        assertEquals( word.toString() , "sez" );
        assertEquals( s.toString() , "\"Spooooooon!\"" );
        word = s.extractWord();
        assertEquals( word.toString() , "\"Spooooooon!\"" );
        assertEquals( s.toString() , "" );
        word = s.extractWord();
        assertEquals( word.toString() , "" );
        assertEquals( s.toString() , "" );

        s.set( " The Tick " );
        word = s.extractWord();
        assertEquals( word.toString() , "The" );
        assertEquals( s.toString() , "Tick" );
    }

    public void test_toInt() throws Exception
    {
        assertEquals( Str.toInt( "999" ) , 999 );
        assertEquals( Str.toInt( "-4" ) , -4 );
        assertEquals( Str.toInt( "gelatinous goo" ) , 0 );
        assertEquals( Str.toInt( "" ) , 0 );
        assertEquals( Str.toInt( "44xx" ) , 0 );
        assertEquals( Str.toInt( "44.4" ) , 0 );
        assertEquals( Str.toInt( "-44.4" ) , 0 );
        assertEquals( Str.toInt( "4e" ) , 0 );
        assertEquals( Str.toInt( "e4" ) , 0 );
    }

    public void test_countDigits() throws Exception
    {
        Str s = new Str( "z8z8z8z" );
        assertEquals( s.countDigits() , 3 );
        s.set( "-92.75" );
        assertEquals( s.countDigits() , 4 );
        s.set( "" );
        assertEquals( s.countDigits() , 0 );
        s.set( "The Tick sez Spoooooon!" );
        assertEquals( s.countDigits() , 0 );
        s.set( "00000" );
        assertEquals( s.countDigits() , 5 );
    }

    public void test_usable() throws Exception
    {
        assertTrue( Str.usable( "x" ) );
        assertTrue( Str.usable( "The Tick" ) );
        assertFalse( Str.usable( null ) );
        assertFalse( Str.usable( "" ) );
    }

    public void test_trim_static() throws Exception
    {
        assertNull( Str.trim( null ) );
        assertNull( Str.trim( "" ) );
        assertEquals( Str.trim( "x" ) , "x" );
        assertEquals( Str.trim( "xxxx" ) , "xxxx" );
    }

    public void test_normalize() throws Exception
    {
        assertEquals( Str.normalize( null ) , "" );
        assertEquals( Str.normalize( "" ) , "" );
        assertEquals( Str.normalize( "x" ) , "x" );
        assertEquals( Str.normalize( "xxxx" ) , "xxxx" );
    }

    public void test_equal() throws Exception
    {
        assertTrue( Str.equal( (String)null , (String)null ) );
        assertTrue( Str.equal( (String[])null , (String[])null ) );
        assertTrue( Str.equal( "" , "" ) );
        assertTrue( Str.equal( "x" , "x" ) );
        Str s = new Str( "The Tick" );
        assertTrue( Str.equal( s.toString() , "The Tick" ) );
        assertFalse( Str.equal( "" , null ) );
        assertFalse( Str.equal( "x" , null ) );
        assertFalse( Str.equal( null , "" ) );
        assertFalse( Str.equal( null , "x" ) );
        assertFalse( Str.equal( "X" , "x" ) );
        assertFalse( Str.equal( null , new String[]{} ) );
        assertFalse( Str.equal( new String[]{} , null ) );
        assertTrue( Str.equal( new String[]{} , new String[]{} ) );
        assertTrue( Str.equal( new String[]{ "" , "" } , new String[]{ "" ,  "" } ) );
        assertTrue( Str.equal( new String[]{ "1" , "2" } , new String[]{ "1" ,  "2" } ) );
        assertTrue( Str.equal( new String[]{ null } , new String[]{ null } ) );
        assertFalse( Str.equal( new String[]{ null , null } , new String[]{ null } ) );
        assertFalse( Str.equal( new String[]{ "1" , "2" } , new String[]{ "1" , "1" } ) );
    }

    public void test_trailingDigits()
    {
        assertEquals( Str.trailingDigits( "abcd12" ) , 2 );
        assertEquals( Str.trailingDigits( "abcd12 " ) , 0 );
        assertEquals( Str.trailingDigits( "abcd" ) , 0 );
        assertEquals( Str.trailingDigits( "" ) , 0 );
    }

    public void test_endsWith()
    {
        Str s = new Str( "spooooon!" );
        assertTrue( s.endsWith( "on!" ) );
        assertTrue( ! s.endsWith( "on" ) );
        assertTrue( s.endsWith( "spooooon!" ) );    }
}
