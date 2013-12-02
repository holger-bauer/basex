package org.basex.gui.layout;

import static java.awt.event.KeyEvent.*;
import static org.basex.gui.GUIConstants.*;
import static org.basex.util.Prop.*;

import java.awt.event.*;

/**
 * This class offers system-dependent key mappings.
 *
 * @author BaseX Team 2005-13, BSD License
 * @author Christian Gruen
 * @author Leo Woerteler
 */
public enum BaseXKeys {

  // Cursor

  /** Left.                  */ PREV(VK_LEFT),
  /** Right.                 */ NEXT(VK_RIGHT),
  /** Word left.             */ PREVWORD(MAC ? ALT : META, VK_LEFT),
  /** Word right.            */ NEXTWORD(MAC ? ALT : META, VK_RIGHT),
  /** Beginning of line.     */ LINESTART(MAC ? META : 0, MAC ? VK_LEFT : VK_HOME),
  /** End of line.           */ LINEEND(MAC ? META : 0, MAC ? VK_RIGHT : VK_END),

  /** Up.                    */ PREVLINE(VK_UP),
  /** Down.                  */ NEXTLINE(VK_DOWN),
  /** Page up.               */ PREVPAGE(VK_PAGE_UP),
  /** Page down.             */ NEXTPAGE(VK_PAGE_DOWN),
  /** Page up (read-only).   */ PREVPAGE_RO(SHIFT, VK_SPACE),
  /** Page down (read-only). */ NEXTPAGE_RO(VK_SPACE),
  /** Beginning of text.     */ TEXTSTART(META, MAC ? VK_UP : VK_HOME),
  /** End of text.           */ TEXTEND(META, MAC ? VK_DOWN : VK_END),
  /** Scroll up.             */ SCROLLUP(MAC ? ALT : META, VK_UP, true),
  /** Scroll down.           */ SCROLLDOWN(MAC ? ALT : META, VK_DOWN, true),

  // Find

  /** Find search term.      */ FIND(META, VK_F, true),
  /** Find next hit.         */ FINDNEXT(META, VK_G, true),
  /** Find next hit.         */ FINDNEXT2(MAC ? META : 0, VK_F3, true),
  /** Find previous hit.     */ FINDPREV(META | SHIFT, VK_G, true),
  /** Find previous hit.     */ FINDPREV2(MAC ? META | SHIFT : SHIFT, VK_F3, true),
  /** Jump to error.         */ FINDERROR(META, VK_PERIOD),
  /** Code completion.       */ COMPLETE(META, VK_SPACE),

  // Editing

  /** Undo.                  */ UNDOSTEP(META, VK_Z, true),
  /** Redo.                  */ REDOSTEP(MAC ? META | SHIFT : META, MAC ? VK_Z : VK_Y, true),

  /** Cut.                   */ CUT1(META, VK_X, true),
  /** Cut.                   */ CUT2(SHIFT, VK_DELETE, true),
  /** Copy.                  */ COPY1(META, VK_C, true),
  /** Copy.                  */ COPY2(META, VK_INSERT, true),
  /** Paste.                 */ PASTE1(META, VK_V, true),
  /** Paste.                 */ PASTE2(SHIFT, VK_INSERT, true),
  /** Select all.            */ SELECTALL(META, VK_A, true),

  /** Delete backwards.      */ DELPREV(VK_BACK_SPACE),
  /** Delete.                */ DELNEXT(VK_DELETE),
  /** Delete word backwards. */ DELPREVWORD(MAC ? ALT : META, VK_BACK_SPACE, true),
  /** Delete word.           */ DELNEXTWORD(MAC ? ALT : META, VK_DELETE, true),
  /** Delete line to begin.  */ DELLINESTART(META | (MAC ? 0 : SHIFT), VK_BACK_SPACE, true),
  /** Delete line to end.    */ DELLINEEND(META | (MAC ? 0 : SHIFT), VK_DELETE, true),
  /** Delete complete line.  */ DELLINE(META | SHIFT, VK_D, true),

  /** (Un)comment.           */ COMMENT(META, VK_K, false),
  /** Execute.               */ EXEC1(META, VK_ENTER, true),
  /** Execute.               */ EXEC2(META, VK_F11, true),

  // Navigation

