/*
 * Class responsible for changing the CompileTime Classes
 */
package ist.meic.pa.FunctionalProfilerExtended;

import javassist.*;

public class ProfilerTranslator implements Translator {

    Command cmd;

    // Default Command
    public ProfilerTranslator() {
        this.cmd = new CommandReadWrite();
    }

    // Command specified with annotation
    public ProfilerTranslator(Command cmd) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        this.cmd = cmd;
    }

    @Override
    public void start(ClassPool pool) throws NotFoundException, CannotCompileException { }

    @Override
    public void onLoad(ClassPool pool, String classname) throws NotFoundException, CannotCompileException {
        CtClass ctClass = pool.get(classname);

        // If annotation NotIntersect is not present execute this
        try {
            Object an = Class.forName(classname).getAnnotation(NotIntersect.class);
            if (an != null) {
                return;
            }

            // Executes command specified in annotation
            cmd.execute(ctClass);

            CommandPrint cmdPrint = new CommandPrint();
            // Adds a print function that prints the results of the writer and reader counter for the CompileTime Classes
            cmdPrint.execute(ctClass);
        } catch (Exception e) {}
    }
}


