package it.filedriver.util;

import it.filedriver.operation.OperationsRunner;
import it.filedriver.operation.SequentialOperationsRunner;

import java.io.File;

public class FileUtils {
	public static final void empty(String path) {
		OperationsRunner runner = new SequentialOperationsRunner();
		File startingPath = new File(path);
		executeCleanOperations(runner, startingPath);
	}

	private static void executeCleanOperations(final OperationsRunner runner,
			File d) {
		File[] files = d.listFiles();
		if (files != null) {
			for (final File f : files) {
				if (f.isDirectory()) {
					runner.start(new Runnable() {

						@Override
						public void run() {
							executeCleanOperations(runner, f);
						}
					});
				} else {
					Logger.log("Deleting " + f.getAbsolutePath());
					f.delete();
				}
			}
		}
	}

}
