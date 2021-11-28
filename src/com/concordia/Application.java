package com.concordia;

import com.concordia.analysis.Measures;
import com.concordia.fifo.FIFO;
import com.concordia.google_tcp.TCP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

public class Application {

    static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws IOException, ParseException {

        if (args.length < 2) {
            logger.error("Please input the filename and the value of Wp");
            System.exit(-1);
        }

        run(args[0], args[1]);

    }

    private static void run(String filename, String Wp) {
        double wp = Double.parseDouble(Wp);
        File file = new File(filename);
        System.out.println(file.getName());
        TCP.run(file, wp, 12, 24);
        FIFO.run(filename);
        Measures.analyze(filename);
    }
}
