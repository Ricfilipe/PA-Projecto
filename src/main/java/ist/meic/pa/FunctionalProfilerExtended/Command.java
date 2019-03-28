/*
 * Abstract class, implements the Command design pattern
 */

package ist.meic.pa.FunctionalProfilerExtended;

import javassist.*;

import java.lang.reflect.InvocationTargetException;

@NotIntersect
public abstract class Command {
    protected String getEntryfromDatabase(String var){
     return  "dictionary.get("+var+")";
    }

    protected String addClassToDatabase(String var){
        return  "addClass("+var+")";
    }

    protected String getField(String var){
        return  "getField(\""+var+"\")";
    }

    public abstract void  execute (CtClass ctClass) throws NotFoundException, CannotCompileException;

    public abstract String totalText() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException;

    public abstract  String sumText(Class c) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException;

    public abstract CtField[] addFields(CtClass entry) throws NotFoundException, CannotCompileException;

    public abstract CtMethod[] addMethods(CtClass database) throws NotFoundException, CannotCompileException;
}

