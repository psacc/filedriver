package it.filedriver.util;

import java.io.PrintStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Date;

public class Logger {
	private static final PrintStream PRINT_STREAM = System.err;
	private static final int CALLER_LEVEL = 2;
	private static Logger logger;

	@SuppressWarnings("unchecked")
	public <T> T proxy(final T object) {
		T proxy;

		if (object == null || !object.getClass().isInterface()) {
			log("Can't proxy: " + object);
			proxy = object;
		} else {
			final Class<? extends Object> clazz = object.getClass();
			final String sourceClass = clazz.getCanonicalName();

			proxy = (T) Proxy.newProxyInstance(clazz.getClassLoader(), object
					.getClass().getInterfaces(), new InvocationHandler() {

				@Override
				public Object invoke(Object proxy, Method method, Object[] args)
						throws Throwable {
					String methodName = method.getName();

					boolean log = !"toString".equals(methodName);
					if (log) {
						entering(sourceClass, methodName, args);
					}
					Object result = method.invoke(object, args);
					if (log) {
						exiting(sourceClass, methodName, result);
					}

					return result;
				}
			});
		}

		return proxy;
	}

	private void entering(String className, String methodName, Object[] args) {
		StringBuilder params = new StringBuilder();
		if (args != null) {
			for (Object o : args) {
				params.append(o).append(", ");
			}
		}
		log(className, methodName, "entering (" + params.toString() + ")");
	}

	private void log(String className, String methodName, String msg) {
		logInternal("[" + className + "." + methodName + "] " + msg);
	}

	private void exiting(String className, String methodName, Object result) {
		log(className, methodName, "exiting (" + result + ")");
	}

	public static void log(String message) {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		if (stackTrace.length > CALLER_LEVEL) {
			StackTraceElement stackTraceElement = stackTrace[CALLER_LEVEL];
			getLogger().log(stackTraceElement.getClassName(),
					stackTraceElement.getMethodName(), message);
		} else {
			logInternal(message);
		}
	}

	private static Logger getLogger() {
		if (logger == null) {
			logger = new Logger();
		}
		return logger;
	}

	private static void logInternal(String message) {
		PRINT_STREAM.println(Thread.currentThread().getId() + " " + new Date()
				+ " " + message);
	}

}
