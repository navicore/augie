package com.onextent.android.codeable;

import java.util.Iterator;

public interface CodeArray<E> extends Iterable<E> {

    void add(E value) throws CodeableException;
    
    E get(int i) throws CodeableException;
    
    int length();
    
    Iterator<E> iterator();
    
    @Override
    String toString();
    @Override
    int hashCode();
}
