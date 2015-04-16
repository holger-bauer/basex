package org.basex.query.util.fingertree;

import java.util.*;

/**
 * List iterator over the elements of a finger tree.
 *
 * @author BaseX Team 2005-15, BSD License
 * @author Leo Woerteler
 *
 * @param <E> element type
 */
final class FingerTreeIterator<E> implements ListIterator<E> {
  /** Size of the root. */
  private final long n;
  /** Current index. */
  private long index;

  /** Stack of deep trees. */
  private DeepTree<?, E>[] trees;
  /** Position of the current node in the digits. */
  private int deepPos;
  /** Stack pointer for the deep trees. */
  private int tTop;

  /** Node stack. */
  private InnerNode<?, E>[] nodes;
  /** Position stack. */
  private int[] poss;
  /** Stack pointer. */
  private int nTop;

  /** Current leaf node. */
  private Node<E, E> leaf;
  /** Position inside the current leaf. */
  private int leafPos;

  /**
   * Constructor.
   * @param n size of the root
   * @param index initial index
   * @param trees stack of deep finger tree nodes
   * @param tTop top of the tree stack
   * @param deepPos position inside the top tree
   * @param root root node
   * @param position position inside the root node
   */
  @SuppressWarnings("unchecked")
  private FingerTreeIterator(final long n, final long index, final DeepTree<?, E>[] trees,
      final int tTop, final int deepPos, final Node<?, E> root, final long position) {

    this.n = n;
    this.index = index;
    this.trees = trees;
    this.tTop = tTop;
    this.deepPos = deepPos;

    this.nodes = new InnerNode[8];
    this.poss = new int[8];
    this.nTop = -1;

    long pos = position;
    Node<?, E> curr = root;
    while(curr instanceof InnerNode) {
      final InnerNode<?, E> inner = (InnerNode<?, E>) curr;

      int idx = 0;
      Node<?, E> sub = inner.getSub(0);
      for(;;) {
        final long sz = sub.size();
        if(pos < sz) break;
        pos -= sz;
        sub = inner.getSub(++idx);
      }

      if(++nTop == nodes.length) {
        nodes = Arrays.copyOf(nodes, 2 * nTop);
        poss = Arrays.copyOf(poss, 2 * nTop);
      }
      nodes[nTop] = inner;
      poss[nTop] = idx;
      curr = sub;
    }

    leaf = (Node<E, E>) curr;
    leafPos = (int) (index < n ? pos : pos + 1);
  }

  /**
   * Returns a list iterator for the given node starting at the given position.
   * @param <E> element type
   * @param root root node
   * @param start starting position
   * @return the iterator
   */
  static <E> ListIterator<E> get(final Node<?, E> root, final long start) {
    final long n = root.size(), index = Math.max(0, Math.min(start, n));
    return new FingerTreeIterator<>(n, index, null, -1, 0, root, Math.min(index, root.size() - 1));
  }

  /**
   * Returns a list iterator for the given finger tree starting at the given position.
   * @param <E> element type
   * @param tree finger tree
   * @param start starting position
   * @return the iterator
   */
  static <E> ListIterator<E> get(final FingerTree<?, E> tree, final long start) {
    if(tree.isEmpty()) return Collections.emptyListIterator();
    if(tree instanceof SingletonTree) return get(tree.head(), start);

    final DeepTree<?, E> root = (DeepTree<?, E>) tree;
    final long n = root.size;
    final long index = Math.max(0, Math.min(start, n));

    @SuppressWarnings("unchecked")
    DeepTree<?, E>[] trees = new DeepTree[8];
    trees[0] = root;
    int tTop = 0;

    int deepPos;
    Node<?, E> node;
    long pos = Math.min(index, n - 1);
    for(;;) {
      final DeepTree<?, E> curr = trees[tTop];
      if(pos < curr.leftSize) {
        // left digit
        final Node<?, E>[] left = curr.left;
        int i = 0;
        for(;; i++) {
          node = left[i];
          final long sz = node.size();
          if(pos < sz) break;
          pos -= sz;
        }
        deepPos = i - left.length;
        break;
      }
      pos -= curr.leftSize;

      final FingerTree<?, E> mid = curr.middle;
      final long midSize = mid.size();
      if(pos >= midSize) {
        // right digit
        pos -= midSize;
        final Node<?, E>[] right = curr.right;
        int i = 0;
        for(;; i++) {
          node = right[i];
          final long sz = node.size();
          if(pos < sz) break;
          pos -= sz;
        }
        deepPos = i + 1;
        break;
      }


      if(mid instanceof SingletonTree) {
        // single middle node
        node = mid.head();
        deepPos = 0;
        break;
      }

      // go one level deeper
      if(++tTop == trees.length) trees = Arrays.copyOf(trees, 2 * tTop);
      trees[tTop] = (DeepTree<?, E>) mid;
    }

    return new FingerTreeIterator<>(n, index, trees, tTop, deepPos, node, pos);
  }

  @Override
  public int nextIndex() {
    return (int) index;
  }

  @Override
  public boolean hasNext() {
    return index < n;
  }

