/*
 * Class extending Command, responsible for adding the print function to the program
 * CompileTime Class with the main method, printing the results of the read and write counters.
 */
package ist.meic.pa.FunctionalProfiler;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class CommandPrint extends Command {
    @Override
    public void execute(CtClass ctClass) throws NotFoundException, CannotCompileException {
        try{
            CtMethod ctMethod = ctClass.getDeclaredMethods("main")[0];
            ctMethod.insertAfter("System.out.println(Database.toText());");
        }catch (Exception e){
            //ignore
        }
    }

    @Override
    public String totalText() {
        return null;
    }

    @Override
    public String sumText(Class c) {
        return null;
    }
}
