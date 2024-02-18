package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import edu.brown.cs.student.main.server.handlers.CSVHandling.CSVData;
import edu.brown.cs.student.main.server.handlers.CSVHandling.LoadCSVHandler;
import edu.brown.cs.student.main.server.handlers.CSVHandling.SearchCSVHandler;
import edu.brown.cs.student.main.server.handlers.CSVHandling.ViewCSVHandler;
import edu.brown.cs.student.main.server.handlers.broadband.data.ACSData;
import edu.brown.cs.student.main.server.handlers.broadband.BroadbandHandler;
import edu.brown.cs.student.main.server.handlers.broadband.data.StateCountyInit;
import spark.Spark;

/**
 * Top-level class for this demo. Contains the main() method which starts Spark and runs the various
 * handlers (4).
 */
public class Server {

  /**
   * The main method for this program. It handles the four endpoint connections and sets up the
   * initial port. It also displays a method with a link to the server.
   *
   * @param args
   */
  public static void main(String[] args) {
    int port = 2004;
    Spark.port(port);

    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    CSVData csvHandler = new CSVData(null);

    Spark.get("broadband", new BroadbandHandler(new ACSData(new StateCountyInit())));
    Spark.get("loadcsv", new LoadCSVHandler(csvHandler));
    Spark.get("searchcsv", new SearchCSVHandler(csvHandler));
    Spark.get("viewcsv", new ViewCSVHandler(csvHandler));

    Spark.init();
    Spark.awaitInitialization();

    System.out.println("Server started at http://localhost:" + port);
  }
}
