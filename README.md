# google-tcp

This is a maven project based on java 8

To run this application, please choose format your dataset based in the datasets in https://github.com/elbaum/CI-Datasets

You can run this application by command line:

 mvn exec:java -Dexec.mainClass="Application" -Dexec.args="GooglePostCleanData.out"
 
You should pass the first argument with the directory of your data file.

The default Wp = 2, Wf = 12, We = 24

Wp should be less than 3h due to the limitation size of dispatch queue.

If you want to customize the values of Wp, Wf, We

Please run this application by command line:

 mvn exec:java -Dexec.mainClass="Application" -Dexec.args="GooglePostCleanData.out 2 12 24"
 
 each of the value passed represents Wp, Wf, We respectively.
