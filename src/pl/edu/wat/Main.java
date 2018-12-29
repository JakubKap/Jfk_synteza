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

    public static int findMinJava(LinkedList <ImportDeclaration> importNames){
        int index= -1;

        for(int i=0; i<importNames.size(); i++){
            if(returnFirstPart(importNames.get(i)).equals("java"))
                return i;
        }

            return index;
    }

    public static int findMinNonJava(LinkedList <ImportDeclaration> importNames){
        int index= -1;

        for(int i=0; i<importNames.size(); i++){
            if(!returnFirstPart(importNames.get(i)).equals("java"))
                return i;
        }

        return index;
    }
    /*
    public static LinkedList <ImportDeclaration> sortImports(LinkedList <ImportDeclaration> importNames){
        LinkedList<ImportDeclaration> javaBegin = new LinkedList<>();

        for(ImportDeclaration id : importNames){
            if(id.getNameAsString().startsWith("java."))
                javaBegin.add(id);
        }

        return javaBegin;
    }
*/


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


        LinkedList<ImportDeclaration> importNames = new LinkedList<>();
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

        /*int minJavaIndex = findMinJava(importNames);
        int minNonJavaIndex = findMinNonJava(importNames);
*/
        /*
        int minNonJavaIndex = 0;
        for(int i=0; i<importNames.size(); i++){
            if(returnFirstPart(importNames.get(i)).equals("java")) {
                Collections.swap(importNames, minNonJavaIndex, i);
                minNonJavaIndex++;
                System.out.println("success");
            }
        }*/




       importNames.forEach(n -> System.out.println("Sorted import Name Collected: " + n));

/*
       while(importNames.size() > 0){
           System.out.println("Size of importNames = " + importNames.size());
           System.out.println(importNames.getFirst());
           importNames.removeFirst();
       }
*/


        new ReplaceImportDeclaration().visit(cu, importNames);



        //importNames.forEach(n -> System.out.println("MetaModel: " + n.getName()));
/*
        LinkedList<ImportDeclaration> importNames2;
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
    private static class ImportNamesCollector extends VoidVisitorAdapter<LinkedList<ImportDeclaration>>{
        public void visit(ImportDeclaration id, LinkedList<ImportDeclaration> collector){
            super.visit(id, collector);
            collector.add(id);
        }


    }

    private static class ReplaceImportDeclaration extends VoidVisitorAdapter<LinkedList<ImportDeclaration>>{
        public void visit(ImportDeclaration id, LinkedList<ImportDeclaration> importNames){

            super.visit(id, importNames);
            id.setName(importNames.getFirst().getName());
            id.setStatic(importNames.getFirst().isStatic());
            id.setAsterisk(importNames.getFirst().isAsterisk());


            System.out.println("Size of importNames = " + importNames.size());
            System.out.println(importNames.getFirst());

            importNames.removeFirst();

        }
    }
}
