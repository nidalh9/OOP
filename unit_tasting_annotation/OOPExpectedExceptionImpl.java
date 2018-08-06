package OOP.Solution;

import OOP.Provided.OOPExpectedException;

import java.util.LinkedList;
import java.util.List;


public class OOPExpectedExceptionImpl implements OOPExpectedException{

    private Class<? extends Exception> exceptionClass = null;
    private List<String> expectedMessages;
    Boolean thereIsExpected = Boolean.FALSE;
    public OOPExpectedExceptionImpl() {
        expectedMessages = new LinkedList<>();
    }

    @Override
    public Class<? extends Exception> getExpectedException() {
        return exceptionClass;
    }

    @Override
    public OOPExpectedException expect(Class<? extends Exception> expected) {
        exceptionClass = expected;
        thereIsExpected = true;
        return this;
    }



    /**
     * expect the exception message to have a message as its substring.
     * Should be okay if the message expected is a substring of the entire exception message.
     * Can expect several messages.
     * Example: for the exception message: "aaa bbb ccc", for an OOPExpectedException e:
     * e.expectMessage("aaa").expectMessage("bb c");
     * - This should be okay.
     *
     * @param msg - the expected message.
     * @return this object.
     */
    @Override
    public OOPExpectedException expectMessage(String msg) {
        expectedMessages.add(msg);
        return this;
    }


    /**
     * checks that the exception that was thrown, and passed as parameter,
     * is of a type as expected. Also checks expected message are contained in the exception message.
     * <p>
     * Should handle inheritance. For example, for expected exception A, if an exception B was thrown,
     * and B extends A, then it should be okay.
     *
     * @param e - the exception that was thrown.
     * @return whether or not the actual exception was as expected.
     */
    @Override
    public boolean assertExpected(Exception e) {
        Boolean allContains = true;
        if(exceptionClass == null){
            return e==null;
        }
        for(String current : expectedMessages)
        {
            if(!(e.getMessage().contains(current)))
            {
                allContains = false;
            }

        }
        return (exceptionClass.isInstance(e) && allContains) ;
    }

    public static OOPExpectedException none(){
        return new OOPExpectedExceptionImpl();
    }

    public Class<? extends Exception> getExceptionClass() {
        return exceptionClass;
    }

    public List<String> getExpectedMessages() {
        return expectedMessages;
    }

    public Boolean getThereIsExpected() {
        return thereIsExpected;
    }
}
