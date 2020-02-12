package org.basex.query.func.sessions;

import org.basex.query.*;
import org.basex.query.value.item.*;
import org.basex.util.*;

/**
 * Function implementation.
 *
 * @author BaseX Team 2005-20, BSD License
 * @author Christian Gruen
 */
public final class SessionsCreated extends SessionsFn {
  @Override
  public Item item(final QueryContext qc, final InputInfo ii) throws QueryException {
    return session(qc).created();
  }
}
