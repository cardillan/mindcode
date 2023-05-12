// Generated from Schemacode.g4 by ANTLR 4.9.1
package info.teksol.schemacode.grammar;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SchemacodeParser}.
 */
public interface SchemacodeListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link SchemacodeParser#definitions}.
	 * @param ctx the parse tree
	 */
	void enterDefinitions(SchemacodeParser.DefinitionsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SchemacodeParser#definitions}.
	 * @param ctx the parse tree
	 */
	void exitDefinitions(SchemacodeParser.DefinitionsContext ctx);
	/**
	 * Enter a parse tree produced by {@link SchemacodeParser#definition}.
	 * @param ctx the parse tree
	 */
	void enterDefinition(SchemacodeParser.DefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SchemacodeParser#definition}.
	 * @param ctx the parse tree
	 */
	void exitDefinition(SchemacodeParser.DefinitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SchemacodeParser#schematics}.
	 * @param ctx the parse tree
	 */
	void enterSchematics(SchemacodeParser.SchematicsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SchemacodeParser#schematics}.
	 * @param ctx the parse tree
	 */
	void exitSchematics(SchemacodeParser.SchematicsContext ctx);
	/**
	 * Enter a parse tree produced by {@link SchemacodeParser#schematicsItem}.
	 * @param ctx the parse tree
	 */
	void enterSchematicsItem(SchemacodeParser.SchematicsItemContext ctx);
	/**
	 * Exit a parse tree produced by {@link SchemacodeParser#schematicsItem}.
	 * @param ctx the parse tree
	 */
	void exitSchematicsItem(SchemacodeParser.SchematicsItemContext ctx);
	/**
	 * Enter a parse tree produced by the {@code name}
	 * labeled alternative in {@link SchemacodeParser#attribute}.
	 * @param ctx the parse tree
	 */
	void enterName(SchemacodeParser.NameContext ctx);
	/**
	 * Exit a parse tree produced by the {@code name}
	 * labeled alternative in {@link SchemacodeParser#attribute}.
	 * @param ctx the parse tree
	 */
	void exitName(SchemacodeParser.NameContext ctx);
	/**
	 * Enter a parse tree produced by the {@code description}
	 * labeled alternative in {@link SchemacodeParser#attribute}.
	 * @param ctx the parse tree
	 */
	void enterDescription(SchemacodeParser.DescriptionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code description}
	 * labeled alternative in {@link SchemacodeParser#attribute}.
	 * @param ctx the parse tree
	 */
	void exitDescription(SchemacodeParser.DescriptionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code dimensions}
	 * labeled alternative in {@link SchemacodeParser#attribute}.
	 * @param ctx the parse tree
	 */
	void enterDimensions(SchemacodeParser.DimensionsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code dimensions}
	 * labeled alternative in {@link SchemacodeParser#attribute}.
	 * @param ctx the parse tree
	 */
	void exitDimensions(SchemacodeParser.DimensionsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code schemaTag}
	 * labeled alternative in {@link SchemacodeParser#attribute}.
	 * @param ctx the parse tree
	 */
	void enterSchemaTag(SchemacodeParser.SchemaTagContext ctx);
	/**
	 * Exit a parse tree produced by the {@code schemaTag}
	 * labeled alternative in {@link SchemacodeParser#attribute}.
	 * @param ctx the parse tree
	 */
	void exitSchemaTag(SchemacodeParser.SchemaTagContext ctx);
	/**
	 * Enter a parse tree produced by {@link SchemacodeParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(SchemacodeParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link SchemacodeParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(SchemacodeParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link SchemacodeParser#labelList}.
	 * @param ctx the parse tree
	 */
	void enterLabelList(SchemacodeParser.LabelListContext ctx);
	/**
	 * Exit a parse tree produced by {@link SchemacodeParser#labelList}.
	 * @param ctx the parse tree
	 */
	void exitLabelList(SchemacodeParser.LabelListContext ctx);
	/**
	 * Enter a parse tree produced by {@link SchemacodeParser#position}.
	 * @param ctx the parse tree
	 */
	void enterPosition(SchemacodeParser.PositionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SchemacodeParser#position}.
	 * @param ctx the parse tree
	 */
	void exitPosition(SchemacodeParser.PositionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SchemacodeParser#coordinates}.
	 * @param ctx the parse tree
	 */
	void enterCoordinates(SchemacodeParser.CoordinatesContext ctx);
	/**
	 * Exit a parse tree produced by {@link SchemacodeParser#coordinates}.
	 * @param ctx the parse tree
	 */
	void exitCoordinates(SchemacodeParser.CoordinatesContext ctx);
	/**
	 * Enter a parse tree produced by {@link SchemacodeParser#relativeCoordinates}.
	 * @param ctx the parse tree
	 */
	void enterRelativeCoordinates(SchemacodeParser.RelativeCoordinatesContext ctx);
	/**
	 * Exit a parse tree produced by {@link SchemacodeParser#relativeCoordinates}.
	 * @param ctx the parse tree
	 */
	void exitRelativeCoordinates(SchemacodeParser.RelativeCoordinatesContext ctx);
	/**
	 * Enter a parse tree produced by {@link SchemacodeParser#coordinatesRelativeTo}.
	 * @param ctx the parse tree
	 */
	void enterCoordinatesRelativeTo(SchemacodeParser.CoordinatesRelativeToContext ctx);
	/**
	 * Exit a parse tree produced by {@link SchemacodeParser#coordinatesRelativeTo}.
	 * @param ctx the parse tree
	 */
	void exitCoordinatesRelativeTo(SchemacodeParser.CoordinatesRelativeToContext ctx);
	/**
	 * Enter a parse tree produced by {@link SchemacodeParser#direction}.
	 * @param ctx the parse tree
	 */
	void enterDirection(SchemacodeParser.DirectionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SchemacodeParser#direction}.
	 * @param ctx the parse tree
	 */
	void exitDirection(SchemacodeParser.DirectionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code virtual}
	 * labeled alternative in {@link SchemacodeParser#configuration}.
	 * @param ctx the parse tree
	 */
	void enterVirtual(SchemacodeParser.VirtualContext ctx);
	/**
	 * Exit a parse tree produced by the {@code virtual}
	 * labeled alternative in {@link SchemacodeParser#configuration}.
	 * @param ctx the parse tree
	 */
	void exitVirtual(SchemacodeParser.VirtualContext ctx);
	/**
	 * Enter a parse tree produced by the {@code connections}
	 * labeled alternative in {@link SchemacodeParser#configuration}.
	 * @param ctx the parse tree
	 */
	void enterConnections(SchemacodeParser.ConnectionsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code connections}
	 * labeled alternative in {@link SchemacodeParser#configuration}.
	 * @param ctx the parse tree
	 */
	void exitConnections(SchemacodeParser.ConnectionsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code item}
	 * labeled alternative in {@link SchemacodeParser#configuration}.
	 * @param ctx the parse tree
	 */
	void enterItem(SchemacodeParser.ItemContext ctx);
	/**
	 * Exit a parse tree produced by the {@code item}
	 * labeled alternative in {@link SchemacodeParser#configuration}.
	 * @param ctx the parse tree
	 */
	void exitItem(SchemacodeParser.ItemContext ctx);
	/**
	 * Enter a parse tree produced by the {@code liquid}
	 * labeled alternative in {@link SchemacodeParser#configuration}.
	 * @param ctx the parse tree
	 */
	void enterLiquid(SchemacodeParser.LiquidContext ctx);
	/**
	 * Exit a parse tree produced by the {@code liquid}
	 * labeled alternative in {@link SchemacodeParser#configuration}.
	 * @param ctx the parse tree
	 */
	void exitLiquid(SchemacodeParser.LiquidContext ctx);
	/**
	 * Enter a parse tree produced by the {@code text}
	 * labeled alternative in {@link SchemacodeParser#configuration}.
	 * @param ctx the parse tree
	 */
	void enterText(SchemacodeParser.TextContext ctx);
	/**
	 * Exit a parse tree produced by the {@code text}
	 * labeled alternative in {@link SchemacodeParser#configuration}.
	 * @param ctx the parse tree
	 */
	void exitText(SchemacodeParser.TextContext ctx);
	/**
	 * Enter a parse tree produced by the {@code boolean}
	 * labeled alternative in {@link SchemacodeParser#configuration}.
	 * @param ctx the parse tree
	 */
	void enterBoolean(SchemacodeParser.BooleanContext ctx);
	/**
	 * Exit a parse tree produced by the {@code boolean}
	 * labeled alternative in {@link SchemacodeParser#configuration}.
	 * @param ctx the parse tree
	 */
	void exitBoolean(SchemacodeParser.BooleanContext ctx);
	/**
	 * Enter a parse tree produced by the {@code logic}
	 * labeled alternative in {@link SchemacodeParser#configuration}.
	 * @param ctx the parse tree
	 */
	void enterLogic(SchemacodeParser.LogicContext ctx);
	/**
	 * Exit a parse tree produced by the {@code logic}
	 * labeled alternative in {@link SchemacodeParser#configuration}.
	 * @param ctx the parse tree
	 */
	void exitLogic(SchemacodeParser.LogicContext ctx);
	/**
	 * Enter a parse tree produced by {@link SchemacodeParser#connectionList}.
	 * @param ctx the parse tree
	 */
	void enterConnectionList(SchemacodeParser.ConnectionListContext ctx);
	/**
	 * Exit a parse tree produced by {@link SchemacodeParser#connectionList}.
	 * @param ctx the parse tree
	 */
	void exitConnectionList(SchemacodeParser.ConnectionListContext ctx);
	/**
	 * Enter a parse tree produced by the {@code connAbs}
	 * labeled alternative in {@link SchemacodeParser#connection}.
	 * @param ctx the parse tree
	 */
	void enterConnAbs(SchemacodeParser.ConnAbsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code connAbs}
	 * labeled alternative in {@link SchemacodeParser#connection}.
	 * @param ctx the parse tree
	 */
	void exitConnAbs(SchemacodeParser.ConnAbsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code connRel}
	 * labeled alternative in {@link SchemacodeParser#connection}.
	 * @param ctx the parse tree
	 */
	void enterConnRel(SchemacodeParser.ConnRelContext ctx);
	/**
	 * Exit a parse tree produced by the {@code connRel}
	 * labeled alternative in {@link SchemacodeParser#connection}.
	 * @param ctx the parse tree
	 */
	void exitConnRel(SchemacodeParser.ConnRelContext ctx);
	/**
	 * Enter a parse tree produced by the {@code connName}
	 * labeled alternative in {@link SchemacodeParser#connection}.
	 * @param ctx the parse tree
	 */
	void enterConnName(SchemacodeParser.ConnNameContext ctx);
	/**
	 * Exit a parse tree produced by the {@code connName}
	 * labeled alternative in {@link SchemacodeParser#connection}.
	 * @param ctx the parse tree
	 */
	void exitConnName(SchemacodeParser.ConnNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link SchemacodeParser#processor}.
	 * @param ctx the parse tree
	 */
	void enterProcessor(SchemacodeParser.ProcessorContext ctx);
	/**
	 * Exit a parse tree produced by {@link SchemacodeParser#processor}.
	 * @param ctx the parse tree
	 */
	void exitProcessor(SchemacodeParser.ProcessorContext ctx);
	/**
	 * Enter a parse tree produced by {@link SchemacodeParser#processorLinks}.
	 * @param ctx the parse tree
	 */
	void enterProcessorLinks(SchemacodeParser.ProcessorLinksContext ctx);
	/**
	 * Exit a parse tree produced by {@link SchemacodeParser#processorLinks}.
	 * @param ctx the parse tree
	 */
	void exitProcessorLinks(SchemacodeParser.ProcessorLinksContext ctx);
	/**
	 * Enter a parse tree produced by the {@code linkPattern}
	 * labeled alternative in {@link SchemacodeParser#linkDef}.
	 * @param ctx the parse tree
	 */
	void enterLinkPattern(SchemacodeParser.LinkPatternContext ctx);
	/**
	 * Exit a parse tree produced by the {@code linkPattern}
	 * labeled alternative in {@link SchemacodeParser#linkDef}.
	 * @param ctx the parse tree
	 */
	void exitLinkPattern(SchemacodeParser.LinkPatternContext ctx);
	/**
	 * Enter a parse tree produced by the {@code linkRef}
	 * labeled alternative in {@link SchemacodeParser#linkDef}.
	 * @param ctx the parse tree
	 */
	void enterLinkRef(SchemacodeParser.LinkRefContext ctx);
	/**
	 * Exit a parse tree produced by the {@code linkRef}
	 * labeled alternative in {@link SchemacodeParser#linkDef}.
	 * @param ctx the parse tree
	 */
	void exitLinkRef(SchemacodeParser.LinkRefContext ctx);
	/**
	 * Enter a parse tree produced by the {@code linkPos}
	 * labeled alternative in {@link SchemacodeParser#linkDef}.
	 * @param ctx the parse tree
	 */
	void enterLinkPos(SchemacodeParser.LinkPosContext ctx);
	/**
	 * Exit a parse tree produced by the {@code linkPos}
	 * labeled alternative in {@link SchemacodeParser#linkDef}.
	 * @param ctx the parse tree
	 */
	void exitLinkPos(SchemacodeParser.LinkPosContext ctx);
	/**
	 * Enter a parse tree produced by the {@code programString}
	 * labeled alternative in {@link SchemacodeParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgramString(SchemacodeParser.ProgramStringContext ctx);
	/**
	 * Exit a parse tree produced by the {@code programString}
	 * labeled alternative in {@link SchemacodeParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgramString(SchemacodeParser.ProgramStringContext ctx);
	/**
	 * Enter a parse tree produced by the {@code programFile}
	 * labeled alternative in {@link SchemacodeParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgramFile(SchemacodeParser.ProgramFileContext ctx);
	/**
	 * Exit a parse tree produced by the {@code programFile}
	 * labeled alternative in {@link SchemacodeParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgramFile(SchemacodeParser.ProgramFileContext ctx);
	/**
	 * Enter a parse tree produced by the {@code textLiteral}
	 * labeled alternative in {@link SchemacodeParser#textDef}.
	 * @param ctx the parse tree
	 */
	void enterTextLiteral(SchemacodeParser.TextLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code textLiteral}
	 * labeled alternative in {@link SchemacodeParser#textDef}.
	 * @param ctx the parse tree
	 */
	void exitTextLiteral(SchemacodeParser.TextLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code textId}
	 * labeled alternative in {@link SchemacodeParser#textDef}.
	 * @param ctx the parse tree
	 */
	void enterTextId(SchemacodeParser.TextIdContext ctx);
	/**
	 * Exit a parse tree produced by the {@code textId}
	 * labeled alternative in {@link SchemacodeParser#textDef}.
	 * @param ctx the parse tree
	 */
	void exitTextId(SchemacodeParser.TextIdContext ctx);
	/**
	 * Enter a parse tree produced by {@link SchemacodeParser#stringValue}.
	 * @param ctx the parse tree
	 */
	void enterStringValue(SchemacodeParser.StringValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link SchemacodeParser#stringValue}.
	 * @param ctx the parse tree
	 */
	void exitStringValue(SchemacodeParser.StringValueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code textLine}
	 * labeled alternative in {@link SchemacodeParser#stringLiteral}.
	 * @param ctx the parse tree
	 */
	void enterTextLine(SchemacodeParser.TextLineContext ctx);
	/**
	 * Exit a parse tree produced by the {@code textLine}
	 * labeled alternative in {@link SchemacodeParser#stringLiteral}.
	 * @param ctx the parse tree
	 */
	void exitTextLine(SchemacodeParser.TextLineContext ctx);
	/**
	 * Enter a parse tree produced by the {@code textBlock}
	 * labeled alternative in {@link SchemacodeParser#stringLiteral}.
	 * @param ctx the parse tree
	 */
	void enterTextBlock(SchemacodeParser.TextBlockContext ctx);
	/**
	 * Exit a parse tree produced by the {@code textBlock}
	 * labeled alternative in {@link SchemacodeParser#stringLiteral}.
	 * @param ctx the parse tree
	 */
	void exitTextBlock(SchemacodeParser.TextBlockContext ctx);
}