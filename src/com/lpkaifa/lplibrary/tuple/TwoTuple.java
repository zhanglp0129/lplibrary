package com.lpkaifa.lplibrary.tuple;

// 两个元素的对组
public class TwoTuple<T,S> {
	// 可以直接访问，无需通过Getter和Setter方法
	public T first;
	public S second;
	public TwoTuple(){};
	public TwoTuple(T first, S second) {
		this.first=first;
		this.second=second;
	}
	public TwoTuple(TwoTuple<T,S> twoTuple) {
		this.first= twoTuple.first;
		this.second= twoTuple.second;
	}
	
	// 判断是否相等
	// 不是判断地址值是否相等，而是调用其equals方法
	public boolean equals(T first, S second) {
		return equalsFirst(first)&&equalsSecond(second);
	}
	public boolean equals(TwoTuple<T,S> twoTuple) {
		return equalsFirst(twoTuple.first)&&equalsSecond(twoTuple.second);
	}
	
	// 单独判断某个元素是否相等
	public boolean equalsFirst(TwoTuple<T,S> twoTuple) {
		return equalsFirst(twoTuple.first);
	}
	public boolean equalsSecond(TwoTuple<T,S> twoTuple) {
		return equalsSecond(twoTuple.second);
	}
	public boolean equalsFirst(T first) {
		if(first==null) {
			return this.first==null;
		} else {
			return first.equals(this.first);
		}
	}
	public boolean equalsSecond(S second) {
		if(second==null) {
			return this.second==null;
		} else {
			return second.equals(this.second);
		}
	}
}
