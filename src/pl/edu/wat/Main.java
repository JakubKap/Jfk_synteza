package pl.edu.wat;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.printer.DotPrinter;


import javax.tools.*;
import java.io.*;
import java.util.*;

public class Main {

    private static LinkedList<ImportDeclaration> importNames = new LinkedList<>();
    private static LinkedList<Import> importStrings = new LinkedList<>();
    private static int firstNonJavaIndex = -1;

    public static void sortImports(){

        //posortowanie po pierwszym członie

        Collections.sort(importStrings, new Comparator<Import>() {
            @Override
            public int compare(Import i1, Import i2) {
                return i1.importParts.get(0).compareTo(i2.importParts.get(0));
            }
        });

        //sortowanie w podgrupach

        Set<String> uniqueFirstPart = new HashSet<>();
        LinkedList<Import> importNamesPom = new LinkedList<>();
        LinkedList<Import> importNamesRest = new LinkedList<>();


        //znalezienie najdłuższego importu
        int max = 0;
        for(Import im : importStrings)
            if(im.importParts.size() > max)
                max = im.importParts.size();

        for(int i=0; i<max; i++) {

            for (int j = 0; j < importStrings.size(); j++) {
                if(importStrings.get(j).importParts.size() > i){
                    uniqueFirstPart.add(importStrings.get(j).importParts.get(i));
                }

            }


            for (String s : uniqueFirstPart) { //1 wybrany początek

                for (Import in : importStrings) {

                    if (in.importParts.size() > i+1 /*&& !in.toString().startsWith("java.util")*/) {
                        if (in.importParts.get(i).equals(s)){
                            importNamesPom.add(in);
                        }
                    }
                    else if((in.importParts.size() == i+1)){
                        if (in.importParts.get(i).equals(s)){
                            importNamesRest.add(in);
                        }
                    }

                }

                final int inc = i;
                Collections.sort(importNamesPom, new Comparator<Import>() {
                    @Override
                    public int compare(Import i1, Import i2) {
                        return i1.importParts.get(inc+1).compareToIgnoreCase(i2.importParts.get(inc+1));
                    }
                });

                //wstawienie posortowanego zbioru do oryginału
                int incI = 0;
                int incP = 0;

                for (Import in : importStrings) {
                    if (in.importParts.size() > i+1) {
                        if(in.importParts.get(i).equals(s)) {
                            importStrings.set(incI, importNamesPom.get(incP));
                            incP++;
                        }
                    }
                    incI++;
                }


                importNamesPom.clear();
            }
            uniqueFirstPart.clear();
        }



        int index=0;
        for(Import im : importNamesRest) {
            for (int i = 0; i < importStrings.size(); i++) {
                if(importStrings.get(i).toString().startsWith(im.toString()) &&
                        im.importParts.size() < importStrings.get(i).importParts.size()){

                    for(int j=0; j<importStrings.size(); j++){
                        if(importStrings.get(j).toString().equals(im.toString()))
                            index=j;
                    }

                    if(i>0) {
                        importStrings.remove(index);
                        importStrings.add(i, im);
                    }
                    else {
                        importStrings.remove(index);
                        importStrings.add(0, im);
                        //importStrings.remove(importStrings.indexOf(im));
                    }


                    //jak został znaleziony i zamieniony, to przechodzi do następnego "początku"
                    i=importStrings.size();
                }
            }
        }


        //przesunięcie java na samą górę

        int minJavaIndex = findMinJava();
        int minNonJavaIndex = findMinNonJava();


        int pomInc = 0;

        if(minJavaIndex>=0 && minNonJavaIndex>=0 && minNonJavaIndex < minJavaIndex) {
            for (int i = 0; i < importStrings.size(); i++) {
                if (importStrings.get(i).toString().startsWith("java.util")) {
                    if(minNonJavaIndex < importStrings.size()) {

                        if(importStrings.get(minNonJavaIndex).toString().charAt(0) < 'j'){
                            importStrings.add(pomInc,importStrings.get(minJavaIndex));
                            importStrings.remove(minJavaIndex+1);
                            minJavaIndex++;
                            pomInc++;
                        }
                        else
                            Collections.swap(importStrings, i, minNonJavaIndex);

                        minNonJavaIndex++;
                    }
                }
            }
        }


        //ponowne przesortowanie zbioru (bez namespace - bez java.)

        //znalezienie najdłuższego importu
        max = 0;
        uniqueFirstPart.clear();
        importNamesPom.clear();
        importNamesRest.clear();

        for(Import im : importStrings)
            if(im.importParts.size() > max )
                max = im.importParts.size();


        for(int i=0; i<max; i++) {

            for (int j = 0; j < importStrings.size(); j++) {
                if(importStrings.get(j).importParts.size() > i){
                    uniqueFirstPart.add(importStrings.get(j).importParts.get(i));
                }

            }


            for (String s : uniqueFirstPart) { //1 wybrany początek

                for (Import in : importStrings) {

                    if (in.importParts.size() > i+1) {
                        if (in.importParts.get(i).equals(s)){
                            importNamesPom.add(in);
                        }
                    }
                    else if((in.importParts.size() == i+1)){
                        if (in.importParts.get(i).equals(s)){
                            importNamesRest.add(in);
                        }
                    }

                }


                final int inc = i;
                Collections.sort(importNamesPom, new Comparator<Import>() {
                    @Override
                    public int compare(Import i1, Import i2) {
                        return i1.importParts.get(inc+1).compareToIgnoreCase(i2.importParts.get(inc+1));
                    }
                });

                //wstawienie posortowanego zbioru do oryginału
                int incI = 0;
                int incP = 0;

                for (Import in : importStrings) {
                    if (in.importParts.size() > i+1) {
                        if(in.importParts.get(i).equals(s)) {
                            if(!importStrings.get(incI).toString().startsWith("java.util")) {
                                importStrings.set(incI, importNamesPom.get(incP));
                                incP++;
                            }
                        }
                    }
                    incI++;
                }


                importNamesPom.clear();
            }
            uniqueFirstPart.clear();
        }


        if(minNonJavaIndex > 0)
            firstNonJavaIndex = minNonJavaIndex;




        for(int i=0; i<importStrings.size(); i++){
            if((i+1) < importStrings.size()){
                if((importStrings.get(i).toString().toLowerCase().equals(importStrings.get(i+1).toString().toLowerCase()))
                        || (importStrings.get(i).toString().startsWith(importStrings.get(i+1).toString()))
                        || (importStrings.get(i+1).toString().startsWith(importStrings.get(i).toString()))){


                    if(((!importStrings.get(i+1).toString().startsWith(importStrings.get(i).toString())
                            || (importStrings.get(i).orgImportDeclaration.getNameAsString().equals(importStrings.get(i+1).orgImportDeclaration.getNameAsString()) && importStrings.get(i).isAsterisk && !importStrings.get(i+1).isAsterisk))))
                        Collections.swap(importStrings, i, i+1);
                }

            }
        }


    }


