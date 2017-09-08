package ImageDownloadTask;

import ImageProcessing.ImageManipulationManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static ImageProcessing.ImageManipulationManager.mainImages;

/**
 * Created by haithemkahil on 30/08/17.
 */
public class ImageDownloaderManager implements Runnable {

    public static ArrayList<String> mainImageUrls;

    public static int NEXT_DOWNLOADABLE_URL_INDEX;
    public static int THREAD_DOWNLOADER_NUMBER = 10;
    public static String JSON_URL;
    public static boolean EXTRACTING_URLS;

    public String getNextDownloadableUrl(){
        synchronized (this){

            try{
                String url = mainImageUrls.get(NEXT_DOWNLOADABLE_URL_INDEX+1);
                NEXT_DOWNLOADABLE_URL_INDEX++;
                return url;
            }catch (Exception ex ){return null;}
        }
    }



    public void extractUrls(){
        synchronized (this){
                EXTRACTING_URLS = true;
                if (JSON_URL.isEmpty()){}
                else
                    try {
                        System.out.println(JSON_URL);
                        ArrayList<String > results = JSONIOHelper.getElements(JSONIOHelper.readFile(JSON_URL,Charset.defaultCharset()));
                        for(String st : results)if (mainImageUrls.contains(st)){}else {System.out.println(st);mainImageUrls.add(st);}
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                EXTRACTING_URLS = false;

        }
    }

    public void setNewMainImageToBeJoined(BufferedImage downloadedImage){
        synchronized (this){
            mainImages.add(downloadedImage);
            ImageManipulationManager.size ++;
        }
    }

    public BufferedImage downloadImage(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        BufferedImage img = ImageIO.read(url);
        return img;
    }

    @Override
    public void run() {
        while (true) {
            if(EXTRACTING_URLS){}
            else{extractUrls();}
            String url = getNextDownloadableUrl();
            if (url==null) {
            } else {

                try {
                    BufferedImage downloaded = downloadImage(url);
                    setNewMainImageToBeJoined(downloaded);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }
    }
}
