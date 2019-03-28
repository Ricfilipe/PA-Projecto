/*
 * Class that keeps the records of CompileTime Classes,
 * responsible for printing the results and depending on Commands executed may have more responsabilities
 */
package ist.meic.pa.FunctionalProfilerExtended;

import java.lang.reflect.Field;
import java.util.*;
import java.lang.reflect.InvocationTargetException;

@NotIntersect
public class Database {

    public static class ClassComparator  implements Comparator<Class> {
        public int compare(Class obj1, Class obj2) {
            String s1 = obj1.getName();
            String s2 = obj2.getName();

            return s1.compareTo(s2);
        }
    }

    public static Map<Class, Entry> dictionary = new HashMap<>();

    public static String toText(String cmdString) throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, ClassNotFoundException, InstantiationException {

        Command cmd = (Command) Class.forName(cmdString).newInstance();

        String buffer= "";

        List<Class> sorted = new ArrayList<>(dictionary.keySet());
        Collections.sort(sorted, new ClassComparator());

        for(Class c: sorted){
            buffer=  buffer + "\n"+ c+" ->" + cmd.sumText(c) ;
        }

        return cmd.totalText()
                + buffer;
    }

    public static void addClass(Class c) {
        dictionary.put(c,new Entry());
    }

    public static Field getField( String fieldName) throws ClassNotFoundException, NoSuchFieldException {
        Class klazz = Class.forName(Entry.class.getName());
        return klazz.getField(fieldName);
    }
}
