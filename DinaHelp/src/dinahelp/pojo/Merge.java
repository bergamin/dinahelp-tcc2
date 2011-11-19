package dinahelp.pojo;

import java.io.File;
import javax.media.*;
import javax.media.format.*;
import javax.media.protocol.*;
import javax.media.protocol.DataSource;
import javax.media.datasink.*;
import java.util.Vector;

/**
 * @author Guilherme Taffarel Bergamin
 * @author Akanbi Strossi de Jesus
 * @author Felipe Bochehin
 */
public class Merge implements ControllerListener, DataSinkListener {

	Vector sourcesURLs = new Vector(1);
	Processor[] processors = null;
	String outputFile = null;
	String videoEncoding = "JPEG";
	String audioEncoding = "LINEAR";
	String outputType = FileTypeDescriptor.QUICKTIME;
	DataSource[] dataOutputs = null;
	DataSource merger = null;
	DataSource outputDataSource;
	Processor outputProcessor;
	ProcessorModel outputPM;
	DataSink outputDataSink;
	MediaLocator outputLocator;
	VideoFormat videoFormat = null;
	AudioFormat audioFormat = null;
	/** This parameter is used to stop the
	 *  merging loop in the doMerge()
	 *  method.
	 */
	boolean done = false;

	public Merge(String[] args) {
		parseArgs(args);
		if (sourcesURLs.size() < 2) {
//			System.err.println("Need at least two source URLs");
			showUsage();
		} else {
			doMerge();
		}
	}

	private void doMerge() {

		int i = 0;
		processors = new Processor[sourcesURLs.size()];
		dataOutputs = new DataSource[sourcesURLs.size()];

		for (i = 0; i < sourcesURLs.size(); i++) {
			String source = (String) sourcesURLs.elementAt(i);
			MediaLocator ml = new MediaLocator(source);
			ProcessorModel pm = new MyPM(ml);
			try {
				processors[i] = Manager.createRealizedProcessor(pm);
				dataOutputs[i] = processors[i].getDataOutput();
				processors[i].start();
			} catch (Exception e) {
				System.err.println("Falha ao criar processador: " + e);
				System.exit(-1);
			}
		}

		// Merge the data sources from the individual processors
		try {
			merger = Manager.createMergingDataSource(dataOutputs);
			merger.connect();
			merger.start();
		} catch (Exception ex) {
//			System.err.println("Failed to merge data sources: " + ex);
			System.exit(-1);
		}
		if (merger == null) {
//			System.err.println("Failed to merge data sources");
			System.exit(-1);
		}
		/*
		try {
		Player p = Manager.createPlayer(merger);
		new com.sun.media.ui.PlayerWindow(p);
		} catch (Exception e) {
		//		System.err.println("Failed to create player " + e);
		}
		 */

		// Create the output processor
		ProcessorModel outputPM = new MyPMOut(merger);

		try {
			outputProcessor = Manager.createRealizedProcessor(outputPM);
			outputDataSource = outputProcessor.getDataOutput();
		} catch (Exception exc) {
//			System.err.println("Failed to create output processor: " + exc);
			System.exit(-1);
		}

		try {
			outputLocator = new MediaLocator(outputFile);
			outputDataSink = Manager.createDataSink(outputDataSource,
					outputLocator);
			outputDataSink.open();
		} catch (Exception exce) {
//			System.err.println("Failed to create output DataSink: " + exce);
			System.exit(-1);
		}

		outputProcessor.addControllerListener(this);
		outputDataSink.addDataSinkListener(this);
//		System.err.println("Merging...");
		try {
			outputDataSink.start();
			outputProcessor.start();
		} catch (Exception excep) {
//			System.err.println("Failed to start file writing: " + excep);
			System.exit(-1);
		}
		int count = 0;

		while (!done) {
			try {
				Thread.currentThread().sleep(100);
			} catch (InterruptedException ie) {
			}

			/** Just check if the merging has been cancelled.
			 *  The first try is in case of a NullPointerException,
			 *  in case the progress bar has not been
			 *  initiated.
			 */
			try {
//                if (myProgressBar.cancelled) {
//                    try {
//                        done = true;
//                        outputDataSink.stop();
//                        outputProcessor.stop();
//                        System.out.println("Stopped merge datasink");
//                    } catch (Exception e) {
//                        System.out.println("Couldn't stop merge datasink");
//                        System.out.println(e);
//                    }
//                }
			} catch (Exception e) {
			}

			if (outputProcessor != null
					&& (int) (outputProcessor.getMediaTime().getSeconds()) > count) {
//				System.err.print(".");
				count = (int) (outputProcessor.getMediaTime().getSeconds());
			}

		}

		if (outputDataSink != null) {
			outputDataSink.close();
		}
		synchronized (this) {
			if (outputProcessor != null) {
				outputProcessor.close();
			}
		}
//		System.err.println("Done!");
	}

