import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.*;

public class Detector {

    //******************************************************//
    //                                                      //
    //      Funcion que encuentra mas de una instruccion    //
    //      en una linea, almacena cada instruccion en una  //
    //      linea independiente.                            //
    //                                                      //
    //******************************************************//

    public void instruction_by_line(CommonTokenStream tks) throws IOException {

        String [] in= tks.getTokens().get(0).getInputStream().toString().split("\n");
        List<String> out = new ArrayList<String>();
        String [] aux;
        String coment="";
        int count;

        for (int i=0; i<in.length-1;i++) {
            if(in[i].contains("for")){
                out.add(in[i]);
                continue;
            }

            coment="";

            if(in[i].indexOf("//")!=-1){
                coment=in[i].substring(in[i].indexOf("//"));
                in[i]=in[i].substring(0,in[i].indexOf("//"));

            }
            if(in[i].indexOf(";")!=-1){
                if(in[i].indexOf(";",in[i].indexOf(";"))!=-1){
                    aux=in[i].split(";");
                    count=count_X(in[i],';');
                    for(int j=0; j<count; j++){
                        out.add(aux[j]+";"+coment);
                    }

                }
            }else{
                out.add(in[i]+coment);
            }

        }
        writeTxt(out);
    }

    //******************************************************//
    //                                                      //
    //      Funcion formato para condicional if             //
    //                                                      //
    //******************************************************//

    public void conditionals (CommonTokenStream tks) throws IOException{
        String [] in= tks.getTokens().get(0).getInputStream().toString().split("\n");
        List<String> out = new ArrayList<String>();

        for (int i=0; i<in.length-1;i++) {
            if(in[i].contains("if") || in[i].contains("else")) {
                if (in[i].contains("if(")){
                    in[i]=(in[i].replace("if(", "if ("));
                }

                if (in[i].contains("){")) {

                    in[i]=(in[i].replace("){", ") {"));
                }
                if (in[i].contains("}else")) {

                    in[i]=(in[i].replace("}else", "} else"));
                }
                if (in[i].contains("else{")) {

                    in[i]=(in[i].replace("else{", "else {"));
                }

            }
            else {
                out.add(in[i]);
                continue;
            }
            out.add(in[i]);

        }
        writeTxt(out);
    }


    public void loops (CommonTokenStream tks) throws IOException{
        String [] in= tks.getTokens().get(0).getInputStream().toString().split("\n");
        List<String> out = new ArrayList<String>();

        for (int i=0; i<in.length-1;i++) {

            if (in[i].contains("for(")) {
                in[i] = (in[i].replace("for(", "for ("));
            }

            if (in[i].contains("){")) {
                in[i] = (in[i].replace("){", ") {"));
            }

            if(in[i].contains("while(") ) {
                in[i] = (in[i].replace("while(", "while ("));

            }
            else {
                out.add(in[i]);
                continue;
            }
            out.add(in[i]);

        }
        writeTxt(out);



    }

    //******************************************************//
    //                                                      //
    //      Funcion que tabula line por linea dependiendo   //
    //      de la hubicacion por corchetes {}               //
    //                                                      //
    //******************************************************//

