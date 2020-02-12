package org.basex.query.func.sessions;

import org.basex.query.*;
import org.basex.query.value.*;

/**
 * Function implementation.
 *
 * @author BaseX Team 2005-20, BSD License
 * @author Christian Gruen
 */
public final class SessionsNames extends SessionsFn {
  @Override
  public Value value(final QueryContext qc) throws QueryException {
    return session(qc).names();
  }
}
