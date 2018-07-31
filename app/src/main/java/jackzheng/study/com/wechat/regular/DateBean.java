package jackzheng.study.com.wechat.regular;

import java.util.ArrayList;

public class DateBean {

    public boolean isHavaLocal = false;
    public boolean isHavaEach = false;
    public boolean isNoSame = false;

    public boolean isHavaFristNumberSpile = false;
    public boolean isHavaSecondNumberSpile = false;
    public boolean isHavaFristKill = false;
    public boolean isHavaSecondkill = false;
    public boolean isHavaFristVirgule = false;
    public boolean isHavaSecondVirgule = false;
    public boolean isHavaFristNosame = false;
    public boolean isHavaSecondNosame = false;
    public boolean isHavaFristLocal = false;
    public boolean isHavaSecondLocal = false;



    public ArrayList<Integer[]> mLastData = new ArrayList<>();
    public ArrayList<ArrayList<Integer>> mDataList = new ArrayList<>();
    public ArrayList<Integer[]> local = new ArrayList<>();

    public int mNumberCount = 0;
    public int mCount = 0;
}
