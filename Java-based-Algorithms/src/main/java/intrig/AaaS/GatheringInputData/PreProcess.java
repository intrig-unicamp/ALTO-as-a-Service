package intrig.AaaS.GatheringInputData;

import intrig.AaaS.Util.CsvFileWriter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import java.security.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

public class PreProcess {
    private static final String PATH_PREPROC_ALTO_AS = "InputData/ALTO/AS/PreProcess/20141208/";
    private static final String INPUT_PATH_DATASET = "../IXP-PTT-BR/20141208/PTTMetro-LG-Dataset/";
    private static final String NA_NETMASK = "NA";
    private static long startTime;
    private static final String ASN_PTT_LG = "20121";
    private static final String ASN_PTT_RS = "26121";

    private static final String[] lstIXP20141208 = { "BA", "BEL", "CAS", "CE",
            "CGB", "CPV", "CXJ", "DF", "GYN", "LAJ", "LDA", "MAO", "MG", "MGF",
            "NAT", "PE", "PR", "RJ", "RS", "SC", "SCA", "SJC", "SJP", "SP",
            "VIX" };

    // private static final String[] lstIXP20141208 = { "SP" };

    public static void main(String[] args) throws Exception {

        try {
            System.setOut(new PrintStream(new FileOutputStream(
                    PATH_PREPROC_ALTO_AS + "log/" + "prefixes_out" + getData()
                            + ".txt")));
            System.out.println("Start");

            prefixes_out("prefixes_out");

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

    private static void prefixes_out(String strFileName) throws Exception {
        System.out.println("start function: " + new Object() {
        }.getClass().getEnclosingMethod().getName() + " " + getData());
        startTime = System.currentTimeMillis();
        // //////////////////////////////////////////////////////////////////////////////

        GZIPInputStream fstream = null;
        BufferedReader br = null;
        String strLine, strPrefix;
        List<String> lstAS = new ArrayList<String>();
        List<String> lstPrefixOutIPv4 = new ArrayList<String>();
        List<String> lstPrefixOutIPv4Repeat = new ArrayList<String>();
        List<String> lstPrefixOutIPv6 = new ArrayList<String>();
        List<String> lstPrefixOutIPv6Repeat = new ArrayList<String>();
        HashMap<String, List<String>> lstPrefixIPv4 = new HashMap<String, List<String>>();
        HashMap<String, List<String>> lstPrefixIPv6 = new HashMap<String, List<String>>();

        for (String strIXP : lstIXP20141208) {
            System.out.println(strIXP + "IPv4");

            if (Files.exists(Paths.get(INPUT_PATH_DATASET
                    + "IPv4/alto/rib.table.lg." + strIXP.toLowerCase()
                    + ".ptt.br-BGP.wau.csv.gz"))) {

                fstream = new GZIPInputStream(new FileInputStream(
                        INPUT_PATH_DATASET + "IPv4/alto/rib.table.lg."
                                + strIXP.toLowerCase()
                                + ".ptt.br-BGP.wau.csv.gz"));
                br = new BufferedReader(new InputStreamReader(fstream));

                while ((strLine = br.readLine()) != null) {

                    if (!strLine.split(",")[1].equals(NA_NETMASK)) {

                        if (!strLine.split(",")[2].equals(ASN_PTT_LG)
                                && !strLine.split(",")[2].equals(ASN_PTT_RS)) {

                            strPrefix = strLine.split(",")[0] + "/"
                                    + strLine.split(",")[1];
                            lstAS = lstPrefixIPv4.get(strPrefix);

                            if (lstAS == null) {

                                lstAS = new ArrayList<String>();
                                lstAS.add(strLine.split(",")[2]);
                                lstPrefixIPv4.put(strPrefix,
                                        new ArrayList<String>());
                                lstPrefixIPv4.put(strPrefix, lstAS);
                            } else {
                                if (!lstAS.contains(strLine.split(",")[2])) {
                                    lstAS.add(strLine.split(",")[2]);
                                    lstPrefixIPv4.put(strPrefix, lstAS);
                                }
                            }
                        } else {
                            if (!lstPrefixOutIPv4
                                    .contains(strLine.split(",")[0] + "/"
                                            + strLine.split(",")[1])) {
                                System.out.println(strIXP
                                        + " IPv4 20121/26121: " + strLine);
                                lstPrefixOutIPv4.add(strLine.split(",")[0]
                                        + "/" + strLine.split(",")[1]);
                            }
                        }

                    } else {
                        if (!lstPrefixOutIPv4.contains(strLine.split(",")[0]
                                + "/" + strLine.split(",")[1])) {
                            System.out.println(strIXP + " IPv4 NA: " + strLine);
                            lstPrefixOutIPv4.add(strLine.split(",")[0] + "/"
                                    + strLine.split(",")[1]);
                        }
                    }
                }

                br.close();
                fstream.close();
            } else
                System.out.println("Not exists file: " + INPUT_PATH_DATASET
                        + "IPv4/alto/rib.table.lg." + strIXP.toLowerCase()
                        + ".ptt.br-BGP.wau.csv.gz");

            System.out.println(strIXP + "IPv6");

            if (Files.exists(Paths.get(INPUT_PATH_DATASET
                    + "IPv6/alto/rib.table.lg." + strIXP.toLowerCase()
                    + ".ptt.br-BGP-IPv6.wau.csv.gz"))) {

                fstream = new GZIPInputStream(new FileInputStream(
                        INPUT_PATH_DATASET + "IPv6/alto/rib.table.lg."
                                + strIXP.toLowerCase()
                                + ".ptt.br-BGP-IPv6.wau.csv.gz"));
                br = new BufferedReader(new InputStreamReader(fstream));

                while ((strLine = br.readLine()) != null) {
                    if (!strLine.split(",")[1].equals(NA_NETMASK)) {

                        if (!strLine.split(",")[2].equals(ASN_PTT_LG)
                                && !strLine.split(",")[2].equals(ASN_PTT_RS)) {

                            strPrefix = strLine.split(",")[0] + "/"
                                    + strLine.split(",")[1];
                            lstAS = lstPrefixIPv6.get(strPrefix);

                            if (lstAS == null) {

                                lstAS = new ArrayList<String>();
                                lstAS.add(strLine.split(",")[2]);
                                lstPrefixIPv6.put(strPrefix,
                                        new ArrayList<String>());
                                lstPrefixIPv6.put(strPrefix, lstAS);
                            } else {
                                if (!lstAS.contains(strLine.split(",")[2])) {
                                    lstAS.add(strLine.split(",")[2]);
                                    lstPrefixIPv6.put(strPrefix, lstAS);
                                }
                            }
                        } else {
                            if (!lstPrefixOutIPv6
                                    .contains(strLine.split(",")[0] + "/"
                                            + strLine.split(",")[1])) {
                                System.out.println(strIXP
                                        + " IPv6 20121/26121: " + strLine);
                                lstPrefixOutIPv6.add(strLine.split(",")[0]
                                        + "/" + strLine.split(",")[1]);
                            }
                        }

                    } else {
                        if (!lstPrefixOutIPv6.contains(strLine.split(",")[0]
                                + "/" + strLine.split(",")[1])) {
                            System.out.println(strIXP + " IPv6 NA: " + strLine);
                            lstPrefixOutIPv6.add(strLine.split(",")[0] + "/"
                                    + strLine.split(",")[1]);
                        }
                    }
                }

                br.close();
                fstream.close();
            } else
                System.out.println("Not exists file: " + INPUT_PATH_DATASET
                        + "IPv6/alto/rib.table.lg." + strIXP.toLowerCase()
                        + ".ptt.br-BGP-IPv6.wau.csv.gz");
        }

        System.out.println("TOTAL IPv4 20121/26121 AND NA: "
                + lstPrefixOutIPv4.size());
        System.out.println("TOTAL IPv6 20121/26121 AND NA: "
                + lstPrefixOutIPv6.size());

        List<String> lstPrefixOutFinalIPv4 = new ArrayList<String>();
        List<String> lstPrefixOutFinalIPv6 = new ArrayList<String>();

        lstPrefixOutFinalIPv4.addAll(lstPrefixOutIPv4);
        lstPrefixOutFinalIPv6.addAll(lstPrefixOutIPv6);

        Iterator<Map.Entry<String, List<String>>> entries = lstPrefixIPv4
                .entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, List<String>> entry = entries.next();
            String key = entry.getKey();
            List<String> value = entry.getValue();
            if (value.size() > 1) {
                if (!lstPrefixOutIPv4Repeat.contains(key)) {
                    lstPrefixOutIPv4Repeat.add(key);
                    System.out.println(key + ": " + value);
                }
            }
        }

        entries = lstPrefixIPv6.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, List<String>> entry = entries.next();
            String key = entry.getKey();
            List<String> value = entry.getValue();
            if (value.size() > 1) {
                if (!lstPrefixOutIPv6Repeat.contains(key)) {
                    lstPrefixOutIPv6Repeat.add(key);
                    System.out.println(key + ": " + value);
                }
            }
        }

        System.out.println("TOTAL IPv4 repeat: "
                + lstPrefixOutIPv4Repeat.size());
        System.out.println("TOTAL IPv6 repeat: "
                + lstPrefixOutIPv6Repeat.size());

        lstPrefixOutFinalIPv4.addAll(lstPrefixOutIPv4Repeat);
        lstPrefixOutFinalIPv6.addAll(lstPrefixOutIPv6Repeat);

        Set<String> lstAux = new HashSet<String>();
        lstAux.addAll(lstPrefixOutFinalIPv4);
        lstPrefixOutFinalIPv4.clear();
        lstPrefixOutFinalIPv4.addAll(lstAux);

        lstAux = new HashSet<String>();
        lstAux.addAll(lstPrefixOutFinalIPv6);
        lstPrefixOutFinalIPv6.clear();
        lstPrefixOutFinalIPv6.addAll(lstAux);

        new CsvFileWriter().writeCsvFile(PATH_PREPROC_ALTO_AS + strFileName
                + "_IPv4.csv.gz", lstPrefixOutFinalIPv4, true);
        System.out.println("TOTAL Prefix IPv4 OUT: "
                + lstPrefixOutFinalIPv4.size());

        new CsvFileWriter().writeCsvFile(PATH_PREPROC_ALTO_AS + strFileName
                + "_IPv6.csv.gz", lstPrefixOutFinalIPv6, true);
        System.out.println("TOTAL Prefix IPv6 OUT: "
                + lstPrefixOutFinalIPv6.size());

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