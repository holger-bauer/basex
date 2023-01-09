/* Generated By:JavaCC: Do not edit this line. RegExParser.java */
package org.basex.query.util.regex.parse;

import static org.basex.query.QueryError.*;

import java.util.regex.*;

import org.basex.query.*;
import org.basex.query.util.regex.*;
import org.basex.util.*;
import static org.basex.util.Token.*;
import static java.util.regex.Pattern.*;

/**
 * A parser for XSD regular expressions.
 *
 * @author BaseX Team 2005-23, BSD License
 * @author Leo Woerteler
 */
@SuppressWarnings("all")
public class RegExParser implements RegExParserConstants {
  /** Group counter. */
  private int groups;
  /** Current backref's number. */
  private int backref;
  /** Closed groups. */
  private final BitArray closed = new BitArray();
  /** If the wildcard {@code .} matches any character. */
  private boolean dotAll;
  /** Multi-line matching mode, {@code ^} and {@code $} match on line bounds. */
  private boolean multiLine;

  /**
   * Constructor.
   * @param regex regular expression to parse
   * @param strip strip whitespace while lexing
   * @param all dot matches all
   * @param multi multi line search
   */
  public RegExParser(final byte[] regex, final boolean strip, final boolean all,
      final boolean multi) {
    this(new RegExLexer(regex, strip));
    dotAll = all;
    multiLine = multi;
  }

  /**
   * Returns the number of parsed groups.
   * @return number of groups
   */
  final public int groups() {
    return groups;
  }

  /**
   * Root production.
   * @return expression
   * @throws ParseException parsing exception
   */
  final public RegExp parse() throws ParseException {
    RegExp regex;
    regex = regExp();
    jj_consume_token(0);
      {if (true) return regex;}
    throw new Error("Missing return statement in function");
  }

