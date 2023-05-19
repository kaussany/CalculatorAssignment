/* @author Sanya
*This class is responsible for processing client requests received by the server.
 */
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

class RequestProcessor implements Runnable {
    private Socket socket = null;
    private OutputStream os = null;
    private BufferedReader in = null;
    private String msgToClient = "HTTP/1.1 200 OK\n"
            + "Server: HTTP server/0.1\n"
            + "Access-Control-Allow-Origin: *\n";
    private JSONObject jsonObject = new JSONObject();

    public RequestProcessor(Socket socket) {
        super();
        try {
            // Initialize the socket, input stream, and output stream
            this.socket = socket;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            os = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // This method extracts query parameters from the request URL and stores them in a HashMap.
    private Map<String, String> getQueryParams(String request) {
        Map<String, String> queryParams = new HashMap<>();
        // Split the request to separate the URL and parameters
        String[] params = request.split(" ")[1].split("\\?");

        if (params.length > 1) {
            // Split the parameters based on the '&' symbol
            String[] pairs = params[1].split("&");

            for (String pair : pairs) {
                // Split each parameter based on the '=' symbol
                String[] keyValue = pair.split("="); 
                if (keyValue.length > 1) {
                    String key = keyValue[0]; 
                    String value = keyValue[1];
                    queryParams.put(key, value);
                }
            }
        }

        return queryParams;
    }
    
    //This method processes client requests, handles query parameters, 
    //performs operations based on the parameters, construct appropriate responses
    //(JSON or HTML), and send the responses back to the client.
    public void run() {
        try {
            // Read the request from the client
            String request = in.readLine();
            String response = "";
            
            if (request != null) {
                // Parse the query parameters from the request
                Map<String, String> queryParams = getQueryParams(request);
                
                if (queryParams.size() > 0) {
                    // If there are query parameters, perform the requested operation on the operands
                    int leftOperand = Integer.parseInt(queryParams.get("leftOperand"));
                    int rightOperand = Integer.parseInt(queryParams.get("rightOperand"));
                    String operation = queryParams.get("operation");
                    
                    int result;
                    switch (operation) {
                        case "+":
                            result = leftOperand + rightOperand;
                            break;
                        case "-":
                            result = leftOperand - rightOperand;
                            break;
                        case "*":
                            result = leftOperand * rightOperand;
                            break;
                        case "/":
                            result = leftOperand / rightOperand;
                            break;
                        case "%":
                            result = leftOperand % rightOperand;
                            break;
                        default:
                            result = 0;
                            break;
                    }

                    // Prepare the JSON response
                    jsonObject.put("Expression", 
                            leftOperand + " " + operation + " " + rightOperand
                    );
                    jsonObject.put("Result", Integer.toString(result));
                    
                    // Set the content type to application/json and construct the response
                    msgToClient += "Content-type: application/json\n\n";
                    response = msgToClient + jsonObject.toString();
                } else {
                    // If there are no query parameters, read the content from an HTML file and construct the response
                    msgToClient += "Content-type: text/html\n\n";
                    File file = new File("C:\\Users\\sanya\\Desktop\\CalculatorAssignment_final\\src\\main\\java\\index.html");
                    BufferedReader fileReader = new BufferedReader(new FileReader(file));
                    StringBuilder fileContent = new StringBuilder();
                    String line;
                    while ((line = fileReader.readLine()) != null) {
                        fileContent.append(line+"\n");
                    }
                    fileReader.close();
                    response = msgToClient + fileContent.toString();
                }
            }
             
            // Send the response to the client
            os.write(response.getBytes());
            os.flush();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
