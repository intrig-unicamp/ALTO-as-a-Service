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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Iterator;

import com.google.common.collect.Lists;

public class Main {
    private static final String SERVER_ROOT_URI = "http://192.168.122.1:7474/db/data/";
    private static final String SERVER_AS_TOPOLOGY = "http://192.168.56.102:7474/db/data/";

    //private static final String PATH_ALTO_AS = "InputData/ALTO/AS/20141208/";
    private static final String PATH_ALTO_AS = "InputData/ALTO/AS/20141208/TEST/";
    //private static final String OUTPUT_FILE = "log/";
    private static final String OUTPUT_FILE = "log/TEST/";
    private static final String NETWORK_MAP_NAME = "my-default-network-map";
    private static final String IPv4_TYPE = "ipv4";
    private static final String IPv6_TYPE = "ipv6";
    private static JsonNode jsonResult = null;

    private static long startTime;
    private static int CostCounter=100000;
    
    private static final String[] lstIXP20141208 = { "BA", "BEL", "CAS", "CE",
        "CGB", "CPV", "CXJ", "DF", "GYN", "LAJ", "LDA", "MAO", "MG", "MGF",
        "NAT", "PE", "PR", "RJ", "RS", "SC", "SCA", "SJC", "SJP", "SP",
        "VIX"};
    //private static final String[] lstIXP20141208 = { "TEST" };
    private static final String strPTTaux = "20121:26121:";

