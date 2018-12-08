package frizzell.flores.polaroidxp.singleton;

import java.util.Vector;

public class ActiveWorkRepo {
    private final String TAG = getClass().getSimpleName();
    private static ActiveWorkRepo instance = null;
    public Vector<String> activeWork = new Vector<String>();
    private ActiveWorkRepo(){}

    public static synchronized ActiveWorkRepo getInstance(){
        if(instance == null){
            instance = new ActiveWorkRepo();
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
