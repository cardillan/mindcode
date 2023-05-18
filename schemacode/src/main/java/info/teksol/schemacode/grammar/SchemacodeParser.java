// Generated from Schemacode.g4 by ANTLR 4.9.1
package info.teksol.schemacode.grammar;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SchemacodeParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.9.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		As=1, At=2, Connected=3, Description=4, Dimensions=5, Disabled=6, Enabled=7, 
		End=8, Facing=9, File=10, Item=11, Links=12, Liquid=13, Logic=14, Mindcode=15, 
		Mlog=16, Name=17, Processor=18, Schematic=19, Tag=20, Text=21, To=22, 
		Virtual=23, Assign=24, Colon=25, Comma=26, Dot=27, Minus=28, Plus=29, 
		North=30, South=31, East=32, West=33, LeftParen=34, RightParen=35, TextBlock1=36, 
		TextBlock2=37, TextLine=38, Int=39, Id=40, Ref=41, Pattern=42, SLComment=43, 
		Ws=44, Any=45;
	public static final int
		RULE_definitions = 0, RULE_definition = 1, RULE_schematic = 2, RULE_schematicItem = 3, 
		RULE_attribute = 4, RULE_block = 5, RULE_labelList = 6, RULE_position = 7, 
		RULE_coordinates = 8, RULE_relativeCoordinates = 9, RULE_coordinatesRelativeTo = 10, 
		RULE_direction = 11, RULE_configuration = 12, RULE_connectionList = 13, 
		RULE_connection = 14, RULE_processor = 15, RULE_processorLinks = 16, RULE_linkDef = 17, 
		RULE_program = 18, RULE_textDef = 19, RULE_stringValue = 20, RULE_stringLiteral = 21;
	private static String[] makeRuleNames() {
		return new String[] {
			"definitions", "definition", "schematic", "schematicItem", "attribute", 
			"block", "labelList", "position", "coordinates", "relativeCoordinates", 
			"coordinatesRelativeTo", "direction", "configuration", "connectionList", 
			"connection", "processor", "processorLinks", "linkDef", "program", "textDef", 
			"stringValue", "stringLiteral"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'as'", "'at'", "'connected'", "'description'", "'dimensions'", 
			"'disabled'", "'enabled'", "'end'", "'facing'", "'file'", "'item'", "'links'", 
			"'liquid'", "'logic'", "'mindcode'", "'mlog'", "'name'", "'processor'", 
			"'schematic'", "'tag'", "'text'", "'to'", "'virtual'", "'='", "':'", 
			"','", "'.'", "'-'", "'+'", "'north'", "'south'", "'east'", "'west'", 
			"'('", "')'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "As", "At", "Connected", "Description", "Dimensions", "Disabled", 
			"Enabled", "End", "Facing", "File", "Item", "Links", "Liquid", "Logic", 
			"Mindcode", "Mlog", "Name", "Processor", "Schematic", "Tag", "Text", 
			"To", "Virtual", "Assign", "Colon", "Comma", "Dot", "Minus", "Plus", 
			"North", "South", "East", "West", "LeftParen", "RightParen", "TextBlock1", 
			"TextBlock2", "TextLine", "Int", "Id", "Ref", "Pattern", "SLComment", 
			"Ws", "Any"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "Schemacode.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public SchemacodeParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class DefinitionsContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(SchemacodeParser.EOF, 0); }
		public List<DefinitionContext> definition() {
			return getRuleContexts(DefinitionContext.class);
		}
		public DefinitionContext definition(int i) {
			return getRuleContext(DefinitionContext.class,i);
		}
		public DefinitionsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_definitions; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterDefinitions(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitDefinitions(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitDefinitions(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DefinitionsContext definitions() throws RecognitionException {
		DefinitionsContext _localctx = new DefinitionsContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_definitions);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(45); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(44);
				definition();
				}
				}
				setState(47); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==Schematic || _la==Id );
			setState(49);
			match(EOF);
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

	public static class DefinitionContext extends ParserRuleContext {
		public SchematicContext schematic() {
			return getRuleContext(SchematicContext.class,0);
		}
		public StringValueContext stringValue() {
			return getRuleContext(StringValueContext.class,0);
		}
		public DefinitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_definition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterDefinition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitDefinition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitDefinition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DefinitionContext definition() throws RecognitionException {
		DefinitionContext _localctx = new DefinitionContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_definition);
		try {
			setState(53);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(51);
				schematic();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(52);
				stringValue();
				}
				break;
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

	public static class SchematicContext extends ParserRuleContext {
		public Token name;
		public SchematicItemContext items;
		public TerminalNode Schematic() { return getToken(SchemacodeParser.Schematic, 0); }
		public TerminalNode End() { return getToken(SchemacodeParser.End, 0); }
		public TerminalNode Colon() { return getToken(SchemacodeParser.Colon, 0); }
		public TerminalNode Id() { return getToken(SchemacodeParser.Id, 0); }
		public List<SchematicItemContext> schematicItem() {
			return getRuleContexts(SchematicItemContext.class);
		}
		public SchematicItemContext schematicItem(int i) {
			return getRuleContext(SchematicItemContext.class,i);
		}
		public SchematicContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_schematic; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterSchematic(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitSchematic(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitSchematic(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SchematicContext schematic() throws RecognitionException {
		SchematicContext _localctx = new SchematicContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_schematic);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(57);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Id) {
				{
				setState(55);
				((SchematicContext)_localctx).name = match(Id);
				setState(56);
				match(Colon);
				}
			}

			setState(59);
			match(Schematic);
			setState(61); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(60);
				((SchematicContext)_localctx).items = schematicItem();
				}
				}
				setState(63); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Description) | (1L << Dimensions) | (1L << Name) | (1L << Tag) | (1L << Id) | (1L << Ref))) != 0) );
			setState(65);
			match(End);
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

	public static class SchematicItemContext extends ParserRuleContext {
		public AttributeContext attribute() {
			return getRuleContext(AttributeContext.class,0);
		}
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public SchematicItemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_schematicItem; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterSchematicItem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitSchematicItem(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitSchematicItem(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SchematicItemContext schematicItem() throws RecognitionException {
		SchematicItemContext _localctx = new SchematicItemContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_schematicItem);
		try {
			setState(69);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Description:
			case Dimensions:
			case Name:
			case Tag:
				enterOuterAlt(_localctx, 1);
				{
				setState(67);
				attribute();
				}
				break;
			case Id:
			case Ref:
				enterOuterAlt(_localctx, 2);
				{
				setState(68);
				block();
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

	public static class AttributeContext extends ParserRuleContext {
		public AttributeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attribute; }
	 
		public AttributeContext() { }
		public void copyFrom(AttributeContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class NameContext extends AttributeContext {
		public TextDefContext name;
		public TerminalNode Name() { return getToken(SchemacodeParser.Name, 0); }
		public TerminalNode Assign() { return getToken(SchemacodeParser.Assign, 0); }
		public TextDefContext textDef() {
			return getRuleContext(TextDefContext.class,0);
		}
		public NameContext(AttributeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SchemaTagContext extends AttributeContext {
		public TextDefContext tag;
		public TerminalNode Tag() { return getToken(SchemacodeParser.Tag, 0); }
		public TerminalNode Assign() { return getToken(SchemacodeParser.Assign, 0); }
		public TextDefContext textDef() {
			return getRuleContext(TextDefContext.class,0);
		}
		public SchemaTagContext(AttributeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterSchemaTag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitSchemaTag(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitSchemaTag(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DescriptionContext extends AttributeContext {
		public TextDefContext description;
		public TerminalNode Description() { return getToken(SchemacodeParser.Description, 0); }
		public TerminalNode Assign() { return getToken(SchemacodeParser.Assign, 0); }
		public TextDefContext textDef() {
			return getRuleContext(TextDefContext.class,0);
		}
		public DescriptionContext(AttributeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterDescription(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitDescription(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitDescription(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DimensionsContext extends AttributeContext {
		public TerminalNode Dimensions() { return getToken(SchemacodeParser.Dimensions, 0); }
		public TerminalNode Assign() { return getToken(SchemacodeParser.Assign, 0); }
		public CoordinatesContext coordinates() {
			return getRuleContext(CoordinatesContext.class,0);
		}
		public DimensionsContext(AttributeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterDimensions(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitDimensions(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitDimensions(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AttributeContext attribute() throws RecognitionException {
		AttributeContext _localctx = new AttributeContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_attribute);
		try {
			setState(83);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Name:
				_localctx = new NameContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(71);
				match(Name);
				setState(72);
				match(Assign);
				setState(73);
				((NameContext)_localctx).name = textDef();
				}
				break;
			case Description:
				_localctx = new DescriptionContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(74);
				match(Description);
				setState(75);
				match(Assign);
				setState(76);
				((DescriptionContext)_localctx).description = textDef();
				}
				break;
			case Dimensions:
				_localctx = new DimensionsContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(77);
				match(Dimensions);
				setState(78);
				match(Assign);
				setState(79);
				coordinates();
				}
				break;
			case Tag:
				_localctx = new SchemaTagContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(80);
				match(Tag);
				setState(81);
				match(Assign);
				setState(82);
				((SchemaTagContext)_localctx).tag = textDef();
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

	public static class BlockContext extends ParserRuleContext {
		public LabelListContext labels;
		public Token type;
		public PositionContext pos;
		public DirectionContext dir;
		public ConfigurationContext cfg;
		public TerminalNode At() { return getToken(SchemacodeParser.At, 0); }
		public TerminalNode Ref() { return getToken(SchemacodeParser.Ref, 0); }
		public PositionContext position() {
			return getRuleContext(PositionContext.class,0);
		}
		public LabelListContext labelList() {
			return getRuleContext(LabelListContext.class,0);
		}
		public DirectionContext direction() {
			return getRuleContext(DirectionContext.class,0);
		}
		public ConfigurationContext configuration() {
			return getRuleContext(ConfigurationContext.class,0);
		}
		public BlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BlockContext block() throws RecognitionException {
		BlockContext _localctx = new BlockContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(86);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Id) {
				{
				setState(85);
				((BlockContext)_localctx).labels = labelList();
				}
			}

			setState(88);
			((BlockContext)_localctx).type = match(Ref);
			setState(89);
			match(At);
			setState(90);
			((BlockContext)_localctx).pos = position();
			setState(92);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Facing) {
				{
				setState(91);
				((BlockContext)_localctx).dir = direction();
				}
			}

			setState(95);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Connected) | (1L << Disabled) | (1L << Enabled) | (1L << Item) | (1L << Liquid) | (1L << Processor) | (1L << Text) | (1L << Virtual))) != 0)) {
				{
				setState(94);
				((BlockContext)_localctx).cfg = configuration();
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

	public static class LabelListContext extends ParserRuleContext {
		public List<TerminalNode> Id() { return getTokens(SchemacodeParser.Id); }
		public TerminalNode Id(int i) {
			return getToken(SchemacodeParser.Id, i);
		}
		public TerminalNode Colon() { return getToken(SchemacodeParser.Colon, 0); }
		public List<TerminalNode> Comma() { return getTokens(SchemacodeParser.Comma); }
		public TerminalNode Comma(int i) {
			return getToken(SchemacodeParser.Comma, i);
		}
		public LabelListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_labelList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterLabelList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitLabelList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitLabelList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LabelListContext labelList() throws RecognitionException {
		LabelListContext _localctx = new LabelListContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_labelList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(97);
			match(Id);
			setState(102);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Comma) {
				{
				{
				setState(98);
				match(Comma);
				setState(99);
				match(Id);
				}
				}
				setState(104);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(105);
			match(Colon);
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

	public static class PositionContext extends ParserRuleContext {
		public CoordinatesContext coordinates() {
			return getRuleContext(CoordinatesContext.class,0);
		}
		public RelativeCoordinatesContext relativeCoordinates() {
			return getRuleContext(RelativeCoordinatesContext.class,0);
		}
		public CoordinatesRelativeToContext coordinatesRelativeTo() {
			return getRuleContext(CoordinatesRelativeToContext.class,0);
		}
		public PositionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_position; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterPosition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitPosition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitPosition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PositionContext position() throws RecognitionException {
		PositionContext _localctx = new PositionContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_position);
		try {
			setState(110);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LeftParen:
				enterOuterAlt(_localctx, 1);
				{
				setState(107);
				coordinates();
				}
				break;
			case Minus:
			case Plus:
				enterOuterAlt(_localctx, 2);
				{
				setState(108);
				relativeCoordinates();
				}
				break;
			case Id:
				enterOuterAlt(_localctx, 3);
				{
				setState(109);
				coordinatesRelativeTo();
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

	public static class CoordinatesContext extends ParserRuleContext {
		public Token x;
		public Token y;
		public TerminalNode LeftParen() { return getToken(SchemacodeParser.LeftParen, 0); }
		public TerminalNode Comma() { return getToken(SchemacodeParser.Comma, 0); }
		public TerminalNode RightParen() { return getToken(SchemacodeParser.RightParen, 0); }
		public List<TerminalNode> Int() { return getTokens(SchemacodeParser.Int); }
		public TerminalNode Int(int i) {
			return getToken(SchemacodeParser.Int, i);
		}
		public CoordinatesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_coordinates; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterCoordinates(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitCoordinates(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitCoordinates(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CoordinatesContext coordinates() throws RecognitionException {
		CoordinatesContext _localctx = new CoordinatesContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_coordinates);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(112);
			match(LeftParen);
			setState(113);
			((CoordinatesContext)_localctx).x = match(Int);
			setState(114);
			match(Comma);
			setState(115);
			((CoordinatesContext)_localctx).y = match(Int);
			setState(116);
			match(RightParen);
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

	public static class RelativeCoordinatesContext extends ParserRuleContext {
		public Token op;
		public CoordinatesContext coord;
		public CoordinatesContext coordinates() {
			return getRuleContext(CoordinatesContext.class,0);
		}
		public TerminalNode Plus() { return getToken(SchemacodeParser.Plus, 0); }
		public TerminalNode Minus() { return getToken(SchemacodeParser.Minus, 0); }
		public RelativeCoordinatesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relativeCoordinates; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterRelativeCoordinates(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitRelativeCoordinates(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitRelativeCoordinates(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RelativeCoordinatesContext relativeCoordinates() throws RecognitionException {
		RelativeCoordinatesContext _localctx = new RelativeCoordinatesContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_relativeCoordinates);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(118);
			((RelativeCoordinatesContext)_localctx).op = _input.LT(1);
			_la = _input.LA(1);
			if ( !(_la==Minus || _la==Plus) ) {
				((RelativeCoordinatesContext)_localctx).op = (Token)_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(119);
			((RelativeCoordinatesContext)_localctx).coord = coordinates();
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

	public static class CoordinatesRelativeToContext extends ParserRuleContext {
		public Token label;
		public RelativeCoordinatesContext relCoord;
		public TerminalNode Id() { return getToken(SchemacodeParser.Id, 0); }
		public RelativeCoordinatesContext relativeCoordinates() {
			return getRuleContext(RelativeCoordinatesContext.class,0);
		}
		public CoordinatesRelativeToContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_coordinatesRelativeTo; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterCoordinatesRelativeTo(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitCoordinatesRelativeTo(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitCoordinatesRelativeTo(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CoordinatesRelativeToContext coordinatesRelativeTo() throws RecognitionException {
		CoordinatesRelativeToContext _localctx = new CoordinatesRelativeToContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_coordinatesRelativeTo);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(121);
			((CoordinatesRelativeToContext)_localctx).label = match(Id);
			setState(122);
			((CoordinatesRelativeToContext)_localctx).relCoord = relativeCoordinates();
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

	public static class DirectionContext extends ParserRuleContext {
		public Token dir;
		public TerminalNode Facing() { return getToken(SchemacodeParser.Facing, 0); }
		public TerminalNode North() { return getToken(SchemacodeParser.North, 0); }
		public TerminalNode South() { return getToken(SchemacodeParser.South, 0); }
		public TerminalNode East() { return getToken(SchemacodeParser.East, 0); }
		public TerminalNode West() { return getToken(SchemacodeParser.West, 0); }
		public DirectionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_direction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterDirection(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitDirection(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitDirection(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DirectionContext direction() throws RecognitionException {
		DirectionContext _localctx = new DirectionContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_direction);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(124);
			match(Facing);
			setState(125);
			((DirectionContext)_localctx).dir = _input.LT(1);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << North) | (1L << South) | (1L << East) | (1L << West))) != 0)) ) {
				((DirectionContext)_localctx).dir = (Token)_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
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

	public static class ConfigurationContext extends ParserRuleContext {
		public ConfigurationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_configuration; }
	 
		public ConfigurationContext() { }
		public void copyFrom(ConfigurationContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class VirtualContext extends ConfigurationContext {
		public TerminalNode Virtual() { return getToken(SchemacodeParser.Virtual, 0); }
		public VirtualContext(ConfigurationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterVirtual(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitVirtual(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitVirtual(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ItemContext extends ConfigurationContext {
		public TerminalNode Item() { return getToken(SchemacodeParser.Item, 0); }
		public TerminalNode Ref() { return getToken(SchemacodeParser.Ref, 0); }
		public ItemContext(ConfigurationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterItem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitItem(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitItem(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BooleanContext extends ConfigurationContext {
		public Token status;
		public TerminalNode Enabled() { return getToken(SchemacodeParser.Enabled, 0); }
		public TerminalNode Disabled() { return getToken(SchemacodeParser.Disabled, 0); }
		public BooleanContext(ConfigurationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterBoolean(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitBoolean(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitBoolean(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LiquidContext extends ConfigurationContext {
		public TerminalNode Liquid() { return getToken(SchemacodeParser.Liquid, 0); }
		public TerminalNode Ref() { return getToken(SchemacodeParser.Ref, 0); }
		public LiquidContext(ConfigurationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterLiquid(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitLiquid(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitLiquid(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class TextContext extends ConfigurationContext {
		public TextDefContext text;
		public TerminalNode Text() { return getToken(SchemacodeParser.Text, 0); }
		public TextDefContext textDef() {
			return getRuleContext(TextDefContext.class,0);
		}
		public TextContext(ConfigurationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterText(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitText(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitText(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LogicContext extends ConfigurationContext {
		public ProcessorContext def;
		public ProcessorContext processor() {
			return getRuleContext(ProcessorContext.class,0);
		}
		public LogicContext(ConfigurationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterLogic(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitLogic(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitLogic(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ConnectionsContext extends ConfigurationContext {
		public TerminalNode Connected() { return getToken(SchemacodeParser.Connected, 0); }
		public TerminalNode To() { return getToken(SchemacodeParser.To, 0); }
		public ConnectionListContext connectionList() {
			return getRuleContext(ConnectionListContext.class,0);
		}
		public ConnectionsContext(ConfigurationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterConnections(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitConnections(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitConnections(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConfigurationContext configuration() throws RecognitionException {
		ConfigurationContext _localctx = new ConfigurationContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_configuration);
		int _la;
		try {
			setState(139);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Virtual:
				_localctx = new VirtualContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(127);
				match(Virtual);
				}
				break;
			case Connected:
				_localctx = new ConnectionsContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(128);
				match(Connected);
				setState(129);
				match(To);
				setState(130);
				connectionList();
				}
				break;
			case Item:
				_localctx = new ItemContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(131);
				match(Item);
				setState(132);
				match(Ref);
				}
				break;
			case Liquid:
				_localctx = new LiquidContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(133);
				match(Liquid);
				setState(134);
				match(Ref);
				}
				break;
			case Text:
				_localctx = new TextContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(135);
				match(Text);
				setState(136);
				((TextContext)_localctx).text = textDef();
				}
				break;
			case Disabled:
			case Enabled:
				_localctx = new BooleanContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(137);
				((BooleanContext)_localctx).status = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==Disabled || _la==Enabled) ) {
					((BooleanContext)_localctx).status = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				break;
			case Processor:
				_localctx = new LogicContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(138);
				((LogicContext)_localctx).def = processor();
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

	public static class ConnectionListContext extends ParserRuleContext {
		public List<ConnectionContext> connection() {
			return getRuleContexts(ConnectionContext.class);
		}
		public ConnectionContext connection(int i) {
			return getRuleContext(ConnectionContext.class,i);
		}
		public List<TerminalNode> Comma() { return getTokens(SchemacodeParser.Comma); }
		public TerminalNode Comma(int i) {
			return getToken(SchemacodeParser.Comma, i);
		}
		public ConnectionListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_connectionList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterConnectionList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitConnectionList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitConnectionList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConnectionListContext connectionList() throws RecognitionException {
		ConnectionListContext _localctx = new ConnectionListContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_connectionList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(141);
			connection();
			setState(146);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Comma) {
				{
				{
				setState(142);
				match(Comma);
				setState(143);
				connection();
				}
				}
				setState(148);
				_errHandler.sync(this);
				_la = _input.LA(1);
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

	public static class ConnectionContext extends ParserRuleContext {
		public ConnectionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_connection; }
	 
		public ConnectionContext() { }
		public void copyFrom(ConnectionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ConnAbsContext extends ConnectionContext {
		public CoordinatesContext coordinates() {
			return getRuleContext(CoordinatesContext.class,0);
		}
		public ConnAbsContext(ConnectionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterConnAbs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitConnAbs(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitConnAbs(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ConnNameContext extends ConnectionContext {
		public TerminalNode Id() { return getToken(SchemacodeParser.Id, 0); }
		public ConnNameContext(ConnectionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterConnName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitConnName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitConnName(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ConnRelContext extends ConnectionContext {
		public RelativeCoordinatesContext relativeCoordinates() {
			return getRuleContext(RelativeCoordinatesContext.class,0);
		}
		public ConnRelContext(ConnectionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterConnRel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitConnRel(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitConnRel(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConnectionContext connection() throws RecognitionException {
		ConnectionContext _localctx = new ConnectionContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_connection);
		try {
			setState(152);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LeftParen:
				_localctx = new ConnAbsContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(149);
				coordinates();
				}
				break;
			case Minus:
			case Plus:
				_localctx = new ConnRelContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(150);
				relativeCoordinates();
				}
				break;
			case Id:
				_localctx = new ConnNameContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(151);
				match(Id);
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

	public static class ProcessorContext extends ParserRuleContext {
		public ProcessorLinksContext links;
		public ProgramContext mindcode;
		public ProgramContext mlog;
		public TerminalNode Processor() { return getToken(SchemacodeParser.Processor, 0); }
		public TerminalNode End() { return getToken(SchemacodeParser.End, 0); }
		public TerminalNode Mindcode() { return getToken(SchemacodeParser.Mindcode, 0); }
		public TerminalNode Mlog() { return getToken(SchemacodeParser.Mlog, 0); }
		public ProcessorLinksContext processorLinks() {
			return getRuleContext(ProcessorLinksContext.class,0);
		}
		public ProgramContext program() {
			return getRuleContext(ProgramContext.class,0);
		}
		public ProcessorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_processor; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterProcessor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitProcessor(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitProcessor(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProcessorContext processor() throws RecognitionException {
		ProcessorContext _localctx = new ProcessorContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_processor);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(154);
			match(Processor);
			setState(156);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Links) {
				{
				setState(155);
				((ProcessorContext)_localctx).links = processorLinks();
				}
			}

			setState(162);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Mindcode:
				{
				setState(158);
				match(Mindcode);
				setState(159);
				((ProcessorContext)_localctx).mindcode = program();
				}
				break;
			case Mlog:
				{
				setState(160);
				match(Mlog);
				setState(161);
				((ProcessorContext)_localctx).mlog = program();
				}
				break;
			case End:
				break;
			default:
				break;
			}
			setState(164);
			match(End);
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

	public static class ProcessorLinksContext extends ParserRuleContext {
		public TerminalNode Links() { return getToken(SchemacodeParser.Links, 0); }
		public TerminalNode End() { return getToken(SchemacodeParser.End, 0); }
		public List<LinkDefContext> linkDef() {
			return getRuleContexts(LinkDefContext.class);
		}
		public LinkDefContext linkDef(int i) {
			return getRuleContext(LinkDefContext.class,i);
		}
		public ProcessorLinksContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_processorLinks; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterProcessorLinks(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitProcessorLinks(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitProcessorLinks(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProcessorLinksContext processorLinks() throws RecognitionException {
		ProcessorLinksContext _localctx = new ProcessorLinksContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_processorLinks);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(166);
			match(Links);
			setState(170);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Minus) | (1L << Plus) | (1L << LeftParen) | (1L << Id) | (1L << Pattern))) != 0)) {
				{
				{
				setState(167);
				linkDef();
				}
				}
				setState(172);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(173);
			match(End);
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

	public static class LinkDefContext extends ParserRuleContext {
		public LinkDefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_linkDef; }
	 
		public LinkDefContext() { }
		public void copyFrom(LinkDefContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class LinkRefContext extends LinkDefContext {
		public Token linkRef;
		public Token alias;
		public Token virtual;
		public List<TerminalNode> Id() { return getTokens(SchemacodeParser.Id); }
		public TerminalNode Id(int i) {
			return getToken(SchemacodeParser.Id, i);
		}
		public TerminalNode As() { return getToken(SchemacodeParser.As, 0); }
		public TerminalNode Virtual() { return getToken(SchemacodeParser.Virtual, 0); }
		public LinkRefContext(LinkDefContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterLinkRef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitLinkRef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitLinkRef(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LinkPatternContext extends LinkDefContext {
		public Token linkPattern;
		public TerminalNode Pattern() { return getToken(SchemacodeParser.Pattern, 0); }
		public LinkPatternContext(LinkDefContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterLinkPattern(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitLinkPattern(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitLinkPattern(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LinkPosContext extends LinkDefContext {
		public ConnectionContext linkPos;
		public Token alias;
		public Token virtual;
		public ConnectionContext connection() {
			return getRuleContext(ConnectionContext.class,0);
		}
		public TerminalNode As() { return getToken(SchemacodeParser.As, 0); }
		public TerminalNode Id() { return getToken(SchemacodeParser.Id, 0); }
		public TerminalNode Virtual() { return getToken(SchemacodeParser.Virtual, 0); }
		public LinkPosContext(LinkDefContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterLinkPos(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitLinkPos(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitLinkPos(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LinkDefContext linkDef() throws RecognitionException {
		LinkDefContext _localctx = new LinkDefContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_linkDef);
		int _la;
		try {
			setState(192);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
			case 1:
				_localctx = new LinkPatternContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(175);
				((LinkPatternContext)_localctx).linkPattern = match(Pattern);
				}
				break;
			case 2:
				_localctx = new LinkRefContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(176);
				((LinkRefContext)_localctx).linkRef = match(Id);
				setState(179);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==As) {
					{
					setState(177);
					match(As);
					setState(178);
					((LinkRefContext)_localctx).alias = match(Id);
					}
				}

				setState(182);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Virtual) {
					{
					setState(181);
					((LinkRefContext)_localctx).virtual = match(Virtual);
					}
				}

				}
				break;
			case 3:
				_localctx = new LinkPosContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(184);
				((LinkPosContext)_localctx).linkPos = connection();
				setState(190);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==As) {
					{
					setState(185);
					match(As);
					setState(186);
					((LinkPosContext)_localctx).alias = match(Id);
					setState(188);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==Virtual) {
						{
						setState(187);
						((LinkPosContext)_localctx).virtual = match(Virtual);
						}
					}

					}
				}

				}
				break;
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

	public static class ProgramContext extends ParserRuleContext {
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
	 
		public ProgramContext() { }
		public void copyFrom(ProgramContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ProgramStringContext extends ProgramContext {
		public TextDefContext text;
		public TerminalNode Assign() { return getToken(SchemacodeParser.Assign, 0); }
		public TextDefContext textDef() {
			return getRuleContext(TextDefContext.class,0);
		}
		public ProgramStringContext(ProgramContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterProgramString(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitProgramString(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitProgramString(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ProgramFileContext extends ProgramContext {
		public TextDefContext file;
		public TerminalNode Assign() { return getToken(SchemacodeParser.Assign, 0); }
		public TerminalNode File() { return getToken(SchemacodeParser.File, 0); }
		public TextDefContext textDef() {
			return getRuleContext(TextDefContext.class,0);
		}
		public ProgramFileContext(ProgramContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterProgramFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitProgramFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitProgramFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_program);
		try {
			setState(199);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				_localctx = new ProgramStringContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(194);
				match(Assign);
				setState(195);
				((ProgramStringContext)_localctx).text = textDef();
				}
				break;
			case 2:
				_localctx = new ProgramFileContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(196);
				match(Assign);
				setState(197);
				match(File);
				setState(198);
				((ProgramFileContext)_localctx).file = textDef();
				}
				break;
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

	public static class TextDefContext extends ParserRuleContext {
		public TextDefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_textDef; }
	 
		public TextDefContext() { }
		public void copyFrom(TextDefContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class TextIdContext extends TextDefContext {
		public Token name;
		public TerminalNode Id() { return getToken(SchemacodeParser.Id, 0); }
		public TextIdContext(TextDefContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterTextId(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitTextId(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitTextId(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class TextLiteralContext extends TextDefContext {
		public StringLiteralContext reference;
		public StringLiteralContext stringLiteral() {
			return getRuleContext(StringLiteralContext.class,0);
		}
		public TextLiteralContext(TextDefContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterTextLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitTextLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitTextLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TextDefContext textDef() throws RecognitionException {
		TextDefContext _localctx = new TextDefContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_textDef);
		try {
			setState(203);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TextBlock1:
			case TextBlock2:
			case TextLine:
				_localctx = new TextLiteralContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(201);
				((TextLiteralContext)_localctx).reference = stringLiteral();
				}
				break;
			case Id:
				_localctx = new TextIdContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(202);
				((TextIdContext)_localctx).name = match(Id);
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

	public static class StringValueContext extends ParserRuleContext {
		public Token name;
		public StringLiteralContext string;
		public TerminalNode Assign() { return getToken(SchemacodeParser.Assign, 0); }
		public TerminalNode Id() { return getToken(SchemacodeParser.Id, 0); }
		public StringLiteralContext stringLiteral() {
			return getRuleContext(StringLiteralContext.class,0);
		}
		public StringValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stringValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterStringValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitStringValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitStringValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StringValueContext stringValue() throws RecognitionException {
		StringValueContext _localctx = new StringValueContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_stringValue);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(205);
			((StringValueContext)_localctx).name = match(Id);
			setState(206);
			match(Assign);
			setState(207);
			((StringValueContext)_localctx).string = stringLiteral();
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

	public static class StringLiteralContext extends ParserRuleContext {
		public StringLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stringLiteral; }
	 
		public StringLiteralContext() { }
		public void copyFrom(StringLiteralContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class TextLineContext extends StringLiteralContext {
		public TerminalNode TextLine() { return getToken(SchemacodeParser.TextLine, 0); }
		public TextLineContext(StringLiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterTextLine(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitTextLine(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitTextLine(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class TextBlockContext extends StringLiteralContext {
		public TerminalNode TextBlock1() { return getToken(SchemacodeParser.TextBlock1, 0); }
		public TerminalNode TextBlock2() { return getToken(SchemacodeParser.TextBlock2, 0); }
		public TextBlockContext(StringLiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterTextBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitTextBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitTextBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StringLiteralContext stringLiteral() throws RecognitionException {
		StringLiteralContext _localctx = new StringLiteralContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_stringLiteral);
		int _la;
		try {
			setState(211);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TextLine:
				_localctx = new TextLineContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(209);
				match(TextLine);
				}
				break;
			case TextBlock1:
			case TextBlock2:
				_localctx = new TextBlockContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(210);
				_la = _input.LA(1);
				if ( !(_la==TextBlock1 || _la==TextBlock2) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
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

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3/\u00d8\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\3\2\6\2\60\n\2\r\2"+
		"\16\2\61\3\2\3\2\3\3\3\3\5\38\n\3\3\4\3\4\5\4<\n\4\3\4\3\4\6\4@\n\4\r"+
		"\4\16\4A\3\4\3\4\3\5\3\5\5\5H\n\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6"+
		"\3\6\3\6\3\6\5\6V\n\6\3\7\5\7Y\n\7\3\7\3\7\3\7\3\7\5\7_\n\7\3\7\5\7b\n"+
		"\7\3\b\3\b\3\b\7\bg\n\b\f\b\16\bj\13\b\3\b\3\b\3\t\3\t\3\t\5\tq\n\t\3"+
		"\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\f\3\f\3\f\3\r\3\r\3\r\3\16\3\16"+
		"\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\5\16\u008e\n\16\3\17"+
		"\3\17\3\17\7\17\u0093\n\17\f\17\16\17\u0096\13\17\3\20\3\20\3\20\5\20"+
		"\u009b\n\20\3\21\3\21\5\21\u009f\n\21\3\21\3\21\3\21\3\21\5\21\u00a5\n"+
		"\21\3\21\3\21\3\22\3\22\7\22\u00ab\n\22\f\22\16\22\u00ae\13\22\3\22\3"+
		"\22\3\23\3\23\3\23\3\23\5\23\u00b6\n\23\3\23\5\23\u00b9\n\23\3\23\3\23"+
		"\3\23\3\23\5\23\u00bf\n\23\5\23\u00c1\n\23\5\23\u00c3\n\23\3\24\3\24\3"+
		"\24\3\24\3\24\5\24\u00ca\n\24\3\25\3\25\5\25\u00ce\n\25\3\26\3\26\3\26"+
		"\3\26\3\27\3\27\5\27\u00d6\n\27\3\27\2\2\30\2\4\6\b\n\f\16\20\22\24\26"+
		"\30\32\34\36 \"$&(*,\2\6\3\2\36\37\3\2 #\3\2\b\t\3\2&\'\2\u00e5\2/\3\2"+
		"\2\2\4\67\3\2\2\2\6;\3\2\2\2\bG\3\2\2\2\nU\3\2\2\2\fX\3\2\2\2\16c\3\2"+
		"\2\2\20p\3\2\2\2\22r\3\2\2\2\24x\3\2\2\2\26{\3\2\2\2\30~\3\2\2\2\32\u008d"+
		"\3\2\2\2\34\u008f\3\2\2\2\36\u009a\3\2\2\2 \u009c\3\2\2\2\"\u00a8\3\2"+
		"\2\2$\u00c2\3\2\2\2&\u00c9\3\2\2\2(\u00cd\3\2\2\2*\u00cf\3\2\2\2,\u00d5"+
		"\3\2\2\2.\60\5\4\3\2/.\3\2\2\2\60\61\3\2\2\2\61/\3\2\2\2\61\62\3\2\2\2"+
		"\62\63\3\2\2\2\63\64\7\2\2\3\64\3\3\2\2\2\658\5\6\4\2\668\5*\26\2\67\65"+
		"\3\2\2\2\67\66\3\2\2\28\5\3\2\2\29:\7*\2\2:<\7\33\2\2;9\3\2\2\2;<\3\2"+
		"\2\2<=\3\2\2\2=?\7\25\2\2>@\5\b\5\2?>\3\2\2\2@A\3\2\2\2A?\3\2\2\2AB\3"+
		"\2\2\2BC\3\2\2\2CD\7\n\2\2D\7\3\2\2\2EH\5\n\6\2FH\5\f\7\2GE\3\2\2\2GF"+
		"\3\2\2\2H\t\3\2\2\2IJ\7\23\2\2JK\7\32\2\2KV\5(\25\2LM\7\6\2\2MN\7\32\2"+
		"\2NV\5(\25\2OP\7\7\2\2PQ\7\32\2\2QV\5\22\n\2RS\7\26\2\2ST\7\32\2\2TV\5"+
		"(\25\2UI\3\2\2\2UL\3\2\2\2UO\3\2\2\2UR\3\2\2\2V\13\3\2\2\2WY\5\16\b\2"+
		"XW\3\2\2\2XY\3\2\2\2YZ\3\2\2\2Z[\7+\2\2[\\\7\4\2\2\\^\5\20\t\2]_\5\30"+
		"\r\2^]\3\2\2\2^_\3\2\2\2_a\3\2\2\2`b\5\32\16\2a`\3\2\2\2ab\3\2\2\2b\r"+
		"\3\2\2\2ch\7*\2\2de\7\34\2\2eg\7*\2\2fd\3\2\2\2gj\3\2\2\2hf\3\2\2\2hi"+
		"\3\2\2\2ik\3\2\2\2jh\3\2\2\2kl\7\33\2\2l\17\3\2\2\2mq\5\22\n\2nq\5\24"+
		"\13\2oq\5\26\f\2pm\3\2\2\2pn\3\2\2\2po\3\2\2\2q\21\3\2\2\2rs\7$\2\2st"+
		"\7)\2\2tu\7\34\2\2uv\7)\2\2vw\7%\2\2w\23\3\2\2\2xy\t\2\2\2yz\5\22\n\2"+
		"z\25\3\2\2\2{|\7*\2\2|}\5\24\13\2}\27\3\2\2\2~\177\7\13\2\2\177\u0080"+
		"\t\3\2\2\u0080\31\3\2\2\2\u0081\u008e\7\31\2\2\u0082\u0083\7\5\2\2\u0083"+
		"\u0084\7\30\2\2\u0084\u008e\5\34\17\2\u0085\u0086\7\r\2\2\u0086\u008e"+
		"\7+\2\2\u0087\u0088\7\17\2\2\u0088\u008e\7+\2\2\u0089\u008a\7\27\2\2\u008a"+
		"\u008e\5(\25\2\u008b\u008e\t\4\2\2\u008c\u008e\5 \21\2\u008d\u0081\3\2"+
		"\2\2\u008d\u0082\3\2\2\2\u008d\u0085\3\2\2\2\u008d\u0087\3\2\2\2\u008d"+
		"\u0089\3\2\2\2\u008d\u008b\3\2\2\2\u008d\u008c\3\2\2\2\u008e\33\3\2\2"+
		"\2\u008f\u0094\5\36\20\2\u0090\u0091\7\34\2\2\u0091\u0093\5\36\20\2\u0092"+
		"\u0090\3\2\2\2\u0093\u0096\3\2\2\2\u0094\u0092\3\2\2\2\u0094\u0095\3\2"+
		"\2\2\u0095\35\3\2\2\2\u0096\u0094\3\2\2\2\u0097\u009b\5\22\n\2\u0098\u009b"+
		"\5\24\13\2\u0099\u009b\7*\2\2\u009a\u0097\3\2\2\2\u009a\u0098\3\2\2\2"+
		"\u009a\u0099\3\2\2\2\u009b\37\3\2\2\2\u009c\u009e\7\24\2\2\u009d\u009f"+
		"\5\"\22\2\u009e\u009d\3\2\2\2\u009e\u009f\3\2\2\2\u009f\u00a4\3\2\2\2"+
		"\u00a0\u00a1\7\21\2\2\u00a1\u00a5\5&\24\2\u00a2\u00a3\7\22\2\2\u00a3\u00a5"+
		"\5&\24\2\u00a4\u00a0\3\2\2\2\u00a4\u00a2\3\2\2\2\u00a4\u00a5\3\2\2\2\u00a5"+
		"\u00a6\3\2\2\2\u00a6\u00a7\7\n\2\2\u00a7!\3\2\2\2\u00a8\u00ac\7\16\2\2"+
		"\u00a9\u00ab\5$\23\2\u00aa\u00a9\3\2\2\2\u00ab\u00ae\3\2\2\2\u00ac\u00aa"+
		"\3\2\2\2\u00ac\u00ad\3\2\2\2\u00ad\u00af\3\2\2\2\u00ae\u00ac\3\2\2\2\u00af"+
		"\u00b0\7\n\2\2\u00b0#\3\2\2\2\u00b1\u00c3\7,\2\2\u00b2\u00b5\7*\2\2\u00b3"+
		"\u00b4\7\3\2\2\u00b4\u00b6\7*\2\2\u00b5\u00b3\3\2\2\2\u00b5\u00b6\3\2"+
		"\2\2\u00b6\u00b8\3\2\2\2\u00b7\u00b9\7\31\2\2\u00b8\u00b7\3\2\2\2\u00b8"+
		"\u00b9\3\2\2\2\u00b9\u00c3\3\2\2\2\u00ba\u00c0\5\36\20\2\u00bb\u00bc\7"+
		"\3\2\2\u00bc\u00be\7*\2\2\u00bd\u00bf\7\31\2\2\u00be\u00bd\3\2\2\2\u00be"+
		"\u00bf\3\2\2\2\u00bf\u00c1\3\2\2\2\u00c0\u00bb\3\2\2\2\u00c0\u00c1\3\2"+
		"\2\2\u00c1\u00c3\3\2\2\2\u00c2\u00b1\3\2\2\2\u00c2\u00b2\3\2\2\2\u00c2"+
		"\u00ba\3\2\2\2\u00c3%\3\2\2\2\u00c4\u00c5\7\32\2\2\u00c5\u00ca\5(\25\2"+
		"\u00c6\u00c7\7\32\2\2\u00c7\u00c8\7\f\2\2\u00c8\u00ca\5(\25\2\u00c9\u00c4"+
		"\3\2\2\2\u00c9\u00c6\3\2\2\2\u00ca\'\3\2\2\2\u00cb\u00ce\5,\27\2\u00cc"+
		"\u00ce\7*\2\2\u00cd\u00cb\3\2\2\2\u00cd\u00cc\3\2\2\2\u00ce)\3\2\2\2\u00cf"+
		"\u00d0\7*\2\2\u00d0\u00d1\7\32\2\2\u00d1\u00d2\5,\27\2\u00d2+\3\2\2\2"+
		"\u00d3\u00d6\7(\2\2\u00d4\u00d6\t\5\2\2\u00d5\u00d3\3\2\2\2\u00d5\u00d4"+
		"\3\2\2\2\u00d6-\3\2\2\2\33\61\67;AGUX^ahp\u008d\u0094\u009a\u009e\u00a4"+
		"\u00ac\u00b5\u00b8\u00be\u00c0\u00c2\u00c9\u00cd\u00d5";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}