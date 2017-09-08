package ImageDownloadTask;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Created by haithemkahil on 31/08/17.
 */
public class JSONIOHelper {

    String sURL = "http://freegeoip.net/json/"; //just a string

    public static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    public static String readFile(String path, Charset encoding) throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
    public static ArrayList<String> getElements(String jsonContent)
    {
        ArrayList<String> results = new ArrayList<>();

        JsonElement jelement = new JsonParser().parse(jsonContent);
        JsonObject jsonObject = jelement.getAsJsonObject();
        jsonObject = jsonObject.getAsJsonObject("data");
        JsonArray jarray = jsonObject.getAsJsonArray("images");
        for (JsonElement j:jarray){

            jsonObject = j.getAsJsonObject();
            String result = jsonObject.get("imageurl").toString().replaceAll("\"","");
            results.add(result);

        }return results;
    }
}
