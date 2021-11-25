package com.concordia.google_tcp;

import com.concordia.entity.TestSuite;
import com.concordia.entity.TestSuiteHistory;

import java.io.*;
import java.text.ParseException;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoogleTCP {

    static Logger logger = LoggerFactory.getLogger(GoogleTCP.class);

    private static Map<String, TestSuiteHistory> history = new HashMap<>();

    private static TestSuite prevTestSuite = null;

    private static boolean global_flag = false;

    public static void run(String filename, int Wf, int We) {

        BufferedReader br;
        BufferedWriter bw;
        try {
            br = new BufferedReader(new FileReader(filename));
            bw = new BufferedWriter(new FileWriter("results.out"));

            List<TestSuite> testSuites = new ArrayList<>();

            while (!global_flag) {
                testSuites.clear();
                testSuites = readTestSuites(filename, br);
                if (testSuites.size() > 0) {
                    Date currentTimeStamp = testSuites.get(0).getLaunchTime();
                    PriorityQueue<TestSuite> priorityQueue = prioritizePOSTTests(testSuites, Wf, We);
                    while (!priorityQueue.isEmpty()) {
                        TestSuite cur = priorityQueue.poll();
                        currentTimeStamp = generateResult(cur, currentTimeStamp, bw);
                    }
                }
            }
            br.close();
            bw.close();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private static Date generateResult(TestSuite cur, Date currentTimeStamp, BufferedWriter bw) throws IOException {
        Double executionTime = cur.getExecutionTime();
        String directory = cur.getDirectory();
        String status = cur.getStatus();
        int exeTime = executionTime.intValue();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTimeStamp);
        calendar.add(Calendar.MILLISECOND, exeTime);

        if (!history.containsKey(directory)) {
            TestSuiteHistory suiteHistory = new TestSuiteHistory();
            suiteHistory.setPrevExecution(currentTimeStamp);
            if (status.equals("FAILED")) {
                suiteHistory.setHasFailed(true);
                suiteHistory.setPrevFailure(currentTimeStamp);
            }
            history.put(directory, suiteHistory);
        } else {
            TestSuiteHistory suiteHistory = history.get(directory);
            suiteHistory.setPrevExecution(currentTimeStamp);
            if (status.equals("FAILED")) {
                suiteHistory.setHasFailed(true);
                suiteHistory.setPrevFailure(currentTimeStamp);
            }
            history.put(directory, suiteHistory);
        }

        cur.setLaunchTime(currentTimeStamp);
        bw.write(cur.toString());
        bw.newLine();
        bw.flush();
        return calendar.getTime();
    }

    private static PriorityQueue<TestSuite> prioritizePOSTTests(List<TestSuite> testSuites, int Wf, int We) {

        Date launchTime = testSuites.get(0).getLaunchTime();
        for (TestSuite testSuite : testSuites) {
            if (!history.containsKey(testSuite.getDirectory())) {
                testSuite.setPriority(1);
            } else {
                TestSuiteHistory testSuiteHistory = history.get(testSuite.getDirectory());
                if (launchTime.before(testSuiteHistory.getPrevExecution())) {
                    testSuite.setPriority(1);
                } else {
                    if ((testSuiteHistory.isHasFailed() && !checkTimeDiff(launchTime, testSuiteHistory.getPrevFailure(), Wf)) || checkTimeDiff(launchTime, testSuiteHistory.getPrevExecution(), We)) {
                        testSuite.setPriority(1);
                    } else {
                        testSuite.setPriority(2);
                    }
                }
            }
        }

        PriorityQueue<TestSuite> priorityQueue = new PriorityQueue<>((t1, t2) -> {
            if (t1.getPriority() != t2.getPriority()) {
                return t2.getPriority() - t1.getPriority();
            }
            return t1.getLaunchTime().before(t2.getLaunchTime()) ? -1 : 1;
        });

        for (TestSuite testSuite : testSuites) {
            priorityQueue.offer(testSuite);
        }
        return priorityQueue;
    }

    private static boolean checkTimeDiff(Date launchTime, Date prevTime, int W) {
        Calendar prevCalendar = Calendar.getInstance();
        prevCalendar.setTime(prevTime);
        prevCalendar.add(Calendar.HOUR, W);
        Date nowTime = prevCalendar.getTime();
        return nowTime.before(launchTime);
    }


    private static List<TestSuite> readTestSuites(String filename, BufferedReader br) throws IOException, ParseException {

        int prevRequestNum = -1;
        List<TestSuite> res = new ArrayList<>();
        if (prevTestSuite != null) {
            res.add(prevTestSuite);
            prevRequestNum = prevTestSuite.getRequestNumber();
        }

        boolean flag = false;
        while (br.ready()) {
            TestSuite testSuite = TestSuite.parseTestSuite(br.readLine());
            if (prevTestSuite == null && !flag) {
                prevRequestNum = testSuite.getRequestNumber();
                flag = true;
            }

            if (testSuite.getRequestNumber() != prevRequestNum) {
                prevTestSuite = testSuite;
                return res;
            }

            res.add(testSuite);
        }
        if (!br.ready()) {
            global_flag = true;
        }
        return res;
    }
}