    public void brackets_indentation(CommonTokenStream tks) throws IOException{
        Stack<Integer> stack = new Stack<Integer>();
        List<String> out = new ArrayList<String>();
        int line=0;
        int count=1;
        String text="";
        String [] in= tks.getTokens().get(0).getInputStream().toString().split("\n");
        boolean b=false;

        for(Token str : tks.getTokens()) {

            if(str.getText().contains("{")){
                if(stack.size()==0) {
                    b=true;
                }
                stack.push(str.getLine());
            }else if(str.getText().contains("}")){
                stack.pop();
            }



            if(str.getLine()!=line ){
                line=str.getLine();


                if(stack.size()==0){
                    out.add(in[line-1].substring(firstCharacter(in[line-1])));

                }else if(stack.peek()==line){
                    if(b){
                        out.add(in[line-1].substring(firstCharacter(in[line-1])));
                        b=false;

                    }else if(firstCharacter(in[line-1])!=-1) {
                        if(firstCharacter(in[line-1])==in[line-1].indexOf("{")+1) {
                            out.add(esp((stack.size() - 2) * 2) + in[line - 1].substring(firstCharacter(in[line - 1])));

                        }else {
                            out.add(esp((stack.size()-1) * 2) + in[line - 1].substring(firstCharacter(in[line - 1])));
                        }
                    }
                }else if(firstCharacter(in[line-1])!=-1){
                    out.add(esp(stack.size() * 2) + in[line - 1].substring(firstCharacter(in[line - 1])));

                }

            }

        }

        writeTxt(out.subList(0,out.size()-1));

    }

    //******************************************************//
    //                                                      //
    //      Funcion que tabula los parametros de una        //
    //      funcion para ajustarse a las normas             //
    //                                                      //
    //******************************************************//
    public void vertical_align(CommonTokenStream tks) throws IOException{
        String [] in= tks.getTokens().get(0).getInputStream().toString().split("\n");
        List<String> out = new ArrayList<String>();

        for (int i=0; i<in.length-1;i++) {
            if(in[i].contains("(") && in[i].length()>80) {
                int length = in[i].length();
                int index = in[i].indexOf("(")+1;
                int numspaces = 0;
                for (int a = 0; a < length && in[i].charAt(a) == ' '; a++) {
                    numspaces++;
                }
                int commaposition = 0;
                int inicio = 0;
                int fin=0;

                if (index > 80 || in[i].indexOf(",")>80) {
                    out.add(in[i].substring(0, index + 1));
                    inicio = index + 1;
                    for (int b = inicio; b < length; b++) {
                        if (in[i].charAt(b) == ',') {
                            out.add(esp(numspaces + 4) + in[i].substring(inicio, b + 1));
                            inicio = b + 1;
                        }
                    }
                    out.add(esp(numspaces + 4) + in[i].substring(inicio));
                } else {
                    for (int j = inicio; j < length; j++) {
                        if (in[i].charAt(j) == ',') {
                            commaposition = j + 1;
                        }
                        if (j % 80 == 0 && inicio == 0 && commaposition!=0) {
                            out.add(in[i].substring(inicio,commaposition));
                            inicio = commaposition;
                            fin = commaposition;
                        }
                        if (j==(inicio+(fin-index)) && inicio!=0){
                            out.add(esp(index)+in[i].substring(inicio,commaposition));
                            inicio = commaposition;
                        }
                    }
                    out.add(esp(index) + in[i].substring(inicio));
                }
            }else {
                out.add(in[i]);
            }
        }
        writeTxt(out);
    }

    //******************************************************//
    //                                                      //
    //      Funcion que cambia las L al final del nombre    //
    //      de un identificador                             //
    //                                                      //
    //******************************************************//
    public void L_changer(CommonTokenStream tks) throws IOException{
        String [] in= tks.getTokens().get(0).getInputStream().toString().split("\n");
        List<String> out = new ArrayList<String>();
        int linea=0;
        String identifier="";

        for (Token str : tks.getTokens()) {
            if (str.getType() == 127 || str.getType() == 137 || str.getType() == 138
                    || str.getType() == 139 || str.getType() == 140) {
                if (str.getText().contains("l") && str.getText().contains("1")) {
                    linea=str.getLine();
                    identifier=str.getText();
                }
            }
        }
        for(int i=0;i<in.length-1;i++){
            if(i==linea-1) {
                String identifierreplace=identifier.replace("l","L");
                out.add(in[i].replace(identifier,identifierreplace));
            }else {
                out.add(in[i]);
            }
        }
        writeTxt(out);
    }

