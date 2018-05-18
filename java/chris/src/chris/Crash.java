package chris;

/**
 * Fatal exception, does not require the "throws" word in the method header where it is thrown.
 * @author su
 */
public class Crash extends RuntimeException {

   /**
    * Constructor
    */
   public Crash() {
      super();
   }

   /**
    * Constructor
    * @param msg error message
    */
   public Crash(String msg) {
      super(msg);
   }

   /**
    * Constructor
    * @param msg error message
    * @param ex exception
    */
   public Crash(String msg, Throwable ex) {
      super(msg, ex);
   }
}
