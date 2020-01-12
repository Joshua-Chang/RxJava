package com.example.rxjava.fx;

import java.util.ArrayList;
import java.util.List;

public class TestClient <T>{
    public void test() {
        T t = null;
        t.hashCode();  // 能够调用到Object里面的方法
    }
    public static void main(String[] args) {
       /*List list = new ArrayList();
        list.add("A");
        list.add(1);
        list.add(6.7);
        Object o = list.get(1); // 1  运行时，类型转换异常
        String s = (String) o; */

        // 泛型出现后
        List<String> list = new ArrayList();
        list.add("A");
        // list.add(6); // 编译期 就可以看到错误
        String s = list.get(0);


        Test<Student>test0=null;
        test0.add(new Student());

        show(new Test<Object>());

        show1(new Test<Person>());
        show1(new Test<Student>());
        show1(new Test<WorkSub>());

        show2(new Test<Worker>());
        show2(new Test<Person>());
        show2(new Test<Object>());
//        show2(new Test<WorkSub>());//超出下限

        // todo 读写模式

        // todo 可读模式
        Test<? extends Person> test1 = null;
//        test1.add(new Person()); // 不可写
//        test1.add(new Student()); // 不可写
//        test1.add(new Object()); // 不可写
//        test1.add(new Worker()); // 不可写
        Person person = test1.get(); // 可读*/

        // todo 可写模式  不完全可读
        Test<? super Person> test = null;
        test.add(new Person()); // 可写
        test.add(new Student()); // 可写
        test.add(new Worker()); // 可写

        Object object = test.get(); // 不完全可读
    }

    /**
     * public static<T> void show(Test<T> test)
     *              <T> 声明          <T> 使用
     * @param test
     * @param <T>
     */
    public static<T> void show(Test<T> test){}
    /**
     * public static void show1(Test<?extends Person> test)
     *              模糊不用声明     <上限> 使用
     * @param test
     * @param test<?extends Person>
     */
    public static void show1(Test<?extends Person> test){

    }
    public static void show2(Test<?super Worker> test){

    }
}
