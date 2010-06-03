package org.basex.server;

import static org.basex.core.Text.*;

import java.io.IOException;

import org.basex.core.Context;
import org.basex.data.XMLSerializer;
import org.basex.io.PrintOutput;
import org.basex.query.QueryException;
import org.basex.query.QueryProcessor;
import org.basex.query.item.Item;
import org.basex.query.iter.Iter;

/**
 * Container for processes executing a query with iterative results.
 *
 * @author Workgroup DBIS, University of Konstanz 2005-10, ISC License
 * @author Andreas Weiler
 */
public class QueryProcess {
  
  /** Id. */
  public int id;
  /** Output. */
  private PrintOutput out;
  /** Processor. */
  private QueryProcessor processor;
  /** Iterator. */
  private Iter iter;
  /** Serializer. */
  private XMLSerializer serializer;
  /** Log. */
  private ServerProcess serverProc;
  /** Update flag. */
  private boolean updating;
  /** Context. */
  private Context ctx;
  
  /**
   * Constructor.
   * @param i id
   * @param o output
   * @param sp serverProcess
   */
  public QueryProcess(final int i, final PrintOutput o,
      final ServerProcess sp) {
    this.id = i;
    this.out = o;
    this.serverProc = sp;
  }
  
  /**
   * Starts the query process.
   * @param s input string
   * @param c context
   * @throws IOException Exception
   */
  public void start(final String s, final Context c) throws IOException {
    ctx = c;
    processor = new QueryProcessor(s, ctx);
    updating = processor.ctx.updating;
    ctx.sema.before(updating);
    try {
    iter = processor.iter();
    serializer = new XMLSerializer(out);
    out.print(String.valueOf(id));
    send(true);
    } catch(QueryException ex) {
      // invalid command was sent by a client; create error feedback
      serverProc.log.write(this, s, INFOERROR + ex.extended());
      out.print(String.valueOf(0));
      send(true);
      out.print(ex.extended());
      send(true);
    }
  }
  
  /**
   * Returns the next item to the client.
   * @throws IOException Exception
   */
  public void more() throws IOException {
    Item item;
    try {
    if((item = iter.next()) != null) {
      send(true);
      item.serialize(serializer);
      send(true);
    } else {
      send(false);
      close();
    }
    } catch(Exception ex) {
      send(false);
      close();
    }
  }
  
  /**
   * Closes the query process.
   * @throws IOException Exception
   */
  public void close() throws IOException {
    ctx.sema.after(updating);
    serializer.close();
    processor.close();
    serverProc.queries.remove(this);
  }
  
  /**
   * Sends the success flag to the client.
   * @param ok success flag
   * @throws IOException Exception
   */
  private void send(final boolean ok) throws IOException {
      serverProc.send(ok);
  }
}
