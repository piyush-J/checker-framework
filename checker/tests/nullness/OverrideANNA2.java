import org.checkerframework.checker.initialization.qual.*;
import org.checkerframework.checker.nullness.qual.*;

class OverrideANNA2 {
  static class Super {
    Object f;

    @EnsuresNonNull("f")
    void setf(@Raw @UnknownInitialization Super this) {
      f = new Object();
    }

    Super() {
      setf();
    }
  }

  static class Sub extends Super {
    Object f;

    @Override
    @EnsuresNonNull("f")
    //:: error: (contracts.postcondition.override.invalid)
    void setf(@Raw @UnknownInitialization Sub this) {
      f = new Object();
    }

    Sub() {
      setf();
    }
  }

  public static void main(String[] args) {
    Super s = new Sub();
    s.f.hashCode();
  }
}
