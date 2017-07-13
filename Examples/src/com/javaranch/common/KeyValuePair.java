package com.javaranch.common ;

// todo - write javadoc
public class KeyValuePair implements java.io.Serializable
{

    private String key ;
    private String value ;

    public KeyValuePair( String key , String value )
    {
        this.key = key ;
        this.value = value ;
    }

    public String getKey()
    {
        return key ;
    }

    public String getValue()
    {
        return value ;
    }

}
