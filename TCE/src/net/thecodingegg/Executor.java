package net.thecodingegg;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

public class Executor {

	public static void main(String[] args) throws Exception,
			IllegalAccessException, ClassNotFoundException {

		// Code to execute is the first argument

		// If this VM is started with suspend=y, the debugger stops HERE.

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		System.setOut(new PrintStream(bos));
		ByteArrayOutputStream bosErr = new ByteArrayOutputStream();
		System.setErr(new PrintStream(bosErr));

		String code = args[0];
		System.out.println("Going to execute code: " + code);
		System.out.println(execCode(code));
		String console = bos.toString();
		String consoleErr = bosErr.toString();

	}

	private static Object execCode(String code) throws Exception,
			IllegalAccessException, ClassNotFoundException {

		System.out.println("This is executor starting.");

		String fullName = "Apple1Test";

		String prefix = "public class Apple1Test implements net.thecodingegg.Executable{ public String execute() {";

		String suffix = "return \"OK\";}}";

		String classCode = prefix + code + suffix;
		System.out.println(classCode);

		
		String[] compilerOptions = {"-g"};
		// Compilare
		System.out.println("Compiling");
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		JavaFileManager fileManager = new ClassFileManager(
				compiler.getStandardFileManager(null, null, null));

		ArrayList<JavaFileObject> jfiles = new ArrayList<JavaFileObject>();
		jfiles.add(new CharSequenceJavaFileObject(fullName, classCode));
		// We specify a task to the compiler. Compiler should use our file
		// manager and our list of "files".
		// Then we run the compilation with call()
		compiler.getTask(null, fileManager, null, Arrays.asList(compilerOptions), null, jfiles).call();
		System.out.println("Compiled. Getting instance");

		// Creating an instance of our compiled class and
		// running its toString() method
		Class clazz = fileManager.getClassLoader(null).loadClass(fullName);
		Executable instance = (Executable)clazz.newInstance(); // Creare istanza

		// Eseguire

		System.out.println("executing code NOW");
		Object result = instance.execute();
		System.out.println("code executed");
		return result;
	}

}
