package intrig.AaaS.Util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class Counter {

    private static final String PATH_UTIL = "InputData/Util/20141208/";
    private static final String INPUT_PATH_DATASET = "IXP-PTT-BR/20141208/PTTMetro-LG-Dataset/";
    private static final String INPUT_MININET = "../Mininet-Code/Trafficv2/results/";
    //private static final String INPUT_MININET = "E:\\results\\";
    private static long startTime;
    private static final String[] lstIXP20141208 = { "BA", "BEL", "CAS", "CE",
            "CGB", "CPV", "CXJ", "DF", "GYN", "LAJ", "LDA", "MAO", "MG", "MGF",
            "NAT", "PE", "PR", "RJ", "RS", "SC", "SCA", "SJC", "SJP", "SP",
            "VIX" };
    private static final String PATH_ALTO_AS = "InputData/ALTO/AS/20141208/";
    private static final String ID_NetworkMap = "my-default-network-map";
    private static final String IPv4_TYPE = "ipv4";
    private static final String IPv6_TYPE = "ipv6";
    private static final String SERVER_ROOT_URI = "http://192.168.122.1:7474/db/data/";
    private static final String SERVER_AS_TOPOLOGY = "http://192.168.56.102:7474/db/data/";
    private static JsonNode jsonResult = null;

    // private static final String[] lstIXP20141208 = { "LDA" };

    private static long lonTime2, lonTime2Aux;
    private static long lonTime3, lonTime3Aux;

    public static void main(String[] args) throws Exception {

        try {
            System.setOut(new PrintStream(new FileOutputStream(
                    "log/Util/20141208/" + "iperf_test1_final" + getData() + ".txt")));
            System.out.println("Start");

            // total_ASes_prefixes_rawdata("total_ASes_prefixes_rawdata");

            // timeNetworkMap("timeNetworkMap");
            // timeCostMap("timeCostMap");
            test_mininet_iperf("iperf_test1_final", "iperf\\",
             "iperf_test1_partial.csv");
            //test_mininet_ping("ping_test1_final", "ping/", "ping_test1_partial.csv");

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

    private static void test_mininet_ping(String strFileName,
            String strInputFolder, String strInputFile) throws Exception {
        System.out.println("start function: " + new Object() {
        }.getClass().getEnclosingMethod().getName() + " " + getData());
        startTime = System.currentTimeMillis();
        // //////////////////////////////////////////////////////////////////////////////

        InputStream fstream = null;
        BufferedReader br = null;
        String strLine, strHosts;
        boolean sw;
        HashMap<String, List<String>> lstMatrix = new HashMap<String, List<String>>();
        int sent, received, sent_Aux, received_Aux;
        float rtt_min, rtt_avg, rtt_max, rtt_mdev, rtt_min_Aux, rtt_avg_Aux, rtt_max_Aux, rtt_mdev_Aux;
        List<String> lstHost = null;
        List<String> lstHostAux = new ArrayList<String>();

        if (Files.exists(Paths.get(INPUT_MININET + strInputFolder
                + strInputFile))) {

            fstream = new FileInputStream(INPUT_MININET + strInputFolder
                    + strInputFile);
            br = new BufferedReader(new InputStreamReader(fstream));

            sw = true;
            while ((strLine = br.readLine()) != null) {
                if (!sw) {
                    strHosts = strLine.split(",")[0] + ","
                            + strLine.split(",")[1];
                    sent = Integer.valueOf(strLine.split(",")[2]);
                    received = Integer.valueOf(strLine.split(",")[3]);
                    rtt_min = Float.valueOf(strLine.split(",")[4]);
                    rtt_avg = Float.valueOf(strLine.split(",")[5]);
                    rtt_max = Float.valueOf(strLine.split(",")[6]);
                    rtt_mdev = Float.valueOf(strLine.split(",")[7]);

                    if (!lstMatrix.containsKey(strHosts)) {
                        lstHost = new ArrayList<String>();
                        lstHost.add(String.valueOf(sent));
                        lstHost.add(String.valueOf(received));
                        lstHost.add(String.valueOf(rtt_min));
                        lstHost.add(String.valueOf(rtt_avg));
                        lstHost.add(String.valueOf(rtt_max));
                        lstHost.add(String.valueOf(rtt_mdev));
                        lstHost.add("1");
                        lstMatrix.put(strHosts, lstHost);
                        lstHostAux.add(strHosts);
                    } else {
                        lstHost = lstMatrix.get(strHosts);
                        sent_Aux = Integer.valueOf(lstHost.get(0));
                        received_Aux = Integer.valueOf(lstHost.get(1));
                        rtt_min_Aux = Float.valueOf(lstHost.get(2));
                        rtt_avg_Aux = Float.valueOf(lstHost.get(3));
                        rtt_max_Aux = Float.valueOf(lstHost.get(4));
                        rtt_mdev_Aux = Float.valueOf(lstHost.get(5));

                        lstHost.set(0, String.valueOf(sent + sent_Aux));
                        lstHost.set(1, String.valueOf(received + received_Aux));
                        lstHost.set(2, String.valueOf(rtt_min + rtt_min_Aux));
                        lstHost.set(3, String.valueOf(rtt_avg + rtt_avg_Aux));
                        lstHost.set(4, String.valueOf(rtt_max + rtt_max_Aux));
                        lstHost.set(5, String.valueOf(rtt_mdev + rtt_mdev_Aux));

                        lstHost.set(6, String.valueOf(Integer.valueOf(lstHost
                                .get(6)) + 1));

                        lstMatrix.put(strHosts, lstHost);
                    }

                } else
                    sw = false;
            }

            br.close();
            fstream.close();

            List<String> lstFinal = new ArrayList<String>();
            for (String strHost : lstHostAux) {
                lstHost = lstMatrix.get(strHost);
                sent_Aux = Integer.valueOf(lstHost.get(0));
                received_Aux = Integer.valueOf(lstHost.get(1));
                rtt_min_Aux = Float.valueOf(lstHost.get(2));
                rtt_avg_Aux = Float.valueOf(lstHost.get(3));
                rtt_max_Aux = Float.valueOf(lstHost.get(4));
                rtt_mdev_Aux = Float.valueOf(lstHost.get(5));

                sent_Aux = sent_Aux / Integer.valueOf(lstHost.get(6));
                received_Aux = received_Aux / Integer.valueOf(lstHost.get(6));
                rtt_min_Aux = rtt_min_Aux / Integer.valueOf(lstHost.get(6));
                rtt_avg_Aux = rtt_avg_Aux / Integer.valueOf(lstHost.get(6));
                rtt_max_Aux = rtt_max_Aux / Integer.valueOf(lstHost.get(6));
                rtt_mdev_Aux = rtt_mdev_Aux / Integer.valueOf(lstHost.get(6));
                lstFinal.add(strHost + "," + sent_Aux + "," + received_Aux
                        + "," + rtt_min_Aux + "," + rtt_avg_Aux + ","
                        + rtt_max_Aux + "," + rtt_mdev_Aux);
            }

            new CsvFileWriter().writeCsvFile(INPUT_MININET + strInputFolder
                    + strFileName + ".csv.gz", lstFinal, false);
        } else
            System.out.println("Not exists file: " + INPUT_MININET
                    + strInputFolder + strInputFile);

        // //////////////////////////////////////////////////////////////////////////////
        calculateTime(new Object() {
        }.getClass().getEnclosingMethod().getName());
    }

    private static void test_mininet_iperf(String strFileName,
            String strInputFolder, String strInputFile) throws Exception {
        System.out.println("start function: " + new Object() {
        }.getClass().getEnclosingMethod().getName() + " " + getData());
        startTime = System.currentTimeMillis();
        // //////////////////////////////////////////////////////////////////////////////

        InputStream fstream = null;
        BufferedReader br = null;
        String strLine, strHosts;
        boolean sw;
        HashMap<String, List<String>> lstMatrix = new HashMap<String, List<String>>();
        float time1, time2, time1Aux, time2Aux;
        List<String> lstHost = null;
        List<String> lstHostAux = new ArrayList<String>();

        if (Files.exists(Paths.get(INPUT_MININET + strInputFolder
                + strInputFile))) {

            fstream = new FileInputStream(INPUT_MININET + strInputFolder
                    + strInputFile);
            br = new BufferedReader(new InputStreamReader(fstream));

            sw = true;
            while ((strLine = br.readLine()) != null) {
                if (!sw) {
                    strHosts = strLine.split(",")[0] + ","
                            + strLine.split(",")[1];
                    time1 = Float.valueOf(strLine.split(",")[2]);
                    time2 = Float.valueOf(strLine.split(",")[3]);

                    if (!lstMatrix.containsKey(strHosts)) {
                        lstHost = new ArrayList<String>();
                        lstHost.add(String.valueOf(time1));
                        lstHost.add(String.valueOf(time2));
                        lstHost.add("1");
                        lstMatrix.put(strHosts, lstHost);
                        lstHostAux.add(strHosts);
                    } else {
                        lstHost = lstMatrix.get(strHosts);
                        time1Aux = Float.valueOf(lstHost.get(0));
                        time2Aux = Float.valueOf(lstHost.get(1));
                        lstHost.set(0, String.valueOf(time1 + time1Aux));
                        lstHost.set(1, String.valueOf(time2 + time2Aux));
                        lstHost.set(2, String.valueOf(Integer.valueOf(lstHost
                                .get(2)) + 1));
                        lstMatrix.put(strHosts, lstHost);
                    }

                } else
                    sw = false;
            }

            br.close();
            fstream.close();

            List<String> lstFinal = new ArrayList<String>();
            for (String strHost : lstHostAux) {
                lstHost = lstMatrix.get(strHost);
                time1Aux = Float.valueOf(lstHost.get(0));
                time2Aux = Float.valueOf(lstHost.get(1));
                time1Aux = time1Aux / Integer.valueOf(lstHost.get(2));
                time2Aux = time2Aux / Integer.valueOf(lstHost.get(2));
                lstFinal.add(strHost + "," + time1Aux + "," + time2Aux);
            }

            new CsvFileWriter().writeCsvFile(INPUT_MININET + strInputFolder
                    + strFileName + ".csv.gz", lstFinal, false);
        } else
            System.out.println("Not exists file: " + INPUT_MININET
                    + strInputFolder + strInputFile);

        // //////////////////////////////////////////////////////////////////////////////
        calculateTime(new Object() {
        }.getClass().getEnclosingMethod().getName());
    }

    private static void timeCostMap(String strFileName) throws Exception {
        System.out.println("start function: " + new Object() {
        }.getClass().getEnclosingMethod().getName() + " " + getData());
        startTime = System.currentTimeMillis();
        // //////////////////////////////////////////////////////////////////////////////

        String strLine;
        GZIPInputStream fstream = new GZIPInputStream(new FileInputStream(
                PATH_ALTO_AS + "ASes_to_PIDs.csv.gz"));
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        List<String> lstAS = new ArrayList<String>();
        while ((strLine = br.readLine()) != null)
            lstAS = Arrays.asList(strLine.split(","));
        br.close();
        fstream.close();

        int MaxPIDsrc = 1;// 100;
        int exeNumber = 10;
        int MaxPID = 100;// lstAS.size();

        String strQuery = "";
        long lonTime;
        long lonTimeTotal;
        int intTotalPIDs;
        boolean isFirst = true;
        int count = 1;
        int pos;

        String strMode, strMetric;
        String strSRC, strDST;

        for (int iPID = 0; iPID < MaxPID; iPID++) {
            strMode = "Numerical";
            strMetric = "HopsNumber";
            strSRC = "";
            strDST = "";

            // MaxPIDsrc = count;
            for (int iAS = 0; iAS < MaxPIDsrc; iAS++) {
                pos = new Random().nextInt(lstAS.size());
                strSRC = strSRC + "'" + "AS" + lstAS.get(pos) + "',";
            }
            strSRC = strSRC.substring(0, strSRC.length() - 1);
            strSRC = "'AS3633'";

            for (int iAS = 0; iAS < count; iAS++) {
                pos = new Random().nextInt(lstAS.size());
                strDST = strDST + "'" + "AS" + lstAS.get(pos) + "',";
            }
            strDST = strDST.substring(0, strDST.length() - 1);

            lonTimeTotal = 0;
            intTotalPIDs = 0;
            lonTime2 = lonTime3 = 0;

            for (int i = 0; i < exeNumber; i++) {
                strQuery = String
                        .format(new StringBuilder()
                                .append("MATCH (v:VersionTag {ResourceID:'%s'})-[r:Has_PID]->(s)")
                                .append(" WHERE s.Name IN [%s]")
                                .append(" OPTIONAL MATCH (s)-[c:Cost]->(d)")
                                .append(" WHERE d.Name IN [%s]")
                                .append(" WITH v.ResourceID AS ResourceID, v.Tag AS Tag,")
                                .append(" s.Name AS PIDsrc, d.Name AS PIDdst, c.%s AS hops")
                                .append(" ORDER BY PIDsrc, hops, PIDdst")
                                .append(" return ResourceID, Tag, PIDsrc, collect([PIDdst, hops])")
                                .toString(), ID_NetworkMap, strSRC, strDST,
                                strMetric);

                lonTime = REST_Query(strQuery, SERVER_ROOT_URI);

                Iterator<JsonNode> queryNeo4j = jsonResult.path("results")
                        .findPath("data").elements();

                createCostMapResult(queryNeo4j, strMode, strMetric, strDST);

                if (!isFirst)
                    lonTimeTotal += lonTime;
                else
                    isFirst = false;

                intTotalPIDs++;
            }

            // long seconds = TimeUnit.MILLISECONDS.toSeconds(lonTimeTotal);
            // float AVGTime = Float.valueOf(seconds)
            // / Float.valueOf((intTotalPIDs - 1));
            // System.out
            // .println(String.format(
            // "%d,%.3f,%.3f,%.3f,%.3f",
            // count,
            // AVGTime,
            // Float.valueOf(TimeUnit.MILLISECONDS.toSeconds(lonTime2)),
            // Float.valueOf(TimeUnit.MILLISECONDS.toSeconds(lonTime3)),
            // Float.valueOf(TimeUnit.MILLISECONDS.toSeconds(lonTime2 +
            // lonTime3))));

            float AVGTime = Float.valueOf(lonTimeTotal)
                    / Float.valueOf((intTotalPIDs - 1));
            System.out.println(String.format("%d,%.3f,%d,%d,%d", count,
                    AVGTime, lonTime2, lonTime3, (lonTime2 + lonTime3)));

            count++;
        }

        // //////////////////////////////////////////////////////////////////////////////
        calculateTime(new Object() {
        }.getClass().getEnclosingMethod().getName());
    }

    private static void createCostMapResult(Iterator<JsonNode> queryNeo4j,
            String strMode, String strMetric, String strDST) throws Exception {

        if (queryNeo4j.hasNext()) {

            Iterator<JsonNode> lstQuery = queryNeo4j.next().path("row")
                    .elements();

            String Aux1 = lstQuery.next().asText();
            String Aux2 = lstQuery.next().asText();

            String PIDsrc = lstQuery.next().asText();
            Map<String, Map<String, Object>> AuxMap = new LinkedHashMap<String, Map<String, Object>>();
            Map<String, Object> mapDST = createPIDdst(lstQuery.next()
                    .elements(), PIDsrc, strDST);

            lonTime2 += lonTime2Aux;
            lonTime3 += lonTime3Aux;

            AuxMap.put(PIDsrc, mapDST);

            while (queryNeo4j.hasNext()) {
                lstQuery = queryNeo4j.next().path("row").elements();

                lstQuery.next();
                lstQuery.next();

                PIDsrc = lstQuery.next().asText();
                mapDST = createPIDdst(lstQuery.next().elements(), PIDsrc,
                        strDST);

                lonTime2 += lonTime2Aux;
                lonTime3 += lonTime3Aux;

                AuxMap.put(PIDsrc, mapDST);
            }
        }
    }

    private static Map<String, Object> createPIDdst(Iterator<JsonNode> PIDdst,
            String PIDsrc, String strDST) throws Exception {
        Map<String, Object> mapDST = new LinkedHashMap<String, Object>();
        String strDSTAux = strDST;

        while (PIDdst.hasNext()) {
            Iterator<JsonNode> cost = PIDdst.next().elements();
            String PID = cost.next().toString().replaceAll("\"", "");
            if (!PID.equals("null")) {
                mapDST.put(PID, cost.next().asInt());
                strDSTAux = strDSTAux.replace(PID, "");
            }
        }

        int intAShops = 0;
        String strQuery;
        lonTime2Aux = lonTime3Aux = 0;

        if (mapDST.size() < strDST.split(",").length) {
            strDSTAux = strDSTAux.replace("'", "");
            strDSTAux = strDSTAux.replace(",,", ",");

            for (String strPIDdst : strDSTAux.split(",")) {
                if (!strPIDdst.isEmpty()) {
                    if (!PIDsrc.equals(strPIDdst)) {
                        strQuery = String
                                .format(new StringBuilder()
                                        .append("MATCH p=shortestPath((startAS:AS {number:'%s'})")
                                        .append("-[r:LINK*]-(endAS:AS {number:'%s'}))")
                                        .append(" WITH extract(n in nodes(p) | n.number) as path")
                                        .append(",reduce(s=0, rel IN r | s+1) as depth")
                                        .append(" RETURN path, depth")
                                        .toString(), PIDsrc.replace("AS", ""),
                                        strPIDdst.replace("AS", ""));

                        lonTime2Aux += REST_Query(strQuery, SERVER_AS_TOPOLOGY);
                        Iterator<JsonNode> ite = jsonResult.path("results")
                                .findPath("data").elements();
                        if (ite.hasNext()) {
                            while (ite.hasNext()) {
                                JsonNode temp = ite.next().path("row");
                                Iterator<JsonNode> ite1 = temp.elements();
                                ite1.next();
                                intAShops = ite1.next().asInt();
                            }
                        } else
                            intAShops = -1;
                    } else
                        intAShops = 0;

                    if (intAShops > -1) {
                        strQuery = String
                                .format(new StringBuilder()
                                        .append("MATCH (startAS:PID {Name:'%s'}),")
                                        .append("(endAS:PID {Name:'%s'})")
                                        .append(" MERGE (startAS)-[:Cost { HopsNumber : %d }]->(endAS)")
                                        .toString(), PIDsrc, strPIDdst,
                                        intAShops);

                        lonTime3Aux += REST_Query(strQuery, SERVER_ROOT_URI);

                        mapDST.put(strPIDdst, intAShops);
                    }
                }
            }
        }

        return mapDST;
    }

    private static void timeNetworkMap(String strFileName) throws Exception {
        System.out.println("start function: " + new Object() {
        }.getClass().getEnclosingMethod().getName() + " " + getData());
        startTime = System.currentTimeMillis();
        // //////////////////////////////////////////////////////////////////////////////

        String strLine;
        GZIPInputStream fstream = new GZIPInputStream(new FileInputStream(
                PATH_ALTO_AS + "ASes_to_PIDs.csv.gz"));
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        List<String> lstAS = new ArrayList<String>();
        while ((strLine = br.readLine()) != null)
            lstAS = Arrays.asList(strLine.split(","));
        br.close();
        fstream.close();

        int exeNumber = 10;
        int MaxPID = 20;// lstAS.size();
        String lstPID;
        String strQuery = "";
        long lonTime;
        long lonTimeTotal;
        int intTotalPIDs;
        boolean isFirst = true;
        int count = 1;
        int pos;

        for (int iPID = 0; iPID < MaxPID; iPID++) {
            lstPID = "";
            for (int iAS = 0; iAS < count; iAS++) {
                pos = new Random().nextInt(lstAS.size());
                lstPID = lstPID + "'" + "AS" + lstAS.get(pos) + "',";
            }
            lstPID = lstPID.substring(0, lstPID.length() - 1);

            lonTimeTotal = 0;
            intTotalPIDs = 0;
            for (int i = 0; i < exeNumber; i++) {
                strQuery = String
                        .format(new StringBuilder()
                                .append("MATCH (v:VersionTag {ResourceID:'%s'})-[r:Has_PID]->(pid)")
                                .append(" WHERE pid.Name IN [%s]")

                                // .append(" WITH v.Tag AS Tag,pid.Name AS Name")
                                // .append(" RETURN Tag, Name").toString(),

                                .append(" OPTIONAL MATCH (pid)-[e:Has_EndPoint]->")
                                .append("(p)<-[r2:Type_EndPoint]-(type)")
                                .append(" WITH v.Tag AS Tag,pid.Name AS Name,")
                                .append(" COLLECT([type.Type,p.Prefix]) AS p")
                                .append(" RETURN Tag, Name,")
                                .append(" [prefix IN FILTER(pAux in p where pAux[0]='%s') | prefix[1]] AS IPv4,")
                                .append(" [prefix in FILTER(pAux IN p where pAux[0]='%s') | prefix[1]] AS IPv6")
                                .append(" ORDER BY Name").toString(),
                                ID_NetworkMap, lstPID, IPv4_TYPE, IPv6_TYPE);

                lonTime = REST_Query(strQuery, SERVER_ROOT_URI);
                // System.out.println("Time (ms)," + lonTime);

                if (!isFirst)
                    lonTimeTotal += lonTime;
                else
                    isFirst = false;

                intTotalPIDs++;
            }

            // long seconds = TimeUnit.MILLISECONDS.toSeconds(lonTimeTotal);
            float AVGTime = Float.valueOf(lonTimeTotal)
                    / Float.valueOf((intTotalPIDs - 1));
            System.out.println(String.format("%d,%.3f", count, AVGTime));

            count++;
        }

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

    private static void total_ASes_prefixes_rawdata(String strFileName)
            throws Exception {
        System.out.println("start function: " + new Object() {
        }.getClass().getEnclosingMethod().getName() + " " + getData());
        startTime = System.currentTimeMillis();
        // //////////////////////////////////////////////////////////////////////////////

        GZIPInputStream fstream = null;
        BufferedReader br = null;
        String strLine, strAS, strPrefix;
        boolean sw;
        List<String> lstAS = new ArrayList<String>();
        List<String> lstPrefixIPv4 = new ArrayList<String>();
        List<String> lstPrefixIPv6 = new ArrayList<String>();

        for (String strIXP : lstIXP20141208) {
            System.out.println(strIXP + "IPv4");

            if (Files.exists(Paths.get(INPUT_PATH_DATASET
                    + "IPv4/processed/rib.table.lg." + strIXP.toLowerCase()
                    + ".ptt.br-BGP.csv.gz"))) {

                fstream = new GZIPInputStream(new FileInputStream(
                        INPUT_PATH_DATASET + "IPv4/processed/rib.table.lg."
                                + strIXP.toLowerCase() + ".ptt.br-BGP.csv.gz"));
                br = new BufferedReader(new InputStreamReader(fstream));

                sw = true;
                while ((strLine = br.readLine()) != null) {
                    if (!sw) {
                        if (strLine.split(",").length > 7) {
                            strAS = strLine.split(",")[7].replace("\"", "");
                            strAS = strAS.split(" ")[strAS.split(" ").length - 1];
                            if (!lstAS.contains(strAS))
                                lstAS.add(strAS);

                            strPrefix = strLine.split(",")[1].replace("\"", "")
                                    + "/"
                                    + strLine.split(",")[2].replace("\"", "");
                            if (!lstPrefixIPv4.contains(strPrefix))
                                lstPrefixIPv4.add(strPrefix);
                        }
                    } else
                        sw = false;
                }

                br.close();
                fstream.close();
            } else
                System.out.println("Not exists file: " + INPUT_PATH_DATASET
                        + "IPv4/processed/rib.table.lg." + strIXP.toLowerCase()
                        + ".ptt.br-BGP.csv.gz");

            System.out.println(strIXP + "IPv6");

            if (Files.exists(Paths.get(INPUT_PATH_DATASET
                    + "IPv6/processed/rib.table.lg." + strIXP.toLowerCase()
                    + ".ptt.br-BGP-IPv6.csv.gz"))) {

                fstream = new GZIPInputStream(new FileInputStream(
                        INPUT_PATH_DATASET + "IPv6/processed/rib.table.lg."
                                + strIXP.toLowerCase()
                                + ".ptt.br-BGP-IPv6.csv.gz"));
                br = new BufferedReader(new InputStreamReader(fstream));

                sw = true;
                while ((strLine = br.readLine()) != null) {
                    if (!sw) {
                        if (strLine.split(",").length > 7) {
                            strAS = strLine.split(",")[7].replace("\"", "");
                            strAS = strAS.split(" ")[strAS.split(" ").length - 1];
                            if (!lstAS.contains(strAS))
                                lstAS.add(strAS);

                            strPrefix = strLine.split(",")[1].replace("\"", "")
                                    + "/"
                                    + strLine.split(",")[2].replace("\"", "");

                            if (!lstPrefixIPv6.contains(strPrefix))
                                lstPrefixIPv6.add(strPrefix);
                        }
                    } else
                        sw = false;
                }

                br.close();
                fstream.close();
            } else
                System.out.println("Not exists file: " + INPUT_PATH_DATASET
                        + "IPv6/processed/rib.table.lg." + strIXP.toLowerCase()
                        + ".ptt.br-BGP-IPv6.csv.gz");
        }

        System.out.println("TOTAL ASes," + lstAS.size());
        System.out.println("TOTAL IPv4," + lstPrefixIPv4.size());
        System.out.println("TOTAL IPv6," + lstPrefixIPv6.size());

        // //////////////////////////////////////////////////////////////////////////////
        calculateTime(new Object() {
        }.getClass().getEnclosingMethod().getName());
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
