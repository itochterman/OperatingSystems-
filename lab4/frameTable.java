import java.util.ArrayList;

public class frameTable{
    public int numFrames;
    public int frameSize;
    public ArrayList<Frame> frameList = new ArrayList<>();

    public static class Frame{

        public boolean empty = true; 
        public int pageNumber;
        public int processNumber;
        public int timeLastUsed; 
        public int timeIn;
        public int startAdd;
        public int endAdd; 

        public Frame(){
            
        }

        public Frame(boolean empty, int pageNumber, int processNumber, int timeLastUsed, int startAdd, int endAdd){
            this.empty = empty; 
            this.pageNumber = pageNumber;
            this.processNumber = processNumber;
            this.timeLastUsed = timeLastUsed;
            this.startAdd = startAdd;
            this.endAdd = endAdd;

        }

    }

    public frameTable(){

    }

    public frameTable(int numFrames, int frameSize, int offset){
        this.numFrames = numFrames;
        this.frameSize = frameSize;
        for(int i = 0; i< numFrames; i++){
            if(i == 0){
                Frame frame = new Frame();
                frame.empty = true;
                this.frameList.add(frame);


            }
            else{
            //    Frame frame = new Frame(true, i, -1, -1, (i*this.frameSize), ((i+1)*this.frameSize)-1);
                Frame frame = new Frame();
                frame.empty = true;
                this.frameList.add(frame);

            }

         }
    }

}