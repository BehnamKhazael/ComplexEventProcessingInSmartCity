/**
 * Created by Behnam Khazael on 4/7/2021.
 * @author Behnam Khazael
 * @version 0.1
 */
public class test {
    public static void main(String args[]) {
//        String[] templateArray = "Public_Packet:{Event_Type:{12},Timestamp:{1617769532515},Attributes:{ Attribute:{ Name:area,Value_Type:STRING,Int_value:0,Float value:0.0,Bool value:false,String value:ce60,GEOMETRY value: } Attribute:{ Name:measuredtemp,Value_Type:INT,Int_value:95,Float value:0.0,Bool value:false,String value:,GEOMETRY value: }}}".split(",", 9);
//        System.out.println((templateArray[7].split(":", 2))[1]);
        for (int j = 0; j < 60; j++) {
            ComplexEvent complexEvent = new ComplexEvent();
            complexEvent.setName("ce" + (j + 1));
            complexEvent.setTime(System.currentTimeMillis());
            complexEvent.setTargetArea(SpatialUtility.createCircle(SpatialUtility.createRandomPoint(1000, 1000), 100));
            System.out.println(complexEvent.getTargetArea().toText());
        }
    }
}
