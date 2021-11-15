import google_tcp.GoogleTCP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;

public class Application {

    static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws IOException, ParseException {

        if (args.length == 0) {
            logger.error("File Directory Required");
            System.exit(-1);
        }

        String filename = args[0];
//        String filename = "/Users/xianqizhang/Downloads/CI-Datasets-master/GooglePostCleanData.out";
        int Wf = 12;
        int We = 24;

        if (args.length == 3) {
            Wf = Integer.parseInt(args[1]);
            We = Integer.parseInt(args[2]);
        }

        run(filename, Wf, We);

    }

    private static void run(String filename, int Wf, int We) {

        GoogleTCP.run(filename, 12, 24);

//        try {
//            BufferedReader br = new BufferedReader(new FileReader("results.out"));
//            while (br.ready()) {
//                System.out.println(br.readLine());
//            }
//            br.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


    }
}
