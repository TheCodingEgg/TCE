package net.thecodingegg;

import java.io.File;
import java.lang.ProcessBuilder.Redirect;
import java.util.List;
import java.util.Map;

import com.sun.jdi.LocalVariable;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;

public class Controller {

	public static void main(String[] args) throws Exception {

		
		// This code must be called by the servlet
		ProcessBuilder pb = new ProcessBuilder(
				"java",
				"-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=1045", // suspend=y to connect with the debugger
				"net.thecodingegg.Executor", "System.out.println(4);");
		Map<String, String> env = pb.environment();
		env.put("CLASSPATH", env.get("CLASSPATH")
				+ ";C:\\Users\\Chiara\\git\\CodingEgg\\TCE\\build\\classes");
		
		
		
		pb.directory(new File("."));
		File log = new File("log");
		pb.redirectErrorStream(true);
		pb.redirectOutput(Redirect.appendTo(log));
		Process p = pb.start();
		
		// Connect to the VM
		VMAcquirer acquirer = new VMAcquirer();
		VirtualMachine vm = acquirer.connect(1045);
		// TODO Add a watch on the "execCode" method of class Executor
		
		
		vm.resume(); // Let the vm proceed to the first breakpoint
		
		
		// Get the main thread (only one is started, unless the client code specifies otherwise)
		// TODO (not now!!!): find a way to limit the client's possibilities to start a thread
		ThreadReference execThread = vm.allThreads().get(0);
		
		// TODO: get the names of all declared variables (probably take a look at frames)
		List<StackFrame> frames =  execThread.frames();
		// the current frame will be the first or the last. should be the last
		
		StackFrame currentFrame = frames.get(frames.size()-1);
		// Here look for vars
		List<LocalVariable> localVariables = currentFrame.visibleVariables();
		for (LocalVariable l:localVariables) {
			System.out.println(l);
		}
		
		
		
		
		
		
		p.waitFor();
		p.destroy();
		System.out.println("Finishing");
		assert pb.redirectInput() == Redirect.PIPE;
		assert pb.redirectOutput().file() == log;
		assert p.getInputStream().read() == -1;
	}
}
