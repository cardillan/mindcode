package info.teksol.schemacode.ast;

import info.teksol.mc.common.InputFile;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.schemacode.SchematicsInternalError;
import info.teksol.schemacode.grammar.SchemacodeParser;
import info.teksol.schemacode.grammar.SchemacodeParser.*;
import info.teksol.schemacode.grammar.SchemacodeParserBaseVisitor;
import info.teksol.schemacode.schematics.Language;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public class AstSchematicsBuilder extends SchemacodeParserBaseVisitor<AstSchemaItem> {

    private final InputFile inputFile;

    public AstSchematicsBuilder(InputFile inputFile) {
        this.inputFile = inputFile;
    }

    public static AstDefinitions generate(InputFile inputFile, DefinitionsContext parseTree,
            MessageConsumer messageListener) {
        final AstSchematicsBuilder builder = new AstSchematicsBuilder(inputFile);
        final AstSchemaItem item = builder.visit(parseTree);
        return (AstDefinitions) item;
    }

    @SuppressWarnings("unchecked")
    private <T extends AstSchemaItem> @Nullable T maybeVisit(@Nullable ParseTree tree) {
        return tree == null ? null : (T) visit(tree);
    }

    @Override
    public AstSchemaItem visitDefinitions(DefinitionsContext ctx) {
        List<AstDefinition> list = ctx.definition().stream()
                .map(this::visit)
                .map(AstDefinition.class::cast)
                .toList();

        return new AstDefinitions(pos(ctx.getStart()), list);
    }

    private SourcePosition pos(Token token) {
        return SourcePosition.create(inputFile, token);
    }

    @Override
    public AstSchemaItem visitSchematic(SchemacodeParser.SchematicContext ctx) {
        //final String id = ctx.name == null ? null : ctx.name.getText();
        final List<AstSchemaAttribute> attributes = new ArrayList<>();
        final List<AstBlock> blocks = new ArrayList<>();
        for (SchemacodeParser.SchematicItemContext item : ctx.schematicItem()) {
            AstSchemaItem schemaItem = visit(item);
            switch (schemaItem) {
                case AstSchemaAttribute a -> attributes.add(a);
                case AstBlock b -> blocks.add(b);
                case null -> throw new SchematicsInternalError("Unexpected item " + schemaItem);
                default -> {}
            }
        }

        return new AstSchematic(pos(ctx.getStart()), attributes, blocks);
    }

    // Attributes

    @Override
    public AstSchemaItem visitName(SchemacodeParser.NameContext ctx) {
        return new AstSchemaAttribute(pos(ctx.getStart()), "name", visit(ctx.textDef()));
    }

    @Override
    public AstSchemaItem visitDescription(SchemacodeParser.DescriptionContext ctx) {
        return new AstSchemaAttribute(pos(ctx.getStart()), "description", visit(ctx.textDef()));
    }

    @Override
    public AstSchemaItem visitDimensions(SchemacodeParser.DimensionsContext ctx) {
        return new AstSchemaAttribute(pos(ctx.getStart()), "dimensions", visit(ctx.coordinates()));
    }

    @Override
    public AstSchemaItem visitSchemaTag(SchemaTagContext ctx) {
        return new AstSchemaAttribute(pos(ctx.getStart()), "label", visit(ctx.tag));
    }

    @Override
    public AstSchemaItem visitFilename(FilenameContext ctx) {
        return new AstSchemaAttribute(pos(ctx.getStart()), "filename", visit(ctx.filename));
    }

    @Override
    public AstSchemaItem visitTarget(TargetContext ctx) {
        return new AstSchemaAttribute(pos(ctx.getStart()), "target",
                new AstStringLiteral(pos(ctx.versionNumber().getStart()), ctx.versionNumber().getText()));
    }

    @Override
    public AstSchemaItem visitMindcodePrologue(MindcodePrologueContext ctx) {
        return new AstSchemaAttribute(pos(ctx.getStart()), "mindcode",
                new AstProgramSnippetText(pos(ctx.tag.getStart()), (AstText) visit(ctx.tag)));
    }

    @Override
    public AstSchemaItem visitMlogPrologue(MlogPrologueContext ctx) {
        return new AstSchemaAttribute(pos(ctx.getStart()), "mlog",
                new AstProgramSnippetText(pos(ctx.tag.getStart()), (AstText) visit(ctx.tag)));
    }

    // Blocks and regions

    @Override
    public AstBlock visitBlock(SchemacodeParser.BlockContext ctx) {
        List<String> labels = processLabels(ctx.labels);
        AstSchemaElement element = ctx.elementType != null ? new AstSchemaBlock(pos(ctx.elementType), ctx.elementType.getText())
                : ctx.elementId != null ? new AstSchemaRegionRef(pos(ctx.elementId), ctx.elementId.getText())
                : new AstSchemaRegion(pos(ctx.getStart()), processBlocks(ctx.block()));

        AstBlockPosition position = (AstBlockPosition) visit(ctx.blockPosition());
        AstDirection direction = maybeVisit(ctx.direction());
        AstConfiguration configuration = maybeVisit(ctx.configuration());

        return new AstBlock(pos(ctx.getStart()), labels, element, position, direction, configuration);
    }

    private List<AstBlock> processBlocks(List<BlockContext> blocks) {
        return blocks.stream().map(this::visitBlock).toList();
    }

    // Configuration

    @Override
    public AstSchemaItem visitBoolean(BooleanContext ctx) {
        return new AstBoolean(pos(ctx.getStart()), ctx.status.getText().equals("enabled"));
    }

    @Override
    public AstColor visitColor(ColorContext ctx) {
        return visitColorDef(ctx.colorDef());
    }

    @Override
    public AstRgbaValue visitColorDef(ColorDefContext ctx) {
        int red   = Integer.parseInt(ctx.red.getText());
        int green = Integer.parseInt(ctx.green.getText());
        int blue  = Integer.parseInt(ctx.blue.getText());
        int alpha  = Integer.parseInt(ctx.alpha.getText());
        return new AstRgbaValue(pos(ctx.getStart()), red, green, blue, alpha);
    }

    @Override
    public AstVirtual visitVirtual(SchemacodeParser.VirtualContext ctx) {
        return new AstVirtual(pos(ctx.getStart()));
    }

    @Override
    public AstConnections visitConnections(SchemacodeParser.ConnectionsContext ctx) {
        List<AstConnection> list = ctx.connectionList().connection().stream()
                .map(this::visit)
                .map(AstConnection.class::cast)
                .toList();

        return new AstConnections(pos(ctx.getStart()), list);
    }

    @Override
    public AstConnection visitConnAbs(SchemacodeParser.ConnAbsContext ctx) {
        return new AstConnection(pos(ctx.getStart()), visitCoordinates(ctx.coordinates()));
    }

    @Override
    public AstConnection visitConnRel(SchemacodeParser.ConnRelContext ctx) {
        return new AstConnection(pos(ctx.getStart()), visitRelativeCoordinates(ctx.relativeCoordinates()));
    }

    @Override
    public AstConnection visitConnName(SchemacodeParser.ConnNameContext ctx) {
        return new AstConnection(pos(ctx.getStart()), ctx.ID().getSymbol().getText());
    }

    @Override
    public AstSchemaItem visitBlocktype(SchemacodeParser.BlocktypeContext ctx) {
        return new AstBlockReference(pos(ctx.getStart()), ctx.REF().getSymbol().getText());
    }

    @Override
    public AstSchemaItem visitUnitcommand(UnitcommandContext ctx) {
        return new AstUnitCommandReference(pos(ctx.getStart()), ctx.REF().getSymbol().getText());
    }

    @Override
    public AstItemReference visitItem(SchemacodeParser.ItemContext ctx) {
        return new AstItemReference(pos(ctx.getStart()), ctx.REF().getSymbol().getText());
    }

    @Override
    public AstSchemaItem visitLiquid(SchemacodeParser.LiquidContext ctx) {
        return new AstLiquidReference(pos(ctx.getStart()), ctx.REF().getSymbol().getText());
    }

    @Override
    public AstSchemaItem visitUnit(SchemacodeParser.UnitContext ctx) {
        return new AstUnitReference(pos(ctx.getStart()), ctx.REF().getSymbol().getText());
    }

    @Override
    public AstText visitText(SchemacodeParser.TextContext ctx) {
        return (AstText) visit(ctx.textDef());
    }

    @Override
    public AstProcessor visitLogic(SchemacodeParser.LogicContext ctx) {
        return visitProcessor(ctx.def);
    }

    // Processors

    @Override
    public AstProcessor visitProcessor(SchemacodeParser.ProcessorContext ctx) {
        List<AstLink> links = ctx.links == null ? List.of()
                : ctx.links.linkDef().stream()
                .map(this::visit)
                .map(AstLink.class::cast)
                .toList();

        AstProgram program;
        Language language;
        if (ctx.mindcode != null) {
            program = (AstProgram) visit(ctx.mindcode);
            language = Language.MINDCODE;
        } else if (ctx.mlog != null) {
            program = (AstProgram) visit(ctx.mlog);
            language = Language.MLOG;
        } else {
            program = AstProgram.EMPTY;
            language = Language.NONE;
        }

        List<AstParameter> parameters = ctx.parameters == null ? List.of()
                 : ctx.parameters.parameter().stream()
                .map(this::visit)
                .map(AstParameter.class::cast)
                .toList();

        return new AstProcessor(pos(ctx.getStart()), links, program, language, parameters);
    }

    @Override
    public AstLinkPattern visitLinkPattern(SchemacodeParser.LinkPatternContext ctx) {
        return new AstLinkPattern(pos(ctx.getStart()), ctx.linkPattern.getText());
    }

    @Override
    public AstLinkPos visitLinkPos(SchemacodeParser.LinkPosContext ctx) {
        AstConnection connection = (AstConnection) visit(ctx.linkPos);
        String name = ctx.alias == null ? null : ctx.alias.getText();
        boolean virtual = ctx.virtual != null;
        return new AstLinkPos(pos(ctx.getStart()), connection, name, virtual);
    }

    @Override
    public AstSchemaItem visitProgram(ProgramContext ctx) {
        List<AstProgramSnippet> snippets = ctx.programSnippet().stream()
                .map(this::visit)
                .map(AstProgramSnippet.class::cast)
                .toList();

        return new AstProgram(pos(ctx.getStart()), snippets);
    }

    @Override
    public AstSchemaItem visitProgramString(SchemacodeParser.ProgramStringContext ctx) {
        return new AstProgramSnippetText(pos(ctx.getStart()), (AstText) visit(ctx.text));
    }

    @Override
    public AstSchemaItem visitProgramFile(SchemacodeParser.ProgramFileContext ctx) {
        return new AstProgramSnippetFile(pos(ctx.getStart()), (AstText) visit(ctx.file));
    }

    @Override
    public AstSchemaItem visitParameter(ParameterContext ctx) {
        return new AstParameter(pos(ctx.getStart()),
                visitParameterToken(ctx.variable),
                visitParameterToken(ctx.strValue == null ? ctx.value : ctx.strValue));
    }

    private AstToken visitParameterToken(Token token) {
        return new AstToken(pos(token), token.getText());
    }

    // Coordinates & direction

    @Override
    public AstSchemaItem visitAreaPosition(AreaPositionContext ctx) {
        return new AstBlockPosition(pos(ctx.getStart()), visitPosition(ctx.start),
                AstBlockPosition.BlockArray.AREA, visitCoordinates(ctx.size),
                ctx.VERTICAL() == null);
    }

    @Override
    public AstSchemaItem visitExclusiveRangePosition(ExclusiveRangePositionContext ctx) {
        return new AstBlockPosition(pos(ctx.getStart()), visitPosition(ctx.start),
                AstBlockPosition.BlockArray.EXCLUSIVE, visitCoordinates(ctx.end),
                ctx.VERTICAL() == null);
    }

    @Override
    public AstSchemaItem visitInclusiveRangePosition(InclusiveRangePositionContext ctx) {
        return new AstBlockPosition(pos(ctx.getStart()), visitPosition(ctx.start),
                AstBlockPosition.BlockArray.INCLUSIVE, visitCoordinates(ctx.end),
                ctx.VERTICAL() == null);
    }

    @Override
    public AstSchemaItem visitSimplePosition(SimplePositionContext ctx) {
        return new AstBlockPosition(pos(ctx.getStart()), visitPosition(ctx.start),
                AstBlockPosition.BlockArray.SINGLE, null, false);
    }

    @Override
    public AstCoordinates visitPosition(SchemacodeParser.PositionContext ctx) {
        return (AstCoordinates) super.visitPosition(ctx);
    }

    @Override
    public AstCoordinates visitCoordinates(SchemacodeParser.CoordinatesContext ctx) {
        int x = Integer.parseInt(ctx.x.getText());
        int y = Integer.parseInt(ctx.y.getText());
        return new AstCoordinates(pos(ctx.getStart()), x, y);
    }

    @Override
    public AstCoordinates visitRelativeCoordinates(SchemacodeParser.RelativeCoordinatesContext ctx) {
        AstCoordinates coordinates = (AstCoordinates) visit(ctx.coordinates());
        return switch (ctx.op.getText()) {
            case "+" -> coordinates.relative(false);
            case "-" -> coordinates.relative(true);
            default -> throw new SchematicsInternalError("Unknown operator " + ctx.op.getText());
        };
    }

    @Override
    public AstCoordinates visitCoordinatesRelativeTo(SchemacodeParser.CoordinatesRelativeToContext ctx) {
        AstCoordinates coordinates = (AstCoordinates) visit(ctx.relativeCoordinates());
        String id = ctx.label.getText();
        return coordinates.relativeTo(id);
    }

    @Override
    public AstDirection visitDirection(SchemacodeParser.DirectionContext ctx) {
        return new AstDirection(pos(ctx.getStart()), ctx.dir.getText());
    }

    // Labels

    private static List<String> processLabels(SchemacodeParser.@Nullable LabelListContext labels) {
        return labels == null
                ? List.of()
                : labels.blockId().stream()
                .map(RuleContext::getText)
                .toList();
    }

    // Texts

    @Override
    public AstStringConstant visitStringValue(SchemacodeParser.StringValueContext ctx) {
        String name = ctx.name.getText();
        AstText text = (AstText) visit(ctx.string);
        return new AstStringConstant(pos(ctx.getStart()), name, text) ;
    }

    @Override
    public AstStringLiteral visitTextLine(SchemacodeParser.TextLineContext ctx) {
        return AstStringLiteral.fromTerminalNode(inputFile, ctx.TEXTLINE());
    }

    @Override
    public AstSchemaItem visitSimpleTextLine(SimpleTextLineContext ctx) {
        return AstStringLiteral.fromTerminalNode(inputFile, ctx.TEXTLINE());
    }

    @Override
    public AstStringBlock visitTextBlock(SchemacodeParser.TextBlockContext ctx) {
        if (ctx.TEXTBLOCK1() != null) {
            return AstStringBlock.fromTerminalNode(pos(ctx.TEXTBLOCK1().getSymbol()), ctx.TEXTBLOCK1().getText());
        } else if (ctx.TEXTBLOCK2() != null) {
            return AstStringBlock.fromTerminalNode(pos(ctx.TEXTBLOCK2().getSymbol()), ctx.TEXTBLOCK2().getText());
        } else {
            throw new SchematicsInternalError("No text value in TextBlock");
        }
    }

    @Override
    public AstSchemaItem visitTextId(SchemacodeParser.TextIdContext ctx) {
        return new AstStringRef(pos(ctx.ID().getSymbol()), ctx.ID().getText());
    }
}
