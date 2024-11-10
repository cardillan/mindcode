package info.teksol.schemacode.ast;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.v3.InputFile;
import info.teksol.schemacode.SchematicsInternalError;
import info.teksol.schemacode.grammar.SchemacodeBaseVisitor;
import info.teksol.schemacode.grammar.SchemacodeParser;
import info.teksol.schemacode.grammar.SchemacodeParser.*;
import info.teksol.schemacode.schematics.Language;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AstSchematicsBuilder extends SchemacodeBaseVisitor<AstSchemaItem> {

    private final InputFile inputFile;

    public AstSchematicsBuilder(InputFile inputFile) {
        this.inputFile = inputFile;
    }

    public static AstDefinitions generate(InputFile inputFile, DefinitionsContext parseTree,
            Consumer<MindcodeMessage> messageListener) {
        final AstSchematicsBuilder builder = new AstSchematicsBuilder(inputFile);
        final AstSchemaItem item = builder.visit(parseTree);
        return (AstDefinitions) item;
    }

    @SuppressWarnings("unchecked")
    private <T extends AstSchemaItem> T maybeVisit(ParseTree tree) {
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

    private InputPosition pos(Token token) {
        return InputPosition.create(inputFile, token);
    }

    @Override
    public AstSchemaItem visitSchematic(SchemacodeParser.SchematicContext ctx) {
        //final String id = ctx.name == null ? null : ctx.name.getText();
        final List<AstSchemaAttribute> attributes = new ArrayList<>();
        final List<AstBlock> blocks = new ArrayList<>();
        for (SchemacodeParser.SchematicItemContext item : ctx.schematicItem()) {
            AstSchemaItem schemaItem = visit(item);
            if (schemaItem instanceof AstSchemaAttribute a) {
                attributes.add(a);
            } else if (schemaItem instanceof AstBlock b) {
                blocks.add(b);
            } else if (schemaItem != null) {
                throw new SchematicsInternalError("Unexpected item " + schemaItem);
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

    // Blocks

    @Override
    public AstSchemaItem visitBlock(SchemacodeParser.BlockContext ctx) {
        List<String> labels = processLabels(ctx.labels);
        String type = ctx.type.getText();
        AstCoordinates position = visitPosition(ctx.position());
        AstDirection direction = maybeVisit(ctx.direction());
        AstConfiguration configuration = maybeVisit(ctx.configuration());

        return new AstBlock(pos(ctx.getStart()), labels, type, position, direction, configuration);
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
        return new AstConnection(pos(ctx.getStart()), ctx.Id().getSymbol().getText());
    }

    @Override
    public AstSchemaItem visitBlocktype(SchemacodeParser.BlocktypeContext ctx) {
        return new AstBlockReference(pos(ctx.getStart()), ctx.Ref().getSymbol().getText());
    }

    @Override
    public AstSchemaItem visitUnitcommand(UnitcommandContext ctx) {
        return new AstUnitCommandReference(pos(ctx.getStart()), ctx.Ref().getSymbol().getText());
    }

    @Override
    public AstItemReference visitItem(SchemacodeParser.ItemContext ctx) {
        return new AstItemReference(pos(ctx.getStart()), ctx.Ref().getSymbol().getText());
    }

    @Override
    public AstSchemaItem visitLiquid(SchemacodeParser.LiquidContext ctx) {
        return new AstLiquidReference(pos(ctx.getStart()), ctx.Ref().getSymbol().getText());
    }

    @Override
    public AstSchemaItem visitUnit(SchemacodeParser.UnitContext ctx) {
        return new AstUnitReference(pos(ctx.getStart()), ctx.Ref().getSymbol().getText());
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
            program = null;
            language = Language.NONE;
        }

        return new AstProcessor(pos(ctx.getStart()), links, program, language);
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

    // Coordinates & direction

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

    private static List<String> processLabels(SchemacodeParser.LabelListContext labels) {
        return labels == null
                ? List.of()
                : labels.Id().stream()
                .map(TerminalNode::getSymbol)
                .map(Token::getText)
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
        return AstStringLiteral.fromTerminalNode(inputFile, ctx.TextLine());
    }

    @Override
    public AstStringBlock visitTextBlock(SchemacodeParser.TextBlockContext ctx) {
        if (ctx.TextBlock1() != null) {
            return AstStringBlock.fromTerminalNode(pos(ctx.TextBlock1().getSymbol()), ctx.TextBlock1().getText());
        } else if (ctx.TextBlock2() != null) {
            return AstStringBlock.fromTerminalNode(pos(ctx.TextBlock2().getSymbol()), ctx.TextBlock2().getText());
        } else {
            throw new SchematicsInternalError("No text value in TextBlock");
        }
    }

    @Override
    public AstSchemaItem visitTextId(SchemacodeParser.TextIdContext ctx) {
        return new AstStringRef(pos(ctx.Id().getSymbol()), ctx.Id().getText());
    }
}
