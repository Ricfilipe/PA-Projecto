/*
 * Class extending Command, responsible for adding methods to the Database class, fields to the Entry class,
 * the read/write counter function to the CompileTime Class read/write calls and counting the reads/writes
 * for the class that called the read/write.
 */
package ist.meic.pa.FunctionalProfilerExtended;

import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("Duplicates")
public class CommandReadWrite extends Command {

    // Adds the code that increments the write/read counter for the class
    public void execute(CtClass ctClass)throws NotFoundException, CannotCompileException {
        final String templateWrite =
            "{" +
                " Database.addWriter($0.getClass());" +
                " $0.%s = $1;" +
            "}";

        final String templateWriteOnConstructor =
            "{" +
                "if($0 != this){"+
                " Database.addWriter($0.getClass());" +
                        "}" +
                " $0.%s = $1;" +
            "}";
        final String templateRead =
            "{" +
                " Database.addReader($0.getClass());" +
                " $_=$0.%s;" +
            "}";
        for (CtConstructor ctConstructor : ctClass.getDeclaredConstructors()) {
            ctConstructor.instrument(addCodeIfReaderWriter(templateRead,templateWriteOnConstructor));
        }
        for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
            ctMethod.instrument(addCodeIfReaderWriter(templateRead,templateWrite));
        }
    }

    // Returns the Expression Editor with the information if the accessed field was a read or write
    // If it was add code, otherwise do nothing
    public ExprEditor addCodeIfReaderWriter(String template1, String template2) {
        return new ExprEditor() {
            public void edit(FieldAccess fa)
                    throws CannotCompileException {
            if (fa.isReader()) {
                String name = fa.getFieldName();
                fa.replace(String.format(template1,
                        name));
            }else if (fa.isWriter() ) {
                String name = fa.getFieldName();
                fa.replace(String.format(template2,
                        name));
            }
            }
        };
    }

    // Returns string with program's total writes/reads
    @Override
    public String totalText() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException{
        Class db = Database.class;

        int totalwrites=0;
        for(Class c: Database.dictionary.keySet()){
            Method getWriterCounter = db.getDeclaredMethod("getWriterCounter", c.getClass());
            totalwrites = totalwrites + (int)getWriterCounter.invoke(null, c);
        }

        int totalreads=0;
        for (Class c : Database.dictionary.keySet()) {
            Method getReaderCounter = db.getDeclaredMethod("getReaderCounter", c.getClass());
            totalreads = totalreads + (int)getReaderCounter.invoke(null, c);
        }
        
        return "Total reads: " + totalreads + " Total writes: " + totalwrites;
    }

    // Returns string with class total writes/reads
    @Override
    public String sumText(Class c) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class db = Database.class;
        Method getReaderCounter = db.getDeclaredMethod("getReaderCounter", c.getClass());
        Method getWriterCounter = db.getDeclaredMethod("getWriterCounter", c.getClass());
        return  " reads: " + getReaderCounter.invoke(null, c) + " write: " + getWriterCounter.invoke(null, c);
    }

    // Adds the fields read and write counters to the Entry class
    @Override
    public CtField[] addFields(CtClass entry) throws NotFoundException, CannotCompileException{
        CtField fields[] = new CtField[2];
        fields[0] = CtField.make("public int readerCounter = 0;", entry);
        fields[1] = CtField.make("public int writeCounter = 0;", entry);
        return fields;
    }

    // Adds the addWriter, addReader methods to the Database class and both the get counter methods
    @Override
    public CtMethod[] addMethods(CtClass database) throws NotFoundException, CannotCompileException{
        CtMethod[]  methods = new CtMethod[4];

        methods[0]= CtNewMethod.make(
        "public static void addWriter(Class c) {" +
                "if ("+getEntryfromDatabase("c")+"== null){" +
                    addClassToDatabase("c")+";" +
                "}" +
                "java.lang.reflect.Field field = "+ getField("writeCounter")+"; " +
                "field.set("+getEntryfromDatabase("c")+",new Integer (((Integer)field.get("+getEntryfromDatabase("c")+")).intValue()+1));" +
                "}", database);

        methods[1] = CtNewMethod.make(
        "public static void addReader(Class c) {" +
                "if ("+getEntryfromDatabase("c")+" == null){" +
                addClassToDatabase("c")+";}" +
                "java.lang.reflect.Field field = "+ getField("readerCounter")+"; " +
                "field.set("+getEntryfromDatabase("c")+",new Integer (((Integer)field.get("+getEntryfromDatabase("c")+")).intValue()+1));" +
                "}", database);

        methods[2] = CtNewMethod.make(
        "public static int getReaderCounter(Class c) {" +
                "java.lang.reflect.Field field = "+ getField("readerCounter")+";" +
                "return ((Integer)field.get("+getEntryfromDatabase("c")+")).intValue(); }", database);

        methods[3] = CtNewMethod.make(
        "public static int getWriterCounter(Class c) {" +
                "java.lang.reflect.Field field = "+ getField("writeCounter")+"; " +
                "return ((Integer)field.get("+getEntryfromDatabase("c")+")).intValue(); }", database);

        return methods;
    }
}
