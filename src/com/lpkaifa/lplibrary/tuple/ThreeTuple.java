package com.lpkaifa.lplibrary.tuple;

// 三个元素的对组
public class ThreeTuple<T,S,R> {
	public T first;
	public S second;
	public R third;
	
	public ThreeTuple() {}
	
	public ThreeTuple(T first, S second, R third) {
		this.first = first;
		this.second = second;
		this.third = third;
	}
	
	public ThreeTuple(ThreeTuple<T,S,R> threeTuple) {
		this.first = threeTuple.first;
		this.second = threeTuple.second;
		this.third = threeTuple.third;
	}
	
	// 判断是否相等
	// 不是判断地址值是否相等，而是调用其equals方法
	public boolean equals(T first,S second, R third) {
		return equalsFirst(first)&&equalsSecond(second)&&equalsThird(third);
	}
	public boolean equals(ThreeTuple<T,S,R> threeTuple) {
		return equalsFirst(threeTuple.first)&&equalsSecond(threeTuple.second)&&equalsThird(threeTuple.third);
	}
	
	// 单独判断某个元素是否相等
	public boolean equalsFirst(ThreeTuple<T,S,R> threeTuple) {
		return equalsFirst(threeTuple.first);
	}
	public boolean equalsSecond(ThreeTuple<T,S,R> threeTuple) {
		return equalsSecond(threeTuple.second);
	}
	public boolean equalsThird(ThreeTuple<T,S,R> threeTuple) {
		return equalsThird(threeTuple.third);
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
	public boolean equalsThird(R third) {
		if(third==null) {
			return this.third==null;
		} else {
			return third.equals(this.third);
		}
	}
	
}
