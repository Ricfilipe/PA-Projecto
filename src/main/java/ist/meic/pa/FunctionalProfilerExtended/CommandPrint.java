/*
 * Class extending Command, responsible for adding the print function to the program
 * CompileTime Class with the main method, printing the results of the read and write counters.
 */
package ist.meic.pa.FunctionalProfilerExtended;

import javassist.*;

public class CommandPrint extends Command {
    Command cmd ;

    public CommandPrint (Command cmd){
        this.cmd = cmd;
    }
    @Override
    public void execute(CtClass ctClass) throws NotFoundException, CannotCompileException {
        try{
            CtMethod ctMethod = ctClass.getDeclaredMethods("main")[0];
            ctMethod.insertAfter("System.out.print(Database.toText(\""+cmd.getClass().getName()+"\"));");
        }catch (Exception e){
            //ignore
        }
    }

    @Override
    public String totalText() {
        return "";
    }

    @Override
    public String sumText(Class c) {
        return "";
    }

    @Override
    public void addFields(ClassPool pool) { return; }

    @Override
    public void addMethods(ClassPool pool) { return; }
}
