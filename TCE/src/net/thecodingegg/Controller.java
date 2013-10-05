package net.thecodingegg;

import java.io.File;
import java.lang.ProcessBuilder.Redirect;
import java.util.Map;

public class Controller {

	public static void main(String[] args) throws Exception {

		
		// Questo in realtà è codice chiamato dalla servlet
		ProcessBuilder pb = new ProcessBuilder(
				"java",
				"-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=1045", // Qui mettere suspend=y per poi attaccarsi con il debugger
				"net.thecodingegg.Executor", "System.out.println(4);");
		Map<String, String> env = pb.environment();
		env.put("CLASSPATH", env.get("CLASSPATH")
				+ ";C:\\Users\\Chiara\\git\\CodingEgg\\TCE\\build\\classes");
		
		
		
		pb.directory(new File("."));
		File log = new File("log");
		pb.redirectErrorStream(true);
		pb.redirectOutput(Redirect.appendTo(log));
		Process p = pb.start();
		p.waitFor();
		p.destroy();
		System.out.println("Finishing");
		assert pb.redirectInput() == Redirect.PIPE;
		assert pb.redirectOutput().file() == log;
		assert p.getInputStream().read() == -1;
	}
}
