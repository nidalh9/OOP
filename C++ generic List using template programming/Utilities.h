//
// Created by nidal on 7/1/2018.
//

#ifndef OOP5_UTILITIES_H
#define OOP5_UTILITIES_H


template<typename...>
struct List{};


/*********************************************/
/*               List                    */
/*********************************************/

template <typename...>
struct List;


template<typename H, typename... T>
struct List<H,T...> {
public:
    typedef H head ;
    typedef List<T...> next ;
    constexpr static int size = 1 + sizeof...(T);
};

template<>
struct List<> {
public:
    constexpr static int size = 0;
};


template <typename T>
struct List<T>
{
    typedef T head;
    typedef List<> next;
    constexpr static int size = 1;
};


/*********************************************/
/*               PrependList                    */
/*********************************************/

template <typename...>
struct PrependList;


template<typename H, typename... T>
struct PrependList<H, List<T...>> {
public:
    typedef List<H,T...> list  ;
};





/*********************************************/
/*               ListGet                    */
/*********************************************/



template <int N ,typename... T>
struct ListGet;

template<int N, typename H,typename... T >
struct ListGet<N, List< H,T... > >
{
public:
    typedef typename ListGet<N-1, List<T... >>::value value;

};

template<typename H, typename... T>
struct ListGet<0,List<H,T... >>{
    typedef H value;
};
/*********************************************/
/*               setLIst                    */
/*********************************************/


template <int N ,typename... T>
struct ListSet;

template<typename H,typename U , typename... T>
struct ListSet<0,U,List<H,T... >>{
    typedef List<U,T... > list;
};



template<int N,typename H, typename... T>
struct ListSet <N, H, List<T...>> {
public:
    typedef typename PrependList< H, typename ListSet<N-1, H,List<T...>> ::list>::list list ;
};


/*********************************************/
/*               INT<value>                    */
/*********************************************/

template<int N>
struct Int{
public:
    constexpr static int value = N;

};





#endif //OOP5_UTILITIES_H
