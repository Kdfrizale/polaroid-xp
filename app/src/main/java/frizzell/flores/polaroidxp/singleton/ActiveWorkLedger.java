package frizzell.flores.polaroidxp.singleton;

import java.util.Vector;

public class ActiveWorkLedger {
    private static ActiveWorkLedger instance = null;
    public Vector<String> activeWork = new Vector<String>();
    private ActiveWorkLedger(){}

    public static synchronized ActiveWorkLedger getInstance(){
        if(instance == null){
            instance = new ActiveWorkLedger();
        }
        return instance;
    }

    public Vector<String> getActiveWork(){
        return this.activeWork;
    }

    public void addActiveWork(String activeWorkKey){
        synchronized (activeWork){
            activeWork.add(activeWorkKey);
        }
    }

    public void removeActiveWork(String activeWorkKey){
        synchronized (activeWork){
            activeWork.remove(activeWorkKey);
        }
    }

}