    public static int findMinJava(){
        int index= -1;

        for(int i=0; i<importStrings.size(); i++){
            if(importStrings.get(i).toString().startsWith("java.util"))
                return i;
        }

        return index;
    }

    public static int findMinNonJava(){
        int index= -1;

        for(int i=0; i<importNames.size(); i++){
            if(!importStrings.get(i).toString().startsWith("java.util"))
                return i;
        }

        return index;
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


        new ImportNamesCollector().visit(cu, null);


        System.out.println("Sortowanie importów");

        sortImports();

        for(int i=0; i<importStrings.size(); i++){
            cu.setImport(i, new ImportDeclaration(new Name(importStrings.get(i).toString()), importStrings.get(i).isStatic, importStrings.get(i).isAsterisk));
        }



        cu.getClassByName("Class").get().setName("ClassAltered");
        try(FileWriter output = new FileWriter(new File(alteredFileName), false)) {
            if(firstNonJavaIndex>=0) {
                output.write(cu.toString().replace(importStrings.get(firstNonJavaIndex).orgImportDeclaration.toString(), "\n" + importStrings.get(firstNonJavaIndex).orgImportDeclaration.toString()));
            }

            else output.write(cu.toString());
        }

        File[] files = {new File(alteredFileName)};
        String[] options = { "-d", "out//production//Synthesis" };


        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)) {
            Iterable<? extends JavaFileObject> compilationUnits =
                    fileManager.getJavaFileObjectsFromFiles(Arrays.asList(files));
            compiler.getTask(
                    null,
                    fileManager,
                    diagnostics,
                    Arrays.asList(options),
                    null,
                    compilationUnits).call();

            diagnostics.getDiagnostics().forEach(d -> System.out.println(d.getMessage(null)));
        }

        System.out.println("\nPosortowane importy znajdują się w ClassAltered.java");
    }

    //zapisanie do kolekcji wszystkich ImportDeclaration
    private static class ImportNamesCollector extends VoidVisitorAdapter<Void>{
        @Override
        public void visit(ImportDeclaration id, Void arg){
            super.visit(id, null);
            importNames.add(id);
            importStrings.add(new Import(id, id.isStatic(), id.isAsterisk()));
        }

    }


}