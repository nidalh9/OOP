package OOP.Solution;

import OOP.Provided.OOPAssertionFailure;
import OOP.Provided.OOPExceptionMismatchError;
import OOP.Provided.OOPExpectedException;
import OOP.Provided.OOPResult;
import OOP.Provided.OOPResult.OOPTestResult;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import java.lang.reflect.Method;
import java.util.stream.Collectors;

import static OOP.Provided.OOPResult.OOPTestResult.*;

public class OOPUnitCore {

    private static Object backUp(Object o) throws IllegalAccessException, NoSuchFieldException, InstantiationException,
            InvocationTargetException {

        //helper variables
        Class<?> classOfBackup = o.getClass();
        Field[] fieldsOfO = classOfBackup.getDeclaredFields();
        Object newCopy = classOfBackup.newInstance();


        //run on all of O's fields
        for(Field currField : fieldsOfO) {
            if(currField.getDeclaringClass().equals(classOfBackup) == false) {
                continue;
            }
            currField.setAccessible(true);
            classOfBackup.getDeclaredField(currField.getName()).setAccessible(true);

            Object fieldObj = currField.get(o);

            //if a clone exists - in this object or above
            if (fieldObj instanceof Cloneable) {
                //find clone method
                Class<?> recursiveMethodSearch = fieldObj.getClass();
                Method cloneMethod=null;



                //need to reach the bottom most layer - worst case we reach object
                do {
                    try {
                        Method method = recursiveMethodSearch.getDeclaredMethod("clone");
                        cloneMethod = method;
                        cloneMethod.setAccessible(true);
                        break;
                    } catch (NoSuchMethodException e) {

                    }
                    recursiveMethodSearch = recursiveMethodSearch.getSuperclass();
                } while (cloneMethod == null);


                currField.set(newCopy, cloneMethod.invoke(fieldObj));
            } else {
                //if we reach here - we need to locate constructor
                Constructor<?> constructor;
                try {
                    Class<?> fClass = fieldObj.getClass();
                    constructor = fClass.getDeclaredConstructor(fClass);
                    constructor.setAccessible(true);

                    Object objFromConstructor = constructor.newInstance(fieldObj);
                    currField.set(newCopy, objFromConstructor);
                } catch (NoSuchMethodException e) {currField.set(newCopy, fieldObj);}
                //default
                catch(Exception e) {}
            }
        }
        return newCopy;
    }


private static void recap(Object obj,Object backup)
{
    List<Field> fields = Arrays.stream(obj.getClass().getDeclaredFields()).collect(Collectors.toList());
    for (Field f : fields) {

        try {
            f.setAccessible(true);
        } catch (Exception e) {
        }
        Object field_o = null;
        try {
            field_o = (Object) f.get(backup);
        } catch (Exception e) {
        }
        try {
            f.set(obj, field_o);
        } catch (Exception e) {
        }

    }

}


    public static void assertEquals(Object expected, Object actual) throws OOPAssertionFailure{
        if (expected != null)
        {
            if(expected.equals(actual))
            {
                return;
            }

        } else {
            if( actual == null)
            {
                return;
            }
        }
        throw new OOPAssertionFailure(expected, actual);
    }

    public static void fail() throws OOPAssertionFailure {
        throw new OOPAssertionFailure();
    }


    public static OOPTestSummary runClass(Class<?> testClass) {
        return runaux(testClass, null);
    }

    public static OOPTestSummary runClass(Class<?> testClass, String tag) {
        return runaux(testClass, tag);
    }



