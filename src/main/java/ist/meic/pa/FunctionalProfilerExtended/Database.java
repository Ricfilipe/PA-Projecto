/*
 * Class that keeps the records of CompileTime Classes,
 * responsible for incrementing counters and printing the results.
 */
package ist.meic.pa.FunctionalProfilerExtended;

import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.InvocationTargetException;


@NotIntersect
public class Database {
    public static Map<Class, Entry> dictionary = new HashMap<>();

    public static String toText() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException{
        CommandReadWrite cmdReadWrite= new CommandReadWrite();

        String buffer= "";
        for(Class c: dictionary.keySet()){
            buffer= buffer + c+" ->" + cmdReadWrite.sumText(c) + "\n";
        }

        return cmdReadWrite.totalText() + "\n"
                + buffer;
    }
}
