package Exceptions;

public class GeneralError extends Exception{
private String ErrorType;
public GeneralError(String a)
{
	ErrorType=a;
}
public String toString()
{
	return ErrorType;
}
}
