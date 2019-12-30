import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import org.antlr.v4.runtime.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    @FXML private ImageView deletebut;
    @FXML private ImageView copybut;
    @FXML private TextArea textinput;
    @FXML private TextArea textoutput;
    @FXML private Button corregirbut;

    public void setfile() throws IOException {
        List<String> in = new ArrayList<String>();
        String [] hola=textinput.getText().split("\n");
        for(String str:hola){
            in.add(str);
        }
        Detector d = new Detector();
        d.writeTxt(in);
    }
    public void getfile() throws IOException {
        CommonTokenStream tks = read();
        String resultado="";
        String [] lista= tks.getTokens().get(0).getInputStream().toString().split("\n");
        for(String str:lista){
            resultado+=str+"\n";
        }
        textoutput.setText(resultado);
    }
    public void ondelete(MouseEvent event){
        textinput.setText("");
    }
    public void oncopy(MouseEvent event){
        final ClipboardContent content = new ClipboardContent();
        content.putString(textoutput.getText());
        Clipboard.getSystemClipboard().setContent(content);
    }
    public void onbutton(MouseEvent event) throws Exception{
        setfile();
        Detector d = new Detector();
        CommonTokenStream tokens= read();
        d.instruction_by_line(tokens);
        tokens=read();
        d.brackets_indentation(tokens);
        tokens=read();
        d.vertical_align(tokens);
        tokens=read();
        d.L_changer(tokens);
        tokens=read();
        d.conditionals(tokens);
        tokens=read();
        d.loops(tokens);
        tokens=read();
        d.oper(tokens);
        getfile();
    }
    public static CommonTokenStream read() throws IOException {
        System.setIn(new FileInputStream(new File("sources/input.txt")));
        ANTLRInputStream input= new ANTLRInputStream(System.in);
        CPP14Lexer lexer= new CPP14Lexer(input);
        CommonTokenStream tokens= new CommonTokenStream(lexer);
        CPP14Parser parser= new CPP14Parser(tokens);
        tokens.fill();
        return tokens;
    }
}
