// ***** This file is automatically generated from SubSequence.java.jpp

package daikon.inv.binary.twoSequence;

import daikon.*;
import daikon.inv.*;
import daikon.inv.unary.sequence.*;
import daikon.derive.*;
import daikon.derive.binary.*;
import daikon.derive.ternary.*;
import daikon.suppress.*;

import utilMDE.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.*;

/**
 * Represents two sequences of double values where one sequence is a
 * subsequence of the other.  Prints as
 * <samp>x[] is a subsequence of y[]</samp>.
 **/

public class SuperSequenceFloat
  extends TwoSequenceFloat
{
  // We are Serializable, so we specify a version to allow changes to
  // method signatures without breaking serialization.  If you add or
  // remove fields, you should change this number to the current date.
  static final long serialVersionUID = 20031024L;

  private static final Logger debug =
    Logger.getLogger("daikon.inv.binary.twoSequence.SubSequenceFloat");

  // Variables starting with dkconfig_ should only be set via the
  // daikon.config.Configuration interface.
  /**
   * Boolean.  True iff SubSequence invariants should be considered.
   **/
  public static boolean dkconfig_enabled = true;

  protected SuperSequenceFloat(PptSlice ppt) {
    super(ppt);
  }

  private static SuperSequenceFloat proto;

  /** Returns the prototype invariant for SuperSequenceFloat **/
  public static Invariant get_proto() {
    if (proto == null)
      proto = new SuperSequenceFloat (null);
    return (proto);
  }

  /** returns whether or not this invariant is enabled **/
  public boolean enabled() {
    return dkconfig_enabled;
  }

  /** instantiates the invariant on the specified slice **/
  protected Invariant instantiate_dyn (PptSlice slice) {
    return new SuperSequenceFloat (slice);
  }

  protected Invariant resurrect_done_swapped() {
    return new SubSequenceFloat (ppt);
  }

  public String format_using(OutputFormat format) {
    if (format == OutputFormat.DAIKON) return format_daikon();
    if (format == OutputFormat.IOA) return format_ioa();
    if (format == OutputFormat.SIMPLIFY) return format_simplify();
    if (format.isJavaFamily()) { return format_unimplemented(format); }

    return format_unimplemented(format);
  }

  public String format_daikon() {
    String v1 = var2().name();
    String v2 = var1().name();
    return v1 + " is a subsequence of " + v2;
  }

  /* IOA */
  public String format_ioa() {
    String result;
    result = var2().ioa_name() + " \\subseteq "
           + var1().ioa_name();

    if (var1().isIOAArray() || var2().isIOAArray()) {
      result += " *** (Invalid syntax for arrays)";
    }

    return result;
  }

  public String format_simplify() {
    if (Invariant.dkconfig_simplify_define_predicates)
      return format_simplify_defined();
    else
      return format_simplify_explicit();
  }

  private String format_simplify_defined() {
    String[] sub_name = var2().simplifyNameAndBounds();
    String[] super_name = var1().simplifyNameAndBounds();

    if (sub_name == null || super_name == null) {
      return "format_simplify can't handle one of these sequences: "
        + format();
    }

    return "(subsequence " +
      sub_name[0] + " " + sub_name[1] + " " + sub_name[2] + " " +
      super_name[0] + " " + super_name[1] + " " + super_name[2] + ")";
  }

  // This is apparently broken somehow, though from the logs it's not
  // clear how. -- smcc
  private String format_simplify_explicit() {
    return "format_simplify disabled";

    /* Since this doesn't work (since at least april 2003)
       and it uses quant in complex ways,
       its just commented out (jhp 8/3/2006)

    // (exists k s.t. (forall i, j; (i bounds & j bounds & (i = j + k)) ==> ...))

    QuantifyReturn qret = QuantHelper.quantify(new VarInfoName[]
                                { var2().name, var1().name} );
    Assert.assertTrue(qret.bound_vars.size() == 2);
    Assert.assertTrue(qret.root_primes.length == 2);

    // These variables are, in order: Example element, free Index
    // variable, Lower bound, Upper bound, Span
    String aE, aI, aL, aH, aS; // a = subsequence
    String bE, bI, bL, bH, bS; // b = supersequence
    {
      VarInfoName[] boundv;

      boundv = qret.bound_vars.get(0);
      aE = qret.root_primes[0].simplify_name();
      aI = boundv[0].simplify_name();
      aL = boundv[1].simplify_name();
      aH = boundv[2].simplify_name();
      aS = "(+ (- " + aH + " " + aL + ") 1)";

      boundv = qret.bound_vars.get(1);
      bE = qret.root_primes[1].simplify_name();
      bI = boundv[0].simplify_name();
      bL = boundv[1].simplify_name();
      bH = boundv[2].simplify_name();
      bS = "(+ (- " + bH + " " + bL + ") 1)";
    }

    // This invariant would not have been given data if a value was
    // missing - for example, if a slice had a negative length.  We
    // must predicate this invariant on the values being sensible.

    String sensible = "(AND (>= " + aS + " 0) (>= " + bS + " 0))";

    // This invariant would have been falsified if the subsequence A
    // length was ever zero.  Also, this invariant would have been
    // falsified if the subsequence A was ever longer than the
    // supersequence B.

    String length_stmt = "(AND (NEQ " + aS + " 0) (>= " + bS + " " + aS + "))";

    // Subsequence means that there exists an offset in supersequence
    // B, where (1) the offset is non-negative, (2) the offset doesn't
    // cause the matching to push past the end of B, and (3) for all
    // indices less than the span of subsequence A, (4) the elements
    // starting from A_low and B_low+offset are equal.

    String index = "|__index|";
    String shift = "|__shift|";
    String subseq_stmt =
      "(EXISTS (" + shift + ") (AND " +
      "(<= 0 " + shift + ") " +                          // 1
      "(<= (+ " + shift + " " + aS + ") " + bS + ") " +  // 2
      "(FORALL (" + index + ") (IMPLIES (AND (<= 0 " + index + ") (< " + index + " " + aS + ")) " + // 3
      "(EQ " +
      UtilMDE.replaceString(aE, aI, "(+ " + aL + " " + index + ")") + " " +
      UtilMDE.replaceString(bE, bI, "(+ (+ " + bL + " " + index + ") " + shift + ")") + ")))))";

    // So, when this in sensible, we know that both the length and
    // subseq statements hold.

    String result = "(IMPLIES " + sensible + " (AND " + length_stmt + " " + subseq_stmt + "))";
    return result;
    */
  }

  public InvariantStatus check_modified (double[] a1, double[] a2,
                                        int count) {
    if ((a1 == null) || (a2 == null))
      return InvariantStatus.FALSIFIED;

      int result = Global.fuzzy.indexOf(a1, a2);

    if (result == -1)
      return InvariantStatus.FALSIFIED;
    else
      return InvariantStatus.NO_CHANGE;
  }

  public InvariantStatus add_modified(double[] a1, double[] a2,
                                      int count) {
    InvariantStatus is = check_modified (a1, a2, count);
    if ((is == InvariantStatus.FALSIFIED) && Debug.logOn())
      log (format() + " destroyed by " + Debug.toString(a1) + " "
           + Debug.toString(a2));
    return (is);
  }

  protected double computeConfidence() {
    return Invariant.CONFIDENCE_JUSTIFIED;
  }

  /**
   * @return Array "a" such that a[0] is a valid discardCode and a[1] is
   * a valid discardString.  If the Invariant is not an obvious subsequence, both
   * are null
   **/
  public static Object[] isObviousSubSequence(VarInfo subvar, VarInfo supervar) {
    // Must typecheck since this could be called with non sequence variables in
    // some methods.
    ProglangType rep1 = subvar.rep_type;
    ProglangType rep2 = supervar.rep_type;
    if (!(((rep1 == ProglangType.INT_ARRAY)
           && (rep2 == ProglangType.INT_ARRAY)) ||
          ((rep1 == ProglangType.DOUBLE_ARRAY)
           && (rep2 == ProglangType.DOUBLE_ARRAY)) ||
          ((rep1 == ProglangType.STRING_ARRAY)
           && (rep2 == ProglangType.STRING_ARRAY))
          )) {
      return new Object[] {null, null};
    }

    if (debug.isLoggable(Level.FINE)) {
      debug.fine ("isObviousSubSequence " +
                  subvar.name() + "in " + supervar.name());
    }

    // Standard discard reason/string
    DiscardCode discardCode = DiscardCode.obvious;
    String discardString = subvar.name()+" obvious subset/subsequence of "+supervar.name();

    // For unions and intersections, it probably doesn't make sense to
    // do subsequence or subset detection.  This is mainly to prevent
    // invariants of the form (x subset of union(x, y)) but this means
    // we also miss those of the form (z subset of union(x,y)) which
    // might be useful.  Subsequence, however, seems totally useless
    // on unions and intersections.
    if (supervar.derived instanceof  SequenceFloatIntersection ||
        supervar.derived instanceof SequenceFloatUnion ||
        subvar.derived instanceof SequenceFloatIntersection ||
        subvar.derived instanceof SequenceFloatUnion) {
      discardCode = DiscardCode.obvious;
      discardString = "Invariants involving subsets/subsequences of unions/intersections"+
        "are suppressed";
      debug.fine ("  returning true because of union or intersection");
      return new Object[] {discardCode, discardString};
    }

    if (subvar.derived instanceof SequencesPredicateFloat) {
      // It's not useful that predicate(x[], b[]) is a subsequence or subset
      // of x[]
      SequencesPredicateFloat derived = (SequencesPredicateFloat) subvar.derived;
      if (derived.var1().equals(supervar)) {
        discardCode = DiscardCode.obvious;
        discardString = subvar.name()+" is derived from "+supervar.name();
        debug.fine ("  returning true because of predicate slicing");
        return new Object[] {discardCode, discardString + " [pred slicing]"};
      }
    }

    VarInfo subvar_super = subvar.isDerivedSubSequenceOf();
    if (subvar_super == null) {
      // If it's not a union, intersection or a subsequence, it's not obvious
      debug.fine ("  returning false because subvar_super == null");
      return new Object[] {null, null};
    }

    if (subvar_super == supervar) {
      // System.out.println("SubSequence.isObviousDerived(" + subvar.name() + ", " + supervar.name + ") = true");
      // System.out.println("  details: subvar_super=" + subvar_super.name + "; supervar_super=" + supervar.isDerivedSubSequenceOf() == null ? "null" : supervar.isDerivedSubSequenceOf().name);
      discardCode = DiscardCode.obvious;
      discardString = subvar.name()+"=="+supervar.name();
      debug.fine ("  returning true because subvar_super == supervar");
      return new Object[] {discardCode, discardString
                            + " [subvar_super == supervar]"};
    }

    // a[i+a..j+b] cmp a[i+c..j+d]
    VarInfo supervar_super = supervar.isDerivedSubSequenceOf();
    // we know subvar_super != null due to check above
    if (subvar_super == supervar_super) {
      // both sequences are derived from the same supersequence
      if ((subvar.derived instanceof SequenceFloatSubsequence ||
           subvar.derived instanceof SequenceFloatArbitrarySubsequence) &&
          (supervar.derived instanceof SequenceFloatSubsequence ||
           supervar.derived instanceof SequenceFloatArbitrarySubsequence)) {
        // In "A[i..j] subseq B[k..l]": i=sub_left_var, j=sub_right_var,
        //  k=super_left_var, l=super_right_var.
        VarInfo sub_left_var = null, sub_right_var = null,
          super_left_var = null, super_right_var = null;
        // I'm careful not to access foo_shift unless foo_var has been set
        // to a non-null value, but Java is too stupid to recognize that.
        int sub_left_shift = 42, sub_right_shift = 69, super_left_shift = 1492,
          super_right_shift = 1776;
        if (subvar.derived instanceof SequenceFloatSubsequence) {
          SequenceFloatSubsequence sub
            = (SequenceFloatSubsequence)subvar.derived;
          if (sub.from_start) {
            sub_right_var = sub.sclvar();
            sub_right_shift = sub.index_shift;
          } else {
            sub_left_var = sub.sclvar();
            sub_left_shift = sub.index_shift;
          }
        } else if (subvar.derived instanceof SequenceFloatArbitrarySubsequence) {
          SequenceFloatArbitrarySubsequence sub = (SequenceFloatArbitrarySubsequence)subvar.derived;
          sub_left_var = sub.startvar();
          sub_left_shift = (sub.left_closed ? 0 : 1);
          sub_right_var = sub.endvar();
          sub_right_shift = (sub.right_closed ? 0 : -1);
        } else {
          Assert.assertTrue(false);
        }
        if (supervar.derived instanceof SequenceFloatSubsequence) {
          SequenceFloatSubsequence super_
            = (SequenceFloatSubsequence)supervar.derived;
          if (super_.from_start) {
            super_right_var = super_.sclvar();
            super_right_shift = super_.index_shift;
          } else {
            super_left_var = super_.sclvar();
            super_left_shift = super_.index_shift;
          }
        } else if (supervar.derived instanceof SequenceFloatArbitrarySubsequence) {
          SequenceFloatArbitrarySubsequence super_ = (SequenceFloatArbitrarySubsequence)supervar.derived;
          super_left_var = super_.startvar();
          super_left_shift = (super_.left_closed ? 0 : 1);
          super_right_var = super_.endvar();
          super_right_shift = (super_.right_closed ? 0 : -1);
        } else {
          Assert.assertTrue(false);
        }
        boolean left_included = false, right_included = false;
        if (super_left_var == null)
          left_included = true;
        if (super_left_var == sub_left_var) {
          if (super_left_shift < sub_left_shift) left_included = true;
        }
        if (super_right_var == null)
          right_included = true;
        if (super_right_var == sub_right_var) {
          if (super_right_shift > sub_right_shift) right_included = true;
        }
        if (left_included && right_included) {
          discardCode = DiscardCode.obvious;
          discardString = subvar.name()+" obvious subset/subsequence of "+supervar.name();
          return new Object[] {discardCode, discardString + " [obvious]"};
        }
      } else if ((subvar.derived instanceof SequenceStringSubsequence)
                 && (supervar.derived instanceof SequenceStringSubsequence)) {
        // Copied from (an old version) just above
        // XXX I think this code is dead; why isn't it just produced
        // from the above by macro expansion? -smcc
        SequenceStringSubsequence sss1 = (SequenceStringSubsequence) subvar.derived;
        SequenceStringSubsequence sss2 = (SequenceStringSubsequence) supervar.derived;
        VarInfo index1 = sss1.sclvar();
        int shift1 = sss1.index_shift;
        boolean start1 = sss1.from_start;
        VarInfo index2 = sss2.sclvar();
        int shift2 = sss2.index_shift;
        boolean start2 = sss2.from_start;
        if (index1 == index2) {
          if (start1 == true && start2 == true) {
            if (shift1 <= shift2) return new Object[] {discardCode,
                                                discardString + " [shift1]"};
          } else if (start1 == false && start2 == false) {
            if (shift1 >= shift2) return new Object[] {discardCode,
                                                discardString + " [shift2]"};
          }
        }
      } else {
        Assert.assertTrue(false, "how can this happen? " + subvar.name() +
                          " " + subvar.derived.getClass() + " " +
                          supervar.name() + " " + supervar.derived.getClass());
      }
    }

    return new Object[] {null, null};
  }

  // Look up a previously instantiated SubSequence relationship.
  public static SuperSequenceFloat find(PptSlice ppt) {
    Assert.assertTrue(ppt.arity() == 2);
    for (Invariant inv : ppt.invs) {
      if (inv instanceof SuperSequenceFloat)
        return (SuperSequenceFloat) inv;
    }
    return null;
  }

  public DiscardInfo isObviousStatically(VarInfo[] vis) {

    VarInfo subvar = var2(vis);
    VarInfo supervar = var1(vis);

    // check for x[i..j] subsequence of x[]
    VarInfo subvar_super = subvar.isDerivedSubSequenceOf();
    if (subvar_super == supervar) {
      debug.fine ("  returning true because subvar_super == supervar");
      return new DiscardInfo(this, DiscardCode.obvious,
                    "x[i..j] subsequence of x[] is obvious");
    }

    Object[] obv1 = SuperSequenceFloat.isObviousSubSequence(subvar, supervar);
    if (obv1[1] != null) {
      return new DiscardInfo(this, (DiscardCode) obv1[0], (String) obv1[1]);
    }

    // JHP: This is not a valid obvious check, since it  doesn't imply that
    // the invariant is true.
    if (!subvar.aux.getFlag(VarInfoAux.HAS_ORDER) ||
        !supervar.aux.getFlag(VarInfoAux.HAS_ORDER)) {
      // Doesn't make sense to consider subsequence if order doens't matter
      return new DiscardInfo(this, DiscardCode.obvious,
                    "Order doesn't matter, so subsequence is meaningless");
    }

    return super.isObviousStatically(vis);
  }

  // Two ways to go about this:
  //   * look at all subseq relationships, see if one is over a variable of
  //     interest
  //   * look at all variables derived from the

  // (Seems overkill to check for other transitive relationships.
  // Eventually that is probably the right thing, however.)
  public DiscardInfo isObviousDynamically(VarInfo[] vis) {

    // System.out.println("checking isObviousImplied for: " + format());
    if (debug.isLoggable(Level.FINE)) {
      debug.fine ("isObviousDynamically: checking " + vis[0].name() + " in " + vis[1].name());
    }

    DiscardInfo super_result = super.isObviousDynamically(vis);
    if (super_result != null) {
      return super_result;
    }

    VarInfo subvar = var2(vis);
    VarInfo supervar = var1(vis);

    // JHP: The next check is an un-interesting check, not
    // an obvious check.  We need to figure out how to resolve this.

    // Uninteresting if this is of the form x[0..i] subsequence
    // x[0..j].  Not necessarily obvious.
    VarInfo subvar_super = subvar.isDerivedSubSequenceOf();
    VarInfo supervar_super = supervar.isDerivedSubSequenceOf();
    if (subvar_super != null && subvar_super == supervar_super) {
      debug.fine ("  returning true because subvar_super == supervar_super");
      return new DiscardInfo(this, DiscardCode.obvious,
                    "x[0..i] subsequence of x[0..j] is uninteresting");
    }

    if (isObviousSubSequenceDynamically (this, subvar, supervar)) {
      return new DiscardInfo(this, DiscardCode.obvious, subvar.name()
                             + " is an obvious subsequence of "
                             + supervar.name());
    }
    return null;
  }

  /**
   * Returns true if the two original variables are related in a way
   * that makes subsequence or subset detection not informative.
   **/
  public static boolean isObviousSubSequenceDynamically (Invariant inv,
                            VarInfo subvar, VarInfo supervar) {

    VarInfo [] vis = {subvar, supervar};

    ProglangType rep1 = subvar.rep_type;
    ProglangType rep2 = supervar.rep_type;
    if (!(((rep1 == ProglangType.INT_ARRAY)
           && (rep2 == ProglangType.INT_ARRAY)) ||
          ((rep1 == ProglangType.DOUBLE_ARRAY)
           && (rep2 == ProglangType.DOUBLE_ARRAY)) ||
          ((rep1 == ProglangType.STRING_ARRAY)
           && (rep2 == ProglangType.STRING_ARRAY))
          )) return false;

    if (debug.isLoggable(Level.FINE)) {
      debug.fine ("Checking isObviousSubSequenceDynamically " +
                   subvar.name() + " in " + supervar.name());
    }

    Object[] di = isObviousSubSequence (subvar, supervar);
    if (di[1] != null) {
      inv.log ("ObvSubSeq- true from isObviousSubSequence: " + di[1]);
      return true;
    }
    debug.fine ("  not isObviousSubSequence(statically)");

    PptTopLevel ppt_parent = subvar.ppt;

    // If the elements of supervar are always the same (EltOneOf),
    // we aren't going to learn anything new from this invariant,
    // since each sequence should have an EltOneOf over it.
    if (false) {
      PptSlice1 slice = ppt_parent.findSlice(supervar);
      if (slice == null) {
        System.out.println("No slice: parent =" + ppt_parent);
      } else {
        System.out.println("Slice var =" + slice.var_infos[0]);
        for (Invariant superinv : slice.invs) {
          System.out.println("Inv = " + superinv);
          if (superinv instanceof EltOneOfFloat) {
            EltOneOfFloat eltinv = (EltOneOfFloat) superinv;
            if (eltinv.num_elts() > 0) {
              inv.log (" obvious because of " + eltinv.format());
              return true;
            }
          }
        }
      }
    }

    // Obvious if subvar is always just []
    if (true) {
      PptSlice1 slice = ppt_parent.findSlice(subvar);
      if (slice != null) {
        for (Invariant subinv : slice.invs) {
          if (subinv instanceof OneOfFloatSequence) {
            OneOfFloatSequence seqinv = (OneOfFloatSequence) subinv;
            if (seqinv.num_elts() == 1) {
              Object elt = seqinv.elt();
              if (elt instanceof long[] && ((long[]) elt).length == 0) {
                Debug.log (debug, inv.getClass(), inv.ppt, vis,
                                "ObvSubSeq- True from subvar being []");
                return true;
              }
              if (elt instanceof double[] && ((double[]) elt).length == 0) {
                inv.log ("ObvSubSeq- True from subvar being []");
                return true;
              }
            }
          }
        }
      }
    }

    // Check for a[0..i] subseq a[0..j] but i < j.
    VarInfo subvar_super = subvar.isDerivedSubSequenceOf();
    VarInfo supervar_super = supervar.isDerivedSubSequenceOf();

    if (subvar_super != null && subvar_super == supervar_super) {
      // both sequences are derived from the same supersequence
      if ((subvar.derived instanceof SequenceFloatSubsequence ||
           subvar.derived instanceof SequenceFloatArbitrarySubsequence) &&
          (supervar.derived instanceof SequenceFloatSubsequence ||
           supervar.derived instanceof SequenceFloatArbitrarySubsequence)) {
        VarInfo sub_left_var = null, sub_right_var = null,
          super_left_var = null, super_right_var = null;
        // I'm careful not to access foo_shift unless foo_var has been set
        // to a non-null value, but Java is too stupid to recognize that.
        int sub_left_shift = 42, sub_right_shift = 69, super_left_shift = 1492,
          super_right_shift = 1776;
        if (subvar.derived instanceof SequenceFloatSubsequence) {
          SequenceFloatSubsequence sub
            = (SequenceFloatSubsequence)subvar.derived;
          if (sub.from_start) {
            sub_right_var = sub.sclvar();
            sub_right_shift = sub.index_shift;
          } else {
            sub_left_var = sub.sclvar();
            sub_left_shift = sub.index_shift;
          }
        } else if (subvar.derived instanceof SequenceFloatArbitrarySubsequence) {
          SequenceFloatArbitrarySubsequence sub = (SequenceFloatArbitrarySubsequence)subvar.derived;
          sub_left_var = sub.startvar();
          sub_left_shift = (sub.left_closed ? 0 : 1);
          sub_right_var = sub.endvar();
          sub_right_shift = (sub.right_closed ? 0 : -1);
        } else {
          Assert.assertTrue(false);
        }
        if (supervar.derived instanceof SequenceFloatSubsequence) {
          SequenceFloatSubsequence super_
            = (SequenceFloatSubsequence)supervar.derived;
          if (super_.from_start) {
            super_right_var = super_.sclvar();
            super_right_shift = super_.index_shift;
          } else {
            super_left_var = super_.sclvar();
            super_left_shift = super_.index_shift;
          }
        } else if (supervar.derived instanceof SequenceFloatArbitrarySubsequence) {
          SequenceFloatArbitrarySubsequence super_ = (SequenceFloatArbitrarySubsequence)supervar.derived;
          super_left_var = super_.startvar();
          super_left_shift = (super_.left_closed ? 0 : 1);
          super_right_var = super_.endvar();
          super_right_shift = (super_.right_closed ? 0 : -1);
        } else {
          Assert.assertTrue(false);
        }
        boolean left_included, right_included;
        if (super_left_var == null)
          left_included = true;
        else if (sub_left_var == null) // we know super_left_var != null here
          left_included = false;
        else
          left_included
            = VarInfo.compare_vars(super_left_var, super_left_shift,
                                   sub_left_var, sub_left_shift,
                                   true /* <= */);
        if (super_right_var == null)
          right_included = true;
        else if (sub_right_var == null) // we know super_right_var != null here
          right_included = false;
        else
          right_included
            = VarInfo.compare_vars(super_right_var, super_right_shift,
                                   sub_right_var, sub_right_shift,
                                   false /* >= */);
//         System.out.println("Is " + subvar.name() + " contained in "
//                            + supervar.name()
//                            + "? left: " + left_included + ", right: "
//                            + right_included);
        if (left_included && right_included) {
          inv.log ("ObvSubSeq- True a[0..i] subseq a[0..j] and i < j");
          return true;
        }
      } else if ((subvar.derived instanceof SequenceStringSubsequence)
                 && (supervar.derived instanceof SequenceStringSubsequence)) {
        // Copied from just above
        SequenceStringSubsequence sss1 = (SequenceStringSubsequence) subvar.derived;
        SequenceStringSubsequence sss2 = (SequenceStringSubsequence) supervar.derived;
        VarInfo index1 = sss1.sclvar();
        int shift1 = sss1.index_shift;
        boolean start1 = sss1.from_start;
        VarInfo index2 = sss2.sclvar();
        int shift2 = sss2.index_shift;
        boolean start2 = sss2.from_start;
        if (start1 == start2)
          if (VarInfo.compare_vars(index1, shift1, index2, shift2, start1)) {
            inv.log ("True from comparing indices");
            return true;
          }
      } else {
        Assert.assertTrue(false, "how can this happen? " + subvar.name()
                        + " " + subvar.derived.getClass() + " "
                        + supervar.name() + " " + supervar.derived.getClass());
      }

    }

    // Also need to check A[0..i] subseq A[0..j] via compare_vars.

    // A subseq B[0..n] => A subseq B

    List<Derivation> derivees = supervar.derivees();
    // For each variable derived from supervar ("B")
    for (Derivation der : derivees) {
      // System.out.println("  ... der = " + der.getVarInfo().name() + " " + der);
      if (der instanceof SequenceFloatSubsequence) {
        // If that variable is "B[0..n]"
        VarInfo supervar_part = der.getVarInfo();
        // Get the canonical version; being equal to it is good enough.
        if (supervar_part.get_equalitySet_leader() == subvar) {
          Debug.log (debug, inv.getClass(), inv.ppt, vis,
                "ObvSubSeq- True from canonical leader");
          return true;
        }

        if (supervar_part.isCanonical()) {
          if (subvar == supervar_part) {
            System.err.println ("Error: variables " +
                                subvar.name() +
                                " and " +
                                supervar_part.name() +
                                " are identical.  Canonical");
            System.err.println (subvar.isCanonical());
            System.err.println (supervar_part.isCanonical());
            throw new Error();
          }

          // Check to see if there is a subsequence over the supervar
          if (ppt_parent.is_subsequence (subvar, supervar_part)) {
            if (Debug.logOn())
              inv.log ("ObvSubSeq- true from A subseq B[0..n] "
                       + subvar.name() + "/"+ supervar_part.name());
            return (true);
          }
        }
      }
    }
    return false;
  }

  public boolean isSameFormula(Invariant inv) {
    Assert.assertTrue(inv instanceof SuperSequenceFloat);
    return (true);
  }

  /** returns the ni-suppressions for SubSequence **/
  public NISuppressionSet get_ni_suppressions() {
    return (suppressions);
  }

  /** definition of this invariant (the suppressee) **/
  private static NISuppressee suppressee
    = new NISuppressee (SuperSequenceFloat.class, 2);

  // suppressor definitions (used in suppressions below)
  private static NISuppressor v1_eq_v2
    = new NISuppressor (0, 1, SeqSeqFloatEqual.class);
  private static NISuppressor v1_pw_eq_v2
    = new NISuppressor (0, 1, PairwiseFloatEqual.class);

  // sub/super sequence suppressions.  We have both SeqSeq and Pairwise
  // as suppressions because each can be enabled separately.
  private static NISuppressionSet suppressions =
    new NISuppressionSet (new NISuppression[] {
      // a[] == b[] ==> a[] sub/superset b[]
      new NISuppression (v1_eq_v2, suppressee),
      // a[] == b[] ==> a[] sub/superset b[]
      new NISuppression (v1_pw_eq_v2, suppressee),
    });

}
