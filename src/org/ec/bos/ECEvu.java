/**
 * 
 */
package org.ec.bos;

/**
 * @author smancill
 *
 */
public class ECEvu
{
    private int ID;
    private double TDC;
    private double ADC;

    public ECEvu(int ID, double TDC, double ADC)
    {
        this.ID  = ID;
        this.TDC = TDC;
        this.ADC = ADC;
    }
    
    public int getID()
    {
        return ID;
    }
    
    
    public double getADC()
    {
        return ADC;
    }
    
    
    public double getTDC()
    {
        return TDC;
    }
}
