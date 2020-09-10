import java.util.function.Function;

public class FunctionHolder
{
	private Runnable method;
	private Function<Void,String> f;

	public FunctionHolder(String option)
	{
		if (option.equals("a")) {
			method = Class1::helloworld;
		} else if (option.equals("b")) {
			method = Class2::goodbyeworld;
		}
	}

	public void runMethod()
	{
		method.run();
	}

	public static void main(String[] args)
	{

		FunctionHolder test = new FunctionHolder(args[0]);
		test.runMethod();

		f.apply();
		

	}
}
