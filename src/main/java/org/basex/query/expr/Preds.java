package org.basex.query.expr;

import static org.basex.query.QueryText.*;
import java.io.IOException;
import org.basex.data.Serializer;
import org.basex.query.QueryContext;
import org.basex.query.QueryException;
import org.basex.query.item.Item;
import org.basex.query.item.Seq;
import org.basex.query.item.Type;
import org.basex.query.path.Step;
import org.basex.query.util.Var;
import org.basex.util.Array;
import org.basex.util.InputInfo;

/**
 * Abstract predicate expression, implemented by {@link Filter} and
 * {@link Step}.
 *
 * @author Workgroup DBIS, University of Konstanz 2005-10, ISC License
 * @author Christian Gruen
 */
public abstract class Preds extends ParseExpr {
  /** Predicates. */
  public Expr[] pred;

  /**
   * Constructor.
   * @param ii input info
   * @param p predicates
   */
  public Preds(final InputInfo ii, final Expr[] p) {
    super(ii);
    pred = p;
  }

  @Override
  public Expr comp(final QueryContext ctx) throws QueryException {
    for(final Expr p : pred) checkUp(p, ctx);

    // as predicates will not necessarily start from the document node,
    // the context item type is temporarily set to element
    final Item ci = ctx.item;
    final Type ct = ci != null ? ci.type : null;
    if(ct == Type.DOC) ctx.item.type = Type.ELM;

    Expr e = this;
    for(int p = 0; p < pred.length; p++) {
      Expr pr = pred[p].comp(ctx);
      pr = Pos.get(CmpV.Op.EQ, pr, pr, input);

      if(pr.value()) {
        if(!pr.ebv(ctx, input).bool(input)) {
          ctx.compInfo(OPTFALSE, pr);
          e = Seq.EMPTY;
          break;
        }
        ctx.compInfo(OPTTRUE, pr);
        pred = Array.delete(pred, p--);
      } else {
        pred[p] = pr;
      }
    }
    ctx.item = ci;
    if(ct != null) ctx.item.type = ct;
    return e;
  }

  @Override
  public boolean uses(final Use u, final QueryContext ctx) {
    for(final Expr p : pred) {
      if(u == Use.POS && p.returned(ctx).mayBeNum() || p.uses(u, ctx))
        return true;
    }
    return false;
  }

  @Override
  public boolean removable(final Var v, final QueryContext ctx) {
    for(final Expr p : pred) if(p.uses(Use.VAR, ctx)) return false;
    return true;
  }

  @Override
  public Expr remove(final Var v) {
    for(int p = 0; p < pred.length; p++) pred[p] = pred[p].remove(v);
    return this;
  }

  @Override
  public final String color() {
    return "FFFF66";
  }

  @Override
  public void plan(final Serializer ser) throws IOException {
    for(final Expr p : pred) p.plan(ser);
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    for(final Expr e : pred) sb.append("[" + e + "]");
    return sb.toString();
  }
}
