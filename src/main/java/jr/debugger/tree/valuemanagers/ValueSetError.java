package jr.debugger.tree.valuemanagers;

import java.io.PrintStream;
import java.io.PrintWriter;

public class ValueSetError extends RuntimeException {
    public ValueSetError(String message) {
        super(message);
    }
    
    public ValueSetError(String message, Throwable cause) {
        super(message, cause);
    }
    
    @Override
    public String toString() {
        if (getCause() != null) {
            return getCause().toString();
        } else {
            return super.toString();
        }
    }
    
    @Override
    public StackTraceElement[] getStackTrace() {
        if (getCause() != null) {
            return getCause().getStackTrace();
        } else {
            return super.getStackTrace();
        }
    }
    
    @Override
    public void printStackTrace() {
        if (getCause() != null) {
            getCause().printStackTrace();
        } else {
            super.printStackTrace();
        }
    }
    
    @Override
    public void printStackTrace(PrintStream s) {
        if (getCause() != null) {
            getCause().printStackTrace(s);
        } else {
            super.printStackTrace(s);
        }
    }
    
    @Override
    public void printStackTrace(PrintWriter s) {
        if (getCause() != null) {
            getCause().printStackTrace(s);
        } else {
            super.printStackTrace(s);
        }
    }
}