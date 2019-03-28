/*
 * Class extending Command, responsible for adding methods to the Database class, fields to the Entry class,
 * the read/write counter function to the CompileTime Class read/write calls and counting the reads/writes
 * for the class that called the read/write.
 */
package ist.meic.pa.FunctionalProfilerExtended;

import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

import javax.xml.crypto.Data;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CommandReadWriteOutside extends Command {
//This command counts the number of times a field in methods outside of their class


    // Adds the code that increments the write/read counter for the class
    public void execute(CtClass ctClass)throws NotFoundException, CannotCompileException {


        final String templateWrite =
                "{" +

                        " Database.addWriterOutside($0.getClass(),\"%s\");" +

                        " $0.%s = $1;" +
                        "}";
        final String templateRead =
                "{" +

                        " Database.addReaderOutside($0.getClass(),\"%s\");" +


                        " $_=$0.%s;" +
                        "}";

        for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
            ctMethod.instrument(addCodeIfReaderWriter(templateRead,templateWrite,ctClass));
        }

    }

    // Returns the Expression Editor with the information if the accessed field was a read or write
    // If it was add code, otherwise do nothing
    public ExprEditor addCodeIfReaderWriter(String template1, String template2, CtClass ctClass) {
        return new ExprEditor() {
            public void edit(FieldAccess fa)
                    throws CannotCompileException {
                if(!fa.getClassName().equals(ctClass.getName())) {
                    if (fa.isReader()) {
                        String name = fa.getFieldName();

                        fa.replace(String.format(template1,
                                name, name));
                    } else if (fa.isWriter()) {
                        String name = fa.getFieldName();
                        fa.replace(String.format(template2,
                                name, name));
                    }
                }
            }
        };
    }


    // Returns string with program's total writes/reads
    @Override
    public String totalText() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException{

        return "";
    }

    // Returns string with class total writes/reads
    @Override
    public String sumText(Class c) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class db = Database.class;
        ;
        Method getReaderCounter = db.getDeclaredMethod("getReaderCounter", c.getClass(),String.class);
        Method getWriterCounter = db.getDeclaredMethod("getWriterCounter", c.getClass(),String.class);
        String buffer="";

        for(; c != Object.class ;c=c.getSuperclass()) {
            Field[] fs = c.getDeclaredFields();
            for (Field field : fs) {

                buffer = buffer + "\n" + field.getName() + "-> reads: " +
                        getReaderCounter.invoke(null, c, field.getName()) + " write: " +
                        getWriterCounter.invoke(null, c, field.getName());
            }
        }
        return buffer+"\n";
    }

    // Adds the fields read and write counters to the Entry class
    @Override
    public CtField[] addFields(CtClass entry) throws NotFoundException, CannotCompileException{
        CtField[] fields = new CtField[2];
        fields[0] = CtField.make("public java.util.HashMap dicRead = new java.util.HashMap();", entry);
        fields[1] = CtField.make("public java.util.HashMap dicWrite = new java.util.HashMap();", entry);
        return fields;
    }

    // Adds the addWriter, addReader methods to the Database class and the both the get counter methods
    @Override
    public CtMethod[] addMethods(CtClass database, String entryClassName) throws NotFoundException, CannotCompileException{

        CtMethod[]  methods = new CtMethod[4];
        methods[0] = CtNewMethod.make(
                "public static void addWriterOutside(Class c,String f) {" +
                        "if (dictionary.get(c) == null){" +
                        "addClass(c);" +
                        "}" +
                        "java.lang.reflect.Field fieldR = getField(\""+entryClassName+"\",\"dicRead\"); " +
                        "java.lang.reflect.Field fieldW = getField(\""+entryClassName+"\",\"dicWrite\");" +
                        "java.util.HashMap dicR =(java.util.HashMap) fieldR.get(dictionary.get(c));" +
                        "java.util.HashMap dicW =(java.util.HashMap) fieldW.get(dictionary.get(c));" +
                       "if(dicW.get(f)==null){ " +
                        "dicR.put(f,new Integer(0));" +
                        "dicW.put(f,new Integer(1));" +
                        "}else{" +
                        "dicW.put(f,new Integer (((Integer)dicW.get(f)).intValue()+ 1));" +
                        "}" +
                     "}", database);


        methods[1] = CtNewMethod.make(
                "public static void addReaderOutside(Class c,String f) {" +
                        "if (dictionary.get(c) == null){" +
                        "addClass(c);" +
                        "}" +
                        "java.lang.reflect.Field fieldR = getField(\""+entryClassName+"\",\"dicRead\"); " +
                        "java.lang.reflect.Field fieldW = getField(\""+entryClassName+"\",\"dicWrite\");" +
                        "java.util.HashMap dicR =(java.util.HashMap) fieldR.get(dictionary.get(c));" +
                        "java.util.HashMap dicW =(java.util.HashMap) fieldW.get(dictionary.get(c));" +
                        "if(dicR.get(f)==null){ " +
                        "dicR.put(f,new Integer(1));" +
                        "dicR.put(f,new Integer(0));" +
                        "}else{" +
                        "dicR.put(f,new Integer (((Integer)dicR.get(f)).intValue()+ 1));" +
                        "}" +
                        "}", database);

        methods[2] = CtNewMethod.make(
                "public static int getReaderCounter(Class c,String f) {" +
                        "java.lang.reflect.Field fieldR = getField(\""+entryClassName+"\",\"dicRead\");" +
                        "java.util.HashMap dicR =(java.util.HashMap) fieldR.get(dictionary.get(c));" +
                        "if(dicR.get(f)==null){" +
                            "return 0;" +
                        "}"+
                        "return  ((Integer) dicR.get(f)).intValue();" +
                        "}", database);


        methods[3] = CtNewMethod.make(
                "public static int getWriterCounter(Class c,String f) {" +
                        "java.lang.reflect.Field fieldW = getField(\""+entryClassName+"\",\"dicWrite\");" +
                        "java.util.HashMap dicW =(java.util.HashMap) fieldW.get(dictionary.get(c));" +
                        "if(dicW.get(f)==null){" +
                        "return 0;}"+
                            "return  ((Integer) dicW.get(f)).intValue();" +
                                "}", database);
        return methods;

    }
}
