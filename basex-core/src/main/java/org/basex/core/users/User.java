package org.basex.core.users;

import static org.basex.core.users.UserText.*;
import static org.basex.util.Strings.*;
import static org.basex.util.Token.*;
import static org.basex.util.Token.eq;
import static org.basex.util.XMLAccess.*;

import java.util.*;
import java.util.Map.Entry;

import org.basex.core.*;
import org.basex.query.*;
import org.basex.query.value.node.*;
import org.basex.util.*;

/**
 * This class contains information on a single user.
 *
 * @author BaseX Team 2005-23, BSD License
 * @author Christian Gruen
 */
public final class User {
  /** Stored password codes. */
  private final EnumMap<Algorithm, EnumMap<Code, String>> passwords =
      new EnumMap<>(Algorithm.class);
  /** Database patterns for local permissions. */
  private final LinkedHashMap<String, Perm> patterns = new LinkedHashMap<>();
  /** Permission. */
  private Perm perm = Perm.NONE;
  /** Name. */
  private String name;
  /** Info node (can be {@code null}). */
  private ANode info;

  /**
   * Constructor.
   * @param name username
   */
  public User(final String name) {
    this.name = name;
  }

  /**
   * Constructor with password.
   * @param name username
   * @param password password
   */
  public User(final String name, final String password) {
    this(name);
    password(password);
  }

  /**
   * Indicates if a passwords are stored for a user.
   * @return result of check
   */
  public boolean enabled() {
    return !passwords.isEmpty();
  }

  /**
   * Parses a single user from the specified node.
   * @param user user node
   * @throws BaseXException database exception
   */
  User(final ANode user) throws BaseXException {
    name = string(attribute("Root", user, NAME));
    perm = attribute(name, user, PERMISSION, Perm.values());

    for(final ANode child : children(user)) {
      if(eq(child.qname().id(), PASSWORD)) {
        final EnumMap<Code, String> ec = new EnumMap<>(Code.class);
        final Algorithm algorithm = attribute(name, child, ALGORITHM, Algorithm.values());
        if(passwords.containsKey(algorithm)) throw new BaseXException(
            name + ": Algorithm \"" + algorithm + "\" supplied more than once.");
        passwords.put(algorithm, ec);

        for(final ANode code : children(child)) {
          final Code cd = value(name, code.qname().id(), algorithm.codes);
          if(ec.containsKey(cd)) throw new BaseXException(
              name + ", " + algorithm + ": Code \"" + code + "\" supplied more than once.");
          ec.put(cd, string(code.string()));
        }
        for(final Code code : algorithm.codes) {
          if(ec.get(code) == null)
            throw new BaseXException(name + ", " + algorithm + ": Code \"" + code + "\" missing.");
        }
      } else if(eq(child.qname().id(), DATABASE)) {
        // parse local permissions
        final String nm = string(attribute(name, child, PATTERN));
        final Perm prm = attribute(name, child, PERMISSION, Perm.values());
        patterns.put(nm, prm);
      } else if(eq(child.qname().id(), INFO)) {
        info = child.finish();
      } else {
        throw new BaseXException(name + ": invalid element: <" + child.qname() + "/>.");
      }
    }
  }

  /**
   * Returns user information as XML.
   * @param qc query context ({@code null} if element will only be created for serialization)
   * @param ii input info
   * @return user element
   * @throws QueryException query exception
   */
  public synchronized FElem toXML(final QueryContext qc, final InputInfo ii) throws QueryException {
    final FElem user = new FElem(USER).add(NAME, name).add(PERMISSION, perm.toString());
    passwords.forEach((key, value) -> {
      final FElem pw = new FElem(PASSWORD).add(ALGORITHM, key.toString());
      value.forEach((k, v) -> {
        if(!v.isEmpty()) pw.add(new FElem(k.toString()).add(v));
      });
      user.add(pw);
    });
    patterns.forEach((key, value) -> user.add(new FElem(DATABASE).add(PATTERN, key).
        add(PERMISSION, value.toString())));
    if(info != null) {
      if(qc != null) {
        // create copy of the info node if query context is available
        user.add(info.materialize(n -> false, ii, qc));
      } else {
        // otherwise, referenced original info node and invalidate parent reference
        user.add(info);
        info.parent(null);
      }
    }
    return user;
  }