  /**
   * Parses the "regExp" rule.
   * @return expression
   * @throws ParseException parsing exception
   */
  final public RegExp regExp() throws ParseException {
    final RegExpList brs = new RegExpList();
        brs.add(branch());
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case OR:
        ;
        break;
      default:
        jj_la1[0] = jj_gen;
        break label_1;
      }
      jj_consume_token(OR);
               brs.add(branch());
    }
      {if (true) return brs.size() == 1 ? brs.get(0) : new Disjunction(brs.finish());}
    throw new Error("Missing return statement in function");
  }

  /**
   * Parses the "branch" rule.
   * Parses the "piece" rule.
   * @return expression
   * @throws ParseException parsing exception
   */
  final public RegExp branch() throws ParseException {
    RegExp atom;
    final RegExpList pieces = new RegExpList();
    Quantifier qu = null;
    label_2:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case NPAR_OPEN:
      case PAR_OPEN:
      case CHAR:
      case DIGIT:
      case BACK_REF:
      case WILDCARD:
      case LINE_START:
      case LINE_END:
      case SINGLE_ESC:
      case MULTI_ESC:
      case CAT_ESC:
      case BR_OPEN:
        ;
        break;
      default:
        jj_la1[1] = jj_gen;
        break label_2;
      }
      atom = atom();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case Q_MARK:
      case STAR:
      case PLUS:
      case QUANT_OPEN:
        qu = quantifier();
        break;
      default:
        jj_la1[2] = jj_gen;
        ;
      }
        pieces.add(qu == null ? atom : new Piece(atom, qu));
        qu = null;
    }
      {if (true) return pieces.size() == 1 ? pieces.get(0) : new Branch(pieces.finish());}
    throw new Error("Missing return statement in function");
  }

  /**
   * Parses the "quantifier" rule.
   * @return expression
   * @throws ParseException parsing exception
   */
  final public Quantifier quantifier() throws ParseException {
    int min = 0, max = 0;
    boolean lazy = false;
    int[] qu = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case Q_MARK:
      jj_consume_token(Q_MARK);
                 max = 1;
      break;
    case STAR:
      jj_consume_token(STAR);
                 max = -1;
      break;
    case PLUS:
      jj_consume_token(PLUS);
                 min = 1; max = -1;
      break;
    case QUANT_OPEN:
      jj_consume_token(QUANT_OPEN);
      qu = quantity();
                                       min = qu[0]; max = qu[1];
      jj_consume_token(QUANT_CLOSE);
      break;
    default:
      jj_la1[3] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case Q_MARK:
      jj_consume_token(Q_MARK);
                 lazy = true;
      break;
    default:
      jj_la1[4] = jj_gen;
      ;
    }
      {if (true) return new Quantifier(min, max, lazy);}
    throw new Error("Missing return statement in function");
  }

  /**
   * Parses the "quantity" rule.
   * Parses the "quantRange" rule.
   * Parses the "quantMin" rule.
   * Parses the "quantExact" rule.
   * @return quantity
   * @throws ParseException parsing exception
   */
  final public int[] quantity() throws ParseException {
    final int[] qty = new int[2];
    jj_consume_token(NUMBER);
      try {
        qty[0] = qty[1] = Integer.parseInt((String) token.getValue());
      } catch(final NumberFormatException ex) {
        {if (true) throw new ParseException("Number in quantifier is too large");}
      }
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case COMMA:
      jj_consume_token(COMMA);
                qty[1] = -1;
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case NUMBER:
        jj_consume_token(NUMBER);
          try {
            qty[1] = Integer.parseInt((String) token.getValue());
          } catch(final NumberFormatException ex) {
            {if (true) throw new ParseException("Number in quantifier is too large");}
          }
          if(qty[0] > qty[1]) {if (true) throw new ParseException("Illegal quantifier: " +
              qty[0] + " > " + qty[1]);}
        break;
      default:
        jj_la1[5] = jj_gen;
        ;
      }
      break;
    default:
      jj_la1[6] = jj_gen;
      ;
    }
      {if (true) return qty;}
    throw new Error("Missing return statement in function");
  }

  /**
   * Parses the "atom" rule.
   * @return expression
   * @throws ParseException parsing exception
   */
  final public RegExp atom() throws ParseException {
    RegExp nd = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case CHAR:
    case DIGIT:
      nd = Char();
      break;
    case WILDCARD:
    case LINE_START:
    case LINE_END:
    case SINGLE_ESC:
    case MULTI_ESC:
    case CAT_ESC:
    case BR_OPEN:
      nd = charClass();
      break;
    case NPAR_OPEN:
      jj_consume_token(NPAR_OPEN);
      nd = regExp();
      jj_consume_token(PAR_CLOSE);
        nd = new Group(nd, false);
      break;
    case PAR_OPEN:
      jj_consume_token(PAR_OPEN);
                    final int grp = ++groups;
      nd = regExp();
      jj_consume_token(PAR_CLOSE);
        closed.set(grp);
        nd = new Group(nd, true);
      break;
    case BACK_REF:
      nd = backReference();
      break;
    default:
      jj_la1[7] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
      {if (true) return nd;}
    throw new Error("Missing return statement in function");
  }

  /**
   * Parses the "Char" rule.
   * @return expression
   * @throws ParseException parsing exception
   */
  final public Literal Char() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case CHAR:
      jj_consume_token(CHAR);
      break;
    case DIGIT:
      jj_consume_token(DIGIT);
      break;
    default:
      jj_la1[8] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
      {if (true) return new Literal(token.image.codePointAt(0));}
    throw new Error("Missing return statement in function");
  }

  /**
   * Parses the "backReference" rule.
   * @return expression
   * @throws ParseException parsing exception
   */
  final public BackRef backReference() throws ParseException {
    Token tok;
    tok = jj_consume_token(BACK_REF);
      backref = token.image.charAt(1) - '0';
    label_3:
    while (true) {
      if (jj_2_1(1) && (10 * backref + token.next.image.charAt(0) - '0' <= groups)) {
        ;
      } else {
        break label_3;
      }
      jj_consume_token(DIGIT);
        backref = 10 * backref + token.image.charAt(0) - '0';
    }
      if(!closed.get(backref))
        {if (true) throw new ParseException("Illegal back-reference: \u005c\u005c" + backref);}
      {if (true) return new BackRef(backref);}
    throw new Error("Missing return statement in function");
  }

  /**
   * Parses the "charClass" rule.
   * @return expression
   * @throws ParseException parsing exception
   */
  final public RegExp charClass() throws ParseException {
    RegExp nd = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case SINGLE_ESC:
    case MULTI_ESC:
    case CAT_ESC:
      nd = charClassEsc();
      break;
    case BR_OPEN:
      nd = charClassExpr();
      break;
    case WILDCARD:
      jj_consume_token(WILDCARD);
                     nd = Wildcard.get(dotAll);
      break;
    case LINE_START:
      jj_consume_token(LINE_START);
                     nd = LineBorder.get(true, multiLine);
      break;
    case LINE_END:
      jj_consume_token(LINE_END);
                     nd = LineBorder.get(false, multiLine);
      break;
    default:
      jj_la1[9] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
      {if (true) return nd;}
    throw new Error("Missing return statement in function");
  }

  /**
   * Parses the "charClassEsc" rule.
   * @return expression
   * @throws ParseException parsing exception
   */
  final public RegExp charClassEsc() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case SINGLE_ESC:
      jj_consume_token(SINGLE_ESC);
      break;
    case MULTI_ESC:
      jj_consume_token(MULTI_ESC);
      break;
    case CAT_ESC:
      jj_consume_token(CAT_ESC);
      break;
    default:
      jj_la1[10] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
      final RegExp esc = Escape.get(token.image);
      if(esc == null) {if (true) throw new ParseException("Unknown escape: " + token);}
      {if (true) return esc;}
    throw new Error("Missing return statement in function");
  }

  /**
   * Parses the "charClassExpr" rule.
   * Parses the "charClassSub" rule.
   * @return character class
   * @throws ParseException parsing exception
   */
  final public CharClass charClassExpr() throws ParseException {
    CharGroup group = null;
    CharClass sub = null;
    jj_consume_token(BR_OPEN);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case NEG:
      jj_consume_token(NEG);
      group = posCharGroup();
          group.negative = true;
      break;
    default:
      jj_la1[11] = jj_gen;
      if (jj_2_2(1)) {
        group = posCharGroup();
      } else {
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case TO:
      jj_consume_token(TO);
      sub = charClassExpr();
      break;
    default:
      jj_la1[12] = jj_gen;
      ;
    }
    jj_consume_token(BR_CLOSE);
      {if (true) return new CharClass(group, sub);}
    throw new Error("Missing return statement in function");
  }

  /**
   * Parses the "posCharGroup" rule.
   * @return character group
   * @throws ParseException parsing exception
   */
  final public CharGroup posCharGroup() throws ParseException {
    final RegExpList cg = new RegExpList();
    RegExp sub = null;
    label_4:
    while (true) {
      if (jj_2_3(3)) {
        sub = charRange();
                                       cg.add(sub);
      } else {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case SINGLE_ESC:
        case MULTI_ESC:
        case CAT_ESC:
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case SINGLE_ESC:
            jj_consume_token(SINGLE_ESC);
            break;
          case MULTI_ESC:
            jj_consume_token(MULTI_ESC);
            break;
          case CAT_ESC:
            jj_consume_token(CAT_ESC);
            break;
          default:
            jj_la1[13] = jj_gen;
            jj_consume_token(-1);
            throw new ParseException();
          }
        for(final RegExp re : Escape.inGroup(token.image)) cg.add(re);
          break;
        default:
          jj_la1[14] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
      }
      if (jj_2_4(1)) {
        ;
      } else {
        break label_4;
      }
    }
      {if (true) return new CharGroup(cg.finish());}
    throw new Error("Missing return statement in function");
  }

  /**
   * Parses the "charRange" rule.
   * @return expression
   * @throws ParseException parsing exception
   */
  final public RegExp charRange() throws ParseException {
    int a = -1, b = -1;
    if (getToken(2).kind == CHAR && "-".equals(getToken(2).image)) {
      a = charOrEsc();
      jj_consume_token(CHAR);
      b = charOrEsc();
        if(a > b) {if (true) throw new ParseException("Illegal range: " +
            Literal.escape(a) + " > " + Literal.escape(b));}
    } else {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CHAR:
      case DIGIT:
        a = XmlChar();
        break;
      default:
        jj_la1[15] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
      {if (true) return b == -1 ? new Literal(a) : new CharRange(a, b);}
    throw new Error("Missing return statement in function");
  }

  /**
   * Parses the "charOrEsc" rule.
   * @return character
   * @throws ParseException parsing exception
   */
  final public int charOrEsc() throws ParseException {
    int cp = -1;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case CHAR:
    case DIGIT:
      cp = XmlChar();
      break;
    case SINGLE_ESC:
      jj_consume_token(SINGLE_ESC);
                     cp = Escape.getCp(token.image);
      break;
    default:
      jj_la1[16] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
      {if (true) return cp;}
    throw new Error("Missing return statement in function");
  }

  /**
   * Parses the "XmlChar" rule.
   * @return character
   * @throws ParseException parsing exception
   */
  final public int XmlChar() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case CHAR:
      jj_consume_token(CHAR);
      break;
    case DIGIT:
      jj_consume_token(DIGIT);
      break;
    default:
      jj_la1[17] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
      {if (true) return token.image.codePointAt(0);}
    throw new Error("Missing return statement in function");
  }

  private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  private boolean jj_2_2(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_2(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(1, xla); }
  }

  private boolean jj_2_3(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_3(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(2, xla); }
  }

  private boolean jj_2_4(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_4(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(3, xla); }
  }

  private boolean jj_3R_12() {
    if (jj_3R_11()) return true;
    return false;
  }

  private boolean jj_3R_10() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_12()) {
    jj_scanpos = xsp;
    if (jj_3R_13()) return true;
    }
    return false;
  }

  private boolean jj_3_2() {
    if (jj_3R_5()) return true;
    return false;
  }

  private boolean jj_3R_11() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(12)) {
    jj_scanpos = xsp;
    if (jj_scan_token(13)) return true;
    }
    return false;
  }

  private boolean jj_3R_7() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(18)) {
    jj_scanpos = xsp;
    if (jj_scan_token(19)) {
    jj_scanpos = xsp;
    if (jj_scan_token(20)) return true;
    }
    }
    return false;
  }

  private boolean jj_3_3() {
    if (jj_3R_6()) return true;
    return false;
  }

  private boolean jj_3_4() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_3()) {
    jj_scanpos = xsp;
    if (jj_3R_7()) return true;
    }
    return false;
  }

  private boolean jj_3R_9() {
    if (jj_3R_11()) return true;
    return false;
  }

  private boolean jj_3R_5() {
    Token xsp;
    if (jj_3_4()) return true;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3_4()) { jj_scanpos = xsp; break; }
    }
    return false;
  }

  private boolean jj_3R_8() {
    if (jj_3R_10()) return true;
    if (jj_scan_token(CHAR)) return true;
    if (jj_3R_10()) return true;
    return false;
  }

  private boolean jj_3_1() {
    if (jj_scan_token(DIGIT)) return true;
    return false;
  }

  private boolean jj_3R_6() {
    Token xsp;
    xsp = jj_scanpos;
    jj_lookingAhead = true;
    jj_semLA = getToken(2).kind == CHAR && "-".equals(getToken(2).image);
    jj_lookingAhead = false;
    if (!jj_semLA || jj_3R_8()) {
    jj_scanpos = xsp;
    if (jj_3R_9()) return true;
    }
    return false;
  }

  private boolean jj_3R_13() {
    if (jj_scan_token(SINGLE_ESC)) return true;
    return false;
  }

  /** User defined Token Manager. */
  public TokenManager token_source;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  /** Whether we are looking ahead. */
  private boolean jj_lookingAhead = false;
  private boolean jj_semLA;
  private int jj_gen;
  final private int[] jj_la1 = new int[18];
  static private int[] jj_la1_0;
  static {
      jj_la1_init_0();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x2,0x3ffa00,0x3c,0x3c,0x4,0x80,0x100,0x3ffa00,0x3000,0x3f8000,0x1c0000,0x400000,0x800000,0x1c0000,0x1c0000,0x3000,0x43000,0x3000,};
   }
  final private JJCalls[] jj_2_rtns = new JJCalls[4];
  private boolean jj_rescan = false;
  private int jj_gc = 0;


  /** Constructor with user supplied Token Manager. */
  public RegExParser(TokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 18; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(TokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 18; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static private final class LookaheadSuccess extends java.lang.Error { }
  final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = jj_lookingAhead ? jj_scanpos : token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      jj_entries_loop: for (java.util.Iterator<?> it = jj_expentries.iterator(); it.hasNext();) {
        int[] oldentry = (int[])(it.next());
        if (oldentry.length == jj_expentry.length) {
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              continue jj_entries_loop;
            }
          }
          jj_expentries.add(jj_expentry);
          break jj_entries_loop;
        }
      }
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[25];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 18; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 25; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

  private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 4; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
            case 1: jj_3_2(); break;
            case 2: jj_3_3(); break;
            case 3: jj_3_4(); break;
          }
        }
        p = p.next;
      } while (p != null);
      } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}