    private static OOPTestSummary runaux(Class<?> testClass, String tag) {

        if(testClass == null  || !testClass.isAnnotationPresent(OOPTestClass.class))
            throw new IllegalArgumentException();


        Map<String,OOPResult> resultMap = new HashMap<>();
        OOPExpectedException backUpExpected = new OOPExpectedExceptionImpl();

        OOPResult res;
        String errorMsg="";
        Field expectedField = null;
        Field backupField = null;
        OOPTestResult testRes= SUCCESS;
        LinkedList<Method> setup = new LinkedList<Method>();
        LinkedList<Method> before = new LinkedList<Method>();
        LinkedList<Method> after = new LinkedList<Method>();
        LinkedList<Method> tests = new LinkedList<Method>();

        LinkedList<Class> allClasses = new LinkedList<Class>();
        Class tmp = testClass;
        while(!(tmp == Object.class)) {
            allClasses.addLast(tmp);
            tmp = tmp.getSuperclass();
        }

        Boolean methodExists = false;

        for(int i=0;    i<allClasses.size() ;i++)
        {
            tmp = allClasses.get(i);
            for (Method m : tmp.getDeclaredMethods()){
                methodExists = false;

                //remove "inherited" from method names that contain it
                //        String mName = m.getName();
                //       String parsedMethodName = parseMethodName(mName);
                if (m.isAnnotationPresent(OOPSetup.class)) {
                    for(Method k: setup)
                    {
                        if(k.getName().equals(m.getName()))
                        {
                            methodExists = true;
                        }
                    }
                    if(!methodExists)
                        setup.addFirst(m);
                }
                else if (m.isAnnotationPresent(OOPTest.class) ){
                    for(Method k: tests)
                    {
                        if(k.getName().equals(m.getName()))
                        {
                            methodExists = true;
                        }
                    }
                    if(!methodExists)
                        tests.addFirst(m);
                }
                else if (m.isAnnotationPresent(OOPBefore.class) ) {
                    for(Method k: before)
                    {
                        if(k.getName().equals(m.getName()))
                        {
                            methodExists = true;
                        }
                    }
                    if(!methodExists)
                        before.addFirst(m);

                }
                else if (m.isAnnotationPresent(OOPAfter.class) ) {
                    for(Method k: after)
                    {
                        if(k.getName().equals(m.getName()))
                        {
                            methodExists = true;
                        }
                    }
                    if(!methodExists)
                        after.addLast(m);              }

            }

        }
        try {


            Object obj = testClass.newInstance();
            //run setup
            for(int z=0;z<setup.size() ; z++){
                setup.get(z).setAccessible(true);
                setup.get(z).invoke(obj);
            }

            boolean isOrdered = testClass.getAnnotation(OOPTestClass.class).value() == OOPTestClass.OOPTestClassType.ORDERED;
            Method m = null;
            boolean beforeOrAfterFailed = false;


            //get expected FIELD
            for (Field f : testClass.getDeclaredFields()) {
                if (f.isAnnotationPresent(OOPExceptionRule.class)) {
                    expectedField = f;
                }
            }
            if(expectedField!=null)
            {
                expectedField.setAccessible(true);
                backUpExpected.expect(((OOPExpectedException)(expectedField.get(obj))).getExpectedException());
            }



            if (isOrdered) {
                tests.sort(new Comparator<Method>() {
                    @Override
                    public int compare(Method o1, Method o2) {
                        return o1.getAnnotation(OOPTest.class).order()-o2.getAnnotation(OOPTest.class).order();
                    }
                });


            }
            //get next test to run
            for (int i = 0; i < tests.size(); i++) {
                if(expectedField != null) {
                    expectedField.setAccessible(true);
                    ((OOPExpectedException)(expectedField.get(obj))).expect(backUpExpected.getExpectedException());

                }
                Boolean wasThrown = false;
                Object  backup = null;

                beforeOrAfterFailed = false;
                m = tests.get(i);
                m.setAccessible(true);

                if (tag != null) {
                    String tagg = m.getAnnotation(OOPTest.class).tag();
                    if (!(tag.equals(tagg))) {
                        continue;
                    }
                }


                //find all before of m
                for (Method b : before) {
                    b.setAccessible(true);

                    String[] currList = b.getAnnotation(OOPBefore.class).value();
                    for (int k = 0; k < currList.length; k++) {
                        String mName = m.getName();
                        if (currList[k].equals(mName)) {
                            try {
                                backup = backUp(obj);
                                b.invoke(obj);
                            } catch (Exception e) {
                                errorMsg = e.getClass().getName();
                                beforeOrAfterFailed = true;
                                break;
                            }
                        }
                    }
                    if (beforeOrAfterFailed) {

                        break;
                    }
                }

                if (beforeOrAfterFailed) {
                    res = new OOPResultImpl(ERROR, errorMsg);
                    resultMap.put(m.getName(), res);
                    recap(obj,backup);
                    continue;
                }


                //run test
                testRes = SUCCESS;
                errorMsg = null;

                try {
                    m.invoke(obj);
                } catch (InvocationTargetException e_aux) {
                    wasThrown = true;
                    Throwable e = e_aux.getCause();
                    //fail
                    if (e instanceof OOPAssertionFailure) {
                        testRes = FAILURE;
                        errorMsg = e.getMessage();
                    } else {
                        if (expectedField != null) {
                            expectedField.setAccessible(true);
                            OOPExpectedException expectedFieldd = ((OOPExpectedException) (expectedField.get(obj)));
                            if (expectedFieldd.getExpectedException()==null)
                            {
                                testRes = ERROR;
                                errorMsg = e.getMessage();
                            }
                            else if ( !expectedFieldd.assertExpected((Exception) e)) {
                                OOPExceptionMismatchError err = new OOPExceptionMismatchError(expectedFieldd.getExpectedException(), ((Exception) e).getClass());
                                testRes = EXPECTED_EXCEPTION_MISMATCH;
                                errorMsg = err.getMessage();
                            }else {
                                testRes = SUCCESS;
                                errorMsg = null;
                            }
                        } else {
                            testRes = ERROR;
                            errorMsg = e.getMessage();
                        }
                    }

                } catch (Exception e) {
                    testRes = ERROR;
                    errorMsg = e.getMessage();
                }


                if(expectedField!=null && !wasThrown )
                {
                    expectedField.setAccessible(true);
                    OOPExpectedException exp1 =   (OOPExpectedException)(expectedField.get(obj));
                    OOPExpectedException exp2 = OOPExpectedExceptionImpl.none();
                    if(exp1.getExpectedException()!=exp2.getExpectedException())
                    {

                        testRes = ERROR;
                        errorMsg = null;
                    }

                }


                //fill in testRes & errorMsg
                //find all after and run
                for (Method a : after) {
                    a.setAccessible(true);

                    String[] currList = a.getAnnotation(OOPAfter.class).value();
                    for (int j = 0; j < currList.length; j++) {
                        String mName = m.getName();
                        if (currList[j].equals(mName)) {
                            try {
                                backup = backUp(obj);
                                a.invoke(obj);
                            } catch (Exception e) {
                                errorMsg = e.getClass().getName();
                                beforeOrAfterFailed = true;
                                testRes = ERROR;
                                break;
                            }
                        }
                    }
                    if (beforeOrAfterFailed) {
                        recap(obj,backup);
                        break;
                    }
                }

                res = new OOPResultImpl(testRes, errorMsg);
                resultMap.put(m.getName(), res);

            }
        }

        catch (Exception e){
            e.printStackTrace();
        }


        return new OOPTestSummary(resultMap);
    }

    private static String parseMethodName(String name) {

        String result;
        String[] spaceSplit = name.split(" ");

        result = spaceSplit[0] + spaceSplit[1];
        String[] dotSplit = spaceSplit[spaceSplit.length-1].split(".");
        result += dotSplit[dotSplit.length-1];
        return result;
    }


}
