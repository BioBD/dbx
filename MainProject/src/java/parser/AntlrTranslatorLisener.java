/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import java.util.Stack;
import java.util.ArrayList;

/**
 *
 * @author Alain
 */
public class AntlrTranslatorLisener extends gram_clausesBaseListener{   
    /* la idea va a ser colocar los conjuntos de nodos del arbol de sintaxis abstracta en una estructura
    que agrupe las condiciones para luego por grupo de a dos ir viendo cual es el padre que los une y si
    es un operador OR son dos intervales distintos (en ese se puede ver si uno cubre el otro es decir la
    intercepcion) y si tenemos un AND nos quedamos con la intercepcion y decimos que es una estructura de datos 
    que llamaremos de conjunto.
    Aqui hay que hacer distincion entre dato numerico y no numerico visto como string y numerico.
    
    La idea cambia en relacion a cada condicion ...
    */
    RootNode root;
    Stack<Node> stack;
     
    public AntlrTranslatorLisener(RootNode root){
        this.root = root;
        stack = new Stack<>();
        stack.push(root);
    }
    @Override
    public void enterClause(gram_clausesParser.ClauseContext ctx){
    }
    @Override
    public void exitClause(gram_clausesParser.ClauseContext ctx){
        Node act = stack.peek();
        Node leaf;
        if(ctx.number() != null){
            if(LeafCondition.parseOperator(ctx.OPERATOR().getText()) == EnumOperator.NEQ){
                LogicOperator or = new LogicOperator(EnumLogicOperator.OR, act);
                act.getChilds().add(or);
                
                LeafConditionNumber lt = new LeafConditionNumber(ctx.attr().NAME().getText(), 
                        EnumOperator.LT, 
                        Double.parseDouble(ctx.number().NUMBER().getText()), or);
                or.getChilds().add(lt);
                
                LeafConditionNumber gt = new LeafConditionNumber(ctx.attr().NAME().getText(), 
                        EnumOperator.GT, 
                        Double.parseDouble(ctx.number().NUMBER().getText()), or);
                or.getChilds().add(gt);
                return;
            }
            else {
                leaf = new LeafConditionNumber(ctx.attr().NAME().getText(), 
                LeafCondition.parseOperator(ctx.OPERATOR().getText()), 
                    Double.parseDouble(ctx.number().NUMBER().getText()), act);
                act.addChild(leaf);
            }
        }
        else if(ctx.string() != null){
            leaf = new LeafConditionString(ctx.attr().NAME().getText(), 
                LeafCondition.parseOperator(ctx.OPERATOR().getText()), 
                    ctx.string().STRING().getText(), act);
            act.addChild(leaf);
        }
        else if(ctx.datetype()!= null){
            leaf = new LeafConditionDate(ctx.attr().NAME().getText(), 
                LeafCondition.parseOperator(ctx.OPERATOR().getText()), 
                    ctx.datetype().STRING().getText(), act);
            act.addChild(leaf);
        }
        else{
            leaf = new LeafAttrCondition(ctx.attr().NAME().getText(), 
                LeafCondition.parseOperator(ctx.OPERATOR().getText()), 
                    ctx.NAME().getText(), act);
            act.addChild(leaf);
        }
        if(root.getLeafs().containsKey(ctx.attr().NAME().getText())){
            root.getLeafs().get(ctx.attr().NAME().getText()).add(leaf);
        }
        else{
            root.getLeafs().put(ctx.attr().NAME().getText(), new ArrayList<Node>());
            root.getLeafs().get(ctx.attr().NAME().getText()).add(leaf);
        }
    }
    
    @Override
    public void enterClauses_expr(gram_clausesParser.Clauses_exprContext ctx){
        Node act = stack.peek();
        if(ctx.clauses_expr() != null){
            Node node = new LogicOperator(LogicOperator.parseOperator(ctx.LOGIC_OPERATOR().getText()), act);
            act.addChild(node);
            stack.add(node);
        }
    }
    @Override
    public void exitClauses_expr(gram_clausesParser.Clauses_exprContext ctx){
        if(ctx.clauses_expr() != null){
            stack.pop();
        }
    }
    
    @Override 
    public void enterExpr(gram_clausesParser.ExprContext ctx){
        Node act = stack.peek();
        if(ctx.not() != null){
            Node node = new LogicOperator(EnumLogicOperator.NOT, act);
            act.addChild(node);
            stack.add(node);
        }
    }
    @Override 
    public void exitExpr(gram_clausesParser.ExprContext ctx){
        if(ctx.not() != null)
            stack.pop();
    }
    
}
