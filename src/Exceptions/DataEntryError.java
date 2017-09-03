/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Exceptions;

/**
 *
 * @author Station Manager
 */
public class DataEntryError extends Exception{
    private String ErrorType;
    public DataEntryError(String a)
    {
       ErrorType=a;
    }
    public String toString()
    {
        return ErrorType;
    }
}
