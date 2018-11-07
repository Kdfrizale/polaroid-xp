package frizzell.flores.polaroidxp.utils;

import android.util.Log;

public class LogHelper {

    public static class Stopwatch{
        private long mStartTime;
        private String mTitle;
        public Stopwatch() {
            this.mStartTime = System.currentTimeMillis();
        }

        public Stopwatch(String title){
            this.mTitle = title;
            this.mStartTime = System.currentTimeMillis();
        }

        public long getElaspedTime(){
            return System.currentTimeMillis() - this.mStartTime;
        }

        public void logStopwatch(){
            Log.e(mTitle,"The stopwatch for "+ mTitle +" ran for: "+ getElaspedTime() + " milliseconds");
        }
        public void logStopwatch(String description){
            Log.e(mTitle,"The stopwatch for "+ description +" ran for: "+ getElaspedTime() + " milliseconds");
        }

    }
}