	public void controllerUpdate(ControllerEvent ce) {
		if (ce instanceof EndOfMediaEvent) {
			synchronized (this) {
				outputProcessor.close();
				outputProcessor = null;
			}
		}
	}

	public void dataSinkUpdate(DataSinkEvent dse) {
		if (dse instanceof EndOfStreamEvent) {
			done = true;
		} else if (dse instanceof DataSinkErrorEvent) {
			done = true;
		}
	}

	class MyPM extends ProcessorModel {

		MediaLocator inputLocator;

		public MyPM(MediaLocator inputLocator) {
			this.inputLocator = inputLocator;
		}

		public ContentDescriptor getContentDescriptor() {
			return new ContentDescriptor(ContentDescriptor.RAW);
		}

		public DataSource getInputDataSource() {
			return null;
		}

		public MediaLocator getInputLocator() {
			return inputLocator;
		}

		public Format getOutputTrackFormat(int index) {
			return null;
		}

		public int getTrackCount(int n) {
			return n;
		}

		public boolean isFormatAcceptable(int index, Format format) {
			if (videoFormat == null) {
				videoFormat = new VideoFormat(videoEncoding);
			}
			if (audioFormat == null) {
				audioFormat = new AudioFormat(audioEncoding);
			}
			if (format.matches(videoFormat) || format.matches(audioFormat)) {
				return true;
			} else {
				return false;
			}
		}
	}

	class MyPMOut extends ProcessorModel {

		DataSource inputDataSource;

		public MyPMOut(DataSource inputDataSource) {
			this.inputDataSource = inputDataSource;
		}

		public ContentDescriptor getContentDescriptor() {
			return new FileTypeDescriptor(outputType);
		}

		public DataSource getInputDataSource() {
			return inputDataSource;
		}

		public MediaLocator getInputLocator() {
			return null;
		}

		public Format getOutputTrackFormat(int index) {
			return null;
		}

		public int getTrackCount(int n) {
			return n;
		}

		public boolean isFormatAcceptable(int index, Format format) {
			if (videoFormat == null) {
				videoFormat = new VideoFormat(videoEncoding);
			}
			if (audioFormat == null) {
				audioFormat = new AudioFormat(audioEncoding);
			}
			if (format.matches(videoFormat) || format.matches(audioFormat)) {
				return true;
			} else {
				return false;
			}
		}
	}

	private void showUsage() {
//		System.err.println("Usage: Merge <url1> <url2> [<url3> ... ] [-o <out URL>] [-v <video_encoding>] [-a <audio_encoding>] [-t <content_type>]");
	}

	private void parseArgs(String[] args) {
		int i = 0;
		while (i < args.length) {
			if (args[i].equals("-h")) {
				showUsage();
			} else if (args[i].equals("-o")) {
				i++;
				outputFile = args[i];
			} else if (args[i].equals("-t")) {
				i++;
				outputType = args[i];
			} else if (args[i].equals("-v")) {
				i++;
				videoEncoding = args[i];
			} else if (args[i].equals("-a")) {
				i++;
				audioEncoding = args[i];
			} else {
				sourcesURLs.addElement(args[i]);
			}
			i++;
		}

		if (outputFile == null) {
			outputFile = "file:" + System.getProperty("user.dir")
					+ File.separator + "merged.mov";
		}
	}

	public static void main(String[] args) {
		new Merge(args);
		System.exit(0);
	}
}