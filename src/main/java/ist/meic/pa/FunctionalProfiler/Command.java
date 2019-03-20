package ist.meic.pa.FunctionalProfiler;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;

public abstract class Command {
    public abstract void  execute (CtClass ctClass) throws NotFoundException, CannotCompileException;

    public abstract String totalText();

    public abstract  String sumText(Class c);
}