  /** Jump to input bar.     */ INPUTBAR(MAC ? META : 0, VK_F6, true),
  /** Next tab.              */ NEXTTAB(CTRL, VK_TAB, true),
  /** Previous tab.          */ PREVTAB(CTRL | SHIFT, VK_TAB, true),
  /** Close tab.             */ CLOSETAB(META, VK_F4, true),

  /** Browse back.           */ GOBACK(MAC ? META : ALT, VK_LEFT, true),
  /** Browse back.           */ GOBACK2(VK_BACK_SPACE, true),
  /** Browse forward.        */ GOFORWARD(MAC ? META : ALT, VK_RIGHT, true),
  /** Browse up.             */ GOUP(MAC ? META : ALT, VK_UP, true),
  /** Browse home.           */ GOHOME(MAC ? META : ALT, VK_HOME, true),

  /** Refresh.               */ REFRESH(VK_F5),
  /** Jump to project.       */ PROJECT(META | SHIFT, VK_R, true),
  /** Jump to filter.        */ FILTER(META | SHIFT, VK_T, true),
  /** New directory.         */ NEWDIR(META | SHIFT, VK_N),

  // Font

  /** Increment size.        */ INCFONT1(META, VK_PLUS, true),
  /** Increment size.        */ INCFONT2(META, VK_EQUALS, true),
  /** Decrease size.         */ DECFONT(META, VK_MINUS, true),
  /** Standard size.         */ NORMFONT(META, VK_0, true),

  // General

  /** Escape.                */ ESCAPE(VK_ESCAPE),
  /** Context menu.          */ CONTEXT(VK_CONTEXT_MENU),

  /** Space key.             */ SPACE(VK_SPACE),
  /** Tab key.               */ TAB(VK_TAB),
  /** Enter.                 */ ENTER(VK_ENTER),
  /** Shift Enter.           */ SHIFT_ENTER(SHIFT, VK_ENTER);

  /** Modifiers. */
  private final int mod;
  /** Key. */
  private final int key;
  /** Exclusive modifiers flag. */
  private final boolean excl;

  /**
   * Constructor.
   * @param m modifiers
   * @param k key code
   * @param ex modifiers exclusive
   */
  BaseXKeys(final int m, final int k, final boolean ex) {
    mod = m;
    key = k;
    excl = ex;
  }

  /**
   * Constructor for non-exclusive modifiers.
   * @param m modifiers
   * @param k key code
   */
  BaseXKeys(final int m, final int k) {
    this(m, k, false);
  }

  /**
   * Constructor for ignoring modifiers.
   * @param k key code
   * @param ig ignore modifiers
   */
  BaseXKeys(final int k, final boolean ig) {
    this(0, k, ig);
  }

  /**
   * Constructor without modifiers.
   * @param k key code
   */
  BaseXKeys(final int k) {
    this(0, k);
  }

  /**
   * Returns true if the specified key combination was pressed.
   * @param e key event
   * @return result of check
   */
  public boolean is(final KeyEvent e) {
    final int c = e.getKeyCode();
    int m = e.getModifiers();
    if(!excl) m &= mod;
    return m == mod && (c == 0 ? e.getKeyChar() : c) == key;
  }

  /**
   * Returns true if the system's shortcut key was pressed.
   * @param e input event
   * @return result of check
   */
  public static boolean sc(final InputEvent e) {
    return (META & e.getModifiers()) == META;
  }

  /**
   * Returns true if the pressed key includes a control key.
   * @param e key event
   * @return result of check
   */
  public static boolean control(final KeyEvent e) {
    // With Mac, special characters are available via ALT
    return e.isControlDown() || e.isMetaDown() || !MAC && e.isAltDown();
  }

  /**
   * Returns true if the pressed key is a modifier key
   * (including 'escape' and 'alt'-'tab').
   * @param e key event
   * @return result of check
   */
  public static boolean modifier(final KeyEvent e) {
    final int c = e.getKeyCode();
    return c == VK_ALT || c == VK_SHIFT || c == VK_META || c == VK_CONTROL ||
        c == VK_PAUSE || c == VK_CAPS_LOCK || c == VK_ESCAPE ||
        c == VK_TAB && e.isAltDown();
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder(getKeyModifiersText(mod));
    if(sb.length() != 0) sb.append('+');
    return sb.append(KeyEvent.getKeyText(key)).toString();
  }
}
