package ImageProcessing;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static imageprinting.PrintActionListener.readyToBePrintedImages;


/**
 * Created by haithemkahil on 28/08/17.
 */
public class ImageManipulationManager implements Runnable{

    public static ArrayList<BufferedImage> mainImages ;
    public static BufferedImage frameImage;

    public static int size;
    public static int NEXT_MAIN_IMAGE_INDEX;
    public int IMG_WIDTH = 600;
    public int IMG_HEIGHT = 600;



    public BufferedImage getNextMainImage(){
        synchronized (this){
                try{
                    BufferedImage image = mainImages.get(NEXT_MAIN_IMAGE_INDEX+1);
                    NEXT_MAIN_IMAGE_INDEX ++;
                    return image;
                }catch (Exception ex){return null;}

        }
    }

    public void setNewImageToBePrinted(BufferedImage imageToBePrinted){
        synchronized (this){
            readyToBePrintedImages.add(imageToBePrinted);
        }
    }

    public BufferedImage resizeImage(BufferedImage originalImage, int type){
        BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
        g.dispose();

        return resizedImage;
    }

    private BufferedImage resizeImageWithHint(BufferedImage originalImage, int type){

        BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
        g.dispose();
        g.setComposite(AlphaComposite.Src);

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        return resizedImage;
    }

    public BufferedImage joinBufferedImage(BufferedImage img1,BufferedImage img2) {

        //do some calculate first
        int offset  = 0;
        int height = img1.getHeight()+img2.getHeight()+offset;
        int wid = Math.max(img1.getWidth(),img2.getWidth())+offset;
        //create a new buffer and draw two image into the new image
        BufferedImage newImage = new BufferedImage(wid,height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = newImage.createGraphics();
        Color oldColor = g2.getColor();
        //fill background
        g2.setPaint(Color.WHITE);
        g2.fillRect(0, 0, wid, height);
        //draw image
        g2.setColor(oldColor);
        g2.drawImage(img1, null, 0, 0);
        g2.drawImage(img2, null, 0, img1.getHeight()+offset);
        g2.dispose();
        return newImage;
    }

    @Override
    public void run() {
        while(true){
            BufferedImage mainImg = getNextMainImage();
            if(mainImg==null){}
            else
            {
                IMG_WIDTH = 600;
                IMG_HEIGHT = 600;
                int type = mainImg.getType() == 0? BufferedImage.TYPE_INT_ARGB : mainImg.getType();
                BufferedImage resizedMainImage = resizeImage(mainImg, type);
                IMG_HEIGHT = 200;
                int type2 = frameImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : frameImage.getType();
                BufferedImage resizedFrameImage = resizeImage(frameImage, type2);
                BufferedImage joinedImg = joinBufferedImage(resizedMainImage,resizedFrameImage);
                setNewImageToBePrinted(joinedImg);

            }
        }

    }
}
