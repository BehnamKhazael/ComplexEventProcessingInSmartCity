import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Behnam Khazael on 4/7/2021.
 * Helper to create a complex event sequence.
 * @author Behnam Khazael
 * @version 0.1
 */
public class PointPrinter {
    public static void main(String args[]) {
        DrawUtility drawChit = new DrawUtility();


        ArrayList<ComplexEvent> complexEventArrayList = new ArrayList<ComplexEvent>();
        readAndWrite(complexEventArrayList);
        for (ComplexEvent e :
                complexEventArrayList) {
            System.out.println(e.getTargetArea().toText());
            drawChit.addShape(e.getTargetArea(), Color.black);
        }
        DrawUtility.showGui(drawChit);
    }

    private static void readAndWrite(ArrayList<ComplexEvent> list) {
        try {
//the file to be opened for reading
            FileInputStream fis = new FileInputStream("eventArea.txt");
            Scanner sc = new Scanner(fis);    //file to be scanned
//returns true if there is another line to read
            int i = 1;
            WKTReader rdr = new WKTReader();
            while (sc.hasNextLine()) {
                i++;
                ComplexEvent complexEvent = new ComplexEvent();
                complexEvent.setName("ce" + (i));
                complexEvent.setTime(System.currentTimeMillis());
                try {
                    complexEvent.setTargetArea(rdr.read(sc.nextLine()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                list.add(complexEvent);
            }
            sc.close();     //closes the scanner
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
