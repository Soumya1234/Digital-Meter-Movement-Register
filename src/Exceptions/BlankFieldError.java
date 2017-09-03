/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Exceptions;

/**
 *
 * @author Soumyadeep
 */
public class BlankFieldError extends Exception{
    
    private String ErrorType;
    public BlankFieldError(String a)
    {
        ErrorType=a;
    }
    public String toString()
    {
        return ErrorType;
    }
    
}
