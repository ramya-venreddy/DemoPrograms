package com.test;

import javax.ejb.Remote;
import java.util.*;

@Remote
public interface LibrarySessionBeanRemote {
	void addBook(String bookName);
	 
    List getBooks();
}
