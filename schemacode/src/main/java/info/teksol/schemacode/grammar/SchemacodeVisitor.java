// Generated from Schemacode.g4 by ANTLR 4.13.1
package info.teksol.schemacode.grammar;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SchemacodeParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface SchemacodeVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link SchemacodeParser#definitions}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefinitions(SchemacodeParser.DefinitionsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SchemacodeParser#definition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefinition(SchemacodeParser.DefinitionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SchemacodeParser#schematic}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSchematic(SchemacodeParser.SchematicContext ctx);
	/**
	 * Visit a parse tree produced by {@link SchemacodeParser#schematicItem}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSchematicItem(SchemacodeParser.SchematicItemContext ctx);
	/**
	 * Visit a parse tree produced by the {@code name}
	 * labeled alternative in {@link SchemacodeParser#attribute}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitName(SchemacodeParser.NameContext ctx);
	/**
	 * Visit a parse tree produced by the {@code description}
	 * labeled alternative in {@link SchemacodeParser#attribute}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDescription(SchemacodeParser.DescriptionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code dimensions}
	 * labeled alternative in {@link SchemacodeParser#attribute}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDimensions(SchemacodeParser.DimensionsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code schemaTag}
	 * labeled alternative in {@link SchemacodeParser#attribute}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSchemaTag(SchemacodeParser.SchemaTagContext ctx);
	/**
	 * Visit a parse tree produced by {@link SchemacodeParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(SchemacodeParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link SchemacodeParser#labelList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLabelList(SchemacodeParser.LabelListContext ctx);
	/**
	 * Visit a parse tree produced by {@link SchemacodeParser#position}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPosition(SchemacodeParser.PositionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SchemacodeParser#coordinates}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCoordinates(SchemacodeParser.CoordinatesContext ctx);
	/**
	 * Visit a parse tree produced by {@link SchemacodeParser#relativeCoordinates}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelativeCoordinates(SchemacodeParser.RelativeCoordinatesContext ctx);
	/**
	 * Visit a parse tree produced by {@link SchemacodeParser#coordinatesRelativeTo}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCoordinatesRelativeTo(SchemacodeParser.CoordinatesRelativeToContext ctx);
	/**
	 * Visit a parse tree produced by {@link SchemacodeParser#direction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDirection(SchemacodeParser.DirectionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code virtual}
	 * labeled alternative in {@link SchemacodeParser#configuration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVirtual(SchemacodeParser.VirtualContext ctx);
	/**
	 * Visit a parse tree produced by the {@code color}
	 * labeled alternative in {@link SchemacodeParser#configuration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitColor(SchemacodeParser.ColorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code connections}
	 * labeled alternative in {@link SchemacodeParser#configuration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConnections(SchemacodeParser.ConnectionsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code item}
	 * labeled alternative in {@link SchemacodeParser#configuration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitItem(SchemacodeParser.ItemContext ctx);
	/**
	 * Visit a parse tree produced by the {@code liquid}
	 * labeled alternative in {@link SchemacodeParser#configuration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiquid(SchemacodeParser.LiquidContext ctx);
	/**
	 * Visit a parse tree produced by the {@code unit}
	 * labeled alternative in {@link SchemacodeParser#configuration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnit(SchemacodeParser.UnitContext ctx);
	/**
	 * Visit a parse tree produced by the {@code text}
	 * labeled alternative in {@link SchemacodeParser#configuration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitText(SchemacodeParser.TextContext ctx);
	/**
	 * Visit a parse tree produced by the {@code boolean}
	 * labeled alternative in {@link SchemacodeParser#configuration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolean(SchemacodeParser.BooleanContext ctx);
	/**
	 * Visit a parse tree produced by the {@code logic}
	 * labeled alternative in {@link SchemacodeParser#configuration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogic(SchemacodeParser.LogicContext ctx);
	/**
	 * Visit a parse tree produced by {@link SchemacodeParser#colorDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitColorDef(SchemacodeParser.ColorDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link SchemacodeParser#connectionList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConnectionList(SchemacodeParser.ConnectionListContext ctx);
	/**
	 * Visit a parse tree produced by the {@code connAbs}
	 * labeled alternative in {@link SchemacodeParser#connection}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConnAbs(SchemacodeParser.ConnAbsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code connRel}
	 * labeled alternative in {@link SchemacodeParser#connection}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConnRel(SchemacodeParser.ConnRelContext ctx);
	/**
	 * Visit a parse tree produced by the {@code connName}
	 * labeled alternative in {@link SchemacodeParser#connection}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConnName(SchemacodeParser.ConnNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link SchemacodeParser#processor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProcessor(SchemacodeParser.ProcessorContext ctx);
	/**
	 * Visit a parse tree produced by {@link SchemacodeParser#processorLinks}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProcessorLinks(SchemacodeParser.ProcessorLinksContext ctx);
	/**
	 * Visit a parse tree produced by the {@code linkPattern}
	 * labeled alternative in {@link SchemacodeParser#linkDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLinkPattern(SchemacodeParser.LinkPatternContext ctx);
	/**
	 * Visit a parse tree produced by the {@code linkPos}
	 * labeled alternative in {@link SchemacodeParser#linkDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLinkPos(SchemacodeParser.LinkPosContext ctx);
	/**
	 * Visit a parse tree produced by the {@code programString}
	 * labeled alternative in {@link SchemacodeParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgramString(SchemacodeParser.ProgramStringContext ctx);
	/**
	 * Visit a parse tree produced by the {@code programFile}
	 * labeled alternative in {@link SchemacodeParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgramFile(SchemacodeParser.ProgramFileContext ctx);
	/**
	 * Visit a parse tree produced by the {@code textLiteral}
	 * labeled alternative in {@link SchemacodeParser#textDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTextLiteral(SchemacodeParser.TextLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code textId}
	 * labeled alternative in {@link SchemacodeParser#textDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTextId(SchemacodeParser.TextIdContext ctx);
	/**
	 * Visit a parse tree produced by {@link SchemacodeParser#stringValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringValue(SchemacodeParser.StringValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code textLine}
	 * labeled alternative in {@link SchemacodeParser#stringLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTextLine(SchemacodeParser.TextLineContext ctx);
	/**
	 * Visit a parse tree produced by the {@code textBlock}
	 * labeled alternative in {@link SchemacodeParser#stringLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTextBlock(SchemacodeParser.TextBlockContext ctx);
}