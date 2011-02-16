package test;

import org.ec.detector.ECLayer;
import org.ec.detector.ECSector;
import org.ec.detector.ECView;

public class TestDataStructures
{
    public static void main(String[] args)
    {
        ECSector s = new ECSector(4);
        
        for (ECLayer l : s.getLayerList()) {
            System.out.println("Layer: " + l.getName());
            for (ECView v : l.getViewList()) {
                v.calAtten[0] = 0.6;
                System.out.println("View: " + v.getLabel());
            }
        }
    }
}
