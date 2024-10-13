package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.ast.AstNode;
import info.teksol.mindcode.ast.FunctionDeclaration;
import info.teksol.mindcode.ast.NoOp;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.generator.CallGraph.LogicFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Order(99)
class AstContextTest {
    private final CompilerProfile profile = CompilerProfile.standardOptimizations(false);
    private final AstContext root = AstContext.createRootNode(profile);
    private final CallGraph callGraph = CallGraph.createEmpty();
    private final FunctionDeclaration functionDeclaration1 = new FunctionDeclaration( null, false, false, "test1",List.of(), new NoOp());
    private final FunctionDeclaration functionDeclaration2 = new FunctionDeclaration( null, false, false, "test2",List.of(), new NoOp());
    private LogicFunction function1;
    private LogicFunction function2;
    private AstContext context;

    @BeforeEach
    void setUp() {
        callGraph.addFunction(functionDeclaration1);
        callGraph.addFunction(functionDeclaration2);
        function1 = callGraph.getFunction(functionDeclaration1.getName());
        function2 = callGraph.getFunction(functionDeclaration2.getName());

        AstNode node = new TestNode(AstContextType.FUNCTION, AstSubcontextType.BODY);
        context = root.createFunctionDeclaration(profile, function1, node, node.getContextType(),1.0);
    }

    @Test
    void createRootNode() {
        assertNull(root.function());
        assertNull(root.parent());
        assertEquals(NoOp.NO_OP, root.node());
        assertEquals(0,  root.level());
        assertEquals(AstContextType.ROOT,  root.contextType());
        assertEquals(AstSubcontextType.BASIC,  root.subcontextType());
        assertEquals(1.0,  root.weight());
    }

    @Test
    void createChild() {
        AstNode node = new TestNode(AstContextType.IF, AstSubcontextType.CONDITION);
        AstContext child = context.createChild(profile, node, node.getContextType());

        assertEquals(context.function(), child.function());
        assertEquals(context.level() + 1, child.level());
        assertEquals(node, child.node());
        assertEquals(AstContextType.IF, child.contextType());
        assertEquals(AstSubcontextType.CONDITION, child.subcontextType());
        assertEquals(1.0, child.weight());
    }

    @Test
    void createFunctionDeclaration() {
        AstNode node = new TestNode(AstContextType.FUNCTION, AstSubcontextType.BODY);
        AstContext child = context.createFunctionDeclaration(profile, function2, node, node.getContextType(),2.0);

        assertEquals(function2, child.function());
        assertEquals(context.level() + 1, child.level());
        assertEquals(node, child.node());
        assertEquals(AstContextType.FUNCTION, child.contextType());
        assertEquals(AstSubcontextType.BODY, child.subcontextType());
        assertEquals(2.0, child.weight());
    }

    @Test
    void createSubcontext1() {
        AstContext child = context.createSubcontext(AstSubcontextType.INIT, 2.0);

        assertEquals(context.function(), child.function());
        assertEquals(context.level(), child.level());
        assertEquals(context.node(), child.node());
        assertEquals(context.contextType(), child.contextType());
        assertEquals(AstSubcontextType.INIT, child.subcontextType());
        assertEquals(2.0, child.weight());
    }

    @Test
    void createSubcontext2() {
        AstContext child = context.createSubcontext(function2, AstSubcontextType.INIT, 2.0);

        assertEquals(function2, child.function());
        assertEquals(context.level(), child.level());
        assertEquals(context.node(), child.node());
        assertEquals(context.contextType(), child.contextType());
        assertEquals(AstSubcontextType.INIT, child.subcontextType());
        assertEquals(2.0, child.weight());
    }

    @Test
    void belongsTo() {
        assertTrue(root.belongsTo(root));
        assertTrue(context.belongsTo(root));
    }

    @Test
    void matches() {
        assertTrue(root.matches(root.contextType()));
        assertFalse(root.matches(AstContextType.ALLOCATION));
    }

    @Test
    void testMatches() {
        assertTrue(root.matches(root.contextType(), root.subcontextType()));
        assertFalse(root.matches(root.contextType(), AstSubcontextType.OUT_OF_LINE_CALL));
        assertFalse(root.matches(AstContextType.ALLOCATION, root.subcontextType()));
        assertFalse(root.matches(AstContextType.ALLOCATION, AstSubcontextType.OUT_OF_LINE_CALL));
    }

    @Test
    void testMatches1() {
        assertTrue(root.matches(root.subcontextType()));
        assertFalse(root.matches(AstSubcontextType.OUT_OF_LINE_CALL));
    }

    @Test
    void testMatches2() {
        assertTrue(root.matches(AstSubcontextType.OUT_OF_LINE_CALL, root.subcontextType()));
        assertFalse(root.matches(AstSubcontextType.OUT_OF_LINE_CALL, AstSubcontextType.CONDITION));
    }

    @Test
    void matchesRecursively() {
        assertTrue(context.matchesRecursively(AstSubcontextType.OUT_OF_LINE_CALL, root.subcontextType()));
        assertTrue(context.matchesRecursively(AstSubcontextType.OUT_OF_LINE_CALL, context.subcontextType()));
        assertFalse(context.matchesRecursively(AstSubcontextType.OUT_OF_LINE_CALL, AstSubcontextType.CONDITION));
    }

