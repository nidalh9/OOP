//
// Created by nidal on 6/25/2018.
//

#ifndef OOP5_MATRIXOPERATIONS_H
#define OOP5_MATRIXOPERATIONS_H

#include "Utilities.h"
#include "TransposeList.h"
/*********************************************/
/*               MatrixGet                    */
/*********************************************/
 
 

template <int N ,int M,typename... T>
struct MatrixGet;

template< int N ,int M , typename H,typename... T >
struct MatrixGet<N,M, List< H,T... >>
{
public:

    typedef typename ListGet<M,typename ListGet<N,List<H,T... >>::value>::value value;
    
};

/*********************************************/
/*               AddList                    */
/*********************************************/
template <typename ...>
struct AddList;

template<>
struct AddList<List<>,List<>> {
    typedef List<> intList;
};

template<typename  U ,typename H >
struct AddList<List<U>,List<H >>
{ 
    public:
        typedef  List< Int<   U::value +   H::value> >    intList;

};


template<typename... T2,typename... T1 >
struct AddList<List<T1...>,List<T2... >>
{ 
    public:
        static_assert( List<T1...>::size ==  List<T2...>::size , "Matixes not colomuns number ");
        typedef   typename PrependList<   Int<  List<T1...>::head ::value  +  List<T2...>::head ::value   >  ,
         typename AddList< typename List<T1... >::next ,typename List<T2... >::next > ::intList >::list  intList;

};




template<typename firstHead, typename... firstTail, typename secondHead, typename... secondTail>
struct AddList<List<firstHead,firstTail...>,List<secondHead,secondTail...>> {
private:
    typedef Int<firstHead::value + secondHead::value> addedHead;
public:
    typedef typename PrependList<addedHead, typename AddList<List<firstTail...>,
                                List<secondTail...>>::intList>::list intList;
};






/*********************************************/ 
/*               Add                   */
/*********************************************/




template <typename ...> 
struct Add;

template<>
struct Add<List<>,List<>> {
    typedef List<> result;
};


template<typename  U ,typename H >
struct Add<List<U>,List<H >>
{ 
    public:
        typedef    List< typename AddList<U,H>::intList>   result;

};



template<typename... T2,typename... T1 >
struct Add<List<T1...>,List<T2... >>
{ 
    public:
        static_assert( List<T1...>::size ==  List<T2...>::size , "Matixes not rows number");
        typedef  typename PrependList< typename AddList<typename  List<T1...>::head , typename List<T2...>::head> ::intList,
         typename   Add< typename List<T1... >::next ,typename List<T2... >::next >::result >::list  result ;

};
 



/*********************************************/ 
/*               sumMult                   */
/*********************************************/

template <typename ...>
struct sumMult;

template<>
struct sumMult<List<>,List<>> {
    typedef Int<0> oneMul;
};
 

template<typename H,typename U>
struct sumMult<List<H>,List<U>> {
    typedef Int<H::value * U::value> oneMul;
};
 

template<typename... T2,typename... T1 >
struct sumMult<List<T1...>,List<T2... >>
{ 
    public:
        static_assert( List<T1...>::size ==  List<T2...>::size , " NOT same size ");

        typedef    Int<  List<T1...>::head ::value  *  List<T2...>::head ::value   +     
          sumMult< typename List<T1... >::next ,typename List<T2... >::next > ::oneMul ::value >  oneMul;

};



/*********************************************/ 
/*               MultyVector                   */
/*********************************************/



template <typename ...>
struct MultyVector;


template<typename ... T , typename LastHead >
struct MultyVector< List< T...> ,List<LastHead > > {
    private:
    typedef typename sumMult< typename PrependList< typename  List<T... >::head ,typename List<T... >::next>::list , typename List<LastHead>::head >::oneMul lastint;
    public:
    typedef   List<lastint> vector;
};


template<>
struct MultyVector<List<> , List<>> {
    typedef List<> vector;
};



template<typename... T1,typename... T2 >
struct MultyVector< List<T1...>,  List<T2... >>
{ 

    private:

    typedef   typename sumMult< typename  PrependList< typename  List<T1... >::head ,typename List<T1... >::next>::list  , typename List<T2... >::head >  ::oneMul firstHead;
    typedef   typename MultyVector<  typename PrependList< typename  List<T1... >::head ,typename List<T1... >::next>::list  , typename List<T2... >::next >::vector restOfvector;

    public:
        typedef  typename  PrependList< firstHead , restOfvector >::list vector;
};




/*********************************************/ 
/*               Multiply                   */
/*********************************************/

template <typename ...>
struct Multiply;

template<>
struct Multiply<List<>,List<>> {
    typedef List<> result;
}; 



template<typename ... T , typename LastHead >
struct Multiply<List<LastHead > , List< T...> > {

    private:
        typedef typename Transpose<List<T... >>::matrix transp;
        typedef typename MultyVector< LastHead ,transp>::vector  singleVector;
    public: 
    typedef  List<singleVector> result;
};
 


template<typename... T2,typename... T1 >
struct Multiply<List<T1...>,List<T2... >>
{ 
    public:
       

        typedef typename PrependList< 
                        typename MultyVector<
                                             typename  List<T1...>::head ,typename Transpose< List<T2...>>::matrix   
                                             >::vector ,
                         typename Multiply<
                                            typename List<T1... >::next  ,typename PrependList<typename  List<T2... >::head ,typename List<T2... >::next >::list
                                         >::result 
                 >::list result;

};

#endif //OOP5_MATRIXOPERATIONS_H
