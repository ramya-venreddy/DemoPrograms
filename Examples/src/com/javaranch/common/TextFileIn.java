package com.javaranch.common ;

import java.io.*;

/** Since TextFileIn extends BufferedReader and adds nothing more than a convenient
    constructor, all other member functions are identical to BufferedReader. <p>

    Example usage of TextFileIn: <p>
    <pre>

    * TextFileIn in = new TextFileIn("file.txt");
    * boolean done = false ;
    * while ( ! done )
    * {
    *     String s = in.readLine();
    *     if ( s == null )
    *     {
    *         done = true ;
    *     }
    *     else
    *     {
    *         System.out.println( s );
    *     }
    * }
    * in.close();

    </pre><p>


    - - - - - - - - - - - - - - - - - <p>

    Copyright (c) 1998-2004 Paul Wheaton <p>

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
public final class TextFileIn extends BufferedReader
{

    public TextFileIn( String fileName ) throws FileNotFoundException
    {
        super( new FileReader( fileName ) );
    }

}



