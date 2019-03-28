/*
 * Class that keeps the records of CompileTime Classes,
 * responsible for printing the results and depending on Commands executed may have more responsabilities
 */
package ist.meic.pa.FunctionalProfilerExtended;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.InvocationTargetException;

@NotIntersect
public class Database {
    public static Map<Class, Entry> dictionary = new HashMap<>();

    public static String toText(String cmdString) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, ClassNotFoundException, InstantiationException {
        Command cmd = (Command) Class.forName(cmdString).newInstance();

        String buffer= "";
        dictionary
        for(Class c: dictionary.keySet()){
            buffer= buffer + c+" ->" + cmd.sumText(c) + "\n";
        }

        return cmd.totalText() + "\n"
                + buffer;
    }

    public static void addClass(Class c) {
        dictionary.put(c,new Entry());
    }

    public static Field getField(String c, String fieldName) throws ClassNotFoundException, NoSuchFieldException {
        Class klazz = Class.forName(c);
        return klazz.getField(fieldName);
    }
}
