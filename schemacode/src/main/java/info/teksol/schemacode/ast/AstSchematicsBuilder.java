package info.teksol.schemacode.ast;

import info.teksol.mindcode.compiler.CompilerMessage;
import info.teksol.schemacode.SchematicsInternalError;
import info.teksol.schemacode.grammar.SchemacodeBaseVisitor;
import info.teksol.schemacode.grammar.SchemacodeParser;
import info.teksol.schemacode.grammar.SchemacodeParser.BooleanContext;
import info.teksol.schemacode.grammar.SchemacodeParser.DefinitionsContext;
import info.teksol.schemacode.grammar.SchemacodeParser.SchemaTagContext;
import info.teksol.schemacode.schema.Language;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AstSchematicsBuilder extends SchemacodeBaseVisitor<AstSchemaItem> {

    private final Consumer<CompilerMessage> messageListener;

    public AstSchematicsBuilder(Consumer<CompilerMessage> messageListener) {
        this.messageListener = messageListener;
    }

    public static AstDefinitions generate(DefinitionsContext parseTree, Consumer<CompilerMessage> messageListener) {
        final AstSchematicsBuilder builder = new AstSchematicsBuilder(messageListener);
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

        return new AstDefinitions(list);
    }

    @Override
    public AstSchemaItem visitSchematic(SchemacodeParser.SchematicContext ctx) {
        //final String id = ctx.name == null ? null : ctx.name.getText();
        final List<AstSchemaAttribute> attributes = new ArrayList<>();
        final List<AstBlock> blocks = new ArrayList<>();
        for (SchemacodeParser.SchematicItemContext item : ctx.schematicItem()) {
            AstSchemaItem schemaItem = visit(item);
            switch (schemaItem) {
                case AstSchemaAttribute a  -> attributes.add(a);
                case AstBlock b            -> blocks.add(b);
                case null               -> {}
                default                 -> throw new SchematicsInternalError("Unexpected item " + schemaItem);
            }
        }

        return new AstSchematic(attributes, blocks);
    }

    // Attributes

    @Override
    public AstSchemaItem visitName(SchemacodeParser.NameContext ctx) {
        return new AstSchemaAttribute("name", visit(ctx.textDef()));
    }

    @Override
    public AstSchemaItem visitDescription(SchemacodeParser.DescriptionContext ctx) {
        return new AstSchemaAttribute("description", visit(ctx.textDef()));
    }

    @Override
    public AstSchemaItem visitDimensions(SchemacodeParser.DimensionsContext ctx) {
        return new AstSchemaAttribute("dimensions", visit(ctx.coordinates()));
    }

    @Override
    public AstSchemaItem visitSchemaTag(SchemaTagContext ctx) {
        return new AstSchemaAttribute("label", visit(ctx.tag));
    }

    // Blocks

    @Override
    public AstSchemaItem visitBlock(SchemacodeParser.BlockContext ctx) {
        List<String> labels = processLabels(ctx.labels);
        String type = ctx.type.getText();
        AstCoordinates position = visitPosition(ctx.position());
        AstDirection direction = maybeVisit(ctx.direction());
        AstConfiguration configuration = maybeVisit(ctx.configuration());

        return new AstBlock(labels, type, position, direction, configuration);
    }


    // Configuration

    @Override
    public AstSchemaItem visitBoolean(BooleanContext ctx) {
        return new AstBoolean(ctx.status.getText().equals("enabled"));
    }

    @Override
    public AstVirtual visitVirtual(SchemacodeParser.VirtualContext ctx) {
        return AstVirtual.VIRTUAL;
    }

    @Override
    public AstConnections visitConnections(SchemacodeParser.ConnectionsContext ctx) {
        List<AstConnection> list = ctx.connectionList().connection().stream()
                .map(this::visit)
                .map(AstConnection.class::cast)
                .toList();

        return new AstConnections(list);
    }

    @Override
    public AstConnection visitConnAbs(SchemacodeParser.ConnAbsContext ctx) {
        return new AstConnection(visitCoordinates(ctx.coordinates()));
    }

    @Override
    public AstConnection visitConnRel(SchemacodeParser.ConnRelContext ctx) {
        return new AstConnection(visitRelativeCoordinates(ctx.relativeCoordinates()));
    }

    @Override
    public AstConnection visitConnName(SchemacodeParser.ConnNameContext ctx) {
        return new AstConnection(ctx.Id().getSymbol().getText());
    }

    @Override
    public AstItemReference visitItem(SchemacodeParser.ItemContext ctx) {
        return new AstItemReference(ctx.Ref().getSymbol().getText());
    }

    @Override
    public AstSchemaItem visitLiquid(SchemacodeParser.LiquidContext ctx) {
        return new AstLiquidReference(ctx.Ref().getSymbol().getText());
    }

    @Override
    public AstSchemaItem visitUnit(SchemacodeParser.UnitContext ctx) {
        return new AstUnitReference(ctx.Ref().getSymbol().getText());
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

        return new AstProcessor(links, program, language);
    }

    @Override
    public AstLinkPattern visitLinkPattern(SchemacodeParser.LinkPatternContext ctx) {
        return new AstLinkPattern(ctx.linkPattern.getText());
    }

    @Override
    public AstLinkPos visitLinkPos(SchemacodeParser.LinkPosContext ctx) {
        AstConnection connection = (AstConnection) visit(ctx.linkPos);
        String name = ctx.alias == null ? null : ctx.alias.getText();
        boolean virtual = ctx.virtual != null;
        return new AstLinkPos(connection, name, virtual);
    }

    @Override
    public AstSchemaItem visitProgramString(SchemacodeParser.ProgramStringContext ctx) {
        return new AstProgramText((AstText) visit(ctx.text));
    }

    @Override
    public AstSchemaItem visitProgramFile(SchemacodeParser.ProgramFileContext ctx) {
        return new AstProgramFile((AstText) visit(ctx.file));
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
        return new AstCoordinates(x, y);
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
        return new AstDirection(ctx.dir.getText());
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
        return new AstStringConstant(name, text) ;
    }

    @Override
    public AstStringLiteral visitTextLine(SchemacodeParser.TextLineContext ctx) {
        return new AstStringLiteral(ctx.TextLine().getText());
    }

    @Override
    public AstStringBlock visitTextBlock(SchemacodeParser.TextBlockContext ctx) {
        if (ctx.TextBlock1() != null) {
            return new AstStringBlock(ctx.TextBlock1().getText());
        } else if (ctx.TextBlock2() != null) {
            return new AstStringBlock(ctx.TextBlock2().getText());
        } else {
            throw new SchematicsInternalError("No text value in TextBlock");
        }
    }

    @Override
    public AstSchemaItem visitTextId(SchemacodeParser.TextIdContext ctx) {
        return new AstStringRef(ctx.Id().getText());
    }
}
