package com.javaranch.common ;

import java.io.* ;
import java.util.* ;
import org.w3c.dom.* ;
import javax.xml.parsers.* ;

/** Some static method shortcuts to help with working with DOM. <p>

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
public class DOM
{

    /** Get the String representation of the attribute. <p>

        @return never returns null.  Empty string if no match. <p>
    */
    public static String getTextAttribute( Element node , String attributeName )
    {
        String s = node.getAttribute( attributeName );
        if ( s == null )
        {
            s = "" ;
        }
        return s ;
    }

    /** Get the int representation of the attribute. <p>

        Throws an exception if there is no match. <p>

    */
    public static int getIntAttribute( Element node , String attributeName ) throws Exception
    {
        String s = getTextAttribute( node , attributeName );
        if ( s.length() == 0 )
        {
            throw new Exception( "could not find attribute " + attributeName + " in " + node.getTagName() );
        }
        return Integer.parseInt( s ); // could throw a number format exception ;
    }

    /** Get the boolean representation of the attribute. <p>

        @param trueText If the text in DOM matches this string, true will be returned. <p>
        @param defaultValue If the attribute is not found, this is what will be returned. <p>
    */
    public static boolean getBooleanAttribute( Element node , String attributeName , String trueText , boolean defaultValue )
    {
        boolean returnVal = defaultValue ;

        String s = node.getAttribute( attributeName );
        if ( ( s != null ) && ( s.length() > 0 ) )
        {
            returnVal = s.equals( trueText );
        }

       return returnVal ;
    }

    /** given an element, extract the text. <p>

        Useful only if you know there is nothing between the node tags other than text. If there
        are other tags, this will return whatever text appears before the first tag. <p>
    */
    public static String getText( Element node )
    {
        String s = ((Text)node.getFirstChild()).getData();
        if ( s == null )
        {
            s = "" ;
        }
        return s ;
    }

    /** Given an element, find all of the text children and concatenate them together. <p>

        All leading and trailing whitespace will be removed. <p>
    */
    public static String getAllText( Element node )
    {
        Str returnVal = new Str();
        NodeList list = node.getChildNodes();
        int i = 0 ;
        boolean done = false ;
        while( ! done )
        {
            if ( i >= list.getLength() )
            {
                done = true ;
            }
            else
            {
                Node n = list.item( i );
                if ( n instanceof Text )
                {
                    Text t = (Text)n ;
                    String s = t.getData();
                    if ( s != null )
                    {
                        returnVal.append( s );
                    }
                }
                i++ ;
            }
        }
        returnVal.trimWhitespace();
        return returnVal.toString();
    }

    /** Find the child element and extract it's text with getText(). <p>
    */
    public static String getChildText( Element node , String childName ) throws Exception
    {
        String returnVal = "";
        NodeList nodes = node.getElementsByTagName( childName );
        if ( nodes.getLength() == 1 )
        {
            String s = getText( (Element)nodes.item( 0 ) );
            if ( s != null )
            {
                returnVal = s ;
            }
        }
        else
        {
            throw new Exception( "could not find child" );
        }
        return returnVal ;
    }

    private static class NoodleList extends ArrayList implements NodeList
    {

        public int getLength()
        {
            return size();
        }

        public Node item( int index )
        {
            return (Node)get( index );
        }

    }

    /** Retreives all of the children that have this particular tag name. <p>
    */
    public static NodeList getChildren( Node node , String tagName )
    {
        NodeList bigList = node.getChildNodes();
        NoodleList shortList = new NoodleList();
        for( int i = 0 ; i < bigList.getLength() ; i++ )
        {
            Node n = bigList.item( i );
            if ( tagName.equals( n.getNodeName() ) )
            {
                shortList.add( n );
            }
        }
        return shortList ;
    }

    /** Pass in an XML string and get back the root DOM element. <p>
    */
    public static Element getRoot( String xml ) throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse( new ByteArrayInputStream( xml.getBytes() ) );
        return doc.getDocumentElement();
    }

}
