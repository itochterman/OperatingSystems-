import java.util.*;

import java.io.File;

public class Machine{

    Scanner randomIn;

    public ArrayList <LittleProcess> pList = new ArrayList<>(); 

    public int clock; 
    public int totalClock; 


    public int machineSize;
    public int pageSize;
    public int processSize;
    public int numRef;
    public String repAlgo; 
    public frameTable frameTable; 

    public Machine(){


    }

    double a = 0;
    double b = 0; 
    double c = 0; 


    public int numProcesses; 
    public static void main(String[] args){

        if(args.length < 6 || args.length > 6){
            System.out.println("No can do. You have to enter 6 command line arguments.");
            System.exit(1);
        }
        Machine machine = new Machine(); 

        machine.machineSize = Integer.parseInt(args[0]);
        machine.pageSize = Integer.parseInt(args[1]);
        machine.processSize = Integer.parseInt(args[2]);
        machine.numRef = Integer.parseInt(args[4]);

        machine.frameTable = new frameTable(machine.machineSize/machine.pageSize, machine.pageSize, 111 % machine.processSize);

        switch(Integer.parseInt(args[3])){

            case 1: 
                machine.pList.add(new LittleProcess(1, 0, 0, 1));

            break;
            case 2: 
                for(int i = 0; i<4; i++){
                    machine.pList.add(new LittleProcess(1, 0, 0, i+1));

                }
            break;
            case 3: 

                for(int i = 0; i<4; i++){
                    machine.pList.add(new LittleProcess(0, 0, 0, i+1));
                    
                }

            break;
            case 4: 
                machine.pList.add(new LittleProcess(.75, .25, 0, 1));
                machine.pList.add(new LittleProcess(.75, 0, .25, 2));
                machine.pList.add(new LittleProcess(.75, .125, .125, 3));
                machine.pList.add(new LittleProcess(.5, .125, .125, 4)); 
            break;

        }

        for(int z = 0; z< machine.pList.size(); z++){
            machine.pList.get(z).numReferences = machine.numRef;
        }


        machine.repAlgo = args[5];

        machine.populateRand();
        runSimulation(machine);

        for(int j = 0; j<machine.pList.size(); j++){
            System.out.printf("Process %d had %d faults \n", (j+1), machine.pList.get(j).faults);
        }


    }
    void populateRand(){
         
        try{
            this.randomIn = new Scanner(new File("random-numbers.txt"));
        } catch (Exception e){
            System.out.println("Can't find file random-numbers.txt. Make sure it's in the current directory.");
            System.exit(1);
        }

     
    }

    public static void runSimulation(Machine machine){

        int i = 0; 
        int currentAdd = 111 % machine.processSize;
        boolean pageFault = true; 

        while(!allReferComplete(machine)){

 

            pageFault = true; 
            

            if(machine.clock == 3 || machine.pList.get(i).numReferences == 0){
                if(i != machine.pList.size()-1 ){
                    i++; 
                }
                else{
                    i = 0;
                }
                if(machine.pList.get(i).started){
                    currentAdd = machine.pList.get(i).lastRef;
                }
                else{
                    currentAdd = (111 * (i+1)) % machine.processSize;
                }
                machine.clock = 0; 

            }
          
            LittleProcess currentP = machine.pList.get(i);
            currentP.started = true; 

            System.out.println("\nprocess " + currentP.pNumber + " references word " + currentAdd + " at time " + (machine.totalClock+1));



            for(int j = 0; j< machine.frameTable.frameList.size(); j++){

                frameTable.Frame currFrame = machine.frameTable.frameList.get(j);

                if(currentAdd >= currFrame.startAdd && currentAdd <= currFrame.endAdd){
                    
                    if(currFrame.processNumber == currentP.pNumber){
                        currFrame.timeLastUsed = machine.totalClock;
                        //a page fault has not occurred

                         pageFault = false;
                    } 
                
                }
            }

            int pageNumber = currentAdd/machine.pageSize;

            if(pageFault){
                currentP.faults++;
                //use corresponding algorithm to add page and/or kick out page 
                replacePage(machine, currentP, pageNumber);

            }
            else{
                System.out.println("");
            }
            currentP.numReferences--;

            currentAdd = machine.calcNextAddr(currentP, currentAdd);

            machine.pList.get(i).lastRef = currentAdd;
    
            machine.clock++;
            machine.totalClock++;
        }


    }