    public static void main(String[] args) throws Exception, IOException {

        try {
            System.setOut(new PrintStream(new FileOutputStream(OUTPUT_FILE
                    + "create_CostMap_AShopsPTT" + getData() + ".txt")));
            System.out.println("Start");
            
            //create_VersionTag();
            //create_AddressType();

            //create_PID("ASes_to_PIDs.csv.gz");

            //create_Prefixes("Matrix_AS_Prefix_IPv4.csv.gz");
            //create_Prefixes("Matrix_AS_Prefix_IPv6.csv.gz");

            //create_Relationships_VersionTag_PID("ASes_to_PIDs.csv.gz");

            //create_Relationships_Type_EndPoint("Matrix_AS_Prefix_IPv4.csv.gz",IPv4_TYPE);
            //create_Relationships_Type_EndPoint("Matrix_AS_Prefix_IPv6.csv.gz",IPv6_TYPE);

            //create_Relationships_PID_EndPoint("Matrix_AS_Prefix_IPv4.csv.gz");
            //create_Relationships_PID_EndPoint("Matrix_AS_Prefix_IPv6.csv.gz");

            //create_CostMap_AShops("ASes_to_PIDs.csv.gz");

            // SOLO PARA TEST
            //create_CostMap_AShops("ASes_to_PIDs.csv.gz");
            create_CostMap_AShopsPTT("ASes_to_PIDs.csv.gz"); 

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

    private static void create_CostMap_AShops(String strInputFile)
            throws Exception {
        System.out.println("start function: " + new Object() {
        }.getClass().getEnclosingMethod().getName() + " " + getData());
        startTime = System.currentTimeMillis();
        // //////////////////////////////////////////////////////////////////////////////
        
        System.out.println("SELECT execution time,CREATE execution time,TOTAL");

        long lonTime, lonTime2;
        long lonTimeTotal = 0, lonTimeTotal2 = 0;
        int intTotalAS_Top = 0, intTotalCost = 0;
        boolean isFirst = true, isFirst2 = true;
        List<String> lstAS = new ArrayList<String>();
        List<String> lstASaux = new ArrayList<String>();
        String strLine;
        GZIPInputStream fstream = new GZIPInputStream(new FileInputStream(
                PATH_ALTO_AS + strInputFile));
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        while ((strLine = br.readLine()) != null) {
            lstAS = Arrays.asList(strLine.split(","));
            lstASaux = Arrays.asList(strLine.split(","));
        }
        br.close();
        fstream.close();

        int intAShops = 0;
        List<JsonNode> lstPath = null;
        String strQuery;

        for (String strASsrc : lstAS) {
            for (String strASdst : lstASaux) {
                lonTime =0;
                if (!strASsrc.equals(strASdst)) {
                    strQuery = String
                            .format(new StringBuilder()
                                    .append("MATCH p=shortestPath((startAS:AS {number:'%s'})")
                                    .append("-[r:LINK*]-(endAS:AS {number:'%s'}))")
                                    .append(" WITH extract(n in nodes(p) | n.number) as path")
                                    .append(",reduce(s=0, rel IN r | s+1) as depth")
                                    .append(" RETURN path, depth").toString(),
                                    strASsrc, strASdst);

                    lonTime = REST_Query(strQuery, SERVER_AS_TOPOLOGY);
                    //System.out.println("Time (ms)," + lonTime);

                    if (!isFirst)
                        lonTimeTotal += lonTime;
                    else
                        isFirst = false;

                    intTotalAS_Top++;

                    Iterator<JsonNode> ite = jsonResult.path("results")
                            .findPath("data").elements();
                    if (ite.hasNext()) {
                        while (ite.hasNext()) {
                            JsonNode temp = ite.next().path("row");
                            Iterator<JsonNode> ite1 = temp.elements();
                            lstPath = Lists
                                    .newArrayList(ite1.next().elements());
                            intAShops = ite1.next().asInt();
                        }
                    } else
                        intAShops = -1;
                } else
                    intAShops = 0;

                lonTime2=0;
                
                if (intAShops > -1) {
                    strQuery = String
                            .format(new StringBuilder()
                                    .append("MATCH (startAS:PID {Name:'AS%s'}),")
                                    .append("(endAS:PID {Name:'AS%s'})")
                                    .append(" MERGE (startAS)-[c:Cost]->(endAS)")
                                    .append(" SET c.HopsNumber = %d") 
                                    .toString(), strASsrc, strASdst, intAShops);

                    lonTime2 = REST_Query(strQuery, SERVER_ROOT_URI);
                    //System.out.println("Time (ms)," + lonTime2);

                    if (!isFirst2)
                        lonTimeTotal2 += lonTime2;
                    else
                        isFirst2 = false;

                    intTotalCost++;
                } else
                    System.out.println("NOT CONNECTION --> AS source: "
                            + strASsrc + " AS Destination: " + strASdst);

                System.out.println(lonTime+","+lonTime2+","+(lonTime+lonTime2));
                
                if(intTotalCost==CostCounter)
                    break;
            }
            
            if(intTotalCost==CostCounter)
                break;
        }

        System.out.println("Total AS Topology Queries," + intTotalAS_Top);
        System.out.println("Total time (ms)," + lonTimeTotal
                + ",discarding first");
        float AVGTime = Float.valueOf(lonTimeTotal)
                / Float.valueOf((intTotalAS_Top - 1));
        System.out
                .println(String
                        .format("AVG time (ms),%.3f,discarding first => Total time/(Total AS Topology Queries - 1)",
                                AVGTime));

        System.out.println("Total COSTS relationships," + intTotalCost);
        System.out.println("Total time (ms)," + lonTimeTotal2
                + ",discarding first");
        AVGTime = Float.valueOf(lonTimeTotal2)
                / Float.valueOf((intTotalCost - 1));
        System.out
                .println(String
                        .format("AVG time (ms),%.3f,discarding first => Total time/(Total COSTS relationships - 1)",
                                AVGTime));

        // ///////////////////////////////////////////////////////////////////////////////////
        calculateTime(new Object() {
        }.getClass().getEnclosingMethod().getName());
    }
    
    private static void create_CostMap_AShopsPTT(String strInputFile)
            throws Exception {
        System.out.println("start function: " + new Object() {
        }.getClass().getEnclosingMethod().getName() + " " + getData());
        startTime = System.currentTimeMillis();
        // //////////////////////////////////////////////////////////////////////////////
        
        System.out.println("SELECT execution time,CREATE execution time,TOTAL");

        long lonTime, lonTime2;
        long lonTimeTotal = 0, lonTimeTotal2 = 0;
        int intTotalAS_Top = 0, intTotalCost = 0;
        boolean isFirst = true, isFirst2 = true;
        List<String> lstAS = new ArrayList<String>();
        List<String> lstASaux = new ArrayList<String>();
        String strLine;
        GZIPInputStream fstream = new GZIPInputStream(new FileInputStream(
                PATH_ALTO_AS + strInputFile));
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        while ((strLine = br.readLine()) != null) {
            lstAS = Arrays.asList(strLine.split(","));
            lstASaux = Arrays.asList(strLine.split(","));
        }
        br.close();
        fstream.close();

        int intAShops = 0;  
        List<JsonNode> lstPath = null;
        String strQuery;
        
        String strPTT = "";
        for (String strIXP : lstIXP20141208) {
            strPTT = strPTT + "'"+ strPTTaux+strIXP +"',";
        }               
        strPTT = strPTT.substring(0, strPTT.length() - 1);
                

        for (String strASsrc : lstAS) {
            for (String strASdst : lstASaux) {
                lonTime =0;
                if (!strASsrc.equals(strASdst)) {
                    strQuery = String
                            .format(new StringBuilder()
                                    .append("MATCH p=shortestPath((startAS:AS {number:'%s'})")
                                    .append("-[r:LINK*]-(endAS:AS {number:'%s'}))")
                                    .append(" WITH extract(n in nodes(p) | n.number) AS path")
                                    .append(",REDUCE(s=0, rel IN r | s+1) AS depth")
                                    .append(",REDUCE(NroPTT=0, ptt in nodes(p) | CASE WHEN ptt.number IN [%s] THEN NroPTT+1 ELSE NroPTT END) AS ptt")
                                    .append(" RETURN path, depth - (2*ptt) AS depth").toString(),
                                    strASsrc, strASdst, strPTT);

                    lonTime = REST_Query(strQuery, SERVER_AS_TOPOLOGY);                    

                    if (!isFirst)
                        lonTimeTotal += lonTime;
                    else
                        isFirst = false;

                    intTotalAS_Top++;

                    Iterator<JsonNode> ite = jsonResult.path("results")
                            .findPath("data").elements();
                    if (ite.hasNext()) {
                        while (ite.hasNext()) {
                            JsonNode temp = ite.next().path("row");
                            Iterator<JsonNode> ite1 = temp.elements();
                            lstPath = Lists
                                    .newArrayList(ite1.next().elements());
                            intAShops = ite1.next().asInt();
                        }
                    } else
                        intAShops = -1;
                } else
                    intAShops = 0;

                lonTime2=0;
                
                if (intAShops > -1) {
                    strQuery = String
                            .format(new StringBuilder()
                                    .append("MATCH (startAS:PID {Name:'AS%s'}),")
                                    .append("(endAS:PID {Name:'AS%s'})")
                                    .append(" MERGE (startAS)-[c:Cost]->(endAS)")
                                    .append(" SET c.HopsNumberPTT = %d") 
                                    .toString(), strASsrc, strASdst, intAShops);

                    lonTime2 = REST_Query(strQuery, SERVER_ROOT_URI);
                    //System.out.println("Time (ms)," + lonTime2);

                    if (!isFirst2)
                        lonTimeTotal2 += lonTime2;
                    else
                        isFirst2 = false;
                        
                    intTotalCost++;
                } else
                    System.out.println("NOT CONNECTION --> AS source: "
                            + strASsrc + " AS Destination: " + strASdst);

                System.out.println(lonTime+","+lonTime2+","+(lonTime+lonTime2));
                
                if(intTotalCost==CostCounter)
                    break;
            }
            
            if(intTotalCost==CostCounter)
                break;
                
        }

        System.out.println("Total AS Topology Queries," + intTotalAS_Top);
        System.out.println("Total time (ms)," + lonTimeTotal
                + ",discarding first");
        float AVGTime = Float.valueOf(lonTimeTotal)
                / Float.valueOf((intTotalAS_Top - 1));
        System.out
                .println(String
                        .format("AVG time (ms),%.3f,discarding first => Total time/(Total AS Topology Queries - 1)",
                                AVGTime));

        System.out.println("Total COSTS relationships," + intTotalCost);
        System.out.println("Total time (ms)," + lonTimeTotal2
                + ",discarding first");
        AVGTime = Float.valueOf(lonTimeTotal2)
                / Float.valueOf((intTotalCost - 1));
        System.out
                .println(String
                        .format("AVG time (ms),%.3f,discarding first => Total time/(Total COSTS relationships - 1)",
                                AVGTime));

        // ///////////////////////////////////////////////////////////////////////////////////
        calculateTime(new Object() {
        }.getClass().getEnclosingMethod().getName());
    }

    private static void create_Relationships_PID_EndPoint(String strInputFile)
            throws Exception {
        System.out.println("start function: " + new Object() {
        }.getClass().getEnclosingMethod().getName() + " " + getData());
        startTime = System.currentTimeMillis();
        // //////////////////////////////////////////////////////////////////////////////

        long lonTime;
        long lonTimeTotal = 0;
        int intTotalPIDs = 0;
        boolean isFirst = true;
        String strLine, strPID;
        String[] lstPrefix;
        GZIPInputStream fstream = null;
        BufferedReader br = null;

        fstream = new GZIPInputStream(new FileInputStream(PATH_ALTO_AS
                + strInputFile));
        br = new BufferedReader(new InputStreamReader(fstream));

        while ((strLine = br.readLine()) != null) {

            lstPrefix = strLine.split(",");
            strPID = lstPrefix[0];
            for (int i = 1; i < lstPrefix.length; i++) {

                String strQuery = String
                        .format("MATCH (p:PID {Name: 'AS%s'}), (a:EndPointAddress {Prefix: '%s'}) CREATE (p)-[:Has_EndPoint]->(a)",
                                strPID, lstPrefix[i]);

                lonTime = REST_Query(strQuery, SERVER_ROOT_URI);
                System.out.println("Time (ms)," + lonTime);

                if (!isFirst)
                    lonTimeTotal += lonTime;
                else
                    isFirst = false;

                intTotalPIDs++;

            }

        }

        br.close();
        fstream.close();

        System.out.println("Total Relationships," + intTotalPIDs);
        System.out.println("Total time (ms)," + lonTimeTotal
                + ",discarding first");
        float AVGTime = Float.valueOf(lonTimeTotal)
                / Float.valueOf((intTotalPIDs - 1));
        System.out
                .println(String
                        .format("AVG time (ms),%.3f,discarding first => Total time/(Total Relationships - 1)",
                                AVGTime));

        // ///////////////////////////////////////////////////////////////////////////////////
        calculateTime(new Object() {
        }.getClass().getEnclosingMethod().getName());

    }

    private static void create_Relationships_Type_EndPoint(String strInputFile,
            String strType) throws Exception {
        System.out.println("start function: " + new Object() {
        }.getClass().getEnclosingMethod().getName() + " " + getData());
        startTime = System.currentTimeMillis();
        // //////////////////////////////////////////////////////////////////////////////

        long lonTime;
        long lonTimeTotal = 0;
        int intTotalPIDs = 0;
        boolean isFirst = true;
        String strLine;
        GZIPInputStream fstream = null;
        BufferedReader br = null;

        fstream = new GZIPInputStream(new FileInputStream(PATH_ALTO_AS
                + strInputFile));
        br = new BufferedReader(new InputStreamReader(fstream));

        while ((strLine = br.readLine()) != null) {
            String[] lstPrefix = strLine.split(",");

            for (int i = 1; i < lstPrefix.length; i++) {

                String strQuery = String
                        .format("MATCH (at:AddressType {Type: '%s'}), (ep:EndPointAddress {Prefix: '%s'}) CREATE (at)-[:Type_EndPoint]->(ep)",
                                strType, lstPrefix[i]);

                lonTime = REST_Query(strQuery, SERVER_ROOT_URI);
                System.out.println("Time (ms)," + lonTime);

                if (!isFirst)
                    lonTimeTotal += lonTime;
                else
                    isFirst = false;

                intTotalPIDs++;

            }

        }

        br.close();
        fstream.close();

        System.out.println("Total Relationships," + intTotalPIDs);
        System.out.println("Total time (ms)," + lonTimeTotal
                + ",discarding first");
        float AVGTime = Float.valueOf(lonTimeTotal)
                / Float.valueOf((intTotalPIDs - 1));
        System.out
                .println(String
                        .format("AVG time (ms),%.3f,discarding first => Total time/(Total Relationships - 1)",
                                AVGTime));

        // //////////////////////////////////////////////////////////////////////////////
        calculateTime(new Object() {
        }.getClass().getEnclosingMethod().getName());
    }

    private static void create_Relationships_VersionTag_PID(String strInputFile)
            throws Exception {
        System.out.println("start function: " + new Object() {
        }.getClass().getEnclosingMethod().getName() + " " + getData());
        startTime = System.currentTimeMillis();
        // //////////////////////////////////////////////////////////////////////////////

        long lonTime;
        long lonTimeTotal = 0;
        int intTotalPIDs = 0;
        boolean isFirst = true;
        String strLine;
        List<String> lstPID = new ArrayList<String>();
        GZIPInputStream fstream = new GZIPInputStream(new FileInputStream(
                PATH_ALTO_AS + strInputFile));
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        while ((strLine = br.readLine()) != null)
            lstPID = Arrays.asList(strLine.split(","));

        for (String strPID : lstPID) {

            String strQuery = String
                    .format("MATCH (vt:VersionTag {ResourceID: '%s'}), (pid:PID {Name: 'AS%s'}) CREATE (vt)-[:Has_PID]->(pid)",
                            NETWORK_MAP_NAME, strPID);

            lonTime = REST_Query(strQuery, SERVER_ROOT_URI);
            System.out.println("Time (ms)," + lonTime);

            if (!isFirst)
                lonTimeTotal += lonTime;
            else
                isFirst = false;

            intTotalPIDs++;
        }

        br.close();
        fstream.close();

        System.out.println("Total Relationships," + intTotalPIDs);
        System.out.println("Total time (ms)," + lonTimeTotal
                + ",discarding first");
        float AVGTime = Float.valueOf(lonTimeTotal)
                / Float.valueOf((intTotalPIDs - 1));
        System.out
                .println(String
                        .format("AVG time (ms),%.3f,discarding first => Total time/(Total Rels - 1)",
                                AVGTime));

        // //////////////////////////////////////////////////////////////////////////////
        calculateTime(new Object() {
        }.getClass().getEnclosingMethod().getName());

    }

    private static void create_Prefixes(String strInputFile) throws Exception {
        System.out.println("start function: " + new Object() {
        }.getClass().getEnclosingMethod().getName() + " " + getData());
        startTime = System.currentTimeMillis();
        // //////////////////////////////////////////////////////////////////////////////

        long lonTime;
        long lonTimeTotal = 0;
        int intTotalPIDs = 0;
        boolean isFirst = true;
        String strLine;
        GZIPInputStream fstream = null;
        BufferedReader br = null;

        fstream = new GZIPInputStream(new FileInputStream(PATH_ALTO_AS
                + strInputFile));
        br = new BufferedReader(new InputStreamReader(fstream));

        while ((strLine = br.readLine()) != null) {
            String[] lstSubAS = strLine.split(",");

            for (int i = 1; i < lstSubAS.length; i++) {
                String strQuery = String.format(
                        "CREATE (:EndPointAddress { Prefix : '%s' })",
                        lstSubAS[i]);

                lonTime = REST_Query(strQuery, SERVER_ROOT_URI);
                System.out.println("Time (ms)," + lonTime);

                if (!isFirst)
                    lonTimeTotal += lonTime;
                else
                    isFirst = false;

                intTotalPIDs++;

            }

        }

        br.close();
        fstream.close();

        System.out.println("Total Prefixes," + intTotalPIDs);
        System.out.println("Total time (ms)," + lonTimeTotal
                + ",discarding first");
        float AVGTime = Float.valueOf(lonTimeTotal)
                / Float.valueOf((intTotalPIDs - 1));
        System.out
                .println(String
                        .format("AVG time (ms),%.3f,discarding first => Total time/(Total PIDs - 1)",
                                AVGTime));
        // //////////////////////////////////////////////////////////////////////////////
        calculateTime(new Object() {
        }.getClass().getEnclosingMethod().getName());
    }

    private static void create_PID(String strInputFile) throws Exception {
        System.out.println("start function: " + new Object() {
        }.getClass().getEnclosingMethod().getName() + " " + getData());
        startTime = System.currentTimeMillis();
        // //////////////////////////////////////////////////////////////////////////////

        long lonTime;
        long lonTimeTotal = 0;
        int intTotalPIDs = 0;
        boolean isFirst = true;
        List<String> lstAS = new ArrayList<String>();
        String strLine;
        GZIPInputStream fstream = new GZIPInputStream(new FileInputStream(
                PATH_ALTO_AS + strInputFile));
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        while ((strLine = br.readLine()) != null) {
            lstAS = Arrays.asList(strLine.split(","));
        }

        for (String strAS : lstAS) {
            String strQuery = String.format("CREATE (:PID { Name : 'AS%s' })",
                    strAS);

            lonTime = REST_Query(strQuery, SERVER_ROOT_URI);
            System.out.println("Time (ms)," + lonTime);

            if (!isFirst)
                lonTimeTotal += lonTime;
            else
                isFirst = false;

            intTotalPIDs++;
        }

        br.close();
        fstream.close();

        System.out.println("Total PIDs," + intTotalPIDs);
        System.out.println("Total time (ms)," + lonTimeTotal
                + ",discarding first");
        float AVGTime = Float.valueOf(lonTimeTotal)
                / Float.valueOf((intTotalPIDs - 1));
        System.out
                .println(String
                        .format("AVG time (ms),%.3f,discarding first => Total time/(Total PIDs - 1)",
                                AVGTime));
        // //////////////////////////////////////////////////////////////////////////////
        calculateTime(new Object() {
        }.getClass().getEnclosingMethod().getName());
    }

    private static void create_AddressType() throws Exception {
        System.out.println("start function: " + new Object() {
        }.getClass().getEnclosingMethod().getName() + " " + getData());
        startTime = System.currentTimeMillis();
        // //////////////////////////////////////////////////////////////////////////////

        long lonTime;
        String strQuery = String.format("CREATE (:AddressType { Type : '%s'})",
                IPv4_TYPE);
        lonTime = REST_Query(strQuery, SERVER_ROOT_URI);
        System.out.println("Time (ms)," + lonTime);

        strQuery = String.format("CREATE (:AddressType { Type : '%s'})",
                IPv6_TYPE);
        lonTime = REST_Query(strQuery, SERVER_ROOT_URI);
        System.out.println("Time (ms)," + lonTime);

        // //////////////////////////////////////////////////////////////////////////////
        calculateTime(new Object() {
        }.getClass().getEnclosingMethod().getName());
    }

    private static void create_VersionTag() throws Exception {
        System.out.println("start function: " + new Object() {
        }.getClass().getEnclosingMethod().getName() + " " + getData());
        startTime = System.currentTimeMillis();
        // //////////////////////////////////////////////////////////////////////////////

        long lonTime;
        String strQuery = String.format(
                "CREATE (:VersionTag { ResourceID : '%s', Tag : '%s' })",
                NETWORK_MAP_NAME, getTag(32));
        lonTime = REST_Query(strQuery, SERVER_ROOT_URI);
        System.out.println("Time (ms)," + lonTime);

        // //////////////////////////////////////////////////////////////////////////////
        calculateTime(new Object() {
        }.getClass().getEnclosingMethod().getName());
    }

    private static long REST_Query(String query, String strSERVER)
            throws Exception {

        // System.out.println("Query " + query);

        // START SNIPPET: queryAllNodes
        final String txUri = strSERVER + "transaction/commit";
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
                .println("Execution time," + msToString(endTime - startTime));
        System.out.println("Execution time (ms)," + (endTime - startTime));
        System.out.println("End function," + strFunction);
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

    public static String getTag(int length) {
        String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
}
