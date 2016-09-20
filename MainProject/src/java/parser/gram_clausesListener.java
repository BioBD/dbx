// Generated from E:\Work\PUC\Tesis\Software\Tester\src\parser\gram_clauses.g4 by ANTLR 4.1
package parser;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link gram_clausesParser}.
 */
public interface gram_clausesListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link gram_clausesParser#clause}.
	 * @param ctx the parse tree
	 */
	void enterClause(@NotNull gram_clausesParser.ClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link gram_clausesParser#clause}.
	 * @param ctx the parse tree
	 */
	void exitClause(@NotNull gram_clausesParser.ClauseContext ctx);

	/**
	 * Enter a parse tree produced by {@link gram_clausesParser#datetype}.
	 * @param ctx the parse tree
	 */
	void enterDatetype(@NotNull gram_clausesParser.DatetypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link gram_clausesParser#datetype}.
	 * @param ctx the parse tree
	 */
	void exitDatetype(@NotNull gram_clausesParser.DatetypeContext ctx);

	/**
	 * Enter a parse tree produced by {@link gram_clausesParser#number}.
	 * @param ctx the parse tree
	 */
	void enterNumber(@NotNull gram_clausesParser.NumberContext ctx);
	/**
	 * Exit a parse tree produced by {@link gram_clausesParser#number}.
	 * @param ctx the parse tree
	 */
	void exitNumber(@NotNull gram_clausesParser.NumberContext ctx);

	/**
	 * Enter a parse tree produced by {@link gram_clausesParser#not}.
	 * @param ctx the parse tree
	 */
	void enterNot(@NotNull gram_clausesParser.NotContext ctx);
	/**
	 * Exit a parse tree produced by {@link gram_clausesParser#not}.
	 * @param ctx the parse tree
	 */
	void exitNot(@NotNull gram_clausesParser.NotContext ctx);

	/**
	 * Enter a parse tree produced by {@link gram_clausesParser#string}.
	 * @param ctx the parse tree
	 */
	void enterString(@NotNull gram_clausesParser.StringContext ctx);
	/**
	 * Exit a parse tree produced by {@link gram_clausesParser#string}.
	 * @param ctx the parse tree
	 */
	void exitString(@NotNull gram_clausesParser.StringContext ctx);

	/**
	 * Enter a parse tree produced by {@link gram_clausesParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(@NotNull gram_clausesParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link gram_clausesParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(@NotNull gram_clausesParser.ExprContext ctx);

	/**
	 * Enter a parse tree produced by {@link gram_clausesParser#expr_LR}.
	 * @param ctx the parse tree
	 */
	void enterExpr_LR(@NotNull gram_clausesParser.Expr_LRContext ctx);
	/**
	 * Exit a parse tree produced by {@link gram_clausesParser#expr_LR}.
	 * @param ctx the parse tree
	 */
	void exitExpr_LR(@NotNull gram_clausesParser.Expr_LRContext ctx);

	/**
	 * Enter a parse tree produced by {@link gram_clausesParser#attr}.
	 * @param ctx the parse tree
	 */
	void enterAttr(@NotNull gram_clausesParser.AttrContext ctx);
	/**
	 * Exit a parse tree produced by {@link gram_clausesParser#attr}.
	 * @param ctx the parse tree
	 */
	void exitAttr(@NotNull gram_clausesParser.AttrContext ctx);

	/**
	 * Enter a parse tree produced by {@link gram_clausesParser#clauses_expr}.
	 * @param ctx the parse tree
	 */
	void enterClauses_expr(@NotNull gram_clausesParser.Clauses_exprContext ctx);
	/**
	 * Exit a parse tree produced by {@link gram_clausesParser#clauses_expr}.
	 * @param ctx the parse tree
	 */
	void exitClauses_expr(@NotNull gram_clausesParser.Clauses_exprContext ctx);

	/**
	 * Enter a parse tree produced by {@link gram_clausesParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValue(@NotNull gram_clausesParser.ValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link gram_clausesParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValue(@NotNull gram_clausesParser.ValueContext ctx);
}