    public void oper(CommonTokenStream tks) throws IOException{
        String [] in= tks.getTokens().get(0).getInputStream().toString().split("\n");
        List<String> out = new ArrayList<String>();

        for (int i=0; i<in.length;i++) {

            if(in[i].contains("==")){
                out.add(operEsp(in[i],"=="));
                continue;
            }

            if(in[i].contains("!=")){
                out.add(operEsp(in[i],"!="));
                continue;
            }

            if(in[i].contains("<=")){
                out.add(operEsp(in[i],"<="));
                continue;
            }

            if(in[i].contains(">=")){
                out.add(operEsp(in[i],">="));
                continue;
            }

            if(in[i].contains("&&")){
                out.add(operEsp(in[i],"&&"));
                continue;
            }

            if(in[i].contains("||")){
                out.add(operEsp(in[i],"||"));
                continue;
            }

            if(in[i].contains("<")){
                out.add(operEsp(in[i],"<"));
                continue;
            }

            if(in[i].contains(">")){
                out.add(operEsp(in[i],">"));
                continue;
            }

            out.add(in[i]);

        }
        writeTxt(out.subList(0,out.size()-1));

    }

    public String operEsp(String x, String op){
        int idx=0;
        for(int i=0; i<count_X(x,op);i++){
            if(x.indexOf(op,idx)>0){

                if(op=="<"){
                    if(x.charAt(x.indexOf(op,idx)-1)!=' ' && x.charAt(x.indexOf(op,idx)-1)!='<' && x.charAt(x.indexOf(op,idx)+1)!='<'){
                        x=x.replace(op," "+op+" ");
                    }
                    idx=x.indexOf(op);
                }else if(op==">"){
                    if(x.charAt(x.indexOf(op,idx)-1)!=' ' && x.charAt(x.indexOf(op,idx)-1)!='>' && x.charAt(x.indexOf(op,idx)+1)!='>'){
                        x=x.replace(op," "+op+" ");
                    }
                    idx=x.indexOf(op);
                }else {
                    if (x.charAt(x.indexOf(op, idx) - 1) != ' ') {
                        x = x.replace(op, " " + op);
                    }
                    if (x.charAt(x.indexOf(op, idx) + op.length()) != ' ') {
                        x = x.replace(op, op + " ");
                    }
                    idx = x.indexOf(op);
                }
            }
        }
        return x;
    }

    public int firstCharacter(String x){
        for(int i=0; i< x.length(); i++){
            if(x.charAt(i)!=' '){
                return i;
            }
        }
        return -1;
    }


    //******************************************************//
    //                                                      //
    //      Funcion que encuentra la frecuencia de un       //
    //      caracter/String en un String                    //
    //                                                      //
    //******************************************************//

    public static int count_X(String x, char b){
        int sum=0;
        for(int i=0; i<x.length();i++){
            if(x.charAt(i)==b){
                sum=sum+1;
            }
        }
        return sum;
    }

    public static int count_X(String x, String b){
        int sum=0;
        while(x.contains(b)){
            sum= sum+1;
            x=x.replace(b,"");
        }
        return sum;
    }


    //******************************************************//
    //                                                      //
    //      Funcion que edita el archivo input.txt          //
    //      recibiendo como parametro una lista de          //
    //      Strings.                                        //
    //                                                      //
    //******************************************************//

    public void writeTxt(List x) throws IOException {

        File archivo = new File("./sources/input.txt");
        FileWriter escribir = new FileWriter(archivo);

        for(int i=0; i<x.size();i++) {
            escribir.write(x.get(i)+"\n");
        }

        escribir.close();
    }

    //******************************************************//
    //                                                      //
    //      Funcion que genera un String con n espacion,    //
    //      usada para tabular dependiendo el nivel del     //
    //      corchete                                        //
    //                                                      //
    //******************************************************//

    public static String esp(int x){
        String out="";
        for(int i=0;i<x;i++){
            out=out+" ";
        }
        return out;
    }


}