  /**
   * Sets the username.
   * @param nm name
   */
  public synchronized void name(final String nm) {
    name = nm;
  }

  /**
   * Drops the specified database pattern.
   * @param pattern database pattern
   */
  public synchronized void drop(final String pattern) {
    patterns.remove(pattern);
  }

  /**
   * Returns the username.
   * @return name
   */
  public synchronized String name() {
    return name;
  }

  /**
   * Computes new password hashes.
   * @param password password (plain text)
   */
  public synchronized void password(final String password) {
    for(final Algorithm algorithm : Algorithm.values()) {
      final EnumMap<Code, String> codes = passwords.computeIfAbsent(algorithm,
          k -> new EnumMap<>(Code.class));
      if(algorithm == Algorithm.SALTED_SHA256) {
        final String salt = Long.toString(System.nanoTime());
        codes.put(Code.SALT, salt);
        codes.put(Code.HASH, sha256(salt + password));
      } else {
        codes.put(Code.HASH, digest(name, password));
      }
    }
  }

  /**
   * Returns the specified code.
   * @param algorithm used algorithm
   * @param code code to be returned
   * @return code, or {@code null} if code does not exist
   */
  public synchronized String code(final Algorithm algorithm, final Code code) {
    return passwords.get(algorithm).get(code);
  }

  /**
   * Returns the global permission, or the permission for the specified database.
   * @param db database pattern (can be {@code null})
   * @return permission
   */
  public synchronized Perm perm(final String db) {
    if(db != null) {
      final Entry<String, Perm> entry = find(db);
      if(entry != null) return entry.getValue();
    }
    return perm;
  }

  /**
   * Returns the first entry for the specified database.
   * @param pattern database pattern
   * @return entry, or {@code null} if no entry exists
   */
  synchronized Entry<String, Perm> find(final String pattern) {
    for(final Entry<String, Perm> entry : patterns.entrySet()) {
      if(Databases.regex(entry.getKey()).matcher(pattern).matches()) return entry;
    }
    return null;
  }

  /**
   * Sets the global permission.
   * @param prm permission
   * @return self reference
   */
  public synchronized User perm(final Perm prm) {
    perm = prm;
    return this;
  }

  /**
   * Sets the permission.
   * @param prm permission
   * @param pattern database pattern (can be empty)
   * @return self reference
   */
  public synchronized User perm(final Perm prm, final String pattern) {
    if(pattern.isEmpty()) {
      perm(prm);
    } else {
      patterns.put(pattern, prm);
    }
    return this;
  }

  /**
   * Tests if the user has the specified permission.
   * @param prm permission to be checked
   * @return result of check
   */
  public synchronized boolean has(final Perm prm) {
    return has(prm, null);
  }

  /**
   * Tests if the user has the specified permission.
   * @param prm permission to be checked
   * @param db database pattern (can be {@code null})
   * @return result of check
   */
  public synchronized boolean has(final Perm prm, final String db) {
    return perm(db).ordinal() >= prm.ordinal();
  }

  /**
   * Computes the hash from the specified password and checks if it is correct.
   * @param password (plain text)
   * @return name
   */
  public synchronized boolean matches(final String password) {
    if(!enabled()) return false;
    final EnumMap<Code, String> algorithm = passwords.get(Algorithm.SALTED_SHA256);
    return sha256(algorithm.get(Code.SALT) + password).equals(algorithm.get(Code.HASH));
  }

  /**
   * Returns the info element.
   * @return info element (can be {@code null})
   */
  public synchronized ANode info() {
    return info;
  }

  /**
   * Sets the info element.
   * @param elem info element
   */
  public synchronized void info(final ANode elem) {
    info = elem.hasChildren() || elem.attributeIter().size() != 0 ? elem : null;
  }

  /**
   * Returns the digest hash value.
   * @param name username
   * @param password password
   * @return digest digest hash
   */
  private static String digest(final String name, final String password) {
    return md5(name + ':' + Prop.NAME + ':' + password);
  }
}
