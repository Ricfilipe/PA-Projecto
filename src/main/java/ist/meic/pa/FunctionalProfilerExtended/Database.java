/*
 * Class that keeps the records of CompileTime Classes,
 * responsible for incrementing counters and printing the results.
 */
package ist.meic.pa.FunctionalProfilerExtended;

import java.util.HashMap;
import java.util.Map;

@NotIntersect
public class Database {
    public static Map<Class, Entry> dictionary = new HashMap<>();

    public static String toText(){
        CommandReadWrite cmdReadWrite= new CommandReadWrite();

        String buffer= "";
        for(Class c: dictionary.keySet()){
            buffer= buffer + c+" ->" + cmdReadWrite.sumText(c) + "\n";
        }

        return cmdReadWrite.totalText() + "\n"
                + buffer;
    }

    public static int getReadCounter(Class c) {
        return dictionary.get(c);
    }

    public static int getWriteCounter(Class c) {
        return dictionary.get(c);
    }
}
