package intrig.AaaS.Creating.ALTO.Information;

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

public class PreProcess_AS_Topology {
    private static final String PATH = "InputData/AS_Topology/20141208/";
    // private static final String PATH =
    // "InputData/AS_Topology/20141208/TEST/";

    private static final String INPUT_PATH = "../IXP-PTT-BR/20141208/PTT_PATH_FIltered/";
    private static long startTime;
    private static final String ASN_PTT_LG = "20121";
    private static final String ASN_PTT_RS = "26121";

    private static final String[] lstIXP20141208 = { "BA", "BEL", "CAS", "CE",
            "CGB", "CPV", "CXJ", "DF", "GYN", "LAJ", "LDA", "MAO", "MG", "MGF",
            "NAT", "PE", "PR", "RJ", "RS", "SC", "SCA", "SJC", "SJP", "SP",
            "VIX" };

    // private static final String[] lstIXP20141208 = { "TEST" };

    public static void main(String[] args) throws Exception {

        try {
            System.setOut(new PrintStream(new FileOutputStream(PATH
                    + "MatrixRoundTrip" + getData() + ".txt")));
            System.out.println("Start");

            // TotalAS("TotalAS");

            MatrixRoundTrip("MatrixRoundTrip");

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

    private static void TotalAS(String strFileName) throws Exception {
        System.out.println("start function: " + new Object() {
        }.getClass().getEnclosingMethod().getName() + " " + getData());
        startTime = System.currentTimeMillis();
        // //////////////////////////////////////////////////////////////////////////////

        GZIPInputStream fstream = null;
        BufferedReader br = null;
        String strLine;
        List<String> lstAS = new ArrayList<String>();

        for (String strIXP : lstIXP20141208) {
            System.out.println(strIXP);

            fstream = new GZIPInputStream(new FileInputStream(INPUT_PATH
                    + "Ptt_Path_" + strIXP + ".txt.gz"));
            br = new BufferedReader(new InputStreamReader(fstream));

            lstAS.add(ASN_PTT_LG + ":" + ASN_PTT_RS + ":" + strIXP);

            while ((strLine = br.readLine()) != null) {
                String[] lstSubAS = strLine.split(" ");
                if (!lstSubAS[lstSubAS.length - 1].equals(ASN_PTT_LG)
                        && !lstSubAS[lstSubAS.length - 1].equals(ASN_PTT_RS)) {
                    for (String strAS : lstSubAS) {
                        if (!lstAS.contains(strAS) && !strAS.equals(ASN_PTT_LG)
                                && !strAS.equals(ASN_PTT_RS)) {
                            lstAS.add(strAS);
                        }
                    }
                } else
                    System.out.println(strIXP + ":" + strLine);
            }

            // Close the input stream
            br.close();
            fstream.close();
        }

        new CsvFileWriter().writeCsvFile(PATH + strFileName + ".csv.gz", lstAS,
                true);
        System.out.println("Total ASes: " + lstAS.size());

        // //////////////////////////////////////////////////////////////////////////////
        calculateTime(new Object() {
        }.getClass().getEnclosingMethod().getName());

    }

    private static void MatrixRoundTrip(String strFileName) throws Exception {
        System.out.println("start function: " + new Object() {
        }.getClass().getEnclosingMethod().getName() + " " + getData());
        startTime = System.currentTimeMillis();
        // //////////////////////////////////////////////////////////////////////////////

        GZIPInputStream fstream = null;
        BufferedReader br = null;
        String strLine;

        fstream = new GZIPInputStream(new FileInputStream(PATH
                + "TotalAS.csv.gz"));
        br = new BufferedReader(new InputStreamReader(fstream));
        HashMap<String, List<String>> lstMatrix = new HashMap<String, List<String>>();
        while ((strLine = br.readLine()) != null) {
            String[] lstSubAS = strLine.split(",");
            for (String strAS : lstSubAS) {
                lstMatrix.put(strAS, new ArrayList<String>());
            }
        }

        br.close();
        fstream.close();

        String strAS1;
        String strAS2;
        for (String strIXP : lstIXP20141208) {
            System.out.println(strIXP);

            fstream = new GZIPInputStream(new FileInputStream(INPUT_PATH
                    + "Ptt_Path_" + strIXP + ".txt.gz"));
            br = new BufferedReader(new InputStreamReader(fstream));

            while ((strLine = br.readLine()) != null) {
                String[] lstSubAS = strLine.split(" ");
                if (!lstSubAS[lstSubAS.length - 1].equals(ASN_PTT_LG)
                        && !lstSubAS[lstSubAS.length - 1].equals(ASN_PTT_RS)) {
                    for (int i = 0; i < lstSubAS.length - 1; i++) {
                        if (i == 0) {
                            strAS1 = ASN_PTT_LG + ":" + ASN_PTT_RS + ":"
                                    + strIXP;
                            strAS2 = lstSubAS[i];
                            fnInsertMatrixOneDirection(lstMatrix, strAS1,
                                    strAS2);
                        }
                        if (i < lstSubAS.length - 1
                                && !lstSubAS[i].equals(lstSubAS[i + 1])) {
                            strAS1 = lstSubAS[i];
                            if (strAS1.equals(ASN_PTT_LG)
                                    || strAS1.equals(ASN_PTT_RS))
                                strAS1 = ASN_PTT_LG + ":" + ASN_PTT_RS + ":"
                                        + strIXP;

                            strAS2 = lstSubAS[i + 1];
                            if (strAS2.equals(ASN_PTT_LG)
                                    || strAS2.equals(ASN_PTT_RS))
                                strAS2 = ASN_PTT_LG + ":" + ASN_PTT_RS + ":"
                                        + strIXP;

                            if (!strAS1.equals(strAS2))
                                fnInsertMatrixOneDirection(lstMatrix, strAS1,
                                        strAS2);
                        }
                    }
                } else
                    System.out.println(strIXP + ":" + strLine);
            }
            br.close();
            fstream.close();
        }

        new CsvFileWriter().writeCsvFile(PATH + strFileName + ".csv.gz",
                lstMatrix);

        // //////////////////////////////////////////////////////////////////////////////
        calculateTime(new Object() {
        }.getClass().getEnclosingMethod().getName());
    }

    private static void fnInsertMatrixOneDirection(
            HashMap<String, List<String>> lstMatrix, String strAS1,
            String strAS2) {
        List<String> lstAS1 = lstMatrix.get(strAS1);

        if (!lstAS1.contains(strAS2)) {
            lstAS1.add(strAS2);
            lstMatrix.put(strAS1, lstAS1);
        }
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
