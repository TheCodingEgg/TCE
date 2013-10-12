package net.thecodingegg;

import java.io.File;
import java.lang.ProcessBuilder.Redirect;
import java.util.List;
import java.util.Map;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Method;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.ModificationWatchpointEvent;
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
				"net.thecodingegg.Executor", "int a = 3;System.out.println(a);");

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

		MethodEntryRequest mReq = ev.createMethodEntryRequest();
		mReq.addThreadFilter(mainThread);
		mReq.addClassFilter("Apple1Test");
		mReq.setEnabled(true);

		mReq = ev.createMethodEntryRequest();
		mReq.addThreadFilter(mainThread);
		mReq.addClassFilter("net.thecodingegg.Executor");
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
		sReq.enable();

		vm.resume(); // Let the vm proceed to the first breakpoint

		// process events
		EventQueue eventQueue = vm.eventQueue();
//		boolean doLog = false;
		while (true) {
			EventSet eventSet = eventQueue.remove();
			for (Event event : eventSet) {
				if (event instanceof StepEvent) {
					List<StackFrame> frames = mainThread.frames();
					// the current frame will be the first or the last. should
					// be the last

					StackFrame currentFrame = frames.get(0);
					// Here look for vars
					List<LocalVariable> localVariables = currentFrame
							.visibleVariables();
					for (LocalVariable l : localVariables) {
						System.out.println(l);
					}
				} else if (event instanceof MethodEntryEvent) {
					MethodEntryEvent mEv = (MethodEntryEvent) event;
					Method method = mEv.method();
					if (method.name().equals("execute")) {
//						doLog = true;
					}
//					if (doLog) 
						System.out.println("entering " + method.name());

					try {
						List<StackFrame> frames = mainThread.frames();
						// the current frame will be the first or the last.
						// should
						// be the last

						StackFrame currentFrame = frames.get(0);
						// Here look for vars
						List<LocalVariable> localVariables = currentFrame
								.visibleVariables();
						for (LocalVariable l : localVariables) {
							System.out.println(l);
						}
					} catch (Throwable exc) {
						// System.out.println("No visible vars in "
						// + method.name());
						// No local variables available. skip
					}

				} else if (event instanceof MethodExitEvent) {
					MethodExitEvent mEv = (MethodExitEvent) event;

					Method method = mEv.method();
					if (method.name().equals("execute")) {
//						doLog = false;
					}

					System.out.println("exiting: "
							+ method.declaringType().name() + "."
							+ method.name());

					List<StackFrame> frames = mainThread.frames();
					// the current frame will be the first or the last. should
					// be the last

					StackFrame currentFrame = frames.get(0);
					// Here look for vars
					try {
						List<LocalVariable> localVariables = currentFrame
								.visibleVariables();
						for (LocalVariable l : localVariables) {
							System.out.println("####" + l);
							if (l.name().equals("instance")) {
								System.out.println(currentFrame.getValue(l));
							}
						}
						
						if (localVariables.isEmpty()) {
							System.out.println("No visible vars in "
							 + method.name());
						}
					} catch (AbsentInformationException exc) {
						if (method.name().equals("execute")) {
							exc.printStackTrace();
						}
						// No local variables available. skip
					}

				} else if (event instanceof VMDeathEvent
						|| event instanceof VMDisconnectEvent) {
					// exit
					return;
					// } else if (event instanceof ClassPrepareEvent) {
					// // watch field on loaded class
					// ClassPrepareEvent classPrepEvent = (ClassPrepareEvent)
					// event;
					// ReferenceType refType = classPrepEvent
					// .referenceType();
					// addFieldWatch(vm, refType);
				} else if (event instanceof ModificationWatchpointEvent) {
					// a Test.foo has changed
					ModificationWatchpointEvent modEvent = (ModificationWatchpointEvent) event;
					System.out.println("old=" + modEvent.valueCurrent());
					System.out.println("new=" + modEvent.valueToBe());
					System.out.println();
				}
			}
			eventSet.resume();
		}

		/*
		 * // TODO: get the names of all declared variables (probably take a
		 * look at frames) List<StackFrame> frames = mainThread.frames(); // the
		 * current frame will be the first or the last. should be the last
		 * 
		 * StackFrame currentFrame = frames.get(frames.size()-1); // Here look
		 * for vars List<LocalVariable> localVariables =
		 * currentFrame.visibleVariables(); for (LocalVariable l:localVariables)
		 * { System.out.println(l); }
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * p.waitFor(); p.destroy(); System.out.println("Finishing"); assert
		 * pb.redirectInput() == Redirect.PIPE; assert
		 * pb.redirectOutput().file() == log; assert p.getInputStream().read()
		 * == -1;
		 */
	}
}
