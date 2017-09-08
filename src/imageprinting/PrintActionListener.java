package imageprinting;

/**
 * Created by haithemkahil on 28/08/17.
 */
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.HashMap;
public class PrintActionListener implements Runnable {

    public static ArrayList<BufferedImage> readyToBePrintedImages;
    public static PrintService printService;

    public static int NEXT_IMAGE_TO_PRINT;
    public static int NEXT_IMAGE_TO_DISPLAY;

    public PrintActionListener() {
        readyToBePrintedImages = new ArrayList<>();
        NEXT_IMAGE_TO_PRINT = -1;
    }


    public BufferedImage getNextImageToPrint(){
        synchronized (this){
            try{
                BufferedImage img = readyToBePrintedImages.get(NEXT_IMAGE_TO_PRINT+1);
                NEXT_IMAGE_TO_PRINT++;
                return img;
            }catch (Exception ex ){return null;}
        }
    }

    public static BufferedImage getNextImageToDisplay(){
        try{
            BufferedImage img = readyToBePrintedImages.get(NEXT_IMAGE_TO_DISPLAY+1);
            NEXT_IMAGE_TO_DISPLAY++;
            return img;
        }catch (Exception ex ){return null;}
    }




    public static void selectPrinter (MenuButton menuButton)
    {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        System.out.println("Number of print services: " + printServices.length);

        if(printServices.length>=0){
            HashMap<String,PrintService> printers = new HashMap<>();
            for (PrintService printer : printServices)  {
                printers.put(printer.getName(),printer);
                MenuItem menuItem = new MenuItem(printer.getName());
                menuItem.setOnAction(event -> {
                    printService = printers.get(menuItem.getText());
                    menuButton.setText(menuItem.getText());
                });
                menuButton.getItems().add(menuItem);
            }

        }else printService = null;
    }

    @Override
    public void run() {
        PrinterJob printJob = PrinterJob.getPrinterJob();
        try {
            PrintService p =printService;
            if(!p.equals(null ))printJob.setPrintService(p); else{
                System.out.println("Printing Thread Stopped .");
                Thread.currentThread().stop();
            }

        } catch (PrinterException e) {
            e.printStackTrace();
        }
        while(true){

            BufferedImage image = getNextImageToPrint();
            if(image.equals(null)){
            }else {
                printJob.setPrintable(new ImagePrintable(printJob, image));

                if (printJob.printDialog()) {
                    try {
                        printJob.print();
                    } catch (PrinterException prt) {
                        prt.printStackTrace();
                    }
                }
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



}
