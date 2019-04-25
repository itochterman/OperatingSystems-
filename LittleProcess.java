public class LittleProcess{

    public double  a;
    public double  b;
    public double  c; 

    public int faults;
    public int residency; 
    public int numReferences; 
    public int currRef; 
    public int pNumber; 
    public double rand; 
    public int lastRef; 
    public boolean started = false; 

    public LittleProcess(){
    }

    public LittleProcess(double a, double b, double c, int num){
        this.a = a; 
        this.b = b;
        this.c = c; 
        this.pNumber = num;
    }
}