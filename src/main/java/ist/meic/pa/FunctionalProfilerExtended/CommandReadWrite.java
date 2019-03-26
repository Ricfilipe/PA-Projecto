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
            ctConstructor.instrument(addCodeIfReader(templateRead));
        }
        for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
            ctMethod.instrument(addCodeIfReader(templateRead));
        }
        for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
            ctMethod.instrument(addCodeIfWriter(templateWrite));
        }
        for (CtConstructor ctConstructor : ctClass.getDeclaredConstructors()) {
            ctConstructor.instrument(addCodeIfWriter(templateWriteOnConstructor));
        }
    }

    // Returns the Expression Editor with the information if the accessed field was a read or not
    // If it was add code, otherwise do nothing
    public ExprEditor addCodeIfReader(String template) {
        return new ExprEditor() {
            public void edit(FieldAccess fa)
                    throws CannotCompileException {
                if (fa.isReader()) {
                    String name = fa.getFieldName();
                    fa.replace(String.format(template,
                            name));
                }
            }
        };
    }

    // Returns the Expression Editor with the information if the accessed field was a write or not
    // If it was and was not a self write in constructor add code, otherwise do nothing
    public ExprEditor addCodeIfWriter(String template) {
        return new ExprEditor() {
            public void edit(FieldAccess fa)
                    throws CannotCompileException {
                if (fa.isWriter() ) {
                    String name = fa.getFieldName();
                    fa.replace(String.format(template,
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
    public void addFields(ClassPool pool) throws NotFoundException, CannotCompileException{
        CtClass entry = ClassPool.getDefault().get(Entry.class.getName());

        CtField readerCounter = CtField.make("public int readerCounter = 0;", entry);
        entry.addField(readerCounter);

        CtField writeCounter = CtField.make("public int writeCounter = 0;", entry);
        entry.addField(writeCounter);
    }

    // Adds the addWriter, addReader methods to the Database class and the both the get counter methods
    @Override
    public void addMethods(ClassPool pool) throws NotFoundException, CannotCompileException{
        CtClass database = ClassPool.getDefault().get(Database.class.getName());

        CtMethod addWriter = CtNewMethod.make(
                "public static void addWriter(Class c) {" +
                        "if (dictionary.get(c) == null){" +
                            "addClass(c);" +
                        "}" +
                        "java.lang.reflect.Field field = getField(\""+Entry.class.getName()+"\",\"writeCounter\"); " +
                        "field.set(dictionary.get(c),new Integer (((Integer)field.get(dictionary.get(c))).intValue()+1));" +
                        "}", database);
        database.addMethod(addWriter);

        CtMethod addReader = CtNewMethod.make(
                "public static void addReader(Class c) {" +
                        "if (dictionary.get(c) == null){" +
                        "addClass(c);}" +
                        "java.lang.reflect.Field field = getField(\""+Entry.class.getName()+"\",\"readerCounter\"); " +
                        "field.set(dictionary.get(c),new Integer (((Integer)field.get(dictionary.get(c))).intValue()+1));" +
                        "}", database);
        database.addMethod(addReader);


        CtMethod getReaderCounter = CtNewMethod.make(
                "public static int getReaderCounter(Class c) {" +
                        "java.lang.reflect.Field field = getField(\""+Entry.class.getName()+"\",\"readerCounter\"); " +
                        "return ((Integer)field.get(dictionary.get(c))).intValue(); }", database);
        database.addMethod(getReaderCounter);

        CtMethod getWriteCounter = CtNewMethod.make(
                "public static int getWriterCounter(Class c) {" +
                        "java.lang.reflect.Field field = getField(\""+Entry.class.getName()+"\",\"writeCounter\"); " +
                        "return ((Integer)field.get(dictionary.get(c))).intValue(); }", database);
        database.addMethod(getWriteCounter);
    }
}
