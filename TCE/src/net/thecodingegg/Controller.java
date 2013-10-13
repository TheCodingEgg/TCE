package net.thecodingegg;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StackFrame;
import com.sun.jdi.StringReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.jdi.request.MethodExitRequest;
import com.sun.jdi.request.StepRequest;

public class Controller {

	public static void main(String[] args) throws Exception {

		// This code must be called by the servlet
		ProcessBuilder pb = new ProcessBuilder(
				"java",
				"-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=1045", // suspend=y
				"net.thecodingegg.Executor", "int a = 3;\nSystem.out.println(a);\nint b=5;\na = 6;\nApple apple1 = new Apple();\n"
						+ "Apple apple2 = new Apple();\n"
						+ "apple1 = apple2;\n");

		// pb.environment is the set of environment variables: SET on command
		// line

		Map<String, String> env = pb.environment();
		// Append a directory to the classpath so that the new
		// process can find the class
		env.put("CLASSPATH", env.get("CLASSPATH")
				+ ";C:\\Users\\Chiara\\git\\CodingEgg\\TCE\\build\\classes");

		// set the working directory
		pb.directory(new File("."));

		// Redirect the process output to a file
		// FIXME: this is not useful, we must read the console from a socket
		File log = new File("log");
		// pb.redirectErrorStream(true);
		// pb.redirectOutput(Redirect.appendTo(log));

		// Start the new process (JVM). It will stop waiting for the debugger
		// to connect because it is started with "suspend=y"
		Process p = pb.start();

		// Connect to the VM
		VMAcquirer acquirer = new VMAcquirer();
		VirtualMachine vm = acquirer.connect(1045);

		// TODO Add a watch on the "execCode" method of class Executor

		// Get the main thread (only one is started, unless the client code
		// specifies otherwise)
		// TODO (not now!!!): find a way to limit the client's possibilities to
		// start a thread

		List<ThreadReference> allThreads = vm.allThreads();

		// Find the main() thread

		ThreadReference mainThread = null;

		for (ThreadReference t : allThreads) {
			if (t.name().equals("main")) {
				mainThread = t;
				break;
			}
		}

		if (mainThread == null) {
			throw new IllegalStateException("Could not find main thread.");
		}

		EventRequestManager ev = vm.eventRequestManager();

		// We need to trace three events:
		// 1 - entering the Apple1Test.execute method --> enable step by step execution
		
		// 2 - exiting the Apple1Test.execute method --> disnable step by step execution
		
		// 3 - exiting the Executor main method --> get console and error log datas

		// Between steps 1 and 2, step by step execution must get var information at each
		// step
		
		// 1 - entering methods in the compiled class
		MethodEntryRequest mReq = ev.createMethodEntryRequest();
		mReq.addThreadFilter(mainThread);
		mReq.addClassFilter("Apple1Test");
		mReq.setEnabled(true);

		MethodExitRequest mexReq = ev.createMethodExitRequest();
		mexReq.addThreadFilter(mainThread);
		mexReq.addClassFilter("Apple1Test");
		mexReq.setEnabled(true);

		mexReq = ev.createMethodExitRequest();
		mexReq.addThreadFilter(mainThread);
		mexReq.addClassFilter("net.thecodingegg.Executor");
		mexReq.setEnabled(true);

		StepRequest sReq = ev.createStepRequest(mainThread,
				StepRequest.STEP_LINE, StepRequest.STEP_OVER);
		sReq.addClassFilter("Apple1Test");

		vm.resume(); // Let the vm proceed to the first breakpoint
		String console = null;
		String consoleErr = null;

		int executeLevel = 0;
		// process events
		EventQueue eventQueue = vm.eventQueue();
		while (true) {
			EventSet eventSet = eventQueue.remove();
			for (Event event : eventSet) {
				if (event instanceof StepEvent) {
					StepEvent step = (StepEvent) event;
					System.out.print(step.location()+":\t");
					System.out.println(listVariables(mainThread));
				} else if (event instanceof MethodEntryEvent) {
					
					MethodEntryEvent mEv = (MethodEntryEvent) event;
					Method method = mEv.method();
					// System.out.println("entering " + method.name());
					if (method.name().equals("execute")) {
						executeLevel++;
						if (executeLevel == 2) sReq.enable();
					}

				} else if (event instanceof MethodExitEvent) {
					MethodExitEvent mEv = (MethodExitEvent) event;
					Method method = mEv.method();
					ReferenceType declaringType = method.declaringType();
					
					if (declaringType.name().equals("Apple1Test") &&method.name().equals("execute")) {
						// TODO: disable step by step debugging
						// but list variables first 
						listVariables(mainThread);
						executeLevel--;
						if (executeLevel < 2) sReq.disable();
					} else if (declaringType.name().equals("net.thecodingegg.Executor") && method.name().equals("main")) {
						List<StackFrame> frames = mainThread.frames();
						StackFrame currentFrame = frames.get(0);
						
						// Get console and error log:
						console = getStringValue("console", currentFrame);
						consoleErr = getStringValue("consoleErr", currentFrame);
					}

					/*System.out.println("exiting: "
							+ method.declaringType().name() + "."
							+ method.name()); */

				} else if (event instanceof VMDeathEvent
						|| event instanceof VMDisconnectEvent) {
					System.out.println("Console: "+console);
					System.out.println("Errors: "+consoleErr);
					
					return;
				}
			}
			eventSet.resume();
		}
	}

	
	private static String getStringValue(String s, StackFrame frame) throws AbsentInformationException {
		LocalVariable var = frame.visibleVariableByName(s);
		Value val = frame.getValue(var);
		if (val instanceof StringReference) {
			StringReference rVal = (StringReference) val;
			return rVal.value();
		}
		return null;
	}
	
	
	private static Map<String, Object> listVariables(ThreadReference mainThread) throws IncompatibleThreadStateException, AbsentInformationException {

		Map<String, Object> vars = new HashMap<>();

		List<StackFrame> frames = mainThread.frames();
		StackFrame currentFrame = frames.get(0);
		// Here look for vars
		List<LocalVariable> localVariables = currentFrame.visibleVariables();
		for (LocalVariable l : localVariables) {
			String var = l.name(); 
			Value val = currentFrame.getValue(l);
			vars.put(var, val);
			if (val instanceof ObjectReference) {
				ObjectReference objRef = (ObjectReference) val;
				ReferenceType refType = (ReferenceType) objRef.type();
				// System.out.println(refType.name());
				long objId = objRef.uniqueID();
				vars.put(var, refType.name() + "@"+objId);
				
				//System.out.println(var + " is an object");
			} 
		}
		return vars;
	}
}
