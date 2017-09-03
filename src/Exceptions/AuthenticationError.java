package Exceptions;

public class AuthenticationError extends Exception{
	private String ErrorType;
    public AuthenticationError(String a)
    {
        ErrorType=a;
    }
    public String toString()
    {
        return ErrorType;
    }

}
