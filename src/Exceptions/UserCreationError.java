package Exceptions;

public class UserCreationError extends Exception{
	private String ErrorType;
    public UserCreationError(String a)
    {
        ErrorType=a;
    }
    public String toString()
    {
        return ErrorType;
    }

}
