package ist.meic.pa.FunctionalProfiler;

import javassist.CtClass;

public interface Command {
    void execute(CtClass ctClass);
}
