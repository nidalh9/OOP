package OOP.Solution;

import OOP.Provided.OOPResult;

import static OOP.Provided.OOPResult.OOPTestResult.*;

public class OOPResultImpl implements OOPResult {

    public OOPTestResult result ;
    public String str;

    public OOPResultImpl() {
        result = SUCCESS;
    }

    public OOPResultImpl(OOPTestResult result, String str) {
        this.result = result;
        this.str = str;
    }


    @Override
    public OOPTestResult getResultType() {

        //TODO calculate result lgoic
        return result;
    }

    @Override
    public String getMessage() {
        return str;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof OOPResultImpl)) return false;

        return ((OOPResultImpl)obj).result.equals(result)
                && ((OOPResultImpl)obj).str.equals(str);
    }

    public OOPTestResult getResult() {
        return result;
    }

    public void setResult(OOPTestResult result) {
        this.result = result;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }
}
