// Generated from E:\Work\PUC\Tesis\Software\Tester\src\parser\gram_clauses.g4 by ANTLR 4.1
package parser;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link gram_clausesParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface gram_clausesVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link gram_clausesParser#clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClause(@NotNull gram_clausesParser.ClauseContext ctx);

	/**
	 * Visit a parse tree produced by {@link gram_clausesParser#datetype}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDatetype(@NotNull gram_clausesParser.DatetypeContext ctx);

	/**
	 * Visit a parse tree produced by {@link gram_clausesParser#number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumber(@NotNull gram_clausesParser.NumberContext ctx);

	/**
	 * Visit a parse tree produced by {@link gram_clausesParser#not}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNot(@NotNull gram_clausesParser.NotContext ctx);

	/**
	 * Visit a parse tree produced by {@link gram_clausesParser#string}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitString(@NotNull gram_clausesParser.StringContext ctx);

	/**
	 * Visit a parse tree produced by {@link gram_clausesParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(@NotNull gram_clausesParser.ExprContext ctx);

	/**
	 * Visit a parse tree produced by {@link gram_clausesParser#expr_LR}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr_LR(@NotNull gram_clausesParser.Expr_LRContext ctx);

	/**
	 * Visit a parse tree produced by {@link gram_clausesParser#attr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttr(@NotNull gram_clausesParser.AttrContext ctx);

	/**
	 * Visit a parse tree produced by {@link gram_clausesParser#clauses_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClauses_expr(@NotNull gram_clausesParser.Clauses_exprContext ctx);

	/**
	 * Visit a parse tree produced by {@link gram_clausesParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValue(@NotNull gram_clausesParser.ValueContext ctx);
}