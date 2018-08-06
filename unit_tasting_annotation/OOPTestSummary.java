package OOP.Solution;

import OOP.Provided.OOPResult;
import java.util.Map;

public class OOPTestSummary {

    public Map<String, OOPResult> testMap;

    public OOPTestSummary(Map<String, OOPResult> testMap) {
        this.testMap = testMap;
    }

    public int getNumSuccesses() {
        int c = 0;
        for(OOPResult curr : testMap.values())
            if(OOPResult.OOPTestResult.SUCCESS.equals(curr.getResultType()))
                c++; // :)
        return c;
    }

    public int getNumFailures() {
        int c = 0;
        for(OOPResult curr : testMap.values())
            if(OOPResult.OOPTestResult.FAILURE.equals(curr.getResultType()))
                c++; // :)
        return c;    }

    public int getNumExceptionMismatches() {
        int c = 0;
        for(OOPResult curr : testMap.values())
            if(OOPResult.OOPTestResult.EXPECTED_EXCEPTION_MISMATCH.equals(curr.getResultType()))
                c++; // :)
        return c;    }

    public int getNumErrors() {
        int c = 0;
        for(OOPResult curr : testMap.values())
            if(OOPResult.OOPTestResult.ERROR.equals(curr.getResultType()))
                c++; // :)
        return c;    }
}
