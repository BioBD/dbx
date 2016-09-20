// Generated from E:\Work\PUC\Tesis\Software\Tester\src\parser\gram_clauses.g4 by ANTLR 4.1
package parser;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class gram_clausesParser extends Parser {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NUMBER=1, STRING=2, LOGIC_OPERATOR=3, OPERATOR=4, WS=5, SP=6, L=7, R=8, 
		COMMA=9, A=10, B=11, AND=12, OR=13, NOT=14, NLT=15, NGT=16, LT=17, GT=18, 
		EQ=19, NEQ=20, UNDER=21, LIKE=22, VALUES=23, INSERT=24, INTO=25, DATETYPE=26, 
		NAME=27;
	public static final String[] tokenNames = {
		"<INVALID>", "NUMBER", "STRING", "LOGIC_OPERATOR", "OPERATOR", "WS", "SP", 
		"'('", "')'", "','", "'''", "'\"'", "'AND'", "'OR'", "'NOT'", "'>='", 
		"'<='", "'<'", "'>'", "'='", "NEQ", "'_'", "'LIKE'", "'VALUES'", "'INSERT'", 
		"'INTO'", "'DATE'", "NAME"
	};
	public static final int
		RULE_clauses_expr = 0, RULE_expr = 1, RULE_expr_LR = 2, RULE_clause = 3, 
		RULE_datetype = 4, RULE_attr = 5, RULE_value = 6, RULE_string = 7, RULE_number = 8, 
		RULE_not = 9;
	public static final String[] ruleNames = {
		"clauses_expr", "expr", "expr_LR", "clause", "datetype", "attr", "value", 
		"string", "number", "not"
	};

	@Override
	public String getGrammarFileName() { return "gram_clauses.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public gram_clausesParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class Clauses_exprContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public Clauses_exprContext clauses_expr() {
			return getRuleContext(Clauses_exprContext.class,0);
		}
		public TerminalNode LOGIC_OPERATOR() { return getToken(gram_clausesParser.LOGIC_OPERATOR, 0); }
		public Clauses_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_clauses_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof gram_clausesListener ) ((gram_clausesListener)listener).enterClauses_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof gram_clausesListener ) ((gram_clausesListener)listener).exitClauses_expr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof gram_clausesVisitor ) return ((gram_clausesVisitor<? extends T>)visitor).visitClauses_expr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Clauses_exprContext clauses_expr() throws RecognitionException {
		Clauses_exprContext _localctx = new Clauses_exprContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_clauses_expr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(20); expr();
			setState(23);
			_la = _input.LA(1);
			if (_la==LOGIC_OPERATOR) {
				{
				setState(21); match(LOGIC_OPERATOR);
				setState(22); clauses_expr();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExprContext extends ParserRuleContext {
		public Expr_LRContext expr_LR() {
			return getRuleContext(Expr_LRContext.class,0);
		}
		public NotContext not() {
			return getRuleContext(NotContext.class,0);
		}
		public ClauseContext clause() {
			return getRuleContext(ClauseContext.class,0);
		}
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof gram_clausesListener ) ((gram_clausesListener)listener).enterExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof gram_clausesListener ) ((gram_clausesListener)listener).exitExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof gram_clausesVisitor ) return ((gram_clausesVisitor<? extends T>)visitor).visitExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		ExprContext _localctx = new ExprContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_expr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(26);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(25); not();
				}
			}

			setState(30);
			switch (_input.LA(1)) {
			case L:
				{
				setState(28); expr_LR();
				}
				break;
			case NAME:
				{
				setState(29); clause();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Expr_LRContext extends ParserRuleContext {
		public Clauses_exprContext clauses_expr() {
			return getRuleContext(Clauses_exprContext.class,0);
		}
		public TerminalNode L() { return getToken(gram_clausesParser.L, 0); }
		public TerminalNode R() { return getToken(gram_clausesParser.R, 0); }
		public Expr_LRContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr_LR; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof gram_clausesListener ) ((gram_clausesListener)listener).enterExpr_LR(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof gram_clausesListener ) ((gram_clausesListener)listener).exitExpr_LR(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof gram_clausesVisitor ) return ((gram_clausesVisitor<? extends T>)visitor).visitExpr_LR(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Expr_LRContext expr_LR() throws RecognitionException {
		Expr_LRContext _localctx = new Expr_LRContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_expr_LR);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(32); match(L);
			setState(33); clauses_expr();
			setState(34); match(R);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ClauseContext extends ParserRuleContext {
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public AttrContext attr() {
			return getRuleContext(AttrContext.class,0);
		}
		public TerminalNode NAME() { return getToken(gram_clausesParser.NAME, 0); }
		public StringContext string() {
			return getRuleContext(StringContext.class,0);
		}
		public DatetypeContext datetype() {
			return getRuleContext(DatetypeContext.class,0);
		}
		public TerminalNode OPERATOR() { return getToken(gram_clausesParser.OPERATOR, 0); }
		public ClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof gram_clausesListener ) ((gram_clausesListener)listener).enterClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof gram_clausesListener ) ((gram_clausesListener)listener).exitClause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof gram_clausesVisitor ) return ((gram_clausesVisitor<? extends T>)visitor).visitClause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ClauseContext clause() throws RecognitionException {
		ClauseContext _localctx = new ClauseContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(36); attr();
			setState(37); match(OPERATOR);
			setState(42);
			switch (_input.LA(1)) {
			case STRING:
				{
				setState(38); string();
				}
				break;
			case NUMBER:
				{
				setState(39); number();
				}
				break;
			case DATETYPE:
				{
				setState(40); datetype();
				}
				break;
			case NAME:
				{
				setState(41); match(NAME);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DatetypeContext extends ParserRuleContext {
		public TerminalNode DATETYPE() { return getToken(gram_clausesParser.DATETYPE, 0); }
		public TerminalNode STRING() { return getToken(gram_clausesParser.STRING, 0); }
		public DatetypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_datetype; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof gram_clausesListener ) ((gram_clausesListener)listener).enterDatetype(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof gram_clausesListener ) ((gram_clausesListener)listener).exitDatetype(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof gram_clausesVisitor ) return ((gram_clausesVisitor<? extends T>)visitor).visitDatetype(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DatetypeContext datetype() throws RecognitionException {
		DatetypeContext _localctx = new DatetypeContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_datetype);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(44); match(DATETYPE);
			setState(45); match(STRING);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AttrContext extends ParserRuleContext {
		public TerminalNode NAME() { return getToken(gram_clausesParser.NAME, 0); }
		public AttrContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof gram_clausesListener ) ((gram_clausesListener)listener).enterAttr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof gram_clausesListener ) ((gram_clausesListener)listener).exitAttr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof gram_clausesVisitor ) return ((gram_clausesVisitor<? extends T>)visitor).visitAttr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AttrContext attr() throws RecognitionException {
		AttrContext _localctx = new AttrContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_attr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(47); match(NAME);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ValueContext extends ParserRuleContext {
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public StringContext string() {
			return getRuleContext(StringContext.class,0);
		}
		public ValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof gram_clausesListener ) ((gram_clausesListener)listener).enterValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof gram_clausesListener ) ((gram_clausesListener)listener).exitValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof gram_clausesVisitor ) return ((gram_clausesVisitor<? extends T>)visitor).visitValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValueContext value() throws RecognitionException {
		ValueContext _localctx = new ValueContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_value);
		try {
			setState(51);
			switch (_input.LA(1)) {
			case STRING:
				enterOuterAlt(_localctx, 1);
				{
				setState(49); string();
				}
				break;
			case NUMBER:
				enterOuterAlt(_localctx, 2);
				{
				setState(50); number();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StringContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(gram_clausesParser.STRING, 0); }
		public StringContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_string; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof gram_clausesListener ) ((gram_clausesListener)listener).enterString(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof gram_clausesListener ) ((gram_clausesListener)listener).exitString(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof gram_clausesVisitor ) return ((gram_clausesVisitor<? extends T>)visitor).visitString(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StringContext string() throws RecognitionException {
		StringContext _localctx = new StringContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_string);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(53); match(STRING);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NumberContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(gram_clausesParser.NUMBER, 0); }
		public NumberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_number; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof gram_clausesListener ) ((gram_clausesListener)listener).enterNumber(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof gram_clausesListener ) ((gram_clausesListener)listener).exitNumber(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof gram_clausesVisitor ) return ((gram_clausesVisitor<? extends T>)visitor).visitNumber(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NumberContext number() throws RecognitionException {
		NumberContext _localctx = new NumberContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_number);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(55); match(NUMBER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NotContext extends ParserRuleContext {
		public TerminalNode NOT() { return getToken(gram_clausesParser.NOT, 0); }
		public NotContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_not; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof gram_clausesListener ) ((gram_clausesListener)listener).enterNot(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof gram_clausesListener ) ((gram_clausesListener)listener).exitNot(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof gram_clausesVisitor ) return ((gram_clausesVisitor<? extends T>)visitor).visitNot(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NotContext not() throws RecognitionException {
		NotContext _localctx = new NotContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_not);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(57); match(NOT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\3\35>\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t\13\3"+
		"\2\3\2\3\2\5\2\32\n\2\3\3\5\3\35\n\3\3\3\3\3\5\3!\n\3\3\4\3\4\3\4\3\4"+
		"\3\5\3\5\3\5\3\5\3\5\3\5\5\5-\n\5\3\6\3\6\3\6\3\7\3\7\3\b\3\b\5\b\66\n"+
		"\b\3\t\3\t\3\n\3\n\3\13\3\13\3\13\2\f\2\4\6\b\n\f\16\20\22\24\2\2:\2\26"+
		"\3\2\2\2\4\34\3\2\2\2\6\"\3\2\2\2\b&\3\2\2\2\n.\3\2\2\2\f\61\3\2\2\2\16"+
		"\65\3\2\2\2\20\67\3\2\2\2\229\3\2\2\2\24;\3\2\2\2\26\31\5\4\3\2\27\30"+
		"\7\5\2\2\30\32\5\2\2\2\31\27\3\2\2\2\31\32\3\2\2\2\32\3\3\2\2\2\33\35"+
		"\5\24\13\2\34\33\3\2\2\2\34\35\3\2\2\2\35 \3\2\2\2\36!\5\6\4\2\37!\5\b"+
		"\5\2 \36\3\2\2\2 \37\3\2\2\2!\5\3\2\2\2\"#\7\t\2\2#$\5\2\2\2$%\7\n\2\2"+
		"%\7\3\2\2\2&\'\5\f\7\2\',\7\6\2\2(-\5\20\t\2)-\5\22\n\2*-\5\n\6\2+-\7"+
		"\35\2\2,(\3\2\2\2,)\3\2\2\2,*\3\2\2\2,+\3\2\2\2-\t\3\2\2\2./\7\34\2\2"+
		"/\60\7\4\2\2\60\13\3\2\2\2\61\62\7\35\2\2\62\r\3\2\2\2\63\66\5\20\t\2"+
		"\64\66\5\22\n\2\65\63\3\2\2\2\65\64\3\2\2\2\66\17\3\2\2\2\678\7\4\2\2"+
		"8\21\3\2\2\29:\7\3\2\2:\23\3\2\2\2;<\7\20\2\2<\25\3\2\2\2\7\31\34 ,\65";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}