  @Override
  @SuppressWarnings("unchecked")
  public E next() {
    if(index >= n) throw new NoSuchElementException();

    index++;
    if(leafPos < leaf.arity()) return leaf.getSub(leafPos++);

    // leaf drained, backtrack
    while(nTop >= 0 && poss[nTop] == nodes[nTop].arity() - 1) nTop--;

    final Node<?, E> start;
    if(nTop >= 0) {
      // go to next sub-node
      start = nodes[nTop].getSub(++poss[nTop]);
    } else {
      // node drained, move to the next one
      final DeepTree<?, E> curr = trees[tTop];
      if(deepPos < -1) {
        // go to next node in digit
        ++deepPos;
        start = curr.left[curr.left.length + deepPos];
      } else if(deepPos == -1) {
        // left digit drained
        final FingerTree<?, E> mid = curr.middle;
        if(mid instanceof EmptyTree) {
          // skip empty middle tree
          deepPos = 1;
          start = curr.right[0];
        } else if(mid instanceof SingletonTree) {
          // iterate through the one middle node
          deepPos = 0;
          start = ((SingletonTree<?, E>) mid).elem;
        } else {
          final DeepTree<?, E> deep = (DeepTree<?, E>) mid;
          if(++tTop == trees.length) {
            final DeepTree<?, E>[] newTrees = new DeepTree[2 * tTop];
            System.arraycopy(trees, 0, newTrees, 0, tTop);
            trees = newTrees;
          }
          trees[tTop] = deep;
          deepPos = -deep.left.length;
          start = deep.left[0];
        }
      } else if(deepPos == 0) {
        // we are in a single middle node
        deepPos = 1;
        start = curr.right[0];
      } else {
        // we are in the right digit
        final int p = deepPos - 1;
        if(p < curr.right.length - 1) {
          // go to next node in digit
          deepPos++;
          start = curr.right[p + 1];
        } else {
          // backtrack one level
          trees[tTop] = null;
          deepPos = 0;
          tTop--;
          start = trees[tTop].right[0];
          deepPos = 1;
        }
      }
    }

    Node<?, E> sub = start;
    while(sub instanceof InnerNode) {
      if(++nTop == nodes.length) {
        final InnerNode<?, E>[] newNodes = new InnerNode[2 * nTop];
        System.arraycopy(nodes, 0, newNodes, 0, nTop);
        nodes = newNodes;
        poss = Arrays.copyOf(poss, 2 * nTop);
      }
      nodes[nTop] = (InnerNode<?, E>) sub;
      poss[nTop] = 0;
      sub = nodes[nTop].getSub(0);
    }

    leaf = (Node<E, E>) sub;
    leafPos = 1;
    return leaf.getSub(0);
  }

  @Override
  public int previousIndex() {
    return (int) (index - 1);
  }

  @Override
  public boolean hasPrevious() {
    return index > 0;
  }

  @Override
  @SuppressWarnings("unchecked")
  public E previous() {
    if(index <= 0) throw new NoSuchElementException();

    --index;
    if(leafPos > 0) return leaf.getSub(--leafPos);

    // leaf drained, backtrack
    while(nTop >= 0 && poss[nTop] == 0) nTop--;

    final Node<?, E> start;
    if(nTop >= 0) {
      // go to previous sub-node
      start = nodes[nTop].getSub(--poss[nTop]);
    } else {
      // node drained, move to the previous one
      final DeepTree<?, E> curr = trees[tTop];
      if(deepPos > 1) {
        // go to next node in right digit
        --deepPos;
        start = curr.right[deepPos - 1];
      } else if(deepPos == 1) {
        // right digit drained
        final FingerTree<?, E> mid = curr.middle;
        if(mid instanceof EmptyTree) {
          // skip empty middle tree
          final int l = curr.left.length;
          start = curr.left[l - 1];
          deepPos = -1;
        } else if(mid instanceof SingletonTree) {
          // iterate through the one middle node
          start = ((SingletonTree<?, E>) mid).elem;
          deepPos = 0;
        } else {
          // go into the middle tree
          final DeepTree<?, E> deep = (DeepTree<?, E>) mid;
          if(++tTop == trees.length) {
            final DeepTree<?, E>[] newTrees = new DeepTree[2 * tTop];
            System.arraycopy(trees, 0, newTrees, 0, tTop);
            trees = newTrees;
          }
          trees[tTop] = deep;
          final int r = deep.right.length;
          start = deep.right[r - 1];
          deepPos = r;
        }
      } else if(deepPos == 0) {
        start = curr.left[curr.left.length - 1];
        deepPos = -1;
      } else {
        // we are in the left digit
        final int l = curr.left.length, p = l + deepPos;
        if(p > 0) {
          // go to previous node in digit
          --deepPos;
          start = curr.left[p - 1];
        } else {
          // backtrack one level
          trees[tTop] = null;
          --tTop;
          final Node<?, E>[] left = trees[tTop].left;
          start = left[left.length - 1];
          deepPos = -1;
        }
      }
    }

    Node<?, E> sub = start;
    while(sub instanceof InnerNode) {
      if(++nTop == nodes.length) {
        final InnerNode<?, E>[] newNodes = new InnerNode[2 * nTop];
        System.arraycopy(nodes, 0, newNodes, 0, nTop);
        nodes = newNodes;
        poss = Arrays.copyOf(poss, 2 * nTop);
      }
      nodes[nTop] = (InnerNode<?, E>) sub;
      poss[nTop] = sub.arity() - 1;
      sub = nodes[nTop].getSub(poss[nTop]);
    }

    leaf = (Node<E, E>) sub;
    leafPos = sub.arity() - 1;
    return leaf.getSub(leafPos);
  }

  @Override
  public void set(final E e) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void add(final E e) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }
}