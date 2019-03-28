/*
 * Class responsible for changing the CompileTime Classes
 */
package ist.meic.pa.FunctionalProfiler;

import javassist.*;

public class ProfilerTranslator implements Translator {
    @Override
    public void start(ClassPool pool) throws NotFoundException, CannotCompileException { }

    // Receives CtClasses and calls commands on them
    @Override
    public void onLoad(ClassPool pool, String classname) throws NotFoundException, CannotCompileException {
        CtClass ctClass = pool.get(classname);

        // If annotation NotIntersect is present, do nothing, otherwise execute remainder of method
        try {
            Object an = Class.forName(classname).getAnnotation(NotIntersect.class);
        if (an != null) {
            return;
        }

        CommandReadWrite cmdReadWrite = new CommandReadWrite();
        CommandPrint cmdPrint = new CommandPrint();

        // Adds reader/writes counter function for each read/write in DeclaredMethods and Constructor of CompileTime Class
        cmdReadWrite.execute(ctClass);

        // Adds a print function that prints the results of the writer and reader counter for the CompileTime Classes
        cmdPrint.execute(ctClass);
        } catch (Exception e){
            // Do nothing
        }
    }
}


