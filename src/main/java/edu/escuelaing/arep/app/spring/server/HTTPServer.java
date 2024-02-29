package edu.escuelaing.arep.app.spring.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.*;

import edu.escuelaing.arep.app.spring.component.Component;
import edu.escuelaing.arep.app.spring.component.GetMapping;



public class HTTPServer {

    public static File file;
    public static Socket clientSocket;
    public static PrintWriter out;
    public static Map<String,Method> services = new HashMap<>();
    public static final String pathToClasses = "edu/escuelaing/arep/app/spring/component";
    private static HTTPServer instance = new HTTPServer();

    private HTTPServer() {
    }

    public static HTTPServer getInstance() {
        return instance;
    }

    public void start(String[] args) throws IOException, ClassNotFoundException, InvocationTargetException, IllegalAccessException {
        inversionOfControl();
        ServerSocket serverSocket = createServerSocket();
    
        boolean running = true;
        while (running) {
            Socket clientSocket = acceptClientConnection(serverSocket);
            processClientRequest(clientSocket);
        }
    
        serverSocket.close();
    }
    
    private ServerSocket createServerSocket() {
        try {
            return new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
            return null; 
        }
    }
    
    private Socket acceptClientConnection(ServerSocket serverSocket) {
        try {
            System.out.println("Listo para recibir ...");
            return serverSocket.accept();
        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
            return null; 
        }
    }
    
