package org.basex.query.value.type;

import static org.basex.query.QueryError.*;
import static org.basex.query.QueryError.normalize;
import static org.basex.query.QueryText.*;
import static org.basex.util.Token.*;
import static org.basex.util.Token.normalize;

import java.math.*;
import java.util.*;
import java.util.regex.*;

import javax.xml.namespace.*;

import org.basex.query.*;
import org.basex.query.util.*;
import org.basex.query.value.*;
import org.basex.query.value.item.*;
import org.basex.util.*;

/**
 * XQuery atomic types.
 *
 * @author BaseX Team 2005-19, BSD License
 * @author Christian Gruen
 */
public enum AtomType implements Type {
  /** Item type. */
  ITEM("item", null, EMPTY, false, false, false, false, Type.ID.ITEM),

  /** Untyped type. */
  UTY("untyped", null, XS_URI, false, false, false, false, Type.ID.UTY),

  /** Any type. */
  ATY("anyType", null, XS_URI, false, false, false, false, Type.ID.ATY),

  /** Any simple type. */
  AST("anySimpleType", null, XS_URI, false, false, false, false, Type.ID.AST),

  /** Any atomic type. */
  AAT("anyAtomicType", ITEM, XS_URI, false, false, false, false, Type.ID.AAT) {
    @Override
    public Atm cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return new Atm(item.string(ii));
    }
    @Override
    public Atm cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) {
      return new Atm(value.toString());
    }
  },

  /** Untyped Atomic type. */
  ATM("untypedAtomic", AAT, XS_URI, false, true, false, true, Type.ID.ATM) {
    @Override
    public Atm cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return new Atm(item.string(ii));
    }
    @Override
    public Atm cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) {
      return new Atm(value.toString());
    }
  },

  /** String type. */
  STR("string", AAT, XS_URI, false, false, true, true, Type.ID.STR) {
    @Override
    public Str cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return Str.get(item.string(ii));
    }
    @Override
    public Str cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return Str.get(value, qc, ii);
    }
  },

  /** Normalized String type. */
  NST("normalizedString", STR, XS_URI, false, false, true, true, Type.ID.NST) {
    @Override
    public Str cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {

      final byte[] str = item.string(ii);
      final int sl = str.length;
      for(int s = 0; s < sl; s++) {
        final byte b = str[s];
        if(b == '\t' || b == '\r' || b == '\n') str[s] = ' ';
      }
      return new Str(str, this);
    }
    @Override
    public Str cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return cast(Str.get(value, qc, ii), qc, sc, ii);
    }
  },

  /** Token type. */
  TOK("token", NST, XS_URI, false, false, true, true, Type.ID.TOK) {
    @Override
    public Str cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return new Str(normalize(item.string(ii)), this);
    }
    @Override
    public Str cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return cast(Str.get(value, qc, ii), qc, sc, ii);
    }
  },

  /** Language type. */
  LAN("language", TOK, XS_URI, false, false, true, true, Type.ID.LAN) {
    @Override
    public Str cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      final byte[] v = normalize(item.string(ii));
      if(!LANGPATTERN.matcher(Token.string(v)).matches()) throw castError(item, ii);
      return new Str(v, this);
    }
    @Override
    public Str cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return cast(Str.get(value, qc, ii), qc, sc, ii);
    }
  },

  /** NMTOKEN type. */
  NMT("NMTOKEN", TOK, XS_URI, false, false, true, true, Type.ID.NMT) {
    @Override
    public Str cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      final byte[] v = normalize(item.string(ii));
      if(!XMLToken.isNMToken(v)) throw castError(item, ii);
      return new Str(v, this);
    }
    @Override
    public Str cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return cast(Str.get(value, qc, ii), qc, sc, ii);
    }
  },

  /** Name type. */
  NAM("Name", TOK, XS_URI, false, false, true, true, Type.ID.NAM) {
    @Override
    public Str cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      final byte[] v = normalize(item.string(ii));
      if(!XMLToken.isName(v)) throw castError(item, ii);
      return new Str(v, this);
    }
    @Override
    public Str cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return cast(Str.get(value, qc, ii), qc, sc, ii);
    }
  },

  /** NCName type. */
  NCN("NCName", NAM, XS_URI, false, false, true, true, Type.ID.NCN) {
    @Override
    public Str cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return new Str(checkName(item, ii), this);
    }
    @Override
    public Str cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return cast(Str.get(value, qc, ii), qc, sc, ii);
    }
  },

  /** ID type. */
  ID("ID", NCN, XS_URI, false, false, true, true, Type.ID.ID) {
    @Override
    public Str cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return new Str(checkName(item, ii), this);
    }
    @Override
    public Str cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return cast(Str.get(value, qc, ii), qc, sc, ii);
    }
  },

  /** IDREF type. */
  IDR("IDREF", NCN, XS_URI, false, false, true, true, Type.ID.IDR) {
    @Override
    public Str cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return new Str(checkName(item, ii), this);
    }
    @Override
    public Str cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return cast(Str.get(value, qc, ii), qc, sc, ii);
    }
  },

  /** Entity type. */
  ENT("ENTITY", NCN, XS_URI, false, false, true, true, Type.ID.ENT) {
    @Override
    public Str cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return new Str(checkName(item, ii), this);
    }
    @Override
    public Str cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return cast(Str.get(value, qc, ii), qc, sc, ii);
    }
  },

  /** Numeric type. */
  NUM("numeric", AAT, XS_URI, true, false, false, true, Type.ID.NUM) {
    @Override
    public Item cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      // return double
      return item.type.isNumber() ? item : Dbl.get(checkNum(item, ii).dbl(ii));
    }
    @Override
    public Item cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      // return double
      return cast(Str.get(value, qc, ii), qc, sc, ii);
    }
  },

  /** Float type. */
  FLT("float", NUM, XS_URI, true, false, false, true, Type.ID.FLT) {
    @Override
    public Flt cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return Flt.get(checkNum(item, ii).flt(ii));
    }
    @Override
    public Flt cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return cast(Str.get(value, qc, ii), qc, sc, ii);
    }
  },

  /** Double type. */
  DBL("double", NUM, XS_URI, true, false, false, true, Type.ID.DBL) {
    @Override
    public Dbl cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return Dbl.get(checkNum(item, ii).dbl(ii));
    }
    @Override
    public Dbl cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return cast(Str.get(value, qc, ii), qc, sc, ii);
    }
  },

  /** Decimal type. */
  DEC("decimal", NUM, XS_URI, true, false, false, true, Type.ID.DEC) {
    @Override
    public Dec cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return Dec.get(checkNum(item, ii).dec(ii));
    }
    @Override
    public Dec cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) {
      return Dec.get(new BigDecimal(value.toString()));
    }
  },

  /** Precision decimal type. */
  PDC("precisionDecimal", null, XS_URI, true, false, false, true, Type.ID.PDC),

  /** Integer type. */
  ITR("integer", DEC, XS_URI, true, false, false, true, Type.ID.ITR) {
    @Override
    public Int cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return cast((Object) item, qc, sc, ii);
    }
    @Override
    public Int cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return Int.get(checkLong(value, 0, 0, ii));
    }
  },

  /** Non-positive integer type. */
  NPI("nonPositiveInteger", ITR, XS_URI, true, false, false, true, Type.ID.NPI) {
    @Override
    public Int cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return cast((Object) item, qc, sc, ii);
    }
    @Override
    public Int cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return new Int(checkLong(value, Long.MIN_VALUE, 0, ii), this);
    }
  },

  /** Negative integer type. */
  NIN("negativeInteger", NPI, XS_URI, true, false, false, true, Type.ID.NIN) {
    @Override
    public Int cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return cast((Object) item, qc, sc, ii);
    }
    @Override
    public Int cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return new Int(checkLong(value, Long.MIN_VALUE, -1, ii), this);
    }
  },

  /** Long type. */
  LNG("long", ITR, XS_URI, true, false, false, true, Type.ID.LNG) {
    @Override
    public Int cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return cast((Object) item, qc, sc, ii);
    }
    @Override
    public Int cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return new Int(checkLong(value, 0, 0, ii), this);
    }
  },

  /** Int type. */
  INT("int", LNG, XS_URI, true, false, false, true, Type.ID.INT) {
    @Override
    public Int cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return cast((Object) item, qc, sc, ii);
    }
    @Override
    public Int cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return new Int(checkLong(value, -0x80000000, 0x7FFFFFFF, ii), this);
    }
  },

  /** Short type. */
  SHR("short", INT, XS_URI, true, false, false, true, Type.ID.SHR) {
    @Override
    public Int cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return cast((Object) item, qc, sc, ii);
    }
    @Override
    public Int cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return new Int(checkLong(value, -0x8000, 0x7FFF, ii), this);
    }
  },

  /** Byte type. */
  BYT("byte", SHR, XS_URI, true, false, false, true, Type.ID.BYT) {
    @Override
    public Int cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return cast((Object) item, qc, sc, ii);
    }
    @Override
    public Int cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return new Int(checkLong(value, -0x80, 0x7F, ii), this);
    }
  },

  /** Non-negative integer type. */
  NNI("nonNegativeInteger", ITR, XS_URI, true, false, false, true, Type.ID.NNI) {
    @Override
    public Int cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return cast((Object) item, qc, sc, ii);
    }
    @Override
    public Int cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return new Int(checkLong(value, 0, Long.MAX_VALUE, ii), this);
    }
  },

  /** Unsigned long type. */
  ULN("unsignedLong", NNI, XS_URI, true, false, false, true, Type.ID.ULN) {
    @Override
    public Uln cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return cast((Object) item, qc, sc, ii);
    }
    @Override
    public Uln cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {

      final Item item = checkNum(value, ii);
      final BigDecimal v = item.dec(ii), i = v.setScale(0, RoundingMode.DOWN);
      // equals() used to also test fractional digits
      if(v.signum() < 0 || v.compareTo(Uln.MAXULN) > 0 ||
        item.type.isStringOrUntyped() && !v.equals(i)) throw castError(item, ii);
      return Uln.get(i.toBigInteger());
    }
  },

  /** Short type. */
  UIN("unsignedInt", ULN, XS_URI, true, false, false, true, Type.ID.UIN) {
    @Override
    public Int cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return cast((Object) item, qc, sc, ii);
    }
    @Override
    public Int cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return new Int(checkLong(value, 0, 0xFFFFFFFFL, ii), this);
    }
  },

  /** Unsigned Short type. */
  USH("unsignedShort", UIN, XS_URI, true, false, false, true, Type.ID.USH) {
    @Override
    public Int cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return cast((Object) item, qc, sc, ii);
    }
    @Override
    public Int cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return new Int(checkLong(value, 0, 0xFFFF, ii), this);
    }
  },

  /** Unsigned byte type. */
  UBY("unsignedByte", USH, XS_URI, true, false, false, true, Type.ID.UBY) {
    @Override
    public Int cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return cast((Object) item, qc, sc, ii);
    }
    @Override
    public Int cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return new Int(checkLong(value, 0, 0xFF, ii), this);
    }
  },

  /** Positive integer type. */
  PIN("positiveInteger", NNI, XS_URI, true, false, false, true, Type.ID.PIN) {
    @Override
    public Int cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return cast((Object) item, qc, sc, ii);
    }
    @Override
    public Int cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return new Int(checkLong(value, 1, Long.MAX_VALUE, ii), this);
    }
  },

  /** Duration type. */
  DUR("duration", AAT, XS_URI, false, false, false, false, Type.ID.DUR) {
    @Override
    public Item cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      if(item instanceof Dur) return new Dur((Dur) item);
      if(str(item)) return new Dur(item.string(ii), ii);
      throw typeError(item, this, ii);
    }
    @Override
    public Item cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return cast(Str.get(value, qc, ii), qc, sc, ii);
    }
  },

  /** Year month duration type. */
  YMD("yearMonthDuration", DUR, XS_URI, false, false, false, true, Type.ID.YMD) {
    @Override
    public Item cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      if(item instanceof Dur) return new YMDur((Dur) item);
      if(str(item)) return new YMDur(item.string(ii), ii);
      throw typeError(item, this, ii);
    }
    @Override
    public Item cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return cast(Str.get(value, qc, ii), qc, sc, ii);
    }
  },

  /** Day time duration type. */
  DTD("dayTimeDuration", DUR, XS_URI, false, false, false, true, Type.ID.DTD) {
    @Override
    public Item cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      if(item instanceof Dur) return new DTDur((Dur) item);
      if(str(item)) return new DTDur(item.string(ii), ii);
      throw typeError(item, this, ii);
    }
    @Override
    public Item cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return cast(Str.get(value, qc, ii), qc, sc, ii);
    }
  },

  /** DateTime type. */
  DTM("dateTime", AAT, XS_URI, false, false, false, true, Type.ID.DTM) {
    @Override
    public Item cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      if(item.type == DAT) return new Dtm((ADate) item);
      if(str(item)) return new Dtm(item.string(ii), ii);
      throw typeError(item, this, ii);
    }
    @Override
    public Item cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return cast(Str.get(value, qc, ii), qc, sc, ii);
    }
  },

  /** DateTimeStamp type. */
  DTS("dateTimeStamp", null, XS_URI, false, false, false, true, Type.ID.DTS),

  /** Date type. */
  DAT("date", AAT, XS_URI, false, false, false, true, Type.ID.DAT) {
    @Override
    public Item cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      if(item.type == DTM) return new Dat((ADate) item);
      if(str(item)) return new Dat(item.string(ii), ii);
      throw typeError(item, this, ii);
    }
    @Override
    public Item cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return cast(Str.get(value, qc, ii), qc, sc, ii);
    }
  },

  /** Time type. */
  TIM("time", AAT, XS_URI, false, false, false, true, Type.ID.TIM) {
    @Override
    public Item cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      if(item.type == DTM) return new Tim((ADate) item);
      if(str(item)) return new Tim(item.string(ii), ii);
      throw typeError(item, this, ii);
    }
    @Override
    public Item cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return cast(Str.get(value, qc, ii), qc, sc, ii);
    }
  },

  /** Year month type. */
  YMO("gYearMonth", AAT, XS_URI, false, false, false, false, Type.ID.YMO) {
    @Override
    public Item cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      if(item.type == DTM || item.type == DAT) return new GDt((ADate) item, this);
      if(str(item)) return new GDt(item.string(ii), this, ii);
      throw typeError(item, this, ii);
    }
    @Override
    public Item cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return cast(Str.get(value, qc, ii), qc, sc, ii);
    }
  },

  /** Year type. */
  YEA("gYear", AAT, XS_URI, false, false, false, false, Type.ID.YEA) {
    @Override
    public Item cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      if(item.type == DTM || item.type == DAT) return new GDt((ADate) item, this);
      if(str(item)) return new GDt(item.string(ii), this, ii);
      throw typeError(item, this, ii);
    }
    @Override
    public Item cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return cast(Str.get(value, qc, ii), qc, sc, ii);
    }
  },

  /** Month day type. */
  MDA("gMonthDay", AAT, XS_URI, false, false, false, false, Type.ID.MDA) {
    @Override
    public Item cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      if(item.type == DTM || item.type == DAT) return new GDt((ADate) item, this);
      if(str(item)) return new GDt(item.string(ii), this, ii);
      throw typeError(item, this, ii);
    }
    @Override
    public Item cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return cast(Str.get(value, qc, ii), qc, sc, ii);
    }
  },

  /** Day type. */
  DAY("gDay", AAT, XS_URI, false, false, false, false, Type.ID.DAY) {
    @Override
    public Item cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      if(item.type == DTM || item.type == DAT) return new GDt((ADate) item, this);
      if(str(item)) return new GDt(item.string(ii), this, ii);
      throw typeError(item, this, ii);
    }
    @Override
    public Item cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return cast(Str.get(value, qc, ii), qc, sc, ii);
    }
  },

  /** Month type. */
  MON("gMonth", AAT, XS_URI, false, false, false, false, Type.ID.MON) {
    @Override
    public Item cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      if(item.type == DTM || item.type == DAT) return new GDt((ADate) item, this);
      if(str(item)) return new GDt(item.string(ii), this, ii);
      throw typeError(item, this, ii);
    }
    @Override
    public Item cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return cast(Str.get(value, qc, ii), qc, sc, ii);
    }
  },

  /** Boolean type. */
  BLN("boolean", AAT, XS_URI, false, false, false, true, Type.ID.BLN) {
    @Override
    public Item cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      if(item instanceof ANum) return Bln.get(item.bool(ii));
      if(str(item)) return Bln.get(Bln.parse(item, ii));
      throw typeError(item, this, ii);
    }
    @Override
    public Bln cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) {
      return Bln.get(value instanceof Boolean ? (Boolean) value :
        Boolean.parseBoolean(value.toString()));
    }
  },

  /** Implementation specific: binary type. */
  BIN("binary", AAT, BASEX_URI, false, false, false, true, Type.ID.BIN),

  /** Base64 binary type. */
  B64("base64Binary", BIN, XS_URI, false, false, false, true, Type.ID.B64) {
    @Override
    public Item cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      if(item instanceof Bin) return org.basex.query.value.item.B64.get((Bin) item, ii);
      if(str(item)) return org.basex.query.value.item.B64.get(item.string(ii), ii);
      throw typeError(item, this, ii);
    }
    @Override
    public Item cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return value instanceof byte[] ? org.basex.query.value.item.B64.get((byte[]) value) :
        org.basex.query.value.item.B64.get(token(value.toString()), ii);
    }
  },

  /** Hex binary type. */
  HEX("hexBinary", BIN, XS_URI, false, false, false, true, Type.ID.HEX) {
    @Override
    public Item cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      if(item instanceof Bin) return new Hex((Bin) item, ii);
      if(str(item)) return new Hex(item.string(ii), ii);
      throw typeError(item, this, ii);
    }
    @Override
    public Item cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {
      return new Hex(value instanceof byte[] ? (byte[]) value : token(value.toString()), ii);
    }
  },

  /** Any URI type. */
  URI("anyURI", AAT, XS_URI, false, false, true, true, Type.ID.URI) {
    @Override
    public Uri cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {

      if(!item.type.isStringOrUntyped()) throw typeError(item, this, ii);
      final Uri u = Uri.uri(item.string(ii));
      if(!u.isValid()) throw castError(item, ii);
      return u;
    }
    @Override
    public Item cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) {
      return Uri.uri(value.toString());
    }
  },

  /** QName Type. */
  QNM("QName", AAT, XS_URI, false, false, false, false, Type.ID.QNM) {
    @Override
    public QNm cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) throws QueryException {

      if(item.type != STR && !item.type.isUntyped()) throw typeError(item, this, ii);
      final byte[] nm = trim(item.string(ii));
      if(!XMLToken.isQName(nm)) throw castError(item, ii);
      final QNm qn = new QNm(nm, sc);
      if(!qn.hasURI() && qn.hasPrefix()) throw NSDECL_X.get(ii, qn.prefix());
      return qn;
    }
    @Override
    public QNm cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) {
      return value instanceof QName ? new QNm((QName) value) : new QNm(value.toString());
    }
  },

  /** NOTATION Type. */
  NOT("NOTATION", AAT, XS_URI, false, false, false, false, Type.ID.NOT),

  /** Java type. */
  JAVA("java", ITEM, BASEX_URI, true, true, true, false, Type.ID.JAVA) {
    @Override
    public Item cast(final Item item, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) {
      return new Jav(item, qc);
    }
    @Override
    public Item cast(final Object value, final QueryContext qc, final StaticContext sc,
        final InputInfo ii) {
      return new Jav(value, qc);
    }
  };

  /** Language pattern. */
  private static final Pattern LANGPATTERN = Pattern.compile("[A-Za-z]{1,8}(-[A-Za-z0-9]{1,8})*");

  /** Cached enums (faster). */
  public static final AtomType[] VALUES = values();
  /** Name. */
  public final QNm name;
  /** Parent type. */
  public final AtomType parent;
  /** Type id . */
  private final Type.ID id;

  /** Number flag. */
  private final boolean numeric;
  /** Untyped flag. */
  private final boolean untyped;
  /** String flag. */
  private final boolean string;
  /** Sortable flag. */
  private final boolean sortable;

  /** Sequence types (lazy instantiation). */
  private EnumMap<Occ, SeqType> seqTypes;

  /**
   * Constructor.
   * @param name string representation
   * @param parent parent type
   * @param uri uri
   * @param numeric numeric flag
   * @param untyped untyped flag
   * @param string string flag
   * @param sortable sortable flag
   * @param id type id
   */
  AtomType(final String name, final AtomType parent, final byte[] uri, final boolean numeric,
      final boolean untyped, final boolean string, final boolean sortable, final Type.ID id) {
    this.name = new QNm(name, uri);
    this.parent = parent;
    this.numeric = numeric;
    this.untyped = untyped;
    this.string = string;
    this.sortable = sortable;
    this.id = id;
  }

  @Override
  public Item cast(final Item item, final QueryContext qc, final StaticContext sc,
      final InputInfo ii) throws QueryException {
    if(item.type == this) return item;
    throw typeError(item, this, ii);
  }

  @Override
  public Item cast(final Object value, final QueryContext qc, final StaticContext sc,
      final InputInfo ii) throws QueryException {
    throw Util.notExpected(value);
  }

  @Override
  public final Item castString(final String value, final QueryContext qc, final StaticContext sc,
      final InputInfo ii) throws QueryException {
    return cast(value, qc, sc, ii);
  }

  @Override
  public final SeqType seqType(final Occ occ) {
    if(seqTypes == null) seqTypes = new EnumMap<>(Occ.class);
    return seqTypes.computeIfAbsent(occ, o -> new SeqType(this, o));
  }

  @Override
  public final boolean eq(final Type type) {
    return this == type;
  }

  @Override
  public final boolean instanceOf(final Type type) {
    return this == type || type == ITEM ||
        type instanceof AtomType && parent != null && parent.instanceOf(type);
  }

  @Override
  public final Type union(final Type type) {
    if(instanceOf(type)) return type;
    if(type.instanceOf(this)) return this;

    if(type instanceof AtomType) {
      final List<AtomType> arr = new ArrayList<>();
      for(AtomType at = (AtomType) type; (at = at.parent) != null;) arr.add(at);
      for(AtomType p = this; (p = p.parent) != null;)
        if(arr.contains(p)) return p;
    }
    return ITEM;
  }

  @Override
  public final Type intersect(final Type type) {
    return instanceOf(type) ? this : type.instanceOf(this) ? type : null;
  }

  @Override
  public final boolean isNumber() {
    return numeric;
  }

  @Override
  public final boolean isUntyped() {
    return untyped;
  }

  @Override
  public final boolean isNumberOrUntyped() {
    return numeric || untyped;
  }

  @Override
  public final boolean isStringOrUntyped() {
    return string || untyped;
  }

  @Override
  public final boolean isSortable() {
    return sortable;
  }

  @Override
  public final byte[] string() {
    return name.string();
  }

  @Override
  public final AtomType atomic() {
    return instanceOf(AAT) ? this : null;
  }

  @Override
  public final Type.ID id() {
    return id;
  }

  @Override
  public final String toString() {
    final boolean xs = Token.eq(XS_URI, name.uri());
    final TokenBuilder tb = new TokenBuilder();
    if(xs) tb.add(NSGlobal.prefix(name.uri())).add(':');
    tb.add(name.string());
    if(!xs) tb.add("()");
    return tb.toString();
  }

  /**
   * Throws an exception if the specified item cannot be converted to a number.
   * @param item item
   * @param ii input info
   * @return item argument
   * @throws QueryException query exception
   */
  final Item checkNum(final Item item, final InputInfo ii) throws QueryException {
    final Type type = item.type;
    if(item instanceof ANum || type.isStringOrUntyped() && type != URI || type == BLN) return item;
    throw typeError(item, this, ii);
  }

  /**
   * Checks the validity of the specified object and returns its long value.
   * @param value value to be checked
   * @param min minimum value
   * @param max maximum value (no limit if identical to min)
   * @param ii input info
   * @return integer value
   * @throws QueryException query exception
   */
  final long checkLong(final Object value, final long min, final long max, final InputInfo ii)
      throws QueryException {

    final Item item = checkNum(value, ii);
    final Type type = item.type;
    if(type == DBL || type == FLT) {
      final double d = item.dbl(ii);
      if(Double.isNaN(d) || Double.isInfinite(d)) throw valueError(this, item.string(ii), ii);
      if(min != max && (d < min || d > max)) throw castError(item, ii);
      if(d < Long.MIN_VALUE || d > Long.MAX_VALUE) throw INTRANGE_X.get(ii, d);
      return (long) d;
    }

    final long l = item.itr(ii);
    if(min != max && (l < min || l > max)) throw castError(item, ii);
    return l;
  }

  /**
   * Checks the validity of the specified object and returns it as item.
   * @param value value to be checked
   * @param ii input info
   * @return integer value
   * @throws QueryException query exception
   */
  final Item checkNum(final Object value, final InputInfo ii) throws QueryException {
    final Item item;
    if(value instanceof Value) {
      final Value v = (Value) value;
      if(v.size() != 1) throw typeError(v, this, ii);
      item = (Item) v;
    } else {
      item = Str.get(value.toString());
    }
    return checkNum(item, ii);
  }

  /**
   * Checks if the specified item is a string.
   * @param item item
   * @return item argument
   */
  static boolean str(final Item item) {
    final Type type = item.type;
    return type.isStringOrUntyped() && type != URI;
  }

  /**
   * Checks the validity of the specified name.
   * @param item value to be checked
   * @param ii input info
   * @return name
   * @throws QueryException query exception
   */
  final byte[] checkName(final Item item, final InputInfo ii) throws QueryException {
    final byte[] v = normalize(item.string(ii));
    if(!XMLToken.isNCName(v)) throw castError(item, ii);
    return v;
  }

  /**
   * Returns a cast exception.
   * @param item item to be converted
   * @param ii input info
   * @return query exception
   */
  public final QueryException castError(final Item item, final InputInfo ii)  {
    return FUNCCAST_X_X_X.get(ii, item.type, this, item);
  }

  /**
   * Returns a cast exception.
   * @param value value to be converted
   * @param ii input info
   * @return query exception
   */
  public final QueryException castError(final byte[] value, final InputInfo ii)  {
    return FUNCCAST_X_X.get(ii, this, normalize(value, ii));
  }

  @Override
  public final boolean nsSensitive() {
    return instanceOf(QNM) || instanceOf(NOT);
  }

  /**
   * Finds and returns the specified type.
   * @param type type
   * @param all accept all types (including those without parent type)
   * @return type or {@code null}
   */
  public static AtomType find(final QNm type, final boolean all) {
    if(!Token.eq(type.uri(), BASEX_URI)) {
      for(final AtomType tp : VALUES) {
        if(tp.name.eq(type) && (all || tp.parent != null)) return tp;
      }
    }
    return null;
  }

  /**
   * Gets the type instance for the given ID.
   * @param id type ID
   * @return corresponding type if found, {@code null} otherwise
   */
  static Type getType(final Type.ID id) {
    for(final AtomType type : VALUES) {
      if(type.id == id) return type;
    }
    return null;
  }
}
