# google-tcp

This is a maven project based on java 8

To run this application, please choose format your dataset based in the datasets in https://github.com/elbaum/CI-Datasets

You can run this application by command line:

 mvn exec:java -Dexec.mainClass="Application" -Dexec.args="GooglePostCleanData.out"
 
You should pass the first argument with the directory of your data file

The default Wp = 24, Wf = 24, We = 48

If you want to customize the values of Wp, Wf, We

Please run this application by command line:

 mvn exec:java -Dexec.mainClass="Application" -Dexec.args="GooglePostCleanData.out 24 24 48"
 
 each of the value passed represents Wp, Wf, We respectively.
