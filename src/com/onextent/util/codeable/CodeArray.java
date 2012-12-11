package com.onextent.util.codeable;

import java.util.Iterator;

public interface CodeArray<E> {

    void add(E value) throws CodeableException;
    
    E get(int i) throws CodeableException;
    
    int length();
    
    Iterator<E> iterator();
    
    @Override
    String toString();
    @Override
    int hashCode();
}