package pl.edu.wat;

import com.github.javaparser.JavaParser;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.printer.DotPrinter;

import javax.tools.*;
import java.io.*;
import java.util.*;

public class Main {

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

    public static List <ImportDeclaration> sortImports(List <ImportDeclaration> importNames){
        List<ImportDeclaration> javaBegin = new ArrayList<>();

        for(ImportDeclaration id : importNames){
            if(id.getNameAsString().startsWith("java."))
                javaBegin.add(id);
        }

        return javaBegin;
    }



    public static void main(String[] args) throws IOException {
        final String fileName = "src\\Class.java";
        final String alteredFileName = "src\\ClassAltered.java";
        CompilationUnit cu;
        try (FileInputStream in = new FileInputStream(fileName)) {
            cu = JavaParser.parse(in);
        }

        DotPrinter printer = new DotPrinter(true);
        try (FileWriter fileWriter = new FileWriter("ast.dot");
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            printWriter.print(printer.output(cu));
        }


        List<ImportDeclaration> importNames = new ArrayList<>();
        new ImportNamesCollector().visit(cu, importNames);


        importNames.forEach(n -> System.out.println("Import Name Collected: " + n.getNameAsString())); /*+
                " ,Identifier = " + n.getName().getIdentifier()
                + " ,getChildNodes() = " + n.getChildNodes()
        + " ,getChildNodes().get(0) = " + n.getChildNodes().get(0)
        + " ,getQualifier.getIdentifier() = " + n.getName().getQualifier().get().getIdentifier()
        + " ,getName().getQualifier().get().getQualifier() = " + n.getName().getQualifier().get().getQualifier().get().getQualifier()));
*/

        for(int i=0; i<importNames.size(); i++)
        System.out.println("FirstValue dla " + importNames.get(i).getName() + " = " + returnFirstPart(importNames.get(i)));

       Collections.sort(importNames, new Comparator<ImportDeclaration>() {
           @Override
           public int compare(ImportDeclaration o1, ImportDeclaration o2) {
                return returnFirstPart(o1).toString().compareTo(returnFirstPart(o2).toString());
           }
       });

       


       importNames.forEach(n -> System.out.println("Sorted import Name Collected: " + n));

        //importNames.forEach(n -> System.out.println("MetaModel: " + n.getName()));
/*
        List<ImportDeclaration> importNames2;
        importNames2 = sortImports(importNames);
*/
        //importNames2.forEach(n -> System.out.println("importNames2: " + n.getName()));

       // new ReplaceImportDeclaration().visit(cu,importNames2);

        cu.getClassByName("Class").get().setName("ClassAltered");
        try(FileWriter output = new FileWriter(new File(alteredFileName), false)) {
            output.write(cu.toString());
        }

        File[] files = {new File(alteredFileName)};
        String[] options = { "-d", "out//production//Synthesis" };


    }

    //zapisanie do kolekcji wszystkich ImportDeclaration
    private static class ImportNamesCollector extends VoidVisitorAdapter<List<ImportDeclaration>>{
        public void visit(ImportDeclaration id, List<ImportDeclaration> collector){
            super.visit(id, collector);
            collector.add(id);
        }


    }

    private static class ReplaceImportDeclaration extends VoidVisitorAdapter<List<ImportDeclaration>>{
        public void visit(ImportDeclaration id, List<ImportDeclaration> javaBegin){
            super.visit(id, javaBegin);
            if(!id.getNameAsString().startsWith("java.")){
                System.out.println("Replaced value = " + id + " ,new value = " + javaBegin.get(0));
                id.replace(javaBegin.get(0), id);
            }

        }
    }
}
