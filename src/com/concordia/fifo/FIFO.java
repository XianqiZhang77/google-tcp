package com.concordia.fifo;

import com.concordia.entity.TestSuite;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FIFO {

    private static boolean global_flag = false;

    private static TestSuite prevTestSuite = null;

    private static Date currentTimeStamp;

    public static void run(String directory) {
        BufferedReader br;
        BufferedWriter bw;
        List<TestSuite> testSuites = new ArrayList<>();
        try {
            br = new BufferedReader(new FileReader(directory));
            bw = new BufferedWriter(new FileWriter("FIFO.out"));
            while (!global_flag) {
                testSuites.clear();
                testSuites = readTestSuites(directory, br);

                if (testSuites.size() > 0) {
                    if (currentTimeStamp == null) {
                        currentTimeStamp = testSuites.get(0).getLaunchTime();
                    }
                    for (TestSuite cur : testSuites) {
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
        int exeTime = executionTime.intValue();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTimeStamp);
        calendar.add(Calendar.MILLISECOND, exeTime);

        cur.setLaunchTime(currentTimeStamp);
        bw.write(cur.toString());
        bw.newLine();
        bw.flush();
        return calendar.getTime();
    }

    private static List<TestSuite> readTestSuites(String directory, BufferedReader br) throws IOException, ParseException {
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
