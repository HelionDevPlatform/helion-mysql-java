/* ============================================================================
 (c) Copyright 2014 Hewlett-Packard Development Company, L.P.
Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights to
use, copy, modify, merge,publish, distribute, sublicense, and/or sell copies of
the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
============================================================================ */

package org.hp.samples;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DriverManager;

public class MysqlServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setStatus(200);
        PrintWriter writer = response.getWriter();
        writer.println("MySQL with Java \n");

        
        
        Connection dbConnection = null;
        
        writer.println("Connecting to MySQL using MYSQL environment variable...");
        String mysql_url = System.getenv("MYSQL_URL");
        
        
        if (mysql_url != null && mysql_url.length() > 0) {
        	try {
        		URI dbUri = new URI(System.getenv("MYSQL_URL"));

        	    String user = dbUri.getUserInfo().split(":")[0];
        	    String password = dbUri.getUserInfo().split(":")[1];
        	    int port = dbUri.getPort();
        	    if( port == -1) {
        	    	port = 3306;
        	    }
        	    
        	    String dbUrl = "jdbc:mysql://" + dbUri.getHost() + ':' + port + dbUri.getPath();
        	    
        	    // Connect to MySQL
				
				
				Class.forName("com.mysql.jdbc.Driver");
				dbConnection = DriverManager.getConnection(dbUrl, user, password);
				executeDbCheck(writer, dbConnection);
				dbConnection.close();
        	}
        	catch (Exception e) {
	            System.out.println("Caught error: ");
	            e.printStackTrace();
        	}
        	
        } else {
        	writer.println("Unable to connect using MYSQL_URL environment variable. Please ensure that it has been set.");
        }

        
        writer.println("Connecting to MySQL using mysql settings in VCAP_SERVICES environment variable...");
        String vcap_services = System.getenv("VCAP_SERVICES");

        if (vcap_services != null && vcap_services.length() > 0) {
            try {
                // Use a JSON parser to get the info we need from  the
                // VCAP_SERVICES environment variable. This variable contains
                // credentials for all services bound to the application.
                // In this case, MySQL is the only bound service.
                JsonRootNode root = new JdomParser().parse(vcap_services);

                JsonNode mysqlNode = root.getNode("mysql");
                JsonNode credentials = mysqlNode.getNode(0).getNode("credentials");

                // Grab login info for MySQL from the credentials node
                String dbname = credentials.getStringValue("name");
                String hostname = credentials.getStringValue("hostname");
                String user = credentials.getStringValue("user");
                String password = credentials.getStringValue("password");
                String port = credentials.getNumberValue("port");

                String dbUrl = "jdbc:mysql://" + hostname + ":" + port + "/" + dbname;

                

                Class.forName("com.mysql.jdbc.Driver");
                dbConnection = DriverManager.getConnection(dbUrl, user, password);
                
                executeDbCheck(writer, dbConnection);
                dbConnection.close();
            } catch (Exception e) {
                System.out.println("Caught error: ");
                e.printStackTrace();
            }
        } else {
        	writer.println("Unable to connect to MySQL using VCAP_SERVICES environment variable. Please ensure that VCAP_SERVICES and mysql information is correctly configured as shown at http://docs.hpcloud.com/als/v1/user/services/data-services/#vcap-services-jumplink-span, it is required to connect to the MySQL database.");
        }

        writer.close();
    }
    
    /**
     * simple database sanity check.
     * @param writer
     * @param dbConnection
     * @throws SQLException 
     */
    public void executeDbCheck(PrintWriter writer, Connection dbConnection) throws SQLException { 
	    if (dbConnection != null && !dbConnection.isClosed()) {
            writer.println("Connected to MySQL!");

            // creating a database table and populating some values
            Statement statement = dbConnection.createStatement();

            ResultSet rs = statement.executeQuery("SELECT \"Hello World!\"");
            writer.println("Executed query \"SELECT \"Hello World!\"\".");

            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();

            while (rs.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(",  ");
                    String columnValue = rs.getString(i);

                    // Since we are selecting a string literal, the column
                    // value and column name are both the same. The values
                    // could be retrieved with the line commented out below.
                    //writer.println("Column value: " + columnValue + " column name " + rsmd.getColumnName(i));

                    writer.println("Result: " + columnValue);
                }
            }

            statement.close();
        } else {
            writer.println("Failed to connect to MySQL");
        }
   }
}
