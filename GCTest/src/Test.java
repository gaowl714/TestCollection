import java.text.ParseException;
import java.util.Map;

//-verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8 -XX:+UseSerialGC
//-XX:PretenureSizeThreshold=3145728
//-XX:MaxTenuringThreshold=1 -XX:+PrintTenuringDistribution
public class Test {

    public static final int _1MB = 1024 * 1024;

    public static void main(String[] args) throws ParseException {
//        byte[] allocation1, allocation2, allocation3, allocation4;
//        allocation1 = new byte[ _1MB / 4];
//        allocation2 = new byte[4 * _1MB];
//        allocation3 = new byte[4 * _1MB];
//        allocation3 = null;
//        allocation4 = new byte[4 * _1MB];
//        for (Map.Entry<Thread, StackTraceElement[]> stackTrace : Thread.getAllStackTraces().entrySet()) {
//            Thread key = stackTrace.getKey();
//            StackTraceElement[] value = stackTrace.getValue();
//            if (key.equals(Thread.currentThread())) {
//                continue;
//            }
//            System.out.print("\n线程" + key.getName() + "\n");
//            for (StackTraceElement element : value) {
//                System.out.print("\t" + element + "\n");
//            }
//        }
        //Thread.sleep(600000);
    }



}