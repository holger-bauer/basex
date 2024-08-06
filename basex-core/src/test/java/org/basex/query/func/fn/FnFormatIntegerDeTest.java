package org.basex.query.func.fn;

import org.basex.query.*;

/**
 * XQuery functions tests.
 *
 * @author BaseX Team 2005-24, BSD License
 * @author Christian Gruen
 */
public final class FnFormatIntegerDeTest extends QueryTest {
  static {
    queries = new Object[][] {
      { "de0", strings("null"), "format-integer(0, 'w', 'de')" },
      { "de1", strings("eins"), "format-integer(1, 'w', 'de')" },
      { "de2", strings("zwei"), "format-integer(2, 'w', 'de')" },
      { "de3", strings("drei"), "format-integer(3, 'w', 'de')" },
      { "de4", strings("vier"), "format-integer(4, 'w', 'de')" },
      { "de5", strings("f\u00fcnf"), "format-integer(5, 'w', 'de')" },
      { "de6", strings("sechs"), "format-integer(6, 'w', 'de')" },
      { "de7", strings("sieben"), "format-integer(7, 'w', 'de')" },
      { "de8", strings("acht"), "format-integer(8, 'w', 'de')" },
      { "de9", strings("neun"), "format-integer(9, 'w', 'de')" },
      { "de10", strings("zehn"), "format-integer(10, 'w', 'de')" },
      { "de11", strings("elf"), "format-integer(11, 'w', 'de')" },
      { "de12", strings("zw\u00f6lf"), "format-integer(12, 'w', 'de')" },
      { "de13", strings("dreizehn"), "format-integer(13, 'w', 'de')" },
      { "de14", strings("vierzehn"), "format-integer(14, 'w', 'de')" },
      { "de15", strings("f\u00fcnfzehn"), "format-integer(15, 'w', 'de')" },
      { "de16", strings("sechzehn"), "format-integer(16, 'w', 'de')" },
      { "de17", strings("siebzehn"), "format-integer(17, 'w', 'de')" },
      { "de18", strings("achtzehn"), "format-integer(18, 'w', 'de')" },
      { "de19", strings("neunzehn"), "format-integer(19, 'w', 'de')" },
      { "de20", strings("zwanzig"), "format-integer(20, 'w', 'de')" },
      { "de21", strings("einundzwanzig"), "format-integer(21, 'w', 'de')" },
      { "de22", strings("zweiundzwanzig"), "format-integer(22, 'w', 'de')" },
      { "de23", strings("dreiundzwanzig"), "format-integer(23, 'w', 'de')" },
      { "de24", strings("vierundzwanzig"), "format-integer(24, 'w', 'de')" },
      { "de25", strings("f\u00fcnfundzwanzig"), "format-integer(25, 'w', 'de')" },
      { "de26", strings("sechsundzwanzig"), "format-integer(26, 'w', 'de')" },
      { "de27", strings("siebenundzwanzig"), "format-integer(27, 'w', 'de')" },
      { "de28", strings("achtundzwanzig"), "format-integer(28, 'w', 'de')" },
      { "de29", strings("neunundzwanzig"), "format-integer(29, 'w', 'de')" },
      { "de30", strings("drei\u00dfig"), "format-integer(30, 'w', 'de')" },
      { "de31", strings("einunddrei\u00dfig"), "format-integer(31, 'w', 'de')" },
      { "de32", strings("zweiunddrei\u00dfig"), "format-integer(32, 'w', 'de')" },
      { "de33", strings("dreiunddrei\u00dfig"), "format-integer(33, 'w', 'de')" },
      { "de34", strings("vierunddrei\u00dfig"), "format-integer(34, 'w', 'de')" },
      { "de35", strings("f\u00fcnfunddrei\u00dfig"), "format-integer(35, 'w', 'de')" },
      { "de36", strings("sechsunddrei\u00dfig"), "format-integer(36, 'w', 'de')" },
      { "de37", strings("siebenunddrei\u00dfig"), "format-integer(37, 'w', 'de')" },
      { "de38", strings("achtunddrei\u00dfig"), "format-integer(38, 'w', 'de')" },
      { "de39", strings("neununddrei\u00dfig"), "format-integer(39, 'w', 'de')" },
      { "de40", strings("vierzig"), "format-integer(40, 'w', 'de')" },
      { "de41", strings("einundvierzig"), "format-integer(41, 'w', 'de')" },
      { "de42", strings("zweiundvierzig"), "format-integer(42, 'w', 'de')" },
      { "de43", strings("dreiundvierzig"), "format-integer(43, 'w', 'de')" },
      { "de44", strings("vierundvierzig"), "format-integer(44, 'w', 'de')" },
      { "de45", strings("f\u00fcnfundvierzig"), "format-integer(45, 'w', 'de')" },
      { "de46", strings("sechsundvierzig"), "format-integer(46, 'w', 'de')" },
      { "de47", strings("siebenundvierzig"), "format-integer(47, 'w', 'de')" },
      { "de48", strings("achtundvierzig"), "format-integer(48, 'w', 'de')" },
      { "de49", strings("neunundvierzig"), "format-integer(49, 'w', 'de')" },
      { "de50", strings("f\u00fcnfzig"), "format-integer(50, 'w', 'de')" },
      { "de51", strings("einundf\u00fcnfzig"), "format-integer(51, 'w', 'de')" },
      { "de52", strings("zweiundf\u00fcnfzig"), "format-integer(52, 'w', 'de')" },
      { "de53", strings("dreiundf\u00fcnfzig"), "format-integer(53, 'w', 'de')" },
      { "de54", strings("vierundf\u00fcnfzig"), "format-integer(54, 'w', 'de')" },
      { "de55", strings("f\u00fcnfundf\u00fcnfzig"), "format-integer(55, 'w', 'de')" },
      { "de56", strings("sechsundf\u00fcnfzig"), "format-integer(56, 'w', 'de')" },
      { "de57", strings("siebenundf\u00fcnfzig"), "format-integer(57, 'w', 'de')" },
      { "de58", strings("achtundf\u00fcnfzig"), "format-integer(58, 'w', 'de')" },
      { "de59", strings("neunundf\u00fcnfzig"), "format-integer(59, 'w', 'de')" },
      { "de60", strings("sechzig"), "format-integer(60, 'w', 'de')" },
      { "de61", strings("einundsechzig"), "format-integer(61, 'w', 'de')" },
      { "de62", strings("zweiundsechzig"), "format-integer(62, 'w', 'de')" },
      { "de63", strings("dreiundsechzig"), "format-integer(63, 'w', 'de')" },
      { "de64", strings("vierundsechzig"), "format-integer(64, 'w', 'de')" },
      { "de65", strings("f\u00fcnfundsechzig"), "format-integer(65, 'w', 'de')" },
      { "de66", strings("sechsundsechzig"), "format-integer(66, 'w', 'de')" },
      { "de67", strings("siebenundsechzig"), "format-integer(67, 'w', 'de')" },
      { "de68", strings("achtundsechzig"), "format-integer(68, 'w', 'de')" },
      { "de69", strings("neunundsechzig"), "format-integer(69, 'w', 'de')" },
      { "de70", strings("siebzig"), "format-integer(70, 'w', 'de')" },
      { "de71", strings("einundsiebzig"), "format-integer(71, 'w', 'de')" },
      { "de72", strings("zweiundsiebzig"), "format-integer(72, 'w', 'de')" },
      { "de73", strings("dreiundsiebzig"), "format-integer(73, 'w', 'de')" },
      { "de74", strings("vierundsiebzig"), "format-integer(74, 'w', 'de')" },
      { "de75", strings("f\u00fcnfundsiebzig"), "format-integer(75, 'w', 'de')" },
      { "de76", strings("sechsundsiebzig"), "format-integer(76, 'w', 'de')" },
      { "de77", strings("siebenundsiebzig"), "format-integer(77, 'w', 'de')" },
      { "de78", strings("achtundsiebzig"), "format-integer(78, 'w', 'de')" },
      { "de79", strings("neunundsiebzig"), "format-integer(79, 'w', 'de')" },
      { "de80", strings("achtzig"), "format-integer(80, 'w', 'de')" },
      { "de81", strings("einundachtzig"), "format-integer(81, 'w', 'de')" },
      { "de82", strings("zweiundachtzig"), "format-integer(82, 'w', 'de')" },
      { "de83", strings("dreiundachtzig"), "format-integer(83, 'w', 'de')" },
      { "de84", strings("vierundachtzig"), "format-integer(84, 'w', 'de')" },
      { "de85", strings("f\u00fcnfundachtzig"), "format-integer(85, 'w', 'de')" },
      { "de86", strings("sechsundachtzig"), "format-integer(86, 'w', 'de')" },
      { "de87", strings("siebenundachtzig"), "format-integer(87, 'w', 'de')" },
      { "de88", strings("achtundachtzig"), "format-integer(88, 'w', 'de')" },
      { "de89", strings("neunundachtzig"), "format-integer(89, 'w', 'de')" },
      { "de90", strings("neunzig"), "format-integer(90, 'w', 'de')" },
      { "de91", strings("einundneunzig"), "format-integer(91, 'w', 'de')" },
      { "de92", strings("zweiundneunzig"), "format-integer(92, 'w', 'de')" },
      { "de93", strings("dreiundneunzig"), "format-integer(93, 'w', 'de')" },
      { "de94", strings("vierundneunzig"), "format-integer(94, 'w', 'de')" },
      { "de95", strings("f\u00fcnfundneunzig"), "format-integer(95, 'w', 'de')" },
      { "de96", strings("sechsundneunzig"), "format-integer(96, 'w', 'de')" },
      { "de97", strings("siebenundneunzig"), "format-integer(97, 'w', 'de')" },
      { "de98", strings("achtundneunzig"), "format-integer(98, 'w', 'de')" },
      { "de99", strings("neunundneunzig"), "format-integer(99, 'w', 'de')" },
      { "de100", strings("einhundert"), "format-integer(100, 'w', 'de')" },
      { "de101", strings("einhunderteins"), "format-integer(101, 'w', 'de')" },
      { "de109", strings("einhundertneun"), "format-integer(109, 'w', 'de')" },
      { "de110", strings("einhundertzehn"), "format-integer(110, 'w', 'de')" },
      { "de111", strings("einhundertelf"), "format-integer(111, 'w', 'de')" },
      { "de190", strings("einhundertneunzig"), "format-integer(190, 'w', 'de')" },
      { "de199", strings("einhundertneunundneunzig"), "format-integer(199, 'w', 'de')" },
      { "de200", strings("zweihundert"), "format-integer(200, 'w', 'de')" },
      { "de201", strings("zweihunderteins"), "format-integer(201, 'w', 'de')" },
      { "de999", strings("neunhundertneunundneunzig"), "format-integer(999, 'w', 'de')" },
      { "de1000", strings("eintausend"), "format-integer(1000, 'w', 'de')" },
      { "de1001", strings("eintausendeins"), "format-integer(1001, 'w', 'de')" },
      { "de1009", strings("eintausendneun"), "format-integer(1009, 'w', 'de')" },
      { "de1010", strings("eintausendzehn"), "format-integer(1010, 'w', 'de')" },
      { "de1011", strings("eintausendelf"), "format-integer(1011, 'w', 'de')" },
      { "de1111", strings("eintausendeinhundertelf"), "format-integer(1111, 'w', 'de')" },
      { "de9999", strings("neuntausendneunhundertneunundneunzig"),
        "format-integer(9999, 'w', 'de')" },
      { "de999999", strings("neunhundertneunundneunzigtausendneunhundertneunundneunzig"),
          "format-integer(999999, 'w', 'de')" },
      { "de999999999", strings("neunhundertneunundneunzig millionen " +
          "neunhundertneunundneunzigtausendneunhundertneunundneunzig"),
          "format-integer(999999999, 'w', 'de')" },

      { "de0.", strings("nullte"), "format-integer(0, 'w;o', 'de')" },
      { "de1.", strings("erste"), "format-integer(1, 'w;o', 'de')" },
      { "de2.", strings("zweite"), "format-integer(2, 'w;o', 'de')" },
      { "de3.", strings("dritte"), "format-integer(3, 'w;o', 'de')" },
      { "de4.", strings("vierte"), "format-integer(4, 'w;o', 'de')" },
      { "de5.", strings("f\u00fcnfte"), "format-integer(5, 'w;o', 'de')" },
      { "de6.", strings("sechste"), "format-integer(6, 'w;o', 'de')" },
      { "de7.", strings("siebte"), "format-integer(7, 'w;o', 'de')" },
      { "de8.", strings("achte"), "format-integer(8, 'w;o', 'de')" },
      { "de9.", strings("neunte"), "format-integer(9, 'w;o', 'de')" },
      { "de10.", strings("zehnte"), "format-integer(10, 'w;o', 'de')" },
      { "de11.", strings("elfte"), "format-integer(11, 'w;o', 'de')" },
      { "de12.", strings("zw\u00f6lfte"), "format-integer(12, 'w;o', 'de')" },
      { "de13.", strings("dreizehnte"), "format-integer(13, 'w;o', 'de')" },
      { "de14.", strings("vierzehnte"), "format-integer(14, 'w;o', 'de')" },
      { "de15.", strings("f\u00fcnfzehnte"), "format-integer(15, 'w;o', 'de')" },
      { "de16.", strings("sechzehnte"), "format-integer(16, 'w;o', 'de')" },
      { "de17.", strings("siebzehnte"), "format-integer(17, 'w;o', 'de')" },
      { "de18.", strings("achtzehnte"), "format-integer(18, 'w;o', 'de')" },
      { "de19.", strings("neunzehnte"), "format-integer(19, 'w;o', 'de')" },
      { "de20.", strings("zwanzigste"), "format-integer(20, 'w;o', 'de')" },
      { "de21.", strings("einundzwanzigste"), "format-integer(21, 'w;o', 'de')" },
      { "de22.", strings("zweiundzwanzigste"), "format-integer(22, 'w;o', 'de')" },
      { "de23.", strings("dreiundzwanzigste"), "format-integer(23, 'w;o', 'de')" },
      { "de99.", strings("neunundneunzigste"), "format-integer(99, 'w;o', 'de')" },
      { "de999.", strings("neunhundertneunundneunzigste"),
        "format-integer(999, 'w;o', 'de')" },
      { "de9999.", strings("neuntausendneunhundertneunundneunzigste"),
        "format-integer(9999, 'w;o', 'de')" },
      { "de999999.", strings("neunhundertneunundneunzigtausendneunhundertneunundneunzigste"),
        "format-integer(999999, 'w;o', 'de')" },
      { "de100.", strings("einhundertste"), "format-integer(100, 'w;o', 'de')" },
      { "de1000.", strings("eintausendste"), "format-integer(1000, 'w;o', 'de')" },
      { "de10000.", strings("zehntausendste"), "format-integer(10000, 'w;o', 'de')" },
      { "de100000.", strings("einhunderttausendste"), "format-integer(100000, 'w;o', 'de')" },
      { "de1000000.", strings("eine millionste"), "format-integer(1000000, 'w;o', 'de')" },
    };
  }
}
