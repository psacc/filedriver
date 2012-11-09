package it.filedriver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import it.filedriver.connection.CollectingConnectionHandler;
import it.filedriver.connection.Connection;
import it.filedriver.connection.ConnectionProvider;
import it.filedriver.connection.ConnectionUtils;
import it.filedriver.connection.FileConnection;
import it.filedriver.connection.FileConnectionProvider;
import it.filedriver.connection.ProxyConnectionHandler;
import it.filedriver.connection.SocketConnectionProvider;
import it.filedriver.endpoint.EndPoint;
import it.filedriver.endpoint.FileEndPoint;
import it.filedriver.endpoint.ServerSocketEndPoint;
import it.filedriver.event.EventEmitter;
import it.filedriver.event.EventReceiver;
import it.filedriver.event.FileEventEmitter;
import it.filedriver.event.FileEventReceiver;
import it.filedriver.operation.MultithreadedOperationsRunner;
import it.filedriver.util.FileUtils;
import it.filedriver.util.Logger;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FileDriverTest {
	private static final int TEST_END_POINT_PORT = 9912;
	private static final String HELLO_WORLD = "hello world!";
	public static final String TEST_END_POINT_PATH = "C:\\windows\\temp\\filedriver\\";
	private final ServerFactory serverFactory;
	private final Logger logger;

	{
		logger = new Logger();
		serverFactory = new ServerFactory();
		serverFactory.setLogger(logger);
	}

	@Before
	public void setup() {
		FileUtils.empty(FileDriverTest.TEST_END_POINT_PATH);
	}

	@Test
	public void testEchoCommandExecution() throws IOException {
		new EndPoint() {

			@Override
			public Connection waitForNewConnection() throws Exception {
				return null;
			}

			@Override
			public void close() throws Exception {
			}

			@Override
			public ConnectionProvider getConnectionProvider() {
				return new ConnectionProvider() {

					@Override
					public Connection createNew() throws Exception {
						return new Connection() {

							@Override
							public void shutdownOutput() throws Exception {
							}

							@Override
							public void shutdownInput() throws Exception {
							}

							@Override
							public OutputStream getOutputStream()
									throws Exception {
								return new OutputStream() {

									@Override
									public void write(int b) throws IOException {
										new InputStreamReader(
												new ByteArrayInputStream(
														new byte[] { (byte) b }))
												.read();
									}
								};
							}

							@Override
							public InputStream getInputStream()
									throws Exception {
								return new InputStream() {

									@Override
									public int read() throws IOException {
										// TODO Auto-generated method stub
										return 0;
									}
								};
							}
						};
					}
				};
			}
		};
		// Server server = new Server();
		// server.startPollingEndpoint(TEST_END_POINT_PATH);

		Client client = new Client();
		client.connectToEndPoint(TEST_END_POINT_PATH);

		client.sendCommand("echo " + HELLO_WORLD);

		InputStream in = client.getOutput();

		String echoResult = new BufferedReader(new InputStreamReader(in))
				.readLine();

		assertEquals(HELLO_WORLD, echoResult);
	}

	@Test
	public void testStreamDataFromMultifileOutputStreamToMultifileInputStream()
			throws Exception {
		MultifileOutputStream outputStream = new MultifileOutputStream(
				TEST_END_POINT_PATH);
		MultifileInputStream inputStream = new MultifileInputStream(
				TEST_END_POINT_PATH);
		assertDataPassesFromTo(outputStream, inputStream);

		outputStream.close();
		assertTrue(inputStream.read() == -1);
	}

	@Test
	public void testFileConnectionCreation() throws Exception {
		FileConnection conn1 = new FileConnection(TEST_END_POINT_PATH, "1",
				false);
		FileConnection conn2 = new FileConnection(TEST_END_POINT_PATH, "1",
				true);

		assertDataPassesFromTo(conn1.getOutputStream(), conn2.getInputStream());
		assertDataPassesFromTo(conn2.getOutputStream(), conn1.getInputStream());

		assertConnectionCloses(conn1, conn2);
		assertConnectionCloses(conn2, conn1);
	}

	private void testEndPointConnection(ConnectionProvider connectionProvider,
			EndPoint endPoint) throws Exception, IOException {
		Connection client = connectionProvider.createNew();
		Connection server = endPoint.waitForNewConnection();

		assertDataPassesFromTo(client.getOutputStream(),
				server.getInputStream());
		assertDataPassesFromTo(server.getOutputStream(),
				client.getInputStream());

		assertConnectionCloses(client, server);
		assertConnectionCloses(server, client);

		endPoint.close();
	}

	@Test
	public void testServerSocketEndPointConnection() throws Exception {
		ConnectionProvider connectionProvider = new SocketConnectionProvider(
				TEST_END_POINT_PORT);
		EndPoint endPoint = new ServerSocketEndPoint(TEST_END_POINT_PORT);

		testEndPointConnection(connectionProvider, endPoint);
	}

	@Test
	public void testFileEndPointConnection() throws Exception {
		ConnectionProvider connectionProvider = new FileConnectionProvider(
				TEST_END_POINT_PATH);
		EndPoint endPoint = new FileEndPoint(TEST_END_POINT_PATH);

		testEndPointConnection(connectionProvider, endPoint);
	}

	private void testConnectToServer(EndPoint endPoint) throws Exception,
			InterruptedException, IOException {
		CollectingConnectionHandler collectingConnectionHandler = new CollectingConnectionHandler();
		Server server = serverFactory.createAndStartServer(endPoint,
				collectingConnectionHandler);

		Connection clientConnection = endPoint.getConnectionProvider()
				.createNew();

		Connection serverConnection = collectingConnectionHandler
				.waitForConnection();

		assertServerReceivedConnection(server);
		assertDataPassesFromTo(clientConnection.getOutputStream(),
				serverConnection.getInputStream());
		assertDataPassesFromTo(serverConnection.getOutputStream(),
				clientConnection.getInputStream());

		server.stop();
	}

	@Test
	public void testConnectToServerSocketEndPointServer() throws Exception {
		testConnectToServer(new ServerSocketEndPoint(TEST_END_POINT_PORT));
	}

	@Test
	public void testConnectToFileEndPointServer() throws Exception {
		testConnectToServer(new FileEndPoint(TEST_END_POINT_PATH));
	}

	@Test
	public void testNoConnectionsServerSocketEndpoint() throws Exception {
		int port = 9912;

		Server server = serverFactory.createAndStartServer(
				new ServerSocketEndPoint(port), null);
		assertTrue(server.getConnectionsCount() == 0);
		server.stop();
	}

	@Test
	public void testServerSocketProxyToFile() throws Exception {
		EndPoint toEndPoint = new FileEndPoint(TEST_END_POINT_PATH);
		EndPoint fromEndPoint = new ServerSocketEndPoint(9913);

		assertProxies(fromEndPoint, toEndPoint);
	}

	@Test
	public void testFileProxyToServerSocket() throws Exception {
		EndPoint toEndPoint = new ServerSocketEndPoint(TEST_END_POINT_PORT);
		EndPoint fromEndPoint = new FileEndPoint(TEST_END_POINT_PATH);

		assertProxies(fromEndPoint, toEndPoint);
	}

	@Test
	public void testServerSocketProxyToServerSocket() throws Exception {
		EndPoint toEndPoint = new ServerSocketEndPoint(9912);
		EndPoint fromEndPoint = new ServerSocketEndPoint(9913);

		assertProxies(fromEndPoint, toEndPoint);
	}

	@Test
	public void testServerSocketProxyToFileProxyToServerSocket()
			throws Exception {
		int port = 9912;
		int proxyPort = 9913;

		CollectingConnectionHandler collectingConnectionHandler = new CollectingConnectionHandler();
		Server unproxiedServer = serverFactory.createAndStartServer(
				new ServerSocketEndPoint(port), collectingConnectionHandler);

		Server proxyServer = createServerSocketProxyToServerSocketThroughFiles(
				unproxiedServer, proxyPort);

		assertProxies(unproxiedServer, proxyServer,
				connectToServer(proxyServer),
				collectingConnectionHandler.waitForConnection());
	}

	private static void assertConnectionCloses(Connection in, Connection out)
			throws Exception {
		in.shutdownOutput();
		assertTrue(out.getInputStream().read() == -1);
	}

	private void assertProxies(EndPoint fromEndPoint, EndPoint toEndPoint)
			throws InterruptedException, IOException, Exception {
		CollectingConnectionHandler collectingConnectionHandler = new CollectingConnectionHandler();
		Server unproxiedServer = serverFactory.createAndStartServer(toEndPoint,
				collectingConnectionHandler);

		ProxyConnectionHandler proxyConnectionHandler = new ProxyConnectionHandler();
		proxyConnectionHandler.setConnectionsProvider(logger
				.proxy(unproxiedServer.getEndPoint().getConnectionProvider()));
		MultithreadedOperationsRunner multithreadedOperationsRunner = logger
				.proxy(new MultithreadedOperationsRunner());
		proxyConnectionHandler
				.setOperationsRunner(multithreadedOperationsRunner);
		Server proxyServer = serverFactory.createAndStartServer(fromEndPoint,
				proxyConnectionHandler, multithreadedOperationsRunner);

		assertProxies(unproxiedServer, proxyServer,
				connectToServer(proxyServer),
				collectingConnectionHandler.waitForConnection());
	}

	private void assertProxies(Server unproxiedServer, Server proxyServer,
			Connection clientConnection, Connection serverConnection)
			throws InterruptedException, IOException, Exception {
		assertServerReceivedConnection(unproxiedServer);
		assertDataPassesFromTo(clientConnection.getOutputStream(),
				serverConnection.getInputStream());
		assertDataPassesFromTo(serverConnection.getOutputStream(),
				clientConnection.getInputStream());

		unproxiedServer.stop();
		proxyServer.stop();
	}

	private void assertDataPassesFromTo(OutputStream outputStream,
			InputStream inputStream) throws IOException {
		byte[] sentData = { 0, 1, 2, 3, 4 };

		outputStream.write(sentData);
		outputStream.flush();

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ConnectionUtils.blockigStreamCopy(inputStream, byteArrayOutputStream,
				sentData.length);

		Assert.assertArrayEquals(sentData, byteArrayOutputStream.toByteArray());

		String expected = "<head>ciao! com'è?</head>";
		PrintWriter printWriter = new PrintWriter(outputStream);
		printWriter.println(expected);
		printWriter.flush();

		String line = new BufferedReader(new InputStreamReader(inputStream))
				.readLine();
		assertEquals(expected, line);

	}

	private static void assertServerReceivedConnection(Server server)
			throws InterruptedException {
		server.waitForAcceptedConnection();
		assertTrue(server.getConnectionsCount() > 0);
	}

	private Connection connectToServer(Server server) throws Exception {
		return logger.proxy(server.getEndPoint().getConnectionProvider()
				.createNew());
	}

	private Server createServerSocketProxyToServerSocketThroughFiles(
			final Server unproxiedServer, int port) throws IOException {
		serverFactory.createFileEndPointProxy(unproxiedServer.getEndPoint()
				.getConnectionProvider(), TEST_END_POINT_PATH);
		ProxyConnectionHandler proxyConnectionHandler = new ProxyConnectionHandler();
		proxyConnectionHandler.setConnectionsProvider(logger
				.proxy(new FileConnectionProvider(TEST_END_POINT_PATH)));
		MultithreadedOperationsRunner multithreadedOperationsRunner = logger
				.proxy(new MultithreadedOperationsRunner());
		proxyConnectionHandler
				.setOperationsRunner(multithreadedOperationsRunner);
		return serverFactory.createAndStartServer(
				new ServerSocketEndPoint(port), proxyConnectionHandler,
				multithreadedOperationsRunner);
	}

	@Test
	public void testEventsMustBeconsumedInOrder() throws Exception {
		EventReceiver eventReceiver = new FileEventReceiver(TEST_END_POINT_PATH);
		EventEmitter eventEmitter = new FileEventEmitter(TEST_END_POINT_PATH);

		eventEmitter.emit("ciao");
		eventEmitter.emit("abc");
		eventEmitter.emit("bcd");

		List<String> list = eventReceiver.waitFor(new String[] { "ciao", "abc",
				"bcd" });

		Assert.assertTrue(list.get(0).contains("ciao"));
		Assert.assertTrue(list.get(1).contains("abc"));
		Assert.assertTrue(list.get(2).contains("bcd"));
	}
}
