package com.concordia.analysis;

import com.concordia.entity.TestSuite;
import com.concordia.utils.DateUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Measures {

    private static Map<Integer, Date> google_tcp_first_fail = new HashMap<>();
    private static Map<Integer, Date> fifo_first_fail = new HashMap<>();

    private static Map<Integer, Date> google_tcp_all_fail = new HashMap<>();
    private static Map<Integer, Date> fifo_all_fail = new HashMap<>();

    private static Map<Integer, Map<String, Date>> tcp_failures = new HashMap<>();
    private static Map<Integer, Map<String, Date>> fifo_failures = new HashMap<>();

    private static Map<Integer, Date> requestTimeStamps = new HashMap<>();

    private static Date dateCriterion;

    private static int size = 0;

    static {
        try {
            dateCriterion = DateUtils.dateFormatter("2014-01-01 00:00:02.000");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static void analyze(String filename) {
        try {

            BufferedReader br1 = new BufferedReader(new FileReader("FIFO.out"));
            BufferedReader br2 = new BufferedReader(new FileReader("TCP.out"));
            BufferedReader br3 = new BufferedReader(new FileReader(filename));

            run(br1, br2, br3);
            br1.close();
            br2.close();
            br3.close();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private static void run(BufferedReader br1, BufferedReader br2, BufferedReader br3) throws IOException, ParseException {

        readResults(br1, "fifo");
        readResults(br2, "tcp");
        readLaunchTime(br3);

        double median1 = calculateMedianWaitingTime(fifo_first_fail);
        double median2 = calculateMedianWaitingTime(fifo_all_fail);

        double firstFail = calculateSpeedup(google_tcp_first_fail, fifo_first_fail);
        System.out.println("FirstFail: " + firstFail / median1 * 100 + "%");

        double all_fail = calculateSpeedup(google_tcp_all_fail, fifo_all_fail);
        System.out.println("AllFail: " + all_fail / median2 * 100 + "%");

        double delayed_rate = calculateDelayRate(fifo_failures, tcp_failures);
        System.out.println("Percentage of Delayed Test Failures: " + delayed_rate * 100 + "%");

    }

    private static void readLaunchTime(BufferedReader br3) throws IOException, ParseException {
        while (br3.ready()) {
            TestSuite testSuite = TestSuite.parseTestSuite(br3.readLine());
            if (!requestTimeStamps.containsKey(testSuite.getRequestNumber())) {
                requestTimeStamps.put(testSuite.getRequestNumber(), testSuite.getLaunchTime());
            }
        }
    }

    private static double calculateMedianWaitingTime(Map<Integer, Date> failures) {
        long time = 0L;
        for (Map.Entry<Integer, Date> entry : failures.entrySet()) {
            Integer requestNumber = entry.getKey();
            long diff = entry.getValue().getTime() - requestTimeStamps.get(requestNumber).getTime();
//            long diff = entry.getValue().getTime() - (dateCriterion.getTime() + entry.getKey() * 2222);
            time = time + diff;
        }
        return (double) time / requestTimeStamps.size();
    }

    private static double calculateSpeedup(Map<Integer, Date> google_tcp, Map<Integer, Date> fifo) {

        long time = 0L;
        for (Map.Entry<Integer, Date> entry : fifo.entrySet()) {
            Integer key = entry.getKey();
            long diff = fifo.get(key).getTime() - google_tcp.get(key).getTime();
            time = time + diff;
        }
        return (double) time / requestTimeStamps.size();
    }


    private static double calculateDelayRate(Map<Integer, Map<String, Date>> fifo_failures, Map<Integer, Map<String, Date>> tcp_failures) {

        double delayed_sum = 0;
        for (Map.Entry<Integer, Map<String, Date>> entry : fifo_failures.entrySet()) {
            Integer key = entry.getKey();
            Map<String, Date> fifo_fail = entry.getValue();
            Map<String, Date> tcp_fail = tcp_failures.get(key);
            for (Map.Entry<String, Date> fifo_entry : fifo_fail.entrySet()) {
                String key1 = fifo_entry.getKey();
                Date date_fifo = fifo_entry.getValue();
                Date date_tcp = tcp_fail.get(key1);
                if (date_fifo.before(date_tcp)) {
                    delayed_sum++;
                }
            }
        }
        return delayed_sum / size;
    }

    private static void readResults(BufferedReader br, String type) throws IOException, ParseException {
        while (br.ready()) {
            TestSuite testSuite = TestSuite.parseTestSuite(br.readLine());
            if (testSuite.getStatus().equals("FAILED")) {
                if (type.equals("tcp")) {
                    size++;
                    getDataMap(testSuite, google_tcp_first_fail, google_tcp_all_fail, tcp_failures);
                }
                if (type.equals("fifo")) {
                    getDataMap(testSuite, fifo_first_fail, fifo_all_fail, fifo_failures);
                }
            }
        }
    }

    private static void getDataMap(TestSuite testSuite, Map<Integer, Date> first_fail, Map<Integer, Date> all_fail, Map<Integer, Map<String, Date>> failures) {
        if (!first_fail.containsKey(testSuite.getRequestNumber())) {
            first_fail.put(testSuite.getRequestNumber(), testSuite.getLaunchTime());
        }
        all_fail.put(testSuite.getRequestNumber(), testSuite.getLaunchTime());
        if (failures.containsKey(testSuite.getRequestNumber())) {
            Map<String, Date> failureMap = failures.get(testSuite.getRequestNumber());
            failureMap.put(testSuite.getDirectory(), testSuite.getLaunchTime());
        } else {
            failures.put(testSuite.getRequestNumber(), new HashMap<>());
            Map<String, Date> dateMap = failures.get(testSuite.getRequestNumber());
            dateMap.put(testSuite.getDirectory(), testSuite.getLaunchTime());
        }
    }

}
