package it.filedriver;

import it.filedriver.connection.ConnectionProvider;
import it.filedriver.connection.FileConnectionProvider;
import it.filedriver.connection.ProxyConnectionHandler;
import it.filedriver.connection.SocketConnectionProvider;
import it.filedriver.endpoint.EndPoint;
import it.filedriver.endpoint.FileEndPoint;
import it.filedriver.endpoint.ServerSocketEndPoint;
import it.filedriver.operation.MultithreadedOperationsRunner;
import it.filedriver.util.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class Main {

	private static final Map<String, Class<? extends ConnectionProvider>> connectionProviderTypes = new HashMap<String, Class<? extends ConnectionProvider>>();
	private static final Map<String, Class<? extends EndPoint>> endPointTypes = new HashMap<String, Class<? extends EndPoint>>();

	static {
		connectionProviderTypes.put("file", FileConnectionProvider.class);
		connectionProviderTypes.put("sock", SocketConnectionProvider.class);
		endPointTypes.put("file", FileEndPoint.class);
		endPointTypes.put("sock", ServerSocketEndPoint.class);
	}

	public static void main(String[] args) {
		try {
			for (String arg : args) {
				String[] split = arg.split("#");

				MultithreadedOperationsRunner multithreadedOperationsRunner = new MultithreadedOperationsRunner();
				MultithreadedServer server = new MultithreadedServer();
				server.setOperationsRunner(multithreadedOperationsRunner);
				server.setEndPoint(createEndPoint(split[0], split[1]));
				ProxyConnectionHandler proxyConnectionHandler = new ProxyConnectionHandler();
				proxyConnectionHandler.setConnectionsProvider(createConnectionProvider(split[2], split[3]));
				proxyConnectionHandler.setOperationsRunner(multithreadedOperationsRunner);
				server.setConnectionHandler(proxyConnectionHandler);
				server.start();
				Logger.log("listening on " + split[0] + "(" + split[1]
						+ ") proxying to " + split[2] + "(" + split[3] + ")");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static ConnectionProvider createConnectionProvider(String name,
			String param) throws Exception {
		return create(connectionProviderTypes, name, param);
	}

	private static EndPoint createEndPoint(String name, String param)
			throws Exception {
		return create(endPointTypes, name, param);
	}

	private static <T> T create(Map<String, Class<? extends T>> map,
			String name, String param) throws NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		Constructor<? extends T> constructor = map.get(name).getConstructor(
				String.class);
		return constructor.newInstance(param);
	}

}
