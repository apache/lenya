package org.wyona.util;

import java.io.*;
import java.util.*;

/**
 * @author Michael Wechner
 * @version 1.12.22
 */
public class Stack extends Vector{
  int maxsize=0;
/**
 * Hello Levi
 */
  public static void main(String[] args){
    Stack stack=new Stack(5);
    stack.push(new String("Hello"));
    stack.push(new String("Levi"));
    stack.push(new String("how"));
    stack.push(new String("are"));
    stack.push(new String("you"));
    stack.push(new String("today"));
    stack.push(new String("?"));
    for(int i=0;i<stack.size();i++){
      System.out.println(stack.elementAt(i));
      }
    }
/**
 *
 */
  public Stack(int maxsize){
    this.maxsize=maxsize;
    }
/**
 *
 */
  public void push(Object object){
    insertElementAt(object,0);
    if(size() == (maxsize+1)){
      removeElementAt(maxsize);
      }
    }
  }
