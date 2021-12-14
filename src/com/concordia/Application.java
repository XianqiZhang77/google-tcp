package com.concordia;

import com.concordia.analysis.Measures;
import com.concordia.fifo.FIFO;
import com.concordia.google_tcp.TCP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Application {

    static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {

        if (args.length == 0) {
            logger.error("Please input the directory of the data file");
            System.exit(-1);
        }

        String Wp = "2";
        String Wf = "12";
        String We = "24";

        String filename = args[0];

        if (args.length == 1) {
            logger.info("The default value of Wp is " + Wp);
            logger.info("The default value of Wf is " + Wf);
            logger.info("The default value of We is " + We);
        }

        if (Double.parseDouble(args[1]) >= 3) {
            logger.error("Wp should be less than 3h due to the limitation size of dispatch Queue");
            System.exit(-1);
        }

        if (args.length == 2) {
            logger.info("You set the value of Wp as " + args[1]);
            logger.info("The default value of Wf is " + Wf);
            logger.info("The default value of We is " + We);
            Wp = args[1];
        }

        if (args.length == 3) {
            logger.info("You set the value of Wp as " + args[1]);
            logger.info("You set the value of Wf as " + args[2]);
            logger.info("The default value of We is " + We);
            Wp = args[1];
            Wf = args[2];
        }

        if (args.length >= 4) {
            logger.info("You set the value of Wp as " + args[1]);
            logger.info("You set the value of Wf as " + args[2]);
            logger.info("You set the value of We as " + args[3]);
            Wp = args[1];
            Wf = args[2];
            We = args[3];
        }

        run(filename, Wp, Wf, We);

    }

    private static void run(String filename, String Wp, String Wf, String We) {
        double wp = 0.0;
        double wf = 0.0;
        double we = 0.0;
        try {
            wp = Double.parseDouble(Wp);
            wf = Double.parseDouble(Wf);
            we = Double.parseDouble(We);
        } catch (Exception ex) {
            logger.error("The format of Wp, We or Wf you input is illegal, please input a number");
            System.exit(-1);
        }

        File file = new File(filename);
        TCP.run(file, wp, wf, we);
        FIFO.run(filename);
        logger.info("Prioritization Finished");
        Measures.analyze(filename);
    }
}