    @Test
    void testMatchesRecursively() {
        assertTrue(context.matchesRecursively(AstContextType.ALLOCATION, root.contextType()));
        assertTrue(context.matchesRecursively(AstContextType.ALLOCATION, context.contextType()));
        assertFalse(context.matchesRecursively(AstContextType.ALLOCATION, AstContextType.LOOP));
    }

    @Test
    void findContextOfType() {
        assertEquals(root, context.findContextOfType(root.contextType()));
        assertEquals(context, context.findContextOfType(context.contextType()));
        assertNull(context.findContextOfType(AstContextType.ALLOCATION));
    }

    @Test
    void findTopContextOfType() {
        AstNode node = new TestNode(root.contextType(), root.subcontextType());
        AstContext child = context.createChild(profile, node, root.contextType());

        assertEquals(root, child.findTopContextOfType(root.contextType()));
        assertEquals(context, child.findTopContextOfType(context.contextType()));
        assertNull(child.findTopContextOfType(AstContextType.ALLOCATION));
    }

    @Test
    void findDirectChild() {
        AstNode node = new TestNode(root.contextType(), root.subcontextType());
        AstContext child = context.createChild(profile, node, root.contextType());

        assertEquals(context, root.findDirectChild(child));
        assertEquals(context, root.findDirectChild(context));
        assertEquals(child, context.findDirectChild(child));
        assertNull(child.findDirectChild(root));
    }

    @Test
    void findSubcontext() {
        AstNode node = new TestNode(context.contextType(), AstSubcontextType.SYSTEM_CALL);
        AstContext child1 = context.createSubcontext(AstSubcontextType.SYSTEM_CALL, 1.0);
        AstContext child2 = context.createSubcontext(AstSubcontextType.SYSTEM_CALL, 1.0);

        assertEquals(child1, context.findSubcontext(AstSubcontextType.SYSTEM_CALL));
        assertNull(context.findSubcontext(AstSubcontextType.OUT_OF_LINE_CALL));
    }

    @Test
    void findLastSubcontext() {
        AstNode node = new TestNode(context.contextType(), AstSubcontextType.SYSTEM_CALL);
        AstContext child1 = context.createSubcontext(AstSubcontextType.SYSTEM_CALL, 1.0);
        AstContext child2 = context.createSubcontext(AstSubcontextType.SYSTEM_CALL, 1.0);

        assertEquals(child2, context.findLastSubcontext(AstSubcontextType.SYSTEM_CALL));
        assertNull(context.findLastSubcontext(AstSubcontextType.OUT_OF_LINE_CALL));
    }

    @Test
    void findSubcontexts() {
        AstNode node = new TestNode(context.contextType(), AstSubcontextType.SYSTEM_CALL);
        AstContext child1 = context.createSubcontext(AstSubcontextType.INIT, 1.0);
        AstContext child2 = context.createSubcontext(AstSubcontextType.CONDITION, 1.0);
        AstContext child3 = context.createSubcontext(AstSubcontextType.INIT, 1.0);

        List<AstContext> subcontexts = context.findSubcontexts(AstSubcontextType.INIT);
        assertEquals(2, subcontexts.size());
        assertEquals(child1, subcontexts.get(0));
        assertEquals(child3, subcontexts.get(1));
    }

    @Test
    void functionPrefix() {
        assertNull(root.functionPrefix());
        assertEquals(function1.getPrefix(), context.functionPrefix());
    }

    @Test
    void updateWeight() {
        root.updateWeight(10.0);
        assertEquals(10.0, root.weight());
    }

    @Test
    void totalWeight() {
        root.updateWeight(2.0);
        context.updateWeight(2.0);
        assertEquals(4.0, context.totalWeight());
    }

    @Test
    void children() {
        AstNode node = new TestNode(context.contextType(), AstSubcontextType.SYSTEM_CALL);
        AstContext child1 = context.createSubcontext(AstSubcontextType.INIT, 1.0);
        AstContext child2 = context.createSubcontext(AstSubcontextType.CONDITION, 1.0);
        AstContext child3 = context.createSubcontext(AstSubcontextType.INIT, 1.0);

        assertEquals(child1,context.firstChild());
        assertEquals(child3,context.lastChild());
        assertEquals(child2,context.nextChild(child1));
        assertEquals(child3,context.nextChild(child2));
        assertNull(context.nextChild(child3));
    }

    private static class TestNode implements AstNode {
        private final AstContextType contextType;
        private final AstSubcontextType subcontextType;

        public TestNode(AstContextType contextType, AstSubcontextType subcontextType) {
            this.contextType = contextType;
            this.subcontextType = subcontextType;
        }

        @Override
        public List<AstNode> getChildren() {
            return List.of();
        }

        @Override
        public InputPosition getInputPosition() {
            return null;
        }

        @Override
        public AstContextType getContextType() {
            return contextType;
        }

        @Override
        public AstSubcontextType getSubcontextType() {
            return subcontextType;
        }
    }
}