package frontend.parser.AST;

import frontend.parser.AST.Func.FuncDef;
import frontend.parser.AST.Func.MainFuncDef;
import frontend.parser.Node;

import java.util.ArrayList;

public class CompUnit implements Node {
    private ArrayList<Decl> decls;
    private ArrayList<FuncDef> funcDefs;
    private MainFuncDef mainFuncDef;


    // CompUnit → {Decl} {FuncDef} MainFuncDef
    public CompUnit(ArrayList<Decl> decls, ArrayList<FuncDef> funcDefs, MainFuncDef mainFuncDef) {
        this.decls  = decls;
        this.funcDefs = funcDefs;
        this.mainFuncDef = mainFuncDef;
    }

    public ArrayList<Decl> getDecls() {
        return decls;
    }

    public ArrayList<FuncDef> getFuncDefs() {
        return funcDefs;
    }

    public MainFuncDef getMainFuncDef() {
        return mainFuncDef;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Decl decl : decls) {
            sb.append(decl.toString());
        }
        for (FuncDef funcDef : funcDefs) {
            sb.append(funcDef.toString());
        }
        if (mainFuncDef != null) {
            sb.append(mainFuncDef.toString());
        }
        sb.append("<CompUnit>\n");
        return sb.toString();
    }
}
