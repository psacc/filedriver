package it.filedriver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
	private static final String LINE_TERMINATOR = "\n";
	private Logger log = Logger.getLogger(Client.class.getName());
	private String endPointPath;
	private String outputfile = "";
	private String inputfile = "";

	public void connectToEndPoint(String endPointPath) {
		this.endPointPath = endPointPath;
		outputfile = endPointPath + "outputfile.txt";
		inputfile = endPointPath + "inputfile.txt";

	}

	public InputStream getOutput() {
		InputStream inputStream = null;

		try {
			inputStream = new BufferedInputStream(new FileInputStream(
					outputfile));
		} catch (FileNotFoundException e) {
			log.log(Level.WARNING, "output not connected, missing file: "
					+ outputfile, e);
		}
		return inputStream;
	}

	public void sendCommand(String string) {
		try {
			new OutputStreamWriter(new BufferedOutputStream(
					new FileOutputStream(inputfile))).write(string
					+ LINE_TERMINATOR);
		} catch (Exception e) {
			log.log(Level.WARNING, "input not connected, missing file: "
					+ inputfile, e);
		}

	}
}
