package web.helpers;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.Json;
import javax.servlet.http.HttpServletResponse;

public abstract class SendErrors {
    public static void sendErrors(HttpServletResponse response, List<String> errores) {
        response.setStatus(400);
        JsonObjectBuilder ob = Json.createObjectBuilder();
        JsonArrayBuilder ab = Json.createArrayBuilder();
        PrintWriter out;
        try {
            out = response.getWriter();
            ob.add("ok", false);
            errores.forEach(error -> {
                if(error.contains("&")) {
                    String[] subErrors = error.split("&");
                    for(String subError: subErrors) {
                        ab.add(subError);
                    }
                } else {
                    ab.add(error);
                }
            });
            ob.add("errores", ab.build());
            Writer writer = new StringWriter();
            Json.createWriter(writer).writeObject(ob.build());
            String jsonString = writer.toString();
            out.print(jsonString);
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        }
    }

}
