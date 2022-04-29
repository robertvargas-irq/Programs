package edu.nmsu.cs.webserver;

/**
 * Web worker: an object of this class executes in its own new thread to receive and respond to a
 * single HTTP request. After the constructor the object executes on its "run" method, and leaves
 * when it is done.
 *
 * One WebWorker object is only responsible for one client connection. This code uses Java threads
 * to parallelize the handling of clients: each WebWorker runs in its own thread. This means that
 * you can essentially just think about what is happening on one client at a time, ignoring the fact
 * that the entirety of the webserver execution might be handling other clients, too.
 *
 * This WebWorker class (i.e., an object of this class) is where all the client interaction is done.
 * The "run()" method is the beginning -- think of it as the "main()" for a client interaction. It
 * does three things in a row, invoking three methods in this class: it reads the incoming HTTP
 * request; it writes out an HTTP header to begin its response, and then it writes out some HTML
 * content for the response content. HTTP requests and responses are just lines of text (in a very
 * particular format).
 * 
 * @author Jon Cook, Ph.D.
 *
 **/

 /**
  * Modified the Web Worker to process GET requests, and return the proper
  * HTTP response code on fulfill. If file is not found, a 404 Not Found
  * error will return, and return the proper HTML message to communicate the
  * error as well.
  * 
  * @author Robert Vargas
  */

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Scanner;
import java.util.List;

public class WebWorker implements Runnable
{

	private Socket socket;

	/**
	 * Constructor: must have a valid open socket
	 **/
	public WebWorker(Socket s)
	{
		socket = s;
	}

	/**
	 * Worker thread starting point. Each worker handles just one HTTP request and then returns, which
	 * destroys the thread. This method assumes that whoever created the worker created it with a
	 * valid open socket object.
	 * @precondition Valid open socket object
	 * @postcondition Web worker handled the incoming connection and returned the proper HTTP header
	 * and HTML page.
	 **/
	public void run() {
		System.err.println("Handling connection...");
		try {
			// create IO stream
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();

			// parse HTTP request and map data to HTTP header and HTML content response
			Object[] responseData  = readHTTPRequest(is, os);
			String responseCode    = (String)responseData[0];
			String responseType    = (String)responseData[1];
			byte[] responseContent = (byte[])responseData[2];

			// write header and content then close the stream
			writeHTTPHeader(os, responseType, responseCode);
			writeContent(os, responseContent);
			os.flush();
			socket.close();
		}
		catch (Exception e) {
			System.err.println("Output error: " + e);
		}
		System.err.println("Done handling connection.");
		return;
	}

	/**
	 * Locate and return requested file along with the proper HTTP Response Code;
	 * if no file was located, 404 and return the appropriate error content.
	 * @param request Given GET request path to a certain file.
	 * @param os Valid output stream.
	 * @return Object array with a (String) HTTP Response Code, (String) HTML Content Type, and (byte[]) Content bytes from the requested file.
	 */
	private Object[] processHTTPGetRequest(String request, OutputStream os) {
		// process GET request
		File requested;
		String parsedName;
		try {
			// parse requested file; index.html if request is empty
			parsedName = request.split(" ")[1].substring(1);
			if (parsedName.equals(""))
				parsedName = "index.html";

			// prevent accessing of non-supported files
			List<String> permittedTypes = List.of(
				"html", "gif", "jpeg", "png", "ico"
			);
			String fileType = parsedName.substring((parsedName.lastIndexOf(".") + 1));
			if (!permittedTypes.contains(fileType)) {
				return new Object[]{
					"400 Bad Request",
					"text/html",
					("<html><head></head><body>The file you requested is not a valid HTML file. All requests must end in one of the following: "
					+ permittedTypes.toString() + "</body></html>").getBytes()
				}; // end return
			} // end if

			// define file and open
			requested = new File(parsedName);
			System.err.println("FILE: " + parsedName);

			// ensure file exists
			if (!requested.exists()) {

				// return error header
				System.err.println("UNABLE TO LOCATE REQUESTED FILE: " + requested.getName());
				return new Object[]{
					"404 Not Found",
					"text/html",
					("<html><head></head><body>Unable to locate the requested file: " + requested.getName() + "</body></html>").getBytes()
				};

			}

			// process the correct file type
			switch (fileType){
				case "html":
					// write file to content, and return 200 OK and content
					Scanner fileReader = new Scanner(requested);
					String responseContent = "";
					while(fileReader.hasNextLine())
						responseContent += fileReader.nextLine();
					fileReader.close();

					// replace custom HTML tags
					Date d = new Date();
					DateFormat df = DateFormat.getDateTimeInstance();
					df.setTimeZone(TimeZone.getTimeZone("MST"));
					responseContent = responseContent
						.replace("<cs371date>", df.format(d).toString())
						.replace("<cs371server>", "Robert's Server");

					return new Object[]{"200 OK", "text" + fileType, responseContent.getBytes()};
				default: // currently only image files
					// map file to BufferedImage
					BufferedImage bi = ImageIO.read(requested);
					
					// convert to byte array
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ImageIO.write(bi, fileType, baos); // write from BufferedImage of fileType to BAOS
					byte[] imageBytes = baos.toByteArray();

					return new Object[]{"200 OK", "image" + fileType, imageBytes};

			}

		}
		// handle IOException via Internal Server Error
		catch (IOException e) {
			return new Object[]{
				"500 Internal Server Error",
				"text/html",
				("<html><head></head>\n<body>Internal server error, full error details provided below:\n" + 
				e.toString() + "\n" +
				e.getStackTrace() +
				".</body></html>").getBytes()
			};
		}
	}

