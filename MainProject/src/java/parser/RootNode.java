/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import agents.sgbd.Table;
import indexableAttribute.IndexableAttrManager;
import parser.SetRestrictions.SetRestriction;
import parser.gram_clausesParser.Clauses_exprContext;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.SortedSet;
import java.util.TreeSet;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 *
 * @author Alain
 */
public class RootNode extends Node {
    Hashtable<String, ArrayList<Node>> attrs;
    
    public RootNode(){
        attrs = new Hashtable<String, ArrayList<Node>>();
    }
    public void setLeafs(Hashtable<String, ArrayList<Node>> attrs){
        this.attrs = attrs;
    }
    public Hashtable<String, ArrayList<Node>> getLeafs(){
        return attrs;
    }
    private void processComplement(){
        if (getChilds().get(0) instanceof LogicOperator){
            ((LogicOperator)(getChilds().get(0))).processComplement();
        }
    }
    public ArrayList<ArrayList<SetRestriction>> process(String restrictions){
        InputStream is = new ByteArrayInputStream(restrictions.toUpperCase().getBytes(StandardCharsets.UTF_8));
        try{
            CharStream cs = new ANTLRInputStream(is);   
            gram_clausesLexer lexer = new gram_clausesLexer(cs);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            gram_clausesParser parser = new gram_clausesParser(tokens);
            Clauses_exprContext exprContext=parser.clauses_expr();
            
            ParseTreeWalker walker = new ParseTreeWalker();
            AntlrTranslatorLisener lis = new AntlrTranslatorLisener(this);
            walker.walk(lis, exprContext);
            if(this.getChilds().get(0) instanceof LogicOperator){
                this.processComplement();
                this.processASTNode();
                //this.DFSTest(); // Solo para probar arbol correctamente estructurado
                return this.getIndexabelAttr();
                
            }
            else if (this.getChilds().get(0) instanceof LeafCondition){
                ArrayList<ArrayList<SetRestriction>> indexable = new ArrayList<ArrayList<SetRestriction>>();
                indexable.add(new ArrayList<SetRestriction>());
                getChilds().get(0).getIndexabelAttr(indexable);
                return indexable;
            }
            
        }catch(Exception e){
            System.out.println(e.getMessage());
        };
        return null;
    }
    public Node clone(){return null;}
    private boolean processASTNode(){
        if (getChilds().get(0) instanceof LogicOperator){
            return ((LogicOperator)(getChilds().get(0))).processASTNode();
        }
        return false;
    }
    private ArrayList<ArrayList<SetRestriction>> splitIndexable(ArrayList<ArrayList<SetRestriction>> indexabel){
        ArrayList<ArrayList<SetRestriction>> split = new ArrayList<ArrayList<SetRestriction>>();
        SortedSet<Integer> processed = new TreeSet<Integer>();
        for(int i=0;i<indexabel.size();i++){
            processed.contains(i);
        }
        return split;
    }
    protected void getIndexabelAttr(ArrayList<ArrayList<SetRestriction>> indexabel){
        if (getChilds().get(0) instanceof LogicOperator){
            ((LogicOperator)(getChilds().get(0))).getIndexabelAttr(indexabel);
        }
    }
    private ArrayList<ArrayList<SetRestriction>> getIndexabelAttr(){
        ArrayList<ArrayList<SetRestriction>> indexabel = new ArrayList<ArrayList<SetRestriction>>();
        
        getIndexabelAttr(indexabel);
        
        return indexabel;
    }
    private boolean equalClause(LeafCondition cond1, LeafCondition cond2){
        return antecesorOf(cond1).equals(antecesorOf(cond2));
    }
    private Node antecesorOf(Node leaf){
        Node actual = leaf;
        while(actual.getFather() != null && !(actual.getFather() instanceof RootNode) && 
                ((LogicOperator)actual.getFather()).getOperator() == EnumLogicOperator.AND){
            actual = actual.getFather();
        }
        return actual;
    }
    private void DFSTest(){
        if (getChilds().get(0) instanceof LogicOperator){
            ((LogicOperator)(getChilds().get(0))).DFSTest();
        }
    }
    @Override
    public String toString(){
        return "";
    }
}
