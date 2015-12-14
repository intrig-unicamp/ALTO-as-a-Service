package intrig.AaaS.Creating.ALTO.Information;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class Create_AS_Topology {
    private static final String SERVER_ROOT_URI = "http://192.168.122.1:7474/db/data/";
    private static final String AS_NODES_FILE = "InputData/AS_Topology/20141208/TotalAS.csv.gz";
    // private static final String AS_NODES_FILE =
    // "InputData/AS_Topology/20141208/TEST/TotalAS.csv.gz";
    private static final String AS_RELATIONSHIPS_FILE = "InputData/AS_Topology/20141208/MatrixRoundTrip.csv.gz";
    // private static final String AS_RELATIONSHIPS_FILE =
    // "InputData/AS_Topology/20141208/TEST/MatrixRoundTrip.csv.gz";
    private static final String OUTPUT_FILE = "log/";
    // private static final String OUTPUT_FILE = "log/TEST/";
    private static long startTime;
    public static JsonNode jsonResult;

    public static void main(String[] args) throws Exception, IOException {

        try {
            System.setOut(new PrintStream(new FileOutputStream(OUTPUT_FILE
                    + "create_AS_Nodes" + getData() + ".txt")));
            System.out.println("Start");

            // create_AS_Nodes();
            create_AS_Relationships();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            // System.out.println(e.getMessage());

            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            e.printStackTrace(printWriter);
            printWriter.flush();
            System.out.println(writer.toString());

            calculateTime(new Object() {
            }.getClass().getEnclosingMethod().getName());
        }

        System.out.println("End");
    }

    private static void create_AS_Relationships() throws Exception {
        System.out.println("start function: " + new Object() {
        }.getClass().getEnclosingMethod().getName() + " " + getData());
        startTime = System.currentTimeMillis();
        // //////////////////////////////////////////////////////////////////////////////

        String strLine;
        long lonTime;
        long lonTimeTotal = 0;
        int intTotalRel = 0;
        boolean isFirst = true;
        GZIPInputStream fstream = new GZIPInputStream(new FileInputStream(
                AS_RELATIONSHIPS_FILE));
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        while ((strLine = br.readLine()) != null) {
            String[] lstSubAS = strLine.split(",");
            // for (String strAS : lstSubAS) {
            if (lstSubAS.length > 1) {
                for (int i = 1; i < lstSubAS.length; i++) {
                    // MATCH (as_src:AS {number: '12956'}), (as_dst:AS {number:
                    // '12956'}) CREATE (as_src)-[:LINK]->(as_dst)

                    String strQuery = String
                            .format("MATCH (as_src:AS {number: '%s'}), (as_dst:AS {number: '%s'}) CREATE (as_src)-[:LINK]->(as_dst)",
                                    lstSubAS[0], lstSubAS[i]);
                    // System.out.println(strQuery);

                    lonTime = REST_Query(strQuery);
                    System.out.println("Time (ms)," + lonTime);

                    if (!isFirst)
                        lonTimeTotal += lonTime;
                    else
                        isFirst = false;

                    intTotalRel++;
                }
            }
        }

        System.out.println("Total Relationships," + intTotalRel);
        System.out.println("Total time (ms)," + lonTimeTotal
                + "//discarding first");
        float AVGTime = Float.valueOf(lonTimeTotal)
                / Float.valueOf((intTotalRel - 1));
        System.out
                .println(String
                        .format("AVG time (ms),%.3f,discarding first => Total time/(Total Relationships - 1)",
                                AVGTime));

        br.close();
        fstream.close();

        // //////////////////////////////////////////////////////////////////////////////
        calculateTime(new Object() {
        }.getClass().getEnclosingMethod().getName());

    }

    private static void create_AS_Nodes() throws Exception {
        System.out.println("start function: " + new Object() {
        }.getClass().getEnclosingMethod().getName() + " " + getData());
        startTime = System.currentTimeMillis();
        // //////////////////////////////////////////////////////////////////////////////

        String strLine;
        long lonTime;
        long lonTimeTotal = 0;
        int intTotalNodes = 0;
        boolean isFirst = true;
        GZIPInputStream fstream = new GZIPInputStream(new FileInputStream(
                AS_NODES_FILE));
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        while ((strLine = br.readLine()) != null) {
            String[] lstSubAS = strLine.split(",");
            for (String strAS : lstSubAS) {
                String strQuery = "CREATE (:AS { number : '" + strAS + "' })";
                // System.out.println(strQuery);

                lonTime = REST_Query(strQuery);
                System.out.println("Time (ms)," + lonTime);

                if (!isFirst)
                    lonTimeTotal += lonTime;
                else
                    isFirst = false;

                intTotalNodes++;
            }
        }

        br.close();
        fstream.close();

        System.out.println("Total Nodes," + intTotalNodes);
        System.out.println("Total time (ms)," + lonTimeTotal
                + "//discarding first");
        float AVGTime = Float.valueOf(lonTimeTotal)
                / Float.valueOf((intTotalNodes - 1));
        System.out
                .println(String
                        .format("AVG time (ms),%.3f,discarding first => Total time/(Total Nodes - 1)",
                                AVGTime));

        // //////////////////////////////////////////////////////////////////////////////
        calculateTime(new Object() {
        }.getClass().getEnclosingMethod().getName());
    }

    private static long REST_Query(String query) throws Exception {

        // System.out.println("Query " + query);

        // START SNIPPET: queryAllNodes
        final String txUri = SERVER_ROOT_URI + "transaction/commit";
        WebResource resource = Client.create().resource(txUri);

        String payload = "{\"statements\" : [ {\"statement\" : \"" + query
                + "\"} ]}";

        long startTimeNeo4j = System.currentTimeMillis();
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON).entity(payload)
                .post(ClientResponse.class);
        long endTimeNeo4j = System.currentTimeMillis();

        jsonResult = new ObjectMapper().readTree(response
                .getEntity(String.class));
        response.close();

        return endTimeNeo4j - startTimeNeo4j;
    }

    private static String getData() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Date now = new Date();
        return "[" + sdfDate.format(now).trim() + "] ";
    }

    private static void calculateTime(String strFunction) {
        long endTime = System.currentTimeMillis();
        System.out
                .println("Execution time: " + msToString(endTime - startTime));
        System.out.println("Execution time (ms): " + (endTime - startTime));
        System.out.println("End function: " + strFunction);
    }

    public static String msToString(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException(
                    "Duration must be greater than zero!");
        }

        // long days = TimeUnit.MILLISECONDS.toDays(millis);
        // millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        millis -= TimeUnit.SECONDS.toMillis(seconds);
        // 90000000
        return String.format("%02dh:%02dm:%02ds:%dms", hours, minutes, seconds,
                millis);
    }

}