	/**
	 * Read the HTTP request header and process a given GET request within.
	 * @return Object array with a (String) HTTP Response Code, (String) HTML Content Type, and (byte[]) Content bytes from the requested file.
	 **/
	private Object[] readHTTPRequest(InputStream is, OutputStream os) {
		String line;
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		while (true) {
			try {
				// instantiate reader and block until ready
				while (!r.ready())
					Thread.sleep(1);
				
				// read each line from the HTTP request and process each request accordingly
				line = r.readLine();
				System.err.println("Request line: (" + line + ")");
				if (line.length() == 0)
					break;
				if (line.contains("GET"))
					return processHTTPGetRequest(line, os);
			}
			catch (Exception e) {
				System.err.println("Request error: " + e);
				return new Object[]{
					"400 Bad Request",
					"text/html",
					("<html><head></head><body>Unable to process incoming HTTP request, full error details provided below:\n" + 
					e.toString() + "\n" +
					e.getStackTrace() +
					".</body></html>").getBytes()
				};
			}
		}

		// by default, return 200 OK with the index.html as the default entry-point
		return new Object[]{"200 OK", "text/html", new File("index.html").toString().getBytes()};
	}

	/**
	 * Write the HTTP header lines to the client network connection.
	 * @param os The OutputStream object to write to
	 * @param contentType The string MIME content type (e.g. "text/html")
	 * @param HTTPResponseCode Final HTTP status code after processing the
	 * full incoming HTTP request.
	 **/
	private void writeHTTPHeader(OutputStream os, String contentType, String HTTPResponseCode) throws Exception
	{
		Date d = new Date();
		DateFormat df = DateFormat.getDateTimeInstance();
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		os.write(("HTTP/1.1 " + HTTPResponseCode + "\n").getBytes());
		os.write("Date: ".getBytes());
		os.write((df.format(d)).getBytes());
		os.write("\n".getBytes());
		os.write("Server: Robert's very own server\n".getBytes());
		os.write(("Last-Modified: " + df.toString() + "\n").getBytes());
		os.write("Connection: close\n".getBytes());
		os.write("Content-Type: ".getBytes());
		os.write(contentType.getBytes());
		os.write("\n\n".getBytes()); // HTTP header ends with 2 newlines
		return;
	}

	/**
	 * Write the data content to the client network connection. This MUST be done after the HTTP
	 * header has been written out.
	 * @param os Valid OutputStream object to write to.
	 * @param content HTML or image to write out to the server, fetched earlier
	 * from readHTTPRequest.
	 **/
	private void writeContent(OutputStream os, byte[] content) throws Exception
	{	
		// write to output
		os.write(content);
	}

} // end class
