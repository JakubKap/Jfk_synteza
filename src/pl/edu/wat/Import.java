package pl.edu.wat;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.expr.Name;

import java.util.LinkedList;

public class Import {
    public LinkedList<String> importParts = new LinkedList<>();
    public ImportDeclaration orgImportDeclaration;
    public boolean isStatic;
    public boolean isAsterisk;
    String[] array;


    public Import(ImportDeclaration id, boolean isStatic, boolean isAsterisk){

        orgImportDeclaration = id;

        array = id.getNameAsString().split("[.]");

        this.isStatic = isStatic;
        this.isAsterisk = isAsterisk;

        for(int i=0; i<array.length; i++)
            importParts.add(array[i]);
    }

    public String toString(){
        String result="";

        for(int i=0; i<importParts.size(); i++){
            if(i>0)
                result+="."+importParts.get(i);
            else result=importParts.get(i);
        }

        return result;
    }
}