    private void processClientRequest(Socket clientSocket) throws IOException, InvocationTargetException, IllegalAccessException {
        try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
    
            String request = readClientRequest(in);
            String response = handleClientRequest(request);
            sendServerResponse(out, response);
    
        } finally {
            clientSocket.close();
        }
    }
    
    private String readClientRequest(BufferedReader in) throws IOException {
        String inputLine, request = "/simple";
        String verb = "";
        boolean firstLine = true;
    
        while ((inputLine = in.readLine()) != null) {
            System.out.println("Received: " + inputLine);
            if (firstLine) {
                String[] requestTokens = inputLine.split(" ");
                if (requestTokens.length >= 2) {
                    request = requestTokens[1];
                    verb = requestTokens[0];
                }
                firstLine = false;
            }
            if (inputLine.contains("title?name")) {
            }
            if (!in.ready()) {
                break;
            }
        }
    
        return verb + " " + request;
    }
    
    private String handleClientRequest(String request) throws InvocationTargetException, IllegalAccessException {
        String[] requestParts = request.split(" ");
        String verb = requestParts[0];
        String path = requestParts[1];
    
        if ("GET".equals(verb) && services.containsKey(path)) {
            return services.get(path).invoke(null).toString();
        } else {
            return getHomeIndex();
        }
    }
    
    private void sendServerResponse(PrintWriter out, String response) {
        out.println(response);
        out.close();
    }


    public static PrintWriter getOut() {
        return out;
    }


    public static String findBoundaries(String inputString) {
        System.out.println(inputString);
        String[] parts = inputString.split(";");
        String filename = null;
        for (String part : parts) {
            if (part.trim().startsWith("filename")) {
                String[] nameParts = part.split("=");
                if (nameParts.length > 1) {
                    filename = nameParts[1].trim().replace("\"", "");
                }
            }
        }
        String path = "src\\resources\\";
        return getTheArchive(filename, path);
    }

    public static String getFileRemaster(String inputString) {
        String path = "TALLER_4_AREP\\src\\resources\\";
        return getTheArchive(inputString, path);
    }

    public static String getTheArchive(String filename, String path) {
        String completePath = path + filename;
        file = new File(completePath);
        int extensionIndex = filename.lastIndexOf(".");
        String type = extensionIndex != -1 ? filename.substring(extensionIndex + 1) : null;
        if (file.exists()) {
            System.out.println("Existe");
            try {
                switch (type) {
                    case "html":
                        return toHTML(file);
                    case "txt":
                        return toHTML(file);
                    case "js":
                        return toJs(file);

                    case "css":
                        return toCSS(file);

                    case "jpg":
                        return toImage(file, type);
                    case "jpge":
                        return toImage(file, type);

                    case "jpeg":
                        return toImage(file, type);

                    case "png":
                        return toImage(file, type);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("NO existe");
        }
        return "404";
    }


    public static String toImage(File file, String type) throws IOException {
        byte[] bytes = Files.readAllBytes(file.toPath());
        String base64 = Base64.getEncoder().encodeToString(bytes);
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<!DOCTYPE html>\r\n"
                + "<html>\r\n"
                + "    <head>\r\n"
                + "        <title>File Content</title>\r\n"
                + "    </head>\r\n"
                + "    <body>\r\n"
                + "         <center><img src=\"data:image/jpeg;base64," + base64 + "\" alt=\"image\"></center>" + "\r\n"
                + "    </body>\r\n"
                + "</html>";
    }

    public static String toHTML(File file) throws IOException {
        StringBuilder body = fromArchiveToString(file);
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<!DOCTYPE html>\r\n" + //
                "<html>\r\n" + //
                "    <head>\r\n" + //
                "        <meta charset=\"UTF-8\">\r\n" + //
                "        <title>File Adder</title>\r\n" + //
                "    </head>\r\n" + //
                "    <body>\r\n" + //
                "        <pre>" + body + "</pre>\r\n" + //
                "    </body>\r\n" + //
                "</html>";
    }

    public static String toCSS(File file) throws IOException {
        StringBuilder body = fromArchiveToString(file);
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<!DOCTYPE html>\r\n" + //
                "<html>\r\n" + //
                "    <head>\r\n" + //
                "        <meta charset=\"UTF-8\">\r\n" + //
                "        <title>File Adder</title>\r\n" + //
                "    </head>\r\n" + //
                "    <body>\r\n" + //
                "        <pre>" + body + "</pre>\r\n" + //
                "    </body>\r\n" + //
                "</html>";
    }

    public static String toJs(File file) throws IOException {
        StringBuilder body = fromArchiveToString(file);
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<!DOCTYPE html>\r\n" + //
                "<html>\r\n" + //
                "    <head>\r\n" + //
                "        <meta charset=\"UTF-8\">\r\n" + //
                "        <title>File Adder</title>\r\n" + //
                "    </head>\r\n" + //
                "    <body>\r\n" + //
                "        <pre>" + body + "</pre>\r\n" + //
                "    </body>\r\n" + //
                "</html>";
    }

    public static StringBuilder fromArchiveToString(File file) throws IOException {
        StringBuilder body = new StringBuilder();

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        String line;
        while ((line = reader.readLine()) != null) {
            body.append(line).append("\n");
        }
        reader.close();
        return body;
    }

    public static Socket getClientSocket() {
        return clientSocket;
    }


    private List<Class<?>> getClasses(){
        List<Class<?>> classes = new ArrayList<>();
        try{
            for (String cp: getClassP()){
                File file = new File(cp + "/" + pathToClasses);
                if(file.exists() && file.isDirectory()){
                    for (File cf: Objects.requireNonNull(file.listFiles())){
                        if(cf.isFile() && cf.getName().endsWith(".class")){
                            String rootTemp = pathToClasses.replace("/",".");
                            String className = rootTemp+"."+cf.getName().replace(".class","");
                            Class<?> clasS =  Class.forName(className);
                            classes.add(clasS);
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return classes;
    }

    public ArrayList<String> getClassP(){
         String classPath = System.getProperty("java.class.path");
        String[] classPaths =  classPath.split(System.getProperty("path.separator"));
        return new ArrayList<>(Arrays.asList(classPaths));
    }
    
    public  void inversionOfControl() throws ClassNotFoundException{
        List<Class<?>> classes = getClasses();
        for (Class<?> clasS:classes){
            if(clasS.isAnnotationPresent(Component.class)){
                Class<?> c = Class.forName(clasS.getName());
                Method[] m = c.getMethods();
                for (Method me: m){
                    if(me.isAnnotationPresent(GetMapping.class)){
                        String key = me.getAnnotation(GetMapping.class).value();
                        services.put(key,me);
                    }
                }
            }

        }
    }

    public static String getHomeIndex() {
        return "HTTP/1.1 200 OK\r\n"
        + "Content-Type: text/html\r\n"
        + "\r\n"
        + "<!DOCTYPE html>\n" +
        "<html>\n" +
        "    <head>\n" +
        "        <title>files</title>\n" +
        "        <meta charset=\"UTF-8\">\n" +
        "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
        "       <style>\n" +
        "           body{\n" +
        "               background-color: #f0f0ff;\n" +
        "               font-family: \"Ubuntu\",sans-serif;\n" +
        "           }\n" +
        "           h1 {\n" +
        "               text-align:center;\n" +
        "               margin-top: 50px; \n" +
        "           }\n" +
        "           label, input[type=\"text\"],input[type=\"button\"]{\n" +
        "               display: block;\n" +
        "               margin: 0 auto;\n" +
        "               text-align: center;\n" +
        "           }"+
        "       </style>" +
        "    </head>\n" +
        "    <body>\n" +
        "        <h1>files</h1>\n" +
        "        <form action=\"/file\">\n" +
        "            <label for=\"name\">Name:</label><br>\n" +
        "            <input type=\"text\" id=\"name\" name=\"name\" value=\"\"><br><br>\n" +
        "            <input type=\"button\" value=\"Submit\" onclick=\"loadGetMsg()\">\n" +
        "        </form> \n" +
        "        <div id=\"getrespmsg\"></div>\n" +
        "\n" +
        "        <script>\n" +
        "            function loadGetMsg() {\n" +
        "                let nameVar = document.getElementById(\"name\").value;\n" +
        "                const xhttp = new XMLHttpRequest();\n" +
        "                xhttp.onload = function() {\n" +
        "                    document.getElementById(\"getrespmsg\").innerHTML =\n" +
        "                    this.responseText;\n" +
        "                }\n" +
        "                xhttp.open(\"GET\", \"/file?name=\"+nameVar);\n" +
        "                xhttp.send();\n" +
        "            }\n" +
        "        </script>\n" +
        "\n" +
        "        </script>\n" +
        "    </body>\n" +
        "</html>";
    }

}