    public int calcNextAddr(LittleProcess p, int currWord){

        int processSize = this.processSize;
        double y = (double)this.randomIn.nextInt();    
        p.rand = y;   
        int RAND_MAX = (Integer.MAX_VALUE);
        y = y/(RAND_MAX + 1.0);
        System.out.println("Y is: " + y);

        System.out.println("Why no? Cuz = " + p.a);


        if(y < p.a){
            System.out.println("HAHAHAHAHAHAHAHAHAHAHAHAHHAHAH");
            return ((currWord + 1 + processSize) % processSize);
        }
                
        else if(y < (p.a + p.b)){
            return (((currWord -5) + processSize) % processSize);
        }
        else if(y < p.a + p.b + p.c){
            return ((currWord + 4 + processSize) % processSize); 
        }
        else{
            int rand = this.randomIn.nextInt();
            int num = rand % processSize;
            System.out.println("Next ref " + num);
            System.out.println("By using random:  " + rand);

            return num;

        }

    }

    public static boolean allReferComplete(Machine machine){
        for(int i = 0; i< machine.pList.size(); i++){
            if (machine.pList.get(i).numReferences > 0){
                return false; 
            }
        }
        System.out.println("");
        return true; 
    }

    public static void replacePage(Machine machine, LittleProcess p, int pageNumber){

        int frameNumber = 0; 
        frameTable.Frame tempFrame = new frameTable.Frame();
        ArrayList<frameTable.Frame> frameList = new ArrayList<>();
        ArrayList<frameTable.Frame> machineList = machine.frameTable.frameList;


        boolean success = false; 
        String algorithm = machine.repAlgo;

        for(int index = 0; index < machineList.size(); index++){
            frameList.add(machineList.get(index));
        }

        for(int i = 0; i<frameList.size(); i++){

            if(frameList.get(i).empty){
                success = true;
                frameNumber = i; 
                tempFrame = frameList.get(i); 
            }
        }
        if(success){
            tempFrame.empty = false;
            tempFrame.processNumber = p.pNumber;
            tempFrame.timeLastUsed = machine.totalClock;
            tempFrame.timeIn = machine.totalClock; 
            tempFrame.pageNumber = pageNumber; 
            tempFrame.startAdd = (pageNumber) * machine.pageSize;
            tempFrame.endAdd = ((pageNumber+1) *machine.pageSize)-1;
        }

        else{
            //then a page must be kicked out 
            switch(algorithm){
                case "lru":
                    int minTime= Integer.MAX_VALUE;
                    frameTable.Frame lru = new frameTable.Frame();
                    for(int in = 0; in < machineList.size(); in++){
                        System.out.println("Time last used: " + machineList.get(in).timeLastUsed);
                    }

                    for(int j = 0; j < machineList.size(); j++){
                        if(machineList.get(j).timeLastUsed < minTime){
                            minTime = machineList.get(j).timeLastUsed;
                            lru = machineList.get(j);
                            frameNumber = j;
                        }
                    }
                    //evict the last recently used page
                    lru.processNumber = p.pNumber;
                    lru.timeLastUsed = machine.totalClock;
                    lru.pageNumber = pageNumber; 
                    lru.startAdd = (pageNumber) * machine.pageSize;
                    lru.endAdd = ((pageNumber+1) *machine.pageSize)-1;
                    break;

                case "random":
                    int y = (int)machine.randomIn.nextInt();
                    System.out.println("Process " + p.pNumber + " uses random:  " + y);
                    int numFrames = machine.frameTable.frameList.size();
                    y = y % numFrames;
                    frameTable.Frame frameToReplace = machine.frameTable.frameList.get(y);
                    frameToReplace.processNumber = p.pNumber;
                    frameToReplace.timeLastUsed = machine.totalClock;
                    frameToReplace.pageNumber = pageNumber; 
                    frameToReplace.startAdd = (pageNumber) * machine.pageSize;
                    frameToReplace.endAdd = ((pageNumber+1) *machine.pageSize)-1;
                    frameNumber = y; 

                    break;
                case  "fifo":
                    int arrTime = Integer.MAX_VALUE;
                    frameTable.Frame firstIn = new frameTable.Frame();
           
                    for(int j = 0; j < machineList.size(); j++){
                        if(machineList.get(j).timeIn < arrTime){
                            arrTime = machineList.get(j).timeIn;
                            firstIn = machineList.get(j);
                            frameNumber = j;
                        }
                    }
                    //evict the last recently used page
                    firstIn.processNumber = p.pNumber;
                    firstIn.timeLastUsed = machine.totalClock;
                    firstIn.timeIn = machine.totalClock;
                    firstIn.pageNumber = pageNumber; 
                    firstIn.startAdd = (pageNumber) * machine.pageSize;
                    firstIn.endAdd = ((pageNumber+1) *machine.pageSize)-1;
                    //if the input is "fifo"
                    break; 
            }
        }
        System.out.print(": Fault, using frame number " + frameNumber + "\n");
    }

}
