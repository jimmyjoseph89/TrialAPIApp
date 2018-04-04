package Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.edm.Edm;
import org.apache.olingo.odata2.api.edm.EdmEntityContainer;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderReadProperties;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.api.exception.ODataException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@Path("/hello")
public class Hello {
	public static final String HTTP_METHOD_PUT = "PUT";
	public static final String HTTP_METHOD_POST = "POST";
	public static final String HTTP_METHOD_GET = "GET";
	private static final String HTTP_METHOD_DELETE = "DELETE";
	public static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
	public static final String HTTP_HEADER_ACCEPT = "Accept";

	public static final String APPLICATION_JSON = "application/json";
	public static final String APPLICATION_XML = "application/xml";
	public static final String APPLICATION_ATOM_XML = "application/atom+xml";
	public static final String APPLICATION_FORM = "application/x-www-form-urlencoded";
	public static final String METADATA = "$metadata";
	public static final String serviceUrl = "http://services.odata.org/V2/Northwind/Northwind.svc";
	public static final boolean PRINT_RAW_CONTENT = true;
	public static final String usedFormat = APPLICATION_JSON;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String sayHello() throws IOException, ODataException {
		Edm edm = readEdm(serviceUrl);
		// print("Read default EntityContainer: " +
		// edm.getDefaultEntityContainer().getName());
		//Product product = new Product();
		ODataFeed feed = readFeed(edm, serviceUrl, usedFormat, "Products");
		/*
		 * List<ODataEntry> products = feed.getEntries(); ODataEntry createdEntry =
		 * products.get(0); Map<String, Object> properties =
		 * createdEntry.getProperties(); Set<Entry<String, Object>> entries =
		 * properties.entrySet(); for (Entry<String, Object> entry : entries) {
		 * if(entry.getKey() == "ProductID") product.setProductID((int)
		 * entry.getValue()); else if(entry.getKey() == "CategoryID")
		 * product.setCategoryID((int) entry.getValue()); else if(entry.getKey() ==
		 * "Discontinued") product.setDiscontinued((boolean) entry.getValue()); else
		 * if(entry.getKey() == "ProductName") product.setProductName((String)
		 * entry.getValue()); else if(entry.getKey() == "QuantityPerUnit")
		 * product.setQuantityPerUnit((String) entry.getValue()); else if(entry.getKey()
		 * == "ReorderLevel") product.setReorderLevel((int) entry.getValue()); else
		 * if(entry.getKey() == "SupplierID") product.setSupplierID((int)
		 * entry.getValue()); else if(entry.getKey() == "UnitPrice")
		 * product.setUnitPrice((String) entry.getValue()); else if(entry.getKey() ==
		 * "UnitsInStock") product.setUnitsInStock((int) entry.getValue()); else
		 * if(entry.getKey() == "UnitsOnOrder") product.setUnitsOnOrder((int)
		 * entry.getValue()); System.out.println(entry.getKey());
		 * System.out.println(entry.getValue()); }
		 */

		print("Read: " + feed.getEntries().size() + " entries: ");
		/*
		 * for (ODataEntry entry : feed.getEntries()) { print("##########");
		 * print("Entry:\n" + prettyPrint(entry)); print("##########"); }
		 */
		Frame[] f = new Frame[10];
		f[0] = new Frame();
		f[1] = new Frame();
		ArrayList<Frame> frames = new ArrayList<>();

		f[0].setText(String.valueOf(feed.getEntries().size()));
		f[0].setIcon("");

		frames.add(f[0]);

		f[1].setText("Hello");
		f[1].setIcon("Nice");

		frames.add(f[1]);

		Gson gson = new Gson();
		JsonElement element = gson.toJsonTree(frames);
		JsonArray jsonArray = element.getAsJsonArray();

		JsonObject object = new JsonObject();
		object.add("frames", jsonArray);
		Gson gsonBuilder = new GsonBuilder().create();
		String jsonFromPojo = gsonBuilder.toJson(object);
		/*
		 * 
		 * 
		 * String jsonFromPojo = gsonBuilder.toJson(object);
		 */

		System.out.println(jsonFromPojo);
		// String resource = "<h1>Hi Jimmy, This is hello from Html</h1>";
		return jsonFromPojo;
		// @Produces(MediaType.TEXT_HTML)
	}

	@Produces(MediaType.APPLICATION_JSON)
	public String sayJSON() {
		String resource = null;
		return resource;
	}

	@Produces(MediaType.TEXT_XML)
	public String sayXML() {
		String resource = "<?xml version='1.0?>" + "<hello>Hi Jimmy! This is hello from XML</hello>";
		return resource;
	}

