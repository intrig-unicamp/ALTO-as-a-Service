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

public class Main {
    private static final String PATH_PREPROC_ALTO_AS = "InputData/ALTO/AS/PreProcess/20141208/";
    private static final String PATH_ALTO_AS = "InputData/ALTO/AS/20141208/";
    private static final String INPUT_PATH_DATASET = "../IXP-PTT-BR/20141208/PTTMetro-LG-Dataset/";
    private static long startTime;
    private static final String ASN_PTT_LG = "20121";
    private static final String ASN_PTT_RS = "26121";
    private static final long ASN_MAX = (long) Math.pow(2, 32);

    private static final String[] lstIXP20141208 = { "BA", "BEL", "CAS", "CE",
            "CGB", "CPV", "CXJ", "DF", "GYN", "LAJ", "LDA", "MAO", "MG", "MGF",
            "NAT", "PE", "PR", "RJ", "RS", "SC", "SCA", "SJC", "SJP", "SP",
            "VIX" };

    // private static final String[] lstIXP20141208 = { "SP" };

    public static void main(String[] args) throws Exception {

        try {
            System.setOut(new PrintStream(new FileOutputStream(PATH_ALTO_AS
                    + "log/" + "ASes_to_PIDs" + getData() + ".txt")));
            System.out.println("Start");

            //Matrix_AS_Prefix("Matrix_AS_Prefix");

            ASes_to_PIDs("ASes_to_PIDs");

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

    private static void Matrix_AS_Prefix(String strFileName) throws Exception {
        System.out.println("start function: " + new Object() {
        }.getClass().getEnclosingMethod().getName() + " " + getData());
        startTime = System.currentTimeMillis();
        // //////////////////////////////////////////////////////////////////////////////

        GZIPInputStream fstream = null;
        BufferedReader br = null;
        List<String> lstPrefixOutIPv4 = new ArrayList<String>();
        List<String> lstPrefixOutIPv6 = new ArrayList<String>();
        HashMap<String, List<String>> lstMatrixIPv4 = new HashMap<String, List<String>>();
        HashMap<String, List<String>> lstMatrixIPv6 = new HashMap<String, List<String>>();
        String strLine, strAS, strPrefix;
        List<String> lstPrefix = new ArrayList<String>();
        int TotalIPv4 = 0, TotalIPv6 = 0;

        fstream = new GZIPInputStream(new FileInputStream(PATH_PREPROC_ALTO_AS
                + "prefixes_out_IPv4.csv.gz"));
        br = new BufferedReader(new InputStreamReader(fstream));
        while ((strLine = br.readLine()) != null)
            lstPrefixOutIPv4 = Arrays.asList(strLine.split(","));
        br.close();
        fstream.close();

        fstream = new GZIPInputStream(new FileInputStream(PATH_PREPROC_ALTO_AS
                + "prefixes_out_IPv6.csv.gz"));
        br = new BufferedReader(new InputStreamReader(fstream));
        while ((strLine = br.readLine()) != null)
            lstPrefixOutIPv6 = Arrays.asList(strLine.split(","));
        br.close();
        fstream.close();

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
                    strPrefix = strLine.split(",")[0] + "/"
                            + strLine.split(",")[1];
                    strAS = strLine.split(",")[2];

                    if (!lstPrefixOutIPv4.contains(strPrefix)) {
                        lstPrefix = lstMatrixIPv4.get(strAS);
                        if (lstPrefix == null) {
                            lstPrefix = new ArrayList<String>();
                            lstPrefix.add(strPrefix);
                            lstMatrixIPv4.put(strAS, new ArrayList<String>());
                            lstMatrixIPv4.put(strAS, lstPrefix);
                            TotalIPv4++;
                        } else {
                            if (!lstPrefix.contains(strPrefix)) {
                                lstPrefix.add(strPrefix);
                                lstMatrixIPv4.put(strAS, lstPrefix);
                                TotalIPv4++;
                            }
                        }
                    }
                }

                // Close the input stream
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
                    strPrefix = strLine.split(",")[0] + "/"
                            + strLine.split(",")[1];
                    strAS = strLine.split(",")[2];

                    if (!lstPrefixOutIPv6.contains(strPrefix)) {
                        lstPrefix = lstMatrixIPv6.get(strAS);
                        if (lstPrefix == null) {
                            lstPrefix = new ArrayList<String>();
                            lstPrefix.add(strPrefix);
                            lstMatrixIPv6.put(strAS, new ArrayList<String>());
                            lstMatrixIPv6.put(strAS, lstPrefix);
                            TotalIPv6++;
                        } else {
                            if (!lstPrefix.contains(strPrefix)) {
                                lstPrefix.add(strPrefix);
                                lstMatrixIPv6.put(strAS, lstPrefix);
                                TotalIPv6++;
                            }
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

        int Total;
        float PorCon, PorOut;

        new CsvFileWriter().writeCsvFile(PATH_ALTO_AS + strFileName
                + "_IPv4.csv.gz", lstMatrixIPv4);
        System.out.println();
        Total = 563164;
        System.out.println("Total IPv4 Prefix: " + Total);
        System.out.println("IPv4 Prefix Considered: " + TotalIPv4);
        PorCon = (Float.valueOf(TotalIPv4) * Float.valueOf(100))
                / Float.valueOf(Total);
        System.out.println(String.format("IPv4 Prefix Considered (PCT): %.3f",
                PorCon));
        System.out.println("IPv4 Prefix OUT: " + (Total - TotalIPv4));
        PorOut = (Float.valueOf(Total - TotalIPv4) * Float.valueOf(100))
                / Float.valueOf(Total);
        System.out
                .println(String.format("IPv4 Prefix OUT (PCT): %.3f", PorOut));

        System.out.println();
        new CsvFileWriter().writeCsvFile(PATH_ALTO_AS + strFileName
                + "_IPv6.csv.gz", lstMatrixIPv6);
        Total = 21666;
        System.out.println("Total IPv6 Prefix: " + Total);
        System.out.println("IPv6 Prefix Considered: " + TotalIPv6);
        PorCon = (Float.valueOf(TotalIPv6) * Float.valueOf(100))
                / Float.valueOf(Total);
        System.out.println(String.format("IPv6 Prefix Considered (PCT): %.3f",
                PorCon));
        System.out.println("IPv6 Prefix OUT: " + (Total - TotalIPv6));
        PorOut = (Float.valueOf(Total - TotalIPv6) * Float.valueOf(100))
                / Float.valueOf(Total);
        System.out
                .println(String.format("IPv6 Prefix OUT (PCT): %.3f", PorOut));

        System.out.println();
        Total = 563164 + 21666;
        System.out.println("Total Prefix: " + Total);
        System.out.println("Prefix Considered: " + (TotalIPv4 + TotalIPv6));
        PorCon = (Float.valueOf(TotalIPv4 + TotalIPv6) * Float.valueOf(100))
                / Float.valueOf(Total);
        System.out.println(String.format("Prefix Considered (PCT): %.3f",
                PorCon));
        System.out
                .println("Prefix OUT: "
                        + (lstPrefixOutIPv4.size() + (Total - (TotalIPv4 + TotalIPv6))));
        PorOut = (Float.valueOf(Total - (TotalIPv4 + TotalIPv6)) * Float
                .valueOf(100)) / Float.valueOf(Total);
        System.out.println(String.format("Prefix OUT (PCT): %.3f", PorOut));

        // //////////////////////////////////////////////////////////////////////////////
        calculateTime(new Object() {
        }.getClass().getEnclosingMethod().getName());
    }

    private static void ASes_to_PIDs(String strFileName) throws Exception {
        System.out.println("start function: " + new Object() {
        }.getClass().getEnclosingMethod().getName() + " " + getData());
        startTime = System.currentTimeMillis();
        // //////////////////////////////////////////////////////////////////////////////

        GZIPInputStream fstream = null;
        BufferedReader br = null;
        String strLine, strAS;
        List<String> lstAS = new ArrayList<String>();

        fstream = new GZIPInputStream(new FileInputStream(PATH_ALTO_AS
                + "Matrix_AS_Prefix_IPv4.csv.gz"));
        br = new BufferedReader(new InputStreamReader(fstream));
        while ((strLine = br.readLine()) != null) {
            if (!lstAS.contains(strLine.split(",")[0]))
                lstAS.add(strLine.split(",")[0]);
        }
        br.close();
        fstream.close();

        fstream = new GZIPInputStream(new FileInputStream(PATH_ALTO_AS
                + "Matrix_AS_Prefix_IPv6.csv.gz"));
        br = new BufferedReader(new InputStreamReader(fstream));
        while ((strLine = br.readLine()) != null) {
            if (!lstAS.contains(strLine.split(",")[0]))
                lstAS.add(strLine.split(",")[0]);
        }
        br.close();
        fstream.close();

        new CsvFileWriter().writeCsvFile(
                PATH_ALTO_AS + strFileName + ".csv.gz", lstAS, true);
        
        int Total;
        float PorCon, PorOut;
        Total = 49586;
        System.out.println("Total ASes: " + Total);
        System.out.println("Total ASes Considered: " + lstAS.size());
        PorCon = (Float.valueOf(lstAS.size()) * Float.valueOf(100))
                / Float.valueOf(Total);
        System.out.println(String.format("ASes Considered (PCT): %.3f",
                PorCon));
        System.out.println("ASes OUT: " + (Total - lstAS.size()));
        PorOut = (Float.valueOf(Total - lstAS.size()) * Float.valueOf(100))
                / Float.valueOf(Total);
        System.out
                .println(String.format("ASes OUT (PCT): %.3f", PorOut));       

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
