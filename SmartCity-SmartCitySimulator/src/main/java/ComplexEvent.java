import com.vividsolutions.jts.geom.Geometry;

/**
 * Created by Behnam Khazael on 2/22/2021.
 * Represents an complex event.
 * @author Behnam Khazael
 * @version 0.1
 */
public class ComplexEvent {
    private String name;
    private Long time;
    private Geometry targetArea;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Geometry getTargetArea() {
        return targetArea;
    }

    public void setTargetArea(Geometry targetArea) {
        this.targetArea = targetArea;
    }
}
