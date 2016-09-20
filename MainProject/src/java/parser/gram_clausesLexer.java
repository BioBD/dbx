// Generated from E:\Work\PUC\Tesis\Software\Tester\src\parser\gram_clauses.g4 by ANTLR 4.1
package parser;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class gram_clausesLexer extends Lexer {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NUMBER=1, STRING=2, LOGIC_OPERATOR=3, OPERATOR=4, WS=5, SP=6, L=7, R=8, 
		COMMA=9, A=10, B=11, AND=12, OR=13, NOT=14, NLT=15, NGT=16, LT=17, GT=18, 
		EQ=19, NEQ=20, UNDER=21, LIKE=22, VALUES=23, INSERT=24, INTO=25, DATETYPE=26, 
		NAME=27;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"NUMBER", "STRING", "LOGIC_OPERATOR", "OPERATOR", "WS", "SP", "'('", "')'", 
		"','", "'''", "'\"'", "'AND'", "'OR'", "'NOT'", "'>='", "'<='", "'<'", 
		"'>'", "'='", "NEQ", "'_'", "'LIKE'", "'VALUES'", "'INSERT'", "'INTO'", 
		"'DATE'", "NAME"
	};
	public static final String[] ruleNames = {
		"NUMBER", "STRING", "LOGIC_OPERATOR", "OPERATOR", "LETTER", "DIGIT", "WS", 
		"SP", "L", "R", "POINT", "COMMA", "A", "B", "AND", "OR", "NOT", "NLT", 
		"NGT", "LT", "GT", "EQ", "NEQ", "UNDER", "LIKE", "VALUES", "INSERT", "INTO", 
		"DATETYPE", "NAME"
	};


	public gram_clausesLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "gram_clauses.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 6: WS_action((RuleContext)_localctx, actionIndex); break;

		case 7: SP_action((RuleContext)_localctx, actionIndex); break;
		}
	}
	private void WS_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0: skip();  break;
		}
	}
	private void SP_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 1: skip();  break;
		}
	}

	public static final String _serializedATN =
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\2\35\u00c9\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\3\2"+
		"\6\2A\n\2\r\2\16\2B\3\2\3\2\6\2G\n\2\r\2\16\2H\5\2K\n\2\3\3\3\3\7\3O\n"+
		"\3\f\3\16\3R\13\3\3\3\3\3\3\4\3\4\3\4\5\4Y\n\4\3\4\3\4\3\5\3\5\3\5\3\5"+
		"\3\5\3\5\3\5\5\5d\n\5\3\6\3\6\3\7\3\7\3\b\6\bk\n\b\r\b\16\bl\3\b\3\b\3"+
		"\t\6\tr\n\t\r\t\16\ts\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3"+
		"\16\3\17\3\17\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3"+
		"\23\3\23\3\23\3\24\3\24\3\24\3\25\3\25\3\26\3\26\3\27\3\27\3\30\3\30\3"+
		"\30\3\30\5\30\u009f\n\30\3\31\3\31\3\32\3\32\3\32\3\32\3\32\3\33\3\33"+
		"\3\33\3\33\3\33\3\33\3\33\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\35\3\35"+
		"\3\35\3\35\3\35\3\36\3\36\3\36\3\36\3\36\3\37\3\37\3\37\3\37\3\37\7\37"+
		"\u00c5\n\37\f\37\16\37\u00c8\13\37\2 \3\3\1\5\4\1\7\5\1\t\6\1\13\2\1\r"+
		"\2\1\17\7\2\21\b\3\23\t\1\25\n\1\27\2\1\31\13\1\33\f\1\35\r\1\37\16\1"+
		"!\17\1#\20\1%\21\1\'\22\1)\23\1+\24\1-\25\1/\26\1\61\27\1\63\30\1\65\31"+
		"\1\67\32\19\33\1;\34\1=\35\1\3\2\6\3\2))\4\2C\\c|\3\2\62;\5\2\13\f\17"+
		"\17\"\"\u00d7\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\17\3\2"+
		"\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2"+
		"\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2"+
		"\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2"+
		"\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\3@\3\2\2"+
		"\2\5L\3\2\2\2\7U\3\2\2\2\tc\3\2\2\2\13e\3\2\2\2\rg\3\2\2\2\17j\3\2\2\2"+
		"\21q\3\2\2\2\23w\3\2\2\2\25y\3\2\2\2\27{\3\2\2\2\31}\3\2\2\2\33\177\3"+
		"\2\2\2\35\u0081\3\2\2\2\37\u0083\3\2\2\2!\u0087\3\2\2\2#\u008a\3\2\2\2"+
		"%\u008e\3\2\2\2\'\u0091\3\2\2\2)\u0094\3\2\2\2+\u0096\3\2\2\2-\u0098\3"+
		"\2\2\2/\u009e\3\2\2\2\61\u00a0\3\2\2\2\63\u00a2\3\2\2\2\65\u00a7\3\2\2"+
		"\2\67\u00ae\3\2\2\29\u00b5\3\2\2\2;\u00ba\3\2\2\2=\u00bf\3\2\2\2?A\5\r"+
		"\7\2@?\3\2\2\2AB\3\2\2\2B@\3\2\2\2BC\3\2\2\2CJ\3\2\2\2DF\5\27\f\2EG\5"+
		"\r\7\2FE\3\2\2\2GH\3\2\2\2HF\3\2\2\2HI\3\2\2\2IK\3\2\2\2JD\3\2\2\2JK\3"+
		"\2\2\2K\4\3\2\2\2LP\5\33\16\2MO\n\2\2\2NM\3\2\2\2OR\3\2\2\2PN\3\2\2\2"+
		"PQ\3\2\2\2QS\3\2\2\2RP\3\2\2\2ST\5\33\16\2T\6\3\2\2\2UX\5\21\t\2VY\5\37"+
		"\20\2WY\5!\21\2XV\3\2\2\2XW\3\2\2\2YZ\3\2\2\2Z[\5\21\t\2[\b\3\2\2\2\\"+
		"d\5%\23\2]d\5\'\24\2^d\5)\25\2_d\5+\26\2`d\5/\30\2ad\5-\27\2bd\5\63\32"+
		"\2c\\\3\2\2\2c]\3\2\2\2c^\3\2\2\2c_\3\2\2\2c`\3\2\2\2ca\3\2\2\2cb\3\2"+
		"\2\2d\n\3\2\2\2ef\t\3\2\2f\f\3\2\2\2gh\t\4\2\2h\16\3\2\2\2ik\t\5\2\2j"+
		"i\3\2\2\2kl\3\2\2\2lj\3\2\2\2lm\3\2\2\2mn\3\2\2\2no\b\b\2\2o\20\3\2\2"+
		"\2pr\7\"\2\2qp\3\2\2\2rs\3\2\2\2sq\3\2\2\2st\3\2\2\2tu\3\2\2\2uv\b\t\3"+
		"\2v\22\3\2\2\2wx\7*\2\2x\24\3\2\2\2yz\7+\2\2z\26\3\2\2\2{|\7\60\2\2|\30"+
		"\3\2\2\2}~\7.\2\2~\32\3\2\2\2\177\u0080\7)\2\2\u0080\34\3\2\2\2\u0081"+
		"\u0082\7$\2\2\u0082\36\3\2\2\2\u0083\u0084\7C\2\2\u0084\u0085\7P\2\2\u0085"+
		"\u0086\7F\2\2\u0086 \3\2\2\2\u0087\u0088\7Q\2\2\u0088\u0089\7T\2\2\u0089"+
		"\"\3\2\2\2\u008a\u008b\7P\2\2\u008b\u008c\7Q\2\2\u008c\u008d\7V\2\2\u008d"+
		"$\3\2\2\2\u008e\u008f\7@\2\2\u008f\u0090\7?\2\2\u0090&\3\2\2\2\u0091\u0092"+
		"\7>\2\2\u0092\u0093\7?\2\2\u0093(\3\2\2\2\u0094\u0095\7>\2\2\u0095*\3"+
		"\2\2\2\u0096\u0097\7@\2\2\u0097,\3\2\2\2\u0098\u0099\7?\2\2\u0099.\3\2"+
		"\2\2\u009a\u009b\7#\2\2\u009b\u009f\7?\2\2\u009c\u009d\7>\2\2\u009d\u009f"+
		"\7@\2\2\u009e\u009a\3\2\2\2\u009e\u009c\3\2\2\2\u009f\60\3\2\2\2\u00a0"+
		"\u00a1\7a\2\2\u00a1\62\3\2\2\2\u00a2\u00a3\7N\2\2\u00a3\u00a4\7K\2\2\u00a4"+
		"\u00a5\7M\2\2\u00a5\u00a6\7G\2\2\u00a6\64\3\2\2\2\u00a7\u00a8\7X\2\2\u00a8"+
		"\u00a9\7C\2\2\u00a9\u00aa\7N\2\2\u00aa\u00ab\7W\2\2\u00ab\u00ac\7G\2\2"+
		"\u00ac\u00ad\7U\2\2\u00ad\66\3\2\2\2\u00ae\u00af\7K\2\2\u00af\u00b0\7"+
		"P\2\2\u00b0\u00b1\7U\2\2\u00b1\u00b2\7G\2\2\u00b2\u00b3\7T\2\2\u00b3\u00b4"+
		"\7V\2\2\u00b48\3\2\2\2\u00b5\u00b6\7K\2\2\u00b6\u00b7\7P\2\2\u00b7\u00b8"+
		"\7V\2\2\u00b8\u00b9\7Q\2\2\u00b9:\3\2\2\2\u00ba\u00bb\7F\2\2\u00bb\u00bc"+
		"\7C\2\2\u00bc\u00bd\7V\2\2\u00bd\u00be\7G\2\2\u00be<\3\2\2\2\u00bf\u00c6"+
		"\5\13\6\2\u00c0\u00c5\5\61\31\2\u00c1\u00c5\5\13\6\2\u00c2\u00c5\5\r\7"+
		"\2\u00c3\u00c5\5\27\f\2\u00c4\u00c0\3\2\2\2\u00c4\u00c1\3\2\2\2\u00c4"+
		"\u00c2\3\2\2\2\u00c4\u00c3\3\2\2\2\u00c5\u00c8\3\2\2\2\u00c6\u00c4\3\2"+
		"\2\2\u00c6\u00c7\3\2\2\2\u00c7>\3\2\2\2\u00c8\u00c6\3\2\2\2\16\2BHJPX"+
		"cls\u009e\u00c4\u00c6";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}