package org.basex.query.expr.path;

import java.util.List;

import org.basex.query.*;
import org.basex.query.expr.*;
import org.basex.query.value.*;
import org.basex.query.value.item.*;
import org.basex.query.value.node.*;
import org.basex.query.value.type.*;
import org.basex.util.*;

/**
 * Abstract node test.
 *
 * @author BaseX Team 2005-20, BSD License
 * @author Christian Gruen
 */
public abstract class Test extends ExprInfo {
  /** Node type. */
  public final NodeType type;

  /**
   * Constructor.
   * @param type node type
   */
  Test(final NodeType type) {
    this.type = type;
  }

  /**
   * Returns a node test, a name test or {@code null}.
   * @param type node type (element, attribute, processing instruction)
   * @param name node name
   * @param ann type annotation
   * @param ns default element namespace (may be {@code null})
   * @return test or {@code null}
   */
  public static Test get(final NodeType type, final QNm name, final Type ann, final byte[] ns) {
    if(!(ann == null || ann == AtomType.ATY || ann == AtomType.UTY || type == NodeType.ATT &&
      (ann == AtomType.AST || ann == AtomType.AAT || ann == AtomType.ATM))) return null;

    return name == null ? KindTest.get(type) :
      new NameTest(type, name, type == NodeType.PI ? NamePart.LOCAL : NamePart.FULL, ns);
  }

  /**
   * Returns a single test for the specified tests.
   * @param tests tests
   * @return test, or {@code null} if no test was supplied.
   */
  public static Test get(final List<Test> tests) {
    final int tl = tests.size();
    if(tl == 0) return null;
    if(tl == 1) return tests.get(0);

    final NodeType type = tests.get(0).type;
    for(int t = 1; t < tl; t++) {
      if(tests.get(t).type != type) return null;
    }
    return new UnionTest(type, tests);
  }

  /**
   * Optimizes the expression.
   * @param value context value (can be {@code null})
   * @return false if test always returns false
   */
  @SuppressWarnings("unused")
  public boolean optimize(final Value value) {
    return true;
  }

  /**
   * Checks if the specified node matches the test.
   * @param node node to be checked
   * @return result of check
   */
  public abstract boolean matches(ANode node);

  /**
   * Checks if the specified item matches the test.
   * @param item item to be checked
   * @return result of check
   */
  public final boolean matches(final Item item) {
    return item instanceof ANode && matches((ANode) item);
  }

  /**
   * Copies this test.
   * @return deep copy
   */
  public abstract Test copy();

  /**
   * Computes the intersection between two tests.
   * @param test other test
   * @return intersection if it exists, {@code null} otherwise
   */
  public abstract Test intersect(Test test);

  @Override
  public void plan(final QueryPlan plan) {
    throw Util.notExpected();
  }
}