	public Edm readEdm(String serviceUrl) throws IOException, ODataException {
		InputStream content = execute(serviceUrl + "/" + METADATA, APPLICATION_XML, HTTP_METHOD_GET);
		return EntityProvider.readMetadata(content, false);
	}

	private static void print(String content) {
		System.out.println(content);
	}

	private InputStream execute(String relativeUri, String contentType, String httpMethod) throws IOException {
		HttpURLConnection connection = initializeConnection(relativeUri, contentType, httpMethod);

		connection.connect();
		checkStatus(connection);

		InputStream content = connection.getInputStream();
		content = logRawContent(httpMethod + " request:\n  ", content, "\n");
		return content;
	}

	private HttpURLConnection initializeConnection(String absolutUri, String contentType, String httpMethod)
			throws MalformedURLException, IOException {
		URL url = new URL(absolutUri);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		connection.setRequestMethod(httpMethod);
		connection.setRequestProperty(HTTP_HEADER_ACCEPT, contentType);
		if (HTTP_METHOD_POST.equals(httpMethod) || HTTP_METHOD_PUT.equals(httpMethod)) {
			connection.setDoOutput(true);
			connection.setRequestProperty(HTTP_HEADER_CONTENT_TYPE, contentType);
		}

		return connection;
	}

	private HttpStatusCodes checkStatus(HttpURLConnection connection) throws IOException {
		HttpStatusCodes httpStatusCode = HttpStatusCodes.fromStatusCode(connection.getResponseCode());
		if (400 <= httpStatusCode.getStatusCode() && httpStatusCode.getStatusCode() <= 599) {
			throw new RuntimeException("Http Connection failed with status " + httpStatusCode.getStatusCode() + " "
					+ httpStatusCode.toString());
		}
		return httpStatusCode;
	}

	private InputStream logRawContent(String prefix, InputStream content, String postfix) throws IOException {
		if (PRINT_RAW_CONTENT) {
			byte[] buffer = streamToArray(content);
			content.close();

			print(prefix + new String(buffer) + postfix);

			return new ByteArrayInputStream(buffer);
		}
		return content;
	}

	private byte[] streamToArray(InputStream stream) throws IOException {
		byte[] result = new byte[0];
		byte[] tmp = new byte[8192];
		int readCount = stream.read(tmp);
		while (readCount >= 0) {
			byte[] innerTmp = new byte[result.length + readCount];
			System.arraycopy(result, 0, innerTmp, 0, result.length);
			System.arraycopy(tmp, 0, innerTmp, result.length, readCount);
			result = innerTmp;
			readCount = stream.read(tmp);
		}
		stream.close();
		return result;
	}

	public ODataFeed readFeed(Edm edm, String serviceUri, String contentType, String entitySetName)
			throws IOException, ODataException {
		EdmEntityContainer entityContainer = edm.getDefaultEntityContainer();
		String absolutUri = createUri(serviceUri, entitySetName, null);

		InputStream content = (InputStream) connect(absolutUri, contentType, HTTP_METHOD_GET).getContent();
		ODataFeed o = EntityProvider.readFeed(contentType, entityContainer.getEntitySet(entitySetName), content,
				EntityProviderReadProperties.init().build());

		return o;
	}

	private String createUri(String serviceUri, String entitySetName, String id) {
		final StringBuilder absolutUri = new StringBuilder(serviceUri).append("/").append(entitySetName);
		if (id != null) {
			absolutUri.append("(").append(id).append(")");
		}
		return absolutUri.toString();
	}

	private HttpURLConnection connect(String relativeUri, String contentType, String httpMethod) throws IOException {
		HttpURLConnection connection = initializeConnection(relativeUri, contentType, httpMethod);

		connection.connect();
		checkStatus(connection);

		return connection;
	}

	private static String prettyPrint(ODataEntry createdEntry) {
		return prettyPrint(createdEntry.getProperties(), 0);
	}

	private static String prettyPrint(Map<String, Object> properties, int level) {
		StringBuilder b = new StringBuilder();
		Set<Entry<String, Object>> entries = properties.entrySet();

		for (Entry<String, Object> entry : entries) {
			intend(b, level);
			b.append(entry.getKey()).append(": ");
			Object value = entry.getValue();
			if (value instanceof Map) {
				value = prettyPrint((Map<String, Object>) value, level + 1);
				b.append("\n");
			} else if (value instanceof Calendar) {
				Calendar cal = (Calendar) value;
				value = SimpleDateFormat.getInstance().format(cal.getTime());
			}
			b.append(value).append("\n");
		}
		// remove last line break
		b.deleteCharAt(b.length() - 1);
		return b.toString();
	}

	private static void intend(StringBuilder builder, int intendLevel) {
		for (int i = 0; i < intendLevel; i++) {
			builder.append("  ");
		}
	}

}
