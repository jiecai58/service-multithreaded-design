# service-multithreaded-design
多线程编程设计模式


## 不可变模式

多线程环境中. 一个对象常常会被多个线程共享. 如果存在多个线程并发地修改该对象的状态或者一个线程访问该对象的状态而另一个线程试图修改该对象的状态. 我们就不得不做一些同步访问控制以保证数据一致性.
不可变模式意图通过使用对外可见的状态不可变对象, 使得被共享对象天生具有线程安全. 而无须额外地同步访问控制. 从而即保证了数据一致性. 又避免了同步访问控制所产生的额外开销和问题.

final 修饰类.  保证类不会被继承
final 修饰类属性. 保证属性一定会被初始化. 且不会被修改了
提供get方法. 不提供set方法.  保证状态不会被修改
提供能够初始化的构造器. 保证类的实例化
私有化无参构造器

## 保护性暂挂模式
## 两阶段终止模式
## 承诺模式
## 生产者/消费者模式
## 主动对象模式
## 线程池模式
## 线程持有存储模式
## 串行线程封闭模式
## 主仆模式
## 流水线模式
## 半同步/半异步模式