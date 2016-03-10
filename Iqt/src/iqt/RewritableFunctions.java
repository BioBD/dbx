package iqt;

import iqt.util.DatabaseFunctions;
import br.com.iqt.zql.ZConstant;
import br.com.iqt.zql.ZExp;
import br.com.iqt.zql.ZExpression;
import java.util.Vector;

/**
 *
 * @author Arlino
 */
public class RewritableFunctions {
    private int sgbg;
    String function;
    ZExpression subExpressionLeft;
    ZExp subExpressionRight;
    Vector tables;
    
    public RewritableFunctions(int database) {
        this.sgbg = database;
    }
    
    /**
     * Reescreve uma expressão de equivalência no formato Funcão([parâmetros]) = Sub-expressão.
     * A função deve, obrigatóriamente, está do lado esquerdo da expressão e um de seus parâmetros
     * deve ser uma coluna com índice. A  expressão será reescrita, deixando a coluna 
     * sozinha do lado esquerdo da expressão.
     * @param expression
     * Expressão de equivalência.
     * @param tables
     * Vetor de tabelas da SQL da expressão Where ou Having dada em <b>exp</b>. 
     * As tabelas são necessárias para que a coluna seja econtrada no SGBD e verificado 
     * se ela possui índice.
     * @return
     * Retorna ZExpression caso a expressão seja reescrita ou nulo caso a expressão não
     * seja reescrita.
     */
    public ZExpression rewriteFunction(ZExpression expression, Vector tables){
        if(expression == null)
            return null;
        
        //Se não for uma expressão com equivalencia, retorna nulo
        if(!(expression.getOperator().equals("=")))
            return null;
        
        ZExp subExpLeft = expression.getOperand(0);
        //Se for uma constante retorna nulo
        if(subExpLeft instanceof ZConstant)
            return null;
        subExpressionLeft = (ZExpression)subExpLeft;
        //Se não for uma expressão com uma função retorna nulo
        if(!(subExpressionLeft.getType() == ZExpression.FUNCTION))
            return null;
        
        subExpressionRight = expression.getOperand(1);
        function = subExpressionLeft.getOperator();
        this.tables = tables;
        
        switch(sgbg){
            case Dbms.POSTGRESQL:
                if(function.equalsIgnoreCase("CONVERT"))
                    return this.convertFunction();
                if(function.equalsIgnoreCase("UPPER"))
                    return this.upperFunction();
                break;
                
            case Dbms.SQLSERVER:
                if(function.equalsIgnoreCase("UPPER"))
                    return this.upperFunction();
                break;
                
            case Dbms.ORACLE: break;
//            case HeuristicSet.MYSQL: break;
        }
        
        return null;
    }
    
    
    //Reescreve CONVERT (SQL Server)
    //   CONVERT(Type01, ColumnIndex) = SubExpression -> ColumnIndex = CONVERT(Type02,SubExpression)
    private ZExpression convertFunction(){
        ZConstant operand = (ZConstant) subExpressionLeft.getOperand(1);
        String type = DatabaseFunctions.getType(operand.getValue(), tables);
        subExpressionRight = new ZExpression(function, new ZConstant(type, ZConstant.TYPENAME), subExpressionRight);
        ((ZExpression)subExpressionRight).setType(ZExpression.FUNCTION);
        ZExpression newExpression = new ZExpression("=", operand, subExpressionRight);
        return newExpression;
    }
    
    //Reescreve Upper (PostgreSQL, SQL Server)
    //   CONVERT(Type01, ColumnIndex) = SubExpression -> ColumnIndex = CONVERT(Type02,SubExpression)
    private ZExpression upperFunction(){
        //Se não for uma constante e não for uma string, retorna nulo
        if(!(subExpressionRight instanceof ZConstant) && (((ZConstant)subExpressionRight).getType() != ZConstant.STRING))
            return null;
        
        String valueRight = ((ZConstant)subExpressionRight).getValue();
        ZConstant operand = (ZConstant) subExpressionLeft.getOperand(0);
        ZExp subExpRight1 = new ZExpression("=", operand, new ZConstant(valueRight.toLowerCase(), ZConstant.STRING));
        ZExp subExpRight2 = new ZExpression("=", operand, new ZConstant(valueRight.toUpperCase(), ZConstant.STRING));
        ZExpression newExpression = new ZExpression("OR", subExpRight1, subExpRight2);
        return newExpression;
    }
}