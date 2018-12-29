package pl.edu.wat;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.expr.Name;

import java.util.LinkedList;

public class Import {
    public LinkedList<String> importParts = new LinkedList<>();
    public boolean isStatic;
    public boolean isAsterisk;
    String[] array;

/*
    public static Name returnFirstPart(ImportDeclaration id){
        Name firstPart = new Name("null");

        int numOfDots = 0;
        char symbol =' ';

        for (int i = 0; i < id.getNameAsString().length(); i++) {
            symbol = id.getNameAsString().charAt(i);
            if (symbol == '.')
                numOfDots++;
        }

        firstPart = id.getName().getQualifier().get();

        for (int i = 0; i < numOfDots - 1; i++) {
            firstPart = firstPart.getQualifier().get();
        }
        return firstPart;
    }
*/

    public Import(ImportDeclaration id, boolean isStatic, boolean isAsterisk){

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
