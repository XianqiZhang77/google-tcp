package com.concordia.google_tcp;

import com.concordia.entity.TestSuite;
import com.concordia.entity.TestSuiteHistory;
import com.concordia.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.ParseException;
import java.util.*;

public class TCP {

    static Logger logger = LoggerFactory.getLogger(TCP.class);

    private static TestSuite prevTestSuite = null;

    private static Map<String, TestSuiteHistory> history = new HashMap<>();

    private static Queue<TestSuite> dispatchQueue = new LinkedList<>();

    private static boolean global_flag = false;

    private static Date currentTimeStamp;

    private static double window = 0;

    public static void run(File file, double Wp, int Wf, int We) {
        BufferedReader br;
        BufferedWriter bw;
        try {
            br = new BufferedReader(new FileReader(file));
            bw = new BufferedWriter(new FileWriter("TCP.out"));
            double windowP = Wp * 3600 * 1000;
            init(br);
            if (dispatchQueue.isEmpty()) {
                return;
            }
            currentTimeStamp = dispatchQueue.peek().getLaunchTime();

            while (!dispatchQueue.isEmpty()) {
                logger.info(dispatchQueue.size()+"");
                if (window < windowP || global_flag) {
                    TestSuite cur = dispatchQueue.poll();
                    assert cur != null;
                    window = window + cur.getExecutionTime();
                    currentTimeStamp = generateResult(cur, bw);
                } else {
                    List<TestSuite> testSuites = readTestSuites(br);
                    if (testSuites.size() > 0) {
                        prioritizePOSTTests(testSuites, Wf, We);
                    }
                    window = 0;
                    testSuites.clear();
                }
               if (dispatchQueue.isEmpty() && !global_flag) {
                   List<TestSuite> testSuites = readTestSuites(br);
                   for (TestSuite testSuite : testSuites) {
                       dispatchQueue.offer(testSuite);
                   }
                   testSuites.clear();
               }
            }
            br.close();
            bw.close();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private static void prioritizePOSTTests(List<TestSuite> testSuites, int Wf, int We) {

        for (TestSuite testSuite : testSuites) {
            if (!history.containsKey(testSuite.getDirectory())) {
                testSuite.setPriority(1);
            } else {
                TestSuiteHistory testSuiteHistory = history.get(testSuite.getDirectory());

                if (testSuiteHistory.isHasFailed() &&
                        (!DateUtils.checkTimeDiff(currentTimeStamp, testSuiteHistory.getPrevFailure(), Wf)
                        || DateUtils.checkTimeDiff(currentTimeStamp, testSuiteHistory.getPrevExecution(), We))) {
                    testSuite.setPriority(1);
                } else {
                    testSuite.setPriority(2);
                }
            }
        }

        PriorityQueue<TestSuite> priorityQueue = new PriorityQueue<>((t1, t2) -> {
            if (t1.getPriority() - t2.getPriority() < 0) {
                return -1;
            } else if (t1.getPriority() - t2.getPriority() > 0) {
                return 1;
            }
            return 0;
        });

        for (TestSuite testSuite : testSuites) {
            priorityQueue.offer(testSuite);
        }

        while (!priorityQueue.isEmpty()) {
            dispatchQueue.offer(priorityQueue.poll());
        }
    }

    private static List<TestSuite> readTestSuites(BufferedReader br) throws IOException, ParseException {
        List<TestSuite> testSuites = new ArrayList<>();
        if (prevTestSuite.getLaunchTime().after(currentTimeStamp)) {
            return testSuites;
        }

        testSuites.add(prevTestSuite);
        int requestNum = prevTestSuite.getRequestNumber();
        while (br.ready()) {
            TestSuite testSuite = TestSuite.parseTestSuite(br.readLine());
            if (testSuite.getRequestNumber() != requestNum) {
                if (testSuite.getLaunchTime().after(currentTimeStamp)) {
                    prevTestSuite = testSuite;
                    return testSuites;
                } else {
                    requestNum = testSuite.getRequestNumber();
                }
            }
            testSuites.add(testSuite);
        }
        if (!br.ready()) {
            global_flag = true;
        }
        return testSuites;
    }

    private static Date generateResult(TestSuite cur, BufferedWriter bw) throws IOException {
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

    private static void init(BufferedReader br) throws IOException, ParseException {
        int requestNum = -1;
        if (br.ready()) {
            TestSuite testSuite = TestSuite.parseTestSuite(br.readLine());
            requestNum = testSuite.getRequestNumber();
            dispatchQueue.offer(testSuite);
        }

        while (br.ready()) {
            TestSuite testSuite = TestSuite.parseTestSuite(br.readLine());
            if (testSuite.getRequestNumber() != requestNum) {
                prevTestSuite = testSuite;
                break;
            }
            dispatchQueue.offer(testSuite);
        }
    }

}
