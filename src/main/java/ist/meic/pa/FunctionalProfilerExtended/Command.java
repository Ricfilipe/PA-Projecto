/*
 * Abstract class, implements the Command design pattern
 */

package ist.meic.pa.FunctionalProfilerExtended;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.ClassPool;

import java.lang.reflect.InvocationTargetException;

@NotIntersect
public abstract class Command {
    public abstract void  execute (CtClass ctClass) throws NotFoundException, CannotCompileException;

    public abstract String totalText() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException;

    public abstract  String sumText(Class c) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException;

    public abstract void addFields(ClassPool pool) throws NotFoundException, CannotCompileException;

    public abstract void addMethods(ClassPool pool) throws NotFoundException, CannotCompileException;